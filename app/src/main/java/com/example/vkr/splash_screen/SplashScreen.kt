package com.example.vkr.splash_screen

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.LinearLayout
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.vkr.R
import com.example.vkr.activity.authorization.AuthorizationActivity
import com.example.vkr.utils.ShowToast
import okhttp3.OkHttpClient
import org.json.JSONArray
import java.util.concurrent.TimeUnit
import kotlin.random.Random

class SplashScreen : AppCompatActivity() {

    private var isConnect : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        val view = findViewById<LinearLayout>(R.id.splash_layout)
        view.alpha = 0f

        checkConnect(0)

        view.animate().setDuration(Random.nextInt(1000, 2500).toLong()).alpha(1f).withEndAction{
            Thread {
                while (!isConnect) { } //ждём когда коннект будет установлен
                startActivity(Intent(this, AuthorizationActivity::class.java))
                finish()
            }.start()
        }
    }

    private fun checkConnect(tryConnect : Int){
        val activity = this
        var timeout = 2
        if(tryConnect == 4) timeout = 120
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/nationality")
            .setPriority(Priority.IMMEDIATE)
            .setOkHttpClient(OkHttpClient.Builder()
                    .connectTimeout(timeout.toLong(), TimeUnit.SECONDS)
                    .build())
            .build()
            .getAsJSONArray(object : JSONArrayRequestListener {
                override fun onResponse(response: JSONArray) {
                    isConnect = true
                }

                override fun onError(anError: ANError?) {
                    ShowToast.show(activity, "Проверьте подключение к интернету")
                    if(tryConnect <= 3)
                        checkConnect(tryConnect + 1)
                }
            })
    }
}