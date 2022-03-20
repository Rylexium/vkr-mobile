package com.example.vkr.activity.registration.ui.education

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.appcompat.app.AppCompatActivity
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.AdapterView.OnItemSelectedListener
import android.widget.TextView
import androidx.core.graphics.drawable.toBitmap
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.vkr.R
import com.example.vkr.databinding.FragmentEducationBinding
import com.example.vkr.utils.*
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import java.util.*
import java.util.function.Consumer
import kotlin.collections.ArrayList
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class EducationFragment: Fragment() {
    private var _binding: FragmentEducationBinding? = null
    private val binding get() = _binding!!
    var sharedPreferences : SharedPreferences? = null

    var listRes: MutableList<String> = ArrayList()

    companion object {
        @JvmStatic var PIC_CODE : Int = 1
    }

    val KEY_TYPE_EDUCATION_POSITION = "selected_position"
    val KEY_NAME_TYPE_EDUCATION = "name_type_education"
    val KEY_ID_EDUCATION = "id_education"
    val KEY_DATE_OF_ISSUE_OF_EDUCATION = "date_of_issue_of_education"
    val KEY_REGISTRATION_NUMBER = "registration_number"
    val KEY_EDUCATION_PICTURE1 = "educationPicture1"
    val KEY_EDUCATION_PICTURE2 = "educationPicture2"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        initComponents()
        applyEvents()
        GlobalScope.launch { comebackAfterOnBackPressed() }
        return root
    }

    private fun applyEvents() {
        val idEducationListener : TextWatcher = CorrectText(binding.textboxIdEducation, "###### #######")
        binding.makePhotosEducation.setOnClickListener {
            ShowToast.show(context, "Сфотографируйте или выберите $PIC_CODE страницу документа")
            SelectImageClass.showMenu(activity!!, this, false)
        }

        binding.listboxDocumentsOfEducation.onItemSelectedListener = object : OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                if (position > 0 && view != null) { //выбран какой
                    val text = view as TextView
                    binding.textboxRegistrationNumber.isEnabled = position != 1
                    if (position == 1) binding.textboxIdEducation.removeTextChangedListener(idEducationListener)
                    else binding.textboxIdEducation.addTextChangedListener(idEducationListener)
                    Optional.ofNullable(text).ifPresent { text.setTextColor(Color.BLACK) }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }

        binding.textboxIdEducation.setOnFocusChangeListener { _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxRegistrationNumber.setOnFocusChangeListener { _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.educationDocument1.setOnClickListener{ setVisibleNavigationBottomView(true) }
        binding.educationDocument2.setOnClickListener{ setVisibleNavigationBottomView(true) }
        binding.educationDocumentLayout.setOnClickListener{ setVisibleNavigationBottomView(true) }

        binding.textboxIdEducation.addTextChangedListener(idEducationListener)
        binding.textboxRegistrationNumber.addTextChangedListener(CorrectText(binding.textboxRegistrationNumber, "##-###-###"))
        binding.textboxDateOfIssueOfEducation.setOnClickListener {
            SelectDateClass.showDatePickerDialogForDateIssuing(activity as AppCompatActivity?, binding.textboxDateOfIssueOfEducation)
        }
    }

    private suspend fun comebackAfterOnBackPressed() {
        return suspendCoroutine {
            val restoredText1 = sharedPreferences!!.getString(KEY_TYPE_EDUCATION_POSITION, null)
            Handler(Looper.getMainLooper()).post{
                binding.listboxDocumentsOfEducation.setSelection(restoredText1?.toInt() ?: 0)
            }

            Handler(Looper.getMainLooper()).post{
                wrapper(KEY_ID_EDUCATION, binding.textboxIdEducation::setText)
                wrapper(KEY_DATE_OF_ISSUE_OF_EDUCATION, binding.textboxDateOfIssueOfEducation::setText)
                wrapper(KEY_REGISTRATION_NUMBER, binding.textboxRegistrationNumber::setText)
            }

            val restoredText2 = sharedPreferences!!.getString(KEY_EDUCATION_PICTURE1, null)
            if (restoredText2 != null) {
                val bitmap1 = ConvertClass.convertStringToBitmap(restoredText2)
                Handler(Looper.getMainLooper()).post{ binding.educationDocument1.setImageBitmap(bitmap1) }
            }
            val restoredText3 = sharedPreferences!!.getString(KEY_EDUCATION_PICTURE2, null)
            if (restoredText3 != null) {
                val bitmap2 = ConvertClass.convertStringToBitmap(restoredText3)
                Handler(Looper.getMainLooper()).post{ binding.educationDocument2.setImageBitmap(bitmap2) }
            }
        }
    }
    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences!!.getString(key, null))
                .ifPresent(editText)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            var bitmap : Bitmap? = null
            if (PIC_CODE == 1) ShowToast.show(context, "Сфотографируйте или выберите 2 страницу документа")
            when (requestCode) {
                SelectImageClass.CAMERA -> {
                    if (PIC_CODE == 1) {
                        bitmap = BitmapFactory.decodeFile(SelectImageClass.currentPhotoPath)
                        SelectImageClass.showMenu(activity!!, this, false)
                        PIC_CODE = 2
                    } else if (PIC_CODE == 2) {
                        bitmap = BitmapFactory.decodeFile(SelectImageClass.currentPhotoPath)
                        PIC_CODE = 1
                    }
                }
                SelectImageClass.GALLERY -> {
                    if (PIC_CODE == 1) {
                        bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
                        SelectImageClass.showMenu(activity!!, false)
                        PIC_CODE = 2
                    } else if (PIC_CODE == 2) {
                        bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
                        PIC_CODE = 1
                    }
                }
            }

            if (bitmap != null && PIC_CODE == 2)
                binding.educationDocument1.setImageBitmap(bitmap)

            if (bitmap != null && PIC_CODE == 1)
                binding.educationDocument2.setImageBitmap(bitmap)
        }
    }
    private fun saveLastState() {
        var selectedItem = binding.listboxDocumentsOfEducation.selectedItem?.toString()
        if(binding.listboxDocumentsOfEducation.selectedItem == null) selectedItem = ""

        sharedPreferences!!.edit()
                .putString(KEY_TYPE_EDUCATION_POSITION, binding.listboxDocumentsOfEducation.selectedItemPosition.toString())
                .putString(KEY_NAME_TYPE_EDUCATION, selectedItem)
                .putString(KEY_ID_EDUCATION, binding.textboxIdEducation.text.toString())
                .putString(KEY_DATE_OF_ISSUE_OF_EDUCATION, binding.textboxDateOfIssueOfEducation.text.toString())
                .putString(KEY_REGISTRATION_NUMBER, binding.textboxRegistrationNumber.text.toString())
                .putString(KEY_EDUCATION_PICTURE1, ConvertClass.convertBitmapToString(binding.educationDocument1.drawable?.toBitmap()))
                .putString(KEY_EDUCATION_PICTURE2, ConvertClass.convertBitmapToString(binding.educationDocument2.drawable?.toBitmap()))
                .apply()
    }
    override fun onStop() {
        saveLastState()
        super.onStop()
    }
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setVisibleNavigationBottomView(status : Boolean){
        if(listRes.size == 1) return
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }

    private fun initComponents(){
        if(listRes.size == 0) listRes.add("Выберите образование")
        downloadEducations()
    }

    private fun downloadEducations() {
        GlobalScope.launch {
            run tryToDownload@{
                repeat(1001) { if (isDownloadEducations()) return@tryToDownload }
            }

            if(_binding == null) return@launch

            Handler(Looper.getMainLooper()).post {
                binding.listboxDocumentsOfEducation.adapter =
                    MySpinnerAdapter(context!!, R.layout.spinner_item, listRes)

                val restoredText = sharedPreferences!!.getString(KEY_TYPE_EDUCATION_POSITION, null)

                if (restoredText != null) binding.listboxDocumentsOfEducation.setSelection(restoredText.toInt())
            }
        }
    }

    private suspend fun isDownloadEducations() : Boolean {
        return suspendCoroutine {
            if (listRes.size == 1) {
                AndroidNetworking.get("https://vkr1-app.herokuapp.com/education")
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
            } else  it.resume(true)

        }
    }
}