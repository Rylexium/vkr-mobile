package com.example.vkr.splash_screen

import android.annotation.SuppressLint
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Gravity
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
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
import com.example.vkr.utils.OpenActivity.openMissedMan
import com.example.vkr.utils.ShowToast
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONArray
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine
import kotlin.random.Random


@SuppressLint("CustomSplashScreen")
class SplashScreen : AppCompatActivity() {
    companion object {
        lateinit var sharedPreferences : SharedPreferences
    }
    private lateinit var logoImageView : ImageView
    private lateinit var mainLayout : LinearLayout
    private lateinit var viewLayout : LinearLayout
    private lateinit var progressBar : ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.splash_screen_activity)

        logoImageView = findViewById(R.id.splash_logo)
        mainLayout = findViewById(R.id.splash_layout_main)
        viewLayout = findViewById(R.id.splash_layout)

        setTheme()
        sharedPreferences = getPreferences(MODE_PRIVATE)
        viewLayout.alpha = 0f
        viewLayout.animate().setDuration(Random.nextInt(500, 1000).toLong()).alpha(1f).withEndAction{
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
                delay(500)
                Handler(Looper.getMainLooper()).post { viewLayout.removeView(progressBar) }
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
                            finishAffinity()
                        }
                    }

                    override fun onError(error: ANError) {
                        startActivity(Intent(this@SplashScreen, AuthorizationActivity::class.java))
                        finishAffinity()
                    }
                })
        }
    }


    private fun setTheme() {
        progressBar = ProgressBar(this)
        progressBar.setPadding(0,30,0, 0)
        viewLayout.addView(progressBar)
        when (Random.nextInt(1, 100)) {
            in 1..30 -> { // 30%
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.color_for_splash_screen1))
                Glide.with(this)
                    .load(R.drawable.splash_screen1)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(logoImageView)
                window.navigationBarColor = applicationContext.getColor(R.color.blue_700)
                window.statusBarColor = applicationContext.getColor(R.color.blue_700)
            }
            in 31..60 -> { // 30%
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.color_for_splash_screen2))
                Glide.with(this)
                    .load(R.drawable.splash_screen2)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(logoImageView)
                window.navigationBarColor = Color.parseColor("#000080")
                window.statusBarColor = Color.parseColor("#000080")
            }
            in 61..90 -> { // 30%
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.color_for_splash_screen3))
                Glide.with(this)
                    .load(R.drawable.splash_screen3)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .into(logoImageView)
                window.navigationBarColor = applicationContext.getColor(R.color.orange_700)
                window.statusBarColor = applicationContext.getColor(R.color.orange_700)
            }
            in 91..95 -> { // 5%
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.grey))
                //viewLayout.removeView(logoImageView)
                viewLayout.removeAllViews()
                val textView = TextView(this)
                textView.text = "Сам знаешь кто здесь должен быть"
                textView.textSize = 24F
                textView.gravity = Gravity.CENTER
                textView.layoutParams = viewLayout.layoutParams
                textView.setTextColor(ContextCompat.getColor(baseContext, R.color.dark_red))
                viewLayout.addView(textView)
                Snackbar.make(textView, "Сам знаешь кто здесь должен быть!!!", Snackbar.LENGTH_SHORT)
                    .setAction("И кто же?") { openMissedMan(this) }
                    .show()
                window.navigationBarColor = applicationContext.getColor(R.color.black)
                window.statusBarColor = applicationContext.getColor(R.color.black)
            }
            in 96..100 -> { // 5%
                mainLayout.setBackgroundColor(applicationContext.getColor(R.color.grey))
                //viewLayout.removeView(logoImageView)
                viewLayout.removeAllViews()
                window.navigationBarColor = applicationContext.getColor(R.color.black)
                window.statusBarColor = applicationContext.getColor(R.color.black)
                ShowToast.show(baseContext, "Ничего пока не придумал")
            }
        }
    }
}