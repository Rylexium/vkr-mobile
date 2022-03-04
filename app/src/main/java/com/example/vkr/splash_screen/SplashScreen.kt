package com.example.vkr.splash_screen

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.MotionEvent
import android.widget.LinearLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.vkr.R
import com.example.vkr.activity.authorization.AuthorizationActivity
import com.example.vkr.utils.OpenActivity
import com.example.vkr.utils.ShowToast
import com.fasterxml.jackson.databind.ObjectMapper
import org.json.JSONArray
import org.json.JSONObject
import java.lang.Exception
import kotlin.random.Random

class SplashScreen : AppCompatActivity() {

    private var isConnect : Boolean = false

    companion object {
        lateinit var sharedPreferences : SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)
        sharedPreferences = getPreferences(MODE_PRIVATE)
        val view = findViewById<LinearLayout>(R.id.splash_layout)
        view.alpha = 0f
        checkConnect()
        Log.e("", sharedPreferences.all.toString())
        view.animate().setDuration(Random.nextInt(1000, 2500).toLong()).alpha(1f).withEndAction{
            Thread {
                while (!isConnect) { } //ждём когда коннект будет установлен
                authorization()
            }.start()
        }
    }

    override fun onTouchEvent(event: MotionEvent?): Boolean {
        checkConnect()
        return true
    }

    private fun checkConnect(){
        val activity = this
        val mainHandler = Handler(Looper.getMainLooper())
        var count = 0
        mainHandler.post(object : Runnable {
            override fun run() {
                AndroidNetworking.get("https://vkr1-app.herokuapp.com/nationality")
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                            isConnect = true
                        }

                        override fun onError(anError: ANError?) {
                            if(count % 7 == 0) ShowToast.show(activity, "Проверьте подключение к интернету")
                        }
                    })
                count++
                if(isConnect)
                    mainHandler.removeCallbacksAndMessages(null)
                else
                    mainHandler.postDelayed(this, 1000)
            }
        })
    }

    private fun authorization(){
        val activity = this
        AndroidNetworking.get(
            String.format(
                "https://vkr1-app.herokuapp.com/authorization?login=%s&password=%s",
                sharedPreferences.all["login"], sharedPreferences.all["password"]
            )
        )
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    try {
                        val jsonNode = ObjectMapper().readTree(response.toString())
                        if (jsonNode["idEducation"] == null) {
                            throw Exception("idEducation is null")
                        } else if (jsonNode["idEducation"].toString().toInt() < 5
                            && !java.lang.Boolean.parseBoolean(jsonNode["user"]["is_entry"].toString())
                        ) {
                            OpenActivity.openExamsResult(
                                activity,
                                jsonNode["user"]["id_abit"].toString(),
                                jsonNode["user"]["login"].toString().replace("\"", ""),
                                jsonNode["idEducation"].toString()
                            )
                        } else {
                            OpenActivity.openPersonalCabinet(
                                activity,
                                jsonNode["user"]["id_abit"].toString(),
                                jsonNode["user"]["login"].toString().replace("\"", ""),
                                jsonNode["idEducation"].toString()
                            )
                        }
                    } catch (e: Exception) {
                        startActivity(Intent(activity, AuthorizationActivity::class.java))
                        finish()
                    }
                }

                override fun onError(error: ANError) {
                    startActivity(Intent(activity, AuthorizationActivity::class.java))
                    finish()
                }
            })
    }
}