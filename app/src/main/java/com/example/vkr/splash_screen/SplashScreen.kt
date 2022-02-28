package com.example.vkr.splash_screen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.MotionEvent
import android.widget.LinearLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.vkr.R
import com.example.vkr.activity.authorization.AuthorizationActivity
import com.example.vkr.utils.ShowToast
import org.json.JSONArray
import kotlin.random.Random

class SplashScreen : AppCompatActivity() {

    private var isConnect : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        val view = findViewById<LinearLayout>(R.id.splash_layout)
        view.alpha = 0f

        checkConnect()

        view.animate().setDuration(Random.nextInt(1000, 2500).toLong()).alpha(1f).withEndAction{
            Thread {
                while (!isConnect) { } //ждём когда коннект будет установлен
                startActivity(Intent(this, AuthorizationActivity::class.java))
                finish()
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
                            if(count < 2) ShowToast.show(activity, "Проверьте подключение к интернету")
                        }
                    })
                count++
                mainHandler.postDelayed(this, 1000)
            }
        })
    }
}