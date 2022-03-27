package com.example.vkr.activity.support

import android.animation.LayoutTransition
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.transition.AutoTransition
import android.transition.TransitionManager
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.vkr.R
import com.example.vkr.activity.authorization.AuthorizationActivity
import com.example.vkr.utils.HashPass
import com.example.vkr.utils.HideKeyboardClass
import com.example.vkr.utils.dialogs.ShowToast
import com.google.android.material.textfield.TextInputEditText
import org.json.JSONObject

class ChangePasswordActivity : AppCompatActivity() {

    var number1 : EditText? = null
    var number2 : EditText? = null
    var number3 : EditText? = null
    var number4 : EditText? = null

    var passwordLayout : LinearLayout? = null
    var mainLayout : LinearLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_password_activity)
        Log.e("login", intent.getStringExtra("login").toString())
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

    private fun applyEvents(){
        number1?.addTextChangedListener(object : TextWatcher{
            private var previousLength = 0
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length == 0) return
                sendCode()
                number2!!.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(number2, InputMethodManager.SHOW_IMPLICIT)
            }

        })
        number2?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length == 0) return
                sendCode()
                number3!!.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(number3, InputMethodManager.SHOW_IMPLICIT)
            }

        })
        number3?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length == 0) return
                sendCode()
                number4!!.requestFocus()
                val imm: InputMethodManager =
                    getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                imm.showSoftInput(number4, InputMethodManager.SHOW_IMPLICIT)
            }

        })
        number4?.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                if(p0?.length == 0) return
                sendCode()
            }

        })
    }

    private fun sendCode(){
        val code = number1?.text.toString()  + number2?.text.toString() + number3?.text.toString() + number4?.text.toString()
        if(code.length != 4) return
        TransitionManager.beginDelayedTransition(passwordLayout, AutoTransition())
        passwordLayout?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

        TransitionManager.beginDelayedTransition(mainLayout, AutoTransition())
        mainLayout?.layoutTransition?.enableTransitionType(LayoutTransition.CHANGING)

        AndroidNetworking.get("https://vkr1-app.herokuapp.com/support/confirm_code?login=" + intent.getStringExtra("login").toString() +
                "&code=" + code)
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
                    val rowView = inflater.inflate(R.layout.field_for_change_password, null)

                    rowView.findViewById<TextView>(R.id.button_send_new_password).setOnClickListener{
                        val pass1 = rowView.findViewById<TextInputEditText>(R.id.new_password1)
                        val pass2 = rowView.findViewById<TextInputEditText>(R.id.new_password2)
                        if(pass1.text.toString() != pass2.text.toString()){
                            ShowToast.show(this@ChangePasswordActivity, "Пароли не совпадают")
                            return@setOnClickListener
                        }
                        else if(pass1.text!!.equals("") || pass2.text!!.equals("")) return@setOnClickListener

                        AndroidNetworking.post("https://vkr1-app.herokuapp.com/support/change_password?login=${intent.getStringExtra("login")}" +
                                "&password=" + HashPass.sha256(pass1.text.toString() + HashPass.STATIC_SALT))
                            .setPriority(Priority.IMMEDIATE)
                            .build()
                            .getAsJSONObject(object : JSONObjectRequestListener {
                                override fun onResponse(response: JSONObject) {
                                    ShowToast.show(this@ChangePasswordActivity, "Пароль успешно изменён")
                                    startActivity(Intent(this@ChangePasswordActivity, AuthorizationActivity::class.java))
                                    finish()
                                }
                                override fun onError(anError: ANError?) {  }
                            })

                    }

                    passwordLayout?.addView(rowView)

                }

                override fun onError(anError: ANError?) {}
            })
    }

    private fun initComponents(){
        number1 = findViewById(R.id.code_number1)
        number2 = findViewById(R.id.code_number2)
        number3 = findViewById(R.id.code_number3)
        number4 = findViewById(R.id.code_number4)

        mainLayout = findViewById(R.id.change_password_main_layout)
        passwordLayout = findViewById(R.id.new_password_layout)
    }
}