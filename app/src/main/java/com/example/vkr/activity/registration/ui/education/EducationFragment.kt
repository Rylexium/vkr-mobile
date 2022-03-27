package com.example.vkr.activity.registration.ui.education

import android.app.Activity
import android.content.Intent
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
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.vkr.R
import com.example.vkr.activity.registration.RegistrationActivity
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.databinding.FragmentEducationBinding
import com.example.vkr.utils.*
import com.example.vkr.utils.dialogs.SelectDateClass
import com.example.vkr.utils.dialogs.SelectImageClass
import com.example.vkr.utils.dialogs.ShowToast
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.coroutineScope
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
    private var bitmap1 : Bitmap? = null
    private var bitmap2 : Bitmap? = null
    var listRes: MutableList<String> = ArrayList()

    companion object {
        var PIC_CODE : Int = 0
        val KEY_TYPE_EDUCATION_POSITION = "selected_position"
        val KEY_NAME_TYPE_EDUCATION = "name_type_education"
        val KEY_ID_EDUCATION = "id_education"
        val KEY_DATE_OF_ISSUE_OF_EDUCATION = "date_of_issue_of_education"
        val KEY_REGISTRATION_NUMBER = "registration_number"
        val KEY_EDUCATION_PICTURE1 = "educationPicture1"
        val KEY_EDUCATION_PICTURE2 = "educationPicture2"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEducationBinding.inflate(inflater, container, false)
        RegistrationActivity.next.isEnabled = false
        RegistrationActivity.previous.isEnabled = false
        val root: View = binding.root
        initComponents()
        applyEvents()
        lifecycleScope.launch { comebackAfterOnBackPressed() }
        return root
    }

    private fun applyEvents() {
        val idEducationListener : TextWatcher = CorrectText(binding.textboxIdEducation, "###### #######")
        binding.makePhotosEducation.setOnClickListener {
            ShowToast.show(context, "Сфотографируйте или выберите ${ (PIC_CODE % 2) + 1 } страницу документа")
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
        return coroutineScope {
            val restoredText1 = sharedPreferences.getString(KEY_TYPE_EDUCATION_POSITION, null)
            Handler(Looper.getMainLooper()).post {
                binding.listboxDocumentsOfEducation.setSelection(restoredText1?.toInt() ?: 0)
                wrapper(KEY_ID_EDUCATION, binding.textboxIdEducation::setText)
                wrapper(KEY_DATE_OF_ISSUE_OF_EDUCATION, binding.textboxDateOfIssueOfEducation::setText)
                wrapper(KEY_REGISTRATION_NUMBER, binding.textboxRegistrationNumber::setText)
            }

            val restoredText2 = sharedPreferences.getString(KEY_EDUCATION_PICTURE1, null)
            if (restoredText2 != null) {
                bitmap1 = ConvertClass.convertStringToBitmap(restoredText2)
                Handler(Looper.getMainLooper()).post {
                    Glide.with(this@EducationFragment)
                        .load(bitmap1)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .fitCenter()
                        .into(binding.educationDocument1)
                }
            }
            val restoredText3 = sharedPreferences.getString(KEY_EDUCATION_PICTURE2, null)
            if (restoredText3 != null) {
                bitmap2 = ConvertClass.convertStringToBitmap(restoredText3)
                Handler(Looper.getMainLooper()).post {
                    Glide.with(this@EducationFragment)
                        .load(bitmap2)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .fitCenter()
                        .into(binding.educationDocument2)
                }
            }
            Handler(Looper.getMainLooper()).post{
                RegistrationActivity.next.isEnabled = true
                RegistrationActivity.previous.isEnabled = true
                RegistrationActivity.info.isChecked = true
                RegistrationActivity.info.title = "6/8"
            }
        }
    }
    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences.getString(key, null))
                .ifPresent(editText)
    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            var bitmap : Bitmap? = null
            if (PIC_CODE % 2 == 0) ShowToast.show(context, "Сфотографируйте или выберите 2 страницу документа")
            when (requestCode) {
                SelectImageClass.CAMERA -> {
                    if (PIC_CODE % 2 == 0) {
                        bitmap = BitmapFactory.decodeFile(SelectImageClass.currentPhotoPath)
                        SelectImageClass.showMenu(activity!!, this, false)
                    } else
                        bitmap = BitmapFactory.decodeFile(SelectImageClass.currentPhotoPath)
                }
                SelectImageClass.GALLERY -> {
                    if (PIC_CODE % 2 == 0) {
                        bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
                        SelectImageClass.showMenu(activity!!, false)
                    }
                    else
                        bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
                }
            }

            if (bitmap != null) {
                if (PIC_CODE % 2 == 0) {
                    Glide.with(this)
                        .load(bitmap)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .fitCenter()
                        .into(binding.educationDocument1)
                    bitmap1 = bitmap
                }
                else {
                    Glide.with(this)
                        .load(bitmap)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .fitCenter()
                        .into(binding.educationDocument2)
                    bitmap2 = bitmap
                }
            }
            PIC_CODE += 1
        }
    }

    private suspend fun saveLastState() {
        var selectedItem = binding.listboxDocumentsOfEducation.selectedItem?.toString()
        if(binding.listboxDocumentsOfEducation.selectedItem == null) selectedItem = ""

        sharedPreferences.edit()
            .putString(KEY_TYPE_EDUCATION_POSITION, binding.listboxDocumentsOfEducation.selectedItemPosition.toString())
            .putString(KEY_NAME_TYPE_EDUCATION, selectedItem)
            .putString(KEY_ID_EDUCATION, binding.textboxIdEducation.text.toString())
            .putString(KEY_DATE_OF_ISSUE_OF_EDUCATION, binding.textboxDateOfIssueOfEducation.text.toString())
            .putString(KEY_REGISTRATION_NUMBER, binding.textboxRegistrationNumber.text.toString())
            .apply()

        return coroutineScope {
            sharedPreferences.edit()
                .putString(KEY_EDUCATION_PICTURE1, ConvertClass.convertBitmapToString(bitmap1))
                .putString(KEY_EDUCATION_PICTURE2, ConvertClass.convertBitmapToString(bitmap2))
                .apply()
        }
    }
    override fun onStop() {
        lifecycleScope.launch { saveLastState() }
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
        lifecycleScope.launch {
            run tryToDownload@{
                repeat(1001) { if (isDownloadEducations()) return@tryToDownload }
            }

            if(_binding == null) return@launch

            Handler(Looper.getMainLooper()).post {
                binding.listboxDocumentsOfEducation.adapter =
                    MySpinnerAdapter(context!!, R.layout.spinner_item, listRes)

                val restoredText = sharedPreferences.getString(KEY_TYPE_EDUCATION_POSITION, null)

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