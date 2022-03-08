package com.example.vkr.activity.registration.ui.passport1

import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.vkr.R
import com.example.vkr.databinding.FragmentPassport1Binding
import com.example.vkr.utils.*
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import org.json.JSONArray
import java.util.*
import java.util.concurrent.TimeUnit
import java.util.function.Consumer


class Passport1Fragment : Fragment() {

    private var _binding: FragmentPassport1Binding? = null

    private val binding get() = _binding!!

    @JvmField
    var sharedPreferences : SharedPreferences? = null

    var bitmap : Bitmap? = null

    val KEY_NAME = "name"
    val KEY_FAMILY = "family"
    val KEY_PATRONYMIC = "patronymic"
    val KEY_SEX = "sex"
    val KEY_DATE_OF_BIRTH = "date_of_birth"
    val KEY_NATIONALITY = "nationality"
    val KEY_NAME_NATIONALITY = "name_nationality"
    var listRes: MutableList<String> = ArrayList()

    var tryToConnect : Int = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentPassport1Binding.inflate(inflater, container, false)
        sharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        downloadNationalitys()
        comebackAfterOnBackPressed()
        ApplyEvents()
        return binding.root
    }

    private fun downloadNationalitys(){
        if (listRes.isEmpty()){
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
            listRes = ArrayList()
            listRes.add("Выберите гражданство")
            AndroidNetworking.get("https://vkr1-app.herokuapp.com/nationality")
                .setPriority(Priority.IMMEDIATE)
                .setOkHttpClient(OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .build())
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        Thread {
                            try {
                                val jsonNode = ObjectMapper().readTree(response.toString())
                                jsonNode.forEach {
                                    listRes.add(
                                        it["name"].toString().replace("\"", "")
                                    )
                                }
                                Handler(Looper.getMainLooper()).post {
                                    binding.listboxNationality.adapter =
                                        MySpinnerAdapter(context!!, R.layout.spinner_item, listRes)
                                    if (activity!!.getPreferences(MODE_PRIVATE)
                                            .getString(KEY_NATIONALITY, null) != null
                                    ) {
                                        binding.listboxNationality.setSelection(
                                            activity!!.getPreferences(
                                                MODE_PRIVATE
                                            ).getString(KEY_NATIONALITY, null)!!.toInt()
                                        )
                                    }
                                    activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility =
                                        View.VISIBLE
                                }
                            } catch (e: JsonProcessingException) {
                                e.printStackTrace()
                            }
                        }.start()
                    }

                    override fun onError(error: ANError) {
                        ShowToast.show(context, "При загрузке что-то пошло не так")
                        listRes.add("Россия")
                        listRes.add("Украина")
                        listRes.add("Белоруссия")
                        activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility =
                            View.VISIBLE
                        tryToConnect += 1
                        if (tryToConnect < 3)
                            downloadNationalitys()
                    }
                })
        }else {
            binding.listboxNationality.adapter = MySpinnerAdapter(context!!, R.layout.spinner_item, listRes)
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
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
        if (sharedPreferences?.getString(KEY_NATIONALITY, null) != null) {
            binding.listboxNationality.setSelection(sharedPreferences?.getString(KEY_NATIONALITY, null)!!.toInt())
        }
        if (binding.textboxFamilyReg.text.length < 2) binding.textboxFamilyReg.setTextColor(Color.RED)
        else binding.textboxFamilyReg.setTextColor(ContextCompat.getColor(context!!, R.color.white))

        if (binding.textboxNameReg.text.length < 2) binding.textboxNameReg.setTextColor(Color.RED)
        else binding.textboxNameReg.setTextColor(ContextCompat.getColor(context!!, R.color.white))

        if (binding.textboxPatronymicReg.text.length < 2) binding.textboxPatronymicReg.setTextColor(Color.RED)
        else binding.textboxPatronymicReg.setTextColor(ContextCompat.getColor(context!!, R.color.white))

    }

    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences!!.getString(key, null))
                .ifPresent(editText)
    }

    fun ApplyEvents(){
        binding.radiobuttonSex.setOnCheckedChangeListener{ _, _ ->
            if(binding.radiobuttonSex.isChecked)
                binding.radiobuttonSex.text = "Пол: Мужской"
            else
                binding.radiobuttonSex.text = "Пол: Женский"
            setVisibleNavigationBottomView(true)
            binding.passport1Layout.requestFocus()
        }
        binding.textboxDateOfBirth.setOnClickListener{
            SelectDateClass.showDatePickerDialogForBirthday(activity!! as AppCompatActivity?, binding.textboxDateOfBirth)
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
            if(!isFocus && binding.textboxFamilyReg.text.length < 2) binding.textboxFamilyReg.setTextColor(Color.RED)
            else binding.textboxFamilyReg.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        }
        binding.textboxNameReg.setOnFocusChangeListener{ _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if(!isFocus && binding.textboxNameReg.text.length < 2) binding.textboxNameReg.setTextColor(Color.RED)
            else binding.textboxNameReg.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        }
        binding.textboxPatronymicReg.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxPatronymicReg.text.length < 2) binding.textboxPatronymicReg.setTextColor(Color.RED)
            else binding.textboxPatronymicReg.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        }
    }


    override fun onStop() {
        saveLastState()
        super.onStop()
    }

    private fun saveLastState() {
        sharedPreferences!!.edit()
                .putString(KEY_NAME, binding.textboxNameReg.text.toString())
                .putString(KEY_FAMILY, binding.textboxFamilyReg.text.toString())
                .putString(KEY_PATRONYMIC, binding.textboxPatronymicReg.text.toString())
                .putString(KEY_SEX, binding.radiobuttonSex.text.toString())
                .putString(KEY_DATE_OF_BIRTH, binding.textboxDateOfBirth.text.toString())
                .putString(KEY_NATIONALITY, binding.listboxNationality.selectedItemPosition.toString())
                .putString(KEY_NAME_NATIONALITY, binding.listboxNationality.selectedItem?.toString())
                .apply()
    }

    private fun setVisibleNavigationBottomView(status : Boolean){
        if(listRes.isEmpty()) return
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}