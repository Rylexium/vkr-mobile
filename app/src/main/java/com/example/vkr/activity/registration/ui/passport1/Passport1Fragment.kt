package com.example.vkr.activity.registration.ui.passport1

import android.content.Context.MODE_PRIVATE
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.vkr.R
import com.example.vkr.activity.registration.RegistrationActivity
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.databinding.FragmentPassport1Binding
import com.example.vkr.utils.*
import com.example.vkr.utils.dialogs.SelectDateClass
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*
import java.util.function.Consumer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class Passport1Fragment : Fragment() {

    private var _binding: FragmentPassport1Binding? = null

    private val binding get() = _binding!!
    var listRes: MutableList<String> = ArrayList()

    companion object {
        val KEY_NAME = "name"
        val KEY_FAMILY = "family"
        val KEY_PATRONYMIC = "patronymic"
        val KEY_SEX = "sex"
        val KEY_DATE_OF_BIRTH = "date_of_birth"
        val KEY_NATIONALITY = "nationality"
        val KEY_NAME_NATIONALITY = "name_nationality"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPassport1Binding.inflate(inflater, container, false)
        initComponents()
        comebackAfterOnBackPressed()
        applyEvents()
        return binding.root
    }

    private fun downloadNationalitys(){
        lifecycleScope.launch {
            run tryToDownload@{
                repeat(1001) { if (isDownloadNationalitys()) return@tryToDownload }
            }

            if(_binding == null) return@launch

            Handler(Looper.getMainLooper()).post {
                binding.listboxNationality.adapter = MySpinnerAdapter(requireContext(), R.layout.spinner_item, listRes)

                if (activity?.getPreferences(MODE_PRIVATE)?.getString(KEY_NATIONALITY, null) != null)
                    binding.listboxNationality.setSelection(activity?.getPreferences(MODE_PRIVATE)?.getString(KEY_NATIONALITY, null)!!.toInt())
            }
        }
    }

    private suspend fun isDownloadNationalitys() : Boolean {
        return suspendCoroutine {
            if (listRes.size == 1) {
                AndroidNetworking.get("https://vkr1-app.herokuapp.com/nationality")
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                            ObjectMapper().readTree(response.toString())
                                .forEach { item -> listRes.add(item["name"].asText()) }
                            it.resume(true)
                        }

                        override fun onError(error: ANError) {
                            it.resume(false)
                        }
                    })
            } else it.resume(true)

        }
    }

    private fun comebackAfterOnBackPressed() {
        wrapper(KEY_NAME, binding.textboxNameReg::setText)
        wrapper(KEY_FAMILY, binding.textboxFamilyReg::setText)
        wrapper(KEY_PATRONYMIC, binding.textboxPatronymicReg::setText)
        wrapper(KEY_SEX) { str ->
            binding.radiobuttonSex.text = str
            binding.radiobuttonSex.isChecked = binding.radiobuttonSex.text.equals("Пол: Мужской")
        }
        wrapper(KEY_DATE_OF_BIRTH, binding.textboxDateOfBirth::setText)
        if (sharedPreferences.getString(KEY_NATIONALITY, null) != null) {
            binding.listboxNationality.setSelection(sharedPreferences.getString(KEY_NATIONALITY, null)!!.toInt())
        }
        if (binding.textboxFamilyReg.text?.length!! < 2) binding.textboxFamilyReg.setTextColor(Color.RED)
        else binding.textboxFamilyReg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        if (binding.textboxNameReg.text?.length!! < 2) binding.textboxNameReg.setTextColor(Color.RED)
        else binding.textboxNameReg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        if (binding.textboxPatronymicReg.text?.length!! < 2) binding.textboxPatronymicReg.setTextColor(Color.RED)
        else binding.textboxPatronymicReg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

        RegistrationActivity.next.isEnabled = true
        RegistrationActivity.previous.isEnabled = true
        RegistrationActivity.info.isChecked = true
        RegistrationActivity.info.title = "2/8"

    }

    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences.getString(key, null))
                .ifPresent(editText)
    }

    fun applyEvents() {
        binding.radiobuttonSex.setOnCheckedChangeListener{ _, _ ->
            if(binding.radiobuttonSex.isChecked)
                binding.radiobuttonSex.text = "Пол: Мужской"
            else
                binding.radiobuttonSex.text = "Пол: Женский"
            setVisibleNavigationBottomView(true)
            binding.passport1Layout.requestFocus()
        }
        binding.textboxDateOfBirth.setOnClickListener{
            SelectDateClass.showDatePickerDialogForBirthday(requireActivity() as AppCompatActivity?, binding.textboxDateOfBirth)
            setVisibleNavigationBottomView(true)
            binding.passport1Layout.requestFocus()
        }
        binding.passport1Layout.setOnClickListener{
            setVisibleNavigationBottomView(true)
            binding.passport1Layout.requestFocus()
        }

        binding.listboxNationality.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent : AdapterView<*>?, view : View?, position: Int, id : Long) {
                if (position > 0 && view != null) { //выбран какой
                    val text : TextView = view as TextView
                    text.setTextColor(Color.BLACK)
                }
            }
            override fun onNothingSelected(parent : AdapterView<*>?) { }
        }

        binding.textboxFamilyReg.setOnFocusChangeListener{ _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if(!isFocus && binding.textboxFamilyReg.text?.length!! < 2) binding.textboxFamilyReg.setTextColor(Color.RED)
            else binding.textboxFamilyReg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        binding.textboxNameReg.setOnFocusChangeListener{ _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if(!isFocus && binding.textboxNameReg.text?.length!! < 2) binding.textboxNameReg.setTextColor(Color.RED)
            else binding.textboxNameReg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        binding.textboxPatronymicReg.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxPatronymicReg.text?.length!! < 2) binding.textboxPatronymicReg.setTextColor(Color.RED)
            else binding.textboxPatronymicReg.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    override fun onStop() {
        saveLastState()
        super.onStop()
    }

    private fun saveLastState() {
        var selectedItem = binding.listboxNationality.selectedItem?.toString()
        if(binding.listboxNationality.selectedItem == null) selectedItem = ""

        sharedPreferences.edit()
                .putString(KEY_NAME, binding.textboxNameReg.text.toString())
                .putString(KEY_FAMILY, binding.textboxFamilyReg.text.toString())
                .putString(KEY_PATRONYMIC, binding.textboxPatronymicReg.text.toString())
                .putString(KEY_SEX, binding.radiobuttonSex.text.toString())
                .putString(KEY_DATE_OF_BIRTH, binding.textboxDateOfBirth.text.toString())
                .putString(KEY_NATIONALITY, binding.listboxNationality.selectedItemPosition.toString())
                .putString(KEY_NAME_NATIONALITY, selectedItem)
                .apply()
    }

    private fun setVisibleNavigationBottomView(status : Boolean){
        if(listRes.size == 1) return
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }

    private fun initComponents(){
        if(listRes.size == 0) listRes.add("Выберите гражданство")
        downloadNationalitys()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}