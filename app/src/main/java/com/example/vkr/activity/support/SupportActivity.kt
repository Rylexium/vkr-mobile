package com.example.vkr.activity.support

import android.animation.LayoutTransition
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.appcompat.app.AppCompatActivity
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.widget.LinearLayout
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.vkr.R
import com.example.vkr.utils.HideKeyboardClass
import com.example.vkr.utils.OpenActivity
import com.example.vkr.utils.dialogs.ShowToast
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.android.material.textfield.TextInputEditText
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class SupportActivity : AppCompatActivity() {

    var supportWithPassword : LinearLayout? = null
    var supportWithLogin : LinearLayout? = null
    var textViewSupportWithPassword : TextView? = null
    var textViewSupportWithLogin : TextView? = null
    var mainLayout : LinearLayout? = null

    var login  : String? = null
    var phone  : String? = null
    var snills : String? = null

    var isPressedPassword = false
    var isPressedLogin = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.support_activity)
        window.navigationBarColor = applicationContext.getColor(R.color.orange_700)
        window.statusBarColor = applicationContext.getColor(R.color.orange_700)
        login = intent.getStringExtra("login")
        initComponents()
        applyEvents()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            HideKeyboardClass.hideKeyboard(this)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    fun applyEvents(){
        textViewSupportWithLogin?.setOnClickListener{
            TransitionManager.beginDelayedTransition(supportWithLogin, AutoTransition())
            supportWithLogin?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

            TransitionManager.beginDelayedTransition(mainLayout, AutoTransition())
            mainLayout?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

            isPressedLogin = !isPressedLogin
            if(isPressedLogin){
                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView = inflater.inflate(R.layout.field_for_support_login, null)

                rowView.findViewById<TextInputEditText>(R.id.textbox_support_phone).setText(phone)
                rowView.findViewById<TextInputEditText>(R.id.textbox_support_id).setText(snills)

                rowView.findViewById<TextInputEditText>(R.id.textbox_support_login).setOnTouchListener(object : OnTouchListener {
                    var gestureDetector = GestureDetector(
                        applicationContext,
                        object : GestureDetector.SimpleOnGestureListener() {
                            override fun onLongPress(motionEvent: MotionEvent) {
                                val loginStr = rowView.findViewById<TextInputEditText>(R.id.textbox_support_login).text.toString()
                                if(loginStr == "") return
                                val clipboard: ClipboardManager =
                                    this@SupportActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("", loginStr)
                                clipboard.setPrimaryClip(clip)
                                ShowToast.show(applicationContext, "?????????? ?????????????? ???????????????????? ?? ?????????? ????????????")
                            }

                            override fun onDoubleTap(e: MotionEvent?): Boolean {
                                val loginStr = rowView.findViewById<TextInputEditText>(R.id.textbox_support_login).text.toString()
                                if(loginStr == "") return super.onDoubleTap(e)
                                val clipboard: ClipboardManager =
                                    this@SupportActivity.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                val clip = ClipData.newPlainText("", loginStr)
                                clipboard.setPrimaryClip(clip)
                                ShowToast.show(applicationContext, "?????????? ?????????????? ???????????????????? ?? ?????????? ????????????")
                                return super.onDoubleTap(e)
                            }
                        })

                    override fun onTouch(view: View, motionEvent: MotionEvent): Boolean {
                        gestureDetector.onTouchEvent(motionEvent)
                        return false
                    }
                })

                rowView.findViewById<TextView>(R.id.button_help_with_login).setOnClickListener{
                    val phone = rowView.findViewById<TextView>(R.id.textbox_support_phone).text.toString()
                    val snills = rowView.findViewById<TextView>(R.id.textbox_support_id).text.toString()
                    val url = when {
                        phone.isNotEmpty() -> "https://vkr1-app.herokuapp.com/support/login?phone=$phone"
                        snills.isNotEmpty() -> "https://vkr1-app.herokuapp.com/support/login?id_abit=$snills"
                        else -> {
                            ShowToast.show(applicationContext, "???????? ???? ?????????? ???????? ??????????????")
                            return@setOnClickListener
                        }
                    }

                    lifecycleScope.launch {
                        val tmpLogin = getLogin(url)
                        Handler(Looper.getMainLooper()).post {
                            if (tmpLogin.toString() == "null") ShowToast.show(applicationContext, "???????????????????????? ?? ???????????? ?????????????? ???? ????????????????????")
                            else {
                                ShowToast.show(applicationContext, "?????? ?????????? \"$tmpLogin\"")
                                rowView.findViewById<TextInputEditText>(R.id.textbox_support_login)
                                    .setText(tmpLogin)
                            }
                        }
                    }
                }
                supportWithLogin?.addView(rowView)
            }
            else{
                textViewSupportWithLogin!!.isEnabled = false
                supportWithLogin?.getChildAt(1)?.visibility = View.GONE

                phone = supportWithLogin?.getChildAt(1)?.findViewById<TextInputEditText>(R.id.textbox_support_phone)?.text.toString()
                snills = supportWithLogin?.getChildAt(1)?.findViewById<TextInputEditText>(R.id.textbox_support_id)?.text.toString()

                Handler(Looper.getMainLooper()).postDelayed({
                    supportWithLogin?.removeViewAt(1)
                    textViewSupportWithLogin!!.isEnabled = true
                }, 250)
            }

        }

        textViewSupportWithPassword?.setOnClickListener{
            TransitionManager.beginDelayedTransition(supportWithPassword, AutoTransition())
            supportWithPassword?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

            TransitionManager.beginDelayedTransition(mainLayout, AutoTransition())
            mainLayout?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

            isPressedPassword = !isPressedPassword
            if(isPressedPassword) {
                val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                val rowView = inflater.inflate(R.layout.field_for_support_password, null)

                rowView.findViewById<TextInputEditText>(R.id.textbox_support_login).setText(login)
                rowView.findViewById<TextInputEditText>(R.id.textbox_support_phone).setText(phone)
                rowView.findViewById<TextInputEditText>(R.id.textbox_support_id).setText(snills)

                rowView.findViewById<TextView>(R.id.button_help_with_password).setOnClickListener{
                    val loginString = rowView.findViewById<TextInputEditText>(R.id.textbox_support_login).text.toString()
                    val phoneString = rowView.findViewById<TextInputEditText>(R.id.textbox_support_phone).text.toString()
                    val snillsString = rowView.findViewById<TextInputEditText>(R.id.textbox_support_id).text.toString()

                    val url = when {
                        loginString.isNotEmpty() -> "https://vkr1-app.herokuapp.com/support/password?login=$loginString"
                        phoneString.isNotEmpty() -> "https://vkr1-app.herokuapp.com/support/password?phone=$phoneString"
                        snillsString.isNotEmpty() -> "https://vkr1-app.herokuapp.com/support/password?id_abit=$snillsString"
                        else -> {
                            ShowToast.show(applicationContext, "???????? ???? ?????????? ???????? ??????????????")
                            return@setOnClickListener
                        }
                    }

                    lifecycleScope.launch {
                        val tmpLogin = sendCodeToEmail(url)
                        Handler(Looper.getMainLooper()).post {
                            if (tmpLogin == null) ShowToast.show(applicationContext, "???????????????????????? ?? ???????????? ?????????????? ???? ????????????????????")
                            else {
                                OpenActivity.openChangePassword(this@SupportActivity, tmpLogin)
                                ShowToast.show(applicationContext, "?????? ?????????????????????????? ?????? ???????????? ?????? ???? ??????????")
                            }
                        }
                    }
                }

                supportWithPassword?.addView(rowView)
            }
            else{
                textViewSupportWithPassword!!.isEnabled = false
                supportWithPassword?.getChildAt(1)?.visibility = View.GONE

                login = supportWithPassword?.getChildAt(1)?.findViewById<TextInputEditText>(R.id.textbox_support_login)?.text.toString()
                phone = supportWithPassword?.getChildAt(1)?.findViewById<TextInputEditText>(R.id.textbox_support_phone)?.text.toString()
                snills = supportWithPassword?.getChildAt(1)?.findViewById<TextInputEditText>(R.id.textbox_support_id)?.text.toString()
                Handler(Looper.getMainLooper()).postDelayed({
                    supportWithPassword?.removeViewAt(1)
                    textViewSupportWithPassword!!.isEnabled = true
                }, 250)
            }
        }
    }

    private suspend fun getLogin(url : String) : String? {
        return suspendCoroutine {
            AndroidNetworking.get(url)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        it.resume(ObjectMapper().readTree(response.toString())["login"].asText())
                    }

                    override fun onError(anError: ANError?) { it.resume(null) }
                })
        }
    }

    private suspend fun sendCodeToEmail(url : String) : String? {
        return suspendCoroutine {
            AndroidNetworking.get(url)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(object : JSONObjectRequestListener {
                    override fun onResponse(response: JSONObject) {
                        it.resume(ObjectMapper().readTree(response.toString())["login"].asText())
                    }

                    override fun onError(anError: ANError?) { it.resume(null) }
                })
        }
    }

    fun initComponents(){
        mainLayout = findViewById(R.id.main_support_layout)
        supportWithLogin = findViewById(R.id.support_with_login)
        supportWithPassword = findViewById(R.id.support_with_password)
        textViewSupportWithPassword = findViewById(R.id.textview_support_with_password)
        textViewSupportWithLogin = findViewById(R.id.textview_support_with_login)
    }
}