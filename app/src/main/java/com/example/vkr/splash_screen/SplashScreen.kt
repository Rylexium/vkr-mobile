package com.example.vkr.splash_screen

import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.vkr.R
import com.example.vkr.activity.authorization.AuthorizationActivity
import com.example.vkr.utils.OpenActivity
import com.example.vkr.utils.ShowToast
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random

class SplashScreen : AppCompatActivity() {

    companion object {
        lateinit var sharedPreferences : SharedPreferences
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)
        val logoImageView = findViewById<ImageView>(R.id.splash_logo)
        val mainLayout = findViewById<LinearLayout>(R.id.splash_layout_main)
        when(Random.nextInt(1, 4)){
            1 -> {
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.color_for_splash_screen1))
                Glide.with(this)
                    .load(R.drawable.splash_screen1)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(logoImageView)
                window.navigationBarColor = applicationContext.getColor(R.color.blue_700)
                window.statusBarColor = applicationContext.getColor(R.color.blue_700)
            }
            2 -> {
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.color_for_splash_screen2))
                Glide.with(this)
                    .load(R.drawable.splash_screen2)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(logoImageView)
                window.navigationBarColor = Color.parseColor("#000080")
                window.statusBarColor = Color.parseColor("#000080")
            }
            3 -> {
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.color_for_splash_screen3))
                Glide.with(this)
                    .load(R.drawable.splash_screen3)
                    .format(DecodeFormat.PREFER_ARGB_8888)
                    .into(logoImageView)
                window.navigationBarColor = applicationContext.getColor(R.color.orange_700)
                window.statusBarColor = applicationContext.getColor(R.color.orange_700)
            }
        }
        sharedPreferences = getPreferences(MODE_PRIVATE)
        val view = findViewById<LinearLayout>(R.id.splash_layout)
        view.alpha = 0f
        view.animate().setDuration(Random.nextInt(1000, 2500).toLong()).alpha(1f).withEndAction{
            lifecycleScope.launch {

                run tryToConnect@{
                    repeat(1001) {
                        if (awaitConnect()) return@tryToConnect
                        if (it % 7 == 0) Handler(Looper.getMainLooper()).post {
                            ShowToast.show(this@SplashScreen, "Проверьте подключение к интернету")
                        }
                        delay(1000)
                    }
                }
                authorization()
            }
        }
    }


    private suspend fun awaitConnect() : Boolean {
        return suspendCoroutine {
            AndroidNetworking.get("https://vkr1-app.herokuapp.com/nationality")
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) { it.resume(true) }
                    override fun onError(anError: ANError?) { it.resume(false) }
                })
        }

    }

    private suspend fun authorization(){
        return suspendCoroutine {
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
                                    this@SplashScreen,
                                    jsonNode["user"]["id_abit"].toString(),
                                    jsonNode["user"]["login"].asText(),
                                    jsonNode["idEducation"].toString()
                                )
                            } else {
                                OpenActivity.openPersonalCabinet(
                                    this@SplashScreen,
                                    jsonNode["user"]["id_abit"].toString(),
                                    jsonNode["user"]["login"].asText(),
                                    jsonNode["idEducation"].toString()
                                )
                            }
                        } catch (e: Exception) {
                            startActivity(Intent(this@SplashScreen, AuthorizationActivity::class.java))
                            finish()
                        }
                    }

                    override fun onError(error: ANError) {
                        startActivity(Intent(this@SplashScreen, AuthorizationActivity::class.java))
                        finish()
                    }
                })
        }
    }
}