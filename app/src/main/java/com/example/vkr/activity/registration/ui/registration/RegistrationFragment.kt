package com.example.vkr.activity.registration.ui.registration

import android.graphics.Color
import android.os.Bundle
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vkr.R
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.databinding.RegistrationFragmentBinding
import com.example.vkr.utils.CorrectText
import com.example.vkr.utils.OpenActivity
import java.util.*
import java.util.function.Consumer
import java.util.regex.Pattern

class RegistrationFragment : Fragment() {

    private var _binding: RegistrationFragmentBinding? = null

    private val binding get() = _binding!!
    companion object {
        var isAgree : Boolean = false
        val KEY_PHONE = "phone"
        val KEY_EMAIL = "email"
        val KEY_LOGIN = "login"
        val KEY_PASS = "pass"
        val KEY_PASS2 = "pass2"
        fun isCorrectPhone(text: String?): Boolean {
            return text?.length == 18 && Pattern.matches("^((8|\\+7)[\\- ]?)?(\\(?\\d{3}\\)?[\\- ]?)?[\\d\\- ]{7,10}", text)
        }

        fun isCorrectEmail(text: String?): Boolean {
            return Pattern.matches("^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$", text!!)
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = RegistrationFragmentBinding.inflate(inflater, container, false)
        val root: View = binding.root
        applyEvents()
        comebackAfterOnBackPressed()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


    fun applyEvents(){
        binding.registrationLayout.setOnClickListener { activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE }
        binding.textboxLoginReg.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxPassReg.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus)
            println(binding.textboxPassReg)
        }
        binding.radioButtonIsAgree.setOnClickListener{
            isAgree = !isAgree
            binding.radioButtonIsAgree.isChecked = isAgree;
            setVisibleNavigationBottomView(true)
            binding.registrationLayout.requestFocus()
        }

        binding.textViewLinkToAgreement.setOnClickListener { OpenActivity.openLinkToAgreement(activity) }

        binding.textboxPhone.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && !isCorrectPhone(binding.textboxPhone.text.toString())) binding.textboxPhone.setTextColor(Color.RED)
            else binding.textboxPhone.setTextColor(ContextCompat.getColor(context!!, R.color.black_grey))
        }

        binding.textboxEmail.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && !isCorrectEmail(binding.textboxEmail.text.toString())) binding.textboxEmail.setTextColor(Color.RED)
            else binding.textboxEmail.setTextColor(ContextCompat.getColor(context!!, R.color.black_grey))
        }

        binding.textboxPass2Reg.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxPassReg.text.toString() != binding.textboxPass2Reg.text.toString()) binding.textboxPass2Reg.setTextColor(Color.RED)
            else binding.textboxPass2Reg.setTextColor(ContextCompat.getColor(context!!, R.color.black_grey))
        }
        binding.textboxPhone.addTextChangedListener(CorrectText(binding.textboxPhone, "+7 (###) ##-##-###"))
    }

    private fun setVisibleNavigationBottomView(status : Boolean){
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }

    private fun comebackAfterOnBackPressed() {
        wrapper(KEY_PHONE, binding.textboxPhone::setText)
        wrapper(KEY_EMAIL, binding.textboxEmail::setText)
        wrapper(KEY_LOGIN, binding.textboxLoginReg::setText)
        wrapper(KEY_PASS,  binding.textboxPassReg::setText)
        wrapper(KEY_PASS2, binding.textboxPass2Reg::setText)
        binding.radioButtonIsAgree.isChecked = isAgree

        binding.textboxPhone.setTextColor(if (!isCorrectPhone(binding.textboxPhone.text.toString())) Color.RED
                                          else ContextCompat.getColor(context!!, R.color.black_grey))

        binding.textboxEmail.setTextColor(if (!isCorrectEmail(binding.textboxEmail.text.toString())) Color.RED
                                          else ContextCompat.getColor(context!!, R.color.black_grey))

        binding.textboxPass2Reg.setTextColor(if (binding.textboxPass2Reg.text.toString() != binding.textboxPass2Reg.text.toString()) Color.RED
                                             else ContextCompat.getColor(context!!, R.color.black_grey))
    }

    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences.getString(key, null))
                .ifPresent(editText)
    }

    override fun onStop() {
        saveLastState()
        super.onStop()
    }

    private fun saveLastState() {
        sharedPreferences.edit()
                .putString(KEY_PHONE, binding.textboxPhone.text.toString()) //запоминаем введённые данные
                .putString(KEY_EMAIL, binding.textboxEmail.text.toString())
                .putString(KEY_LOGIN, binding.textboxLoginReg.text.toString())
                .putString(KEY_PASS,  binding.textboxPassReg.text.toString())
                .putString(KEY_PASS2, binding.textboxPass2Reg.text.toString())
                .apply()
    }

}