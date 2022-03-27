package com.example.vkr.activity.registration.ui.passport3

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.vkr.R
import com.example.vkr.activity.registration.RegistrationActivity
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.databinding.FragmentPassport3Binding
import com.example.vkr.utils.ConvertClass
import com.example.vkr.utils.dialogs.SelectImageClass
import com.example.vkr.utils.dialogs.ShowCustomDialog
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.function.Consumer


class Passport3Fragment : Fragment() {
    private var _binding: FragmentPassport3Binding? = null
    private val binding get() = _binding!!

    private var isYes = false

    companion object {
        val KEY_POST_INDEX_REG = "postIndexReg"
        val KEY_SUBJECT_REG = "subjectReg"
        val KEY_CITY_REG = "cityReg"
        val KEY_RESIDENCE_STREET_REG = "residenceStreetReg"

        val KEY_POST_INDEX_ACTUAL = "postIndexActual"
        val KEY_SUBJECT_ACTUAL = "subjectActual"
        val KEY_CITY_ACTUAL = "cityActual"
        val KEY_RESIDENCE_STREET_ACTUAL = "residenceStreetActual"
        val KEY_IS_YES = "isYes"
        val KEY_IMAGE_PASSPORT3 = "imagePassport3"
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        RegistrationActivity.next.isEnabled = false
        RegistrationActivity.previous.isEnabled = false
        _binding = FragmentPassport3Binding.inflate(inflater, container, false)
        val root: View = binding.root
        applyEvents()

        val listOfSubject = listOf(resources.getString(R.string.subject_of_russia))[0].split(";")
                .map { str -> str.replaceFirst(" ", "") }


        binding.textboxSubjectReg.setAdapter(ArrayAdapter<Any?>(requireContext(), R.layout.list_item, listOfSubject))
        binding.textboxSubjectActual.setAdapter(ArrayAdapter<Any?>(requireContext(), R.layout.list_item, listOfSubject))
        lifecycleScope.launch { comebackAfterOnBackPressed() }
        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun applyEvents() {
        binding.makePassport3Photo.setOnClickListener { SelectImageClass.showMenu(requireActivity(),this, false) }

        binding.textboxPostIndexReg.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxSubjectReg.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxCityReg.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxResidenceStreetReg.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxPostIndexActual.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxSubjectActual.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxCityActual.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }
        binding.textboxResidenceStreetActual.setOnFocusChangeListener{ _, isFocus -> setVisibleNavigationBottomView(!isFocus) }

        binding.passport3ScrollView.setOnClickListener{
            setVisibleNavigationBottomView(true)
            binding.passport3Layout.requestFocus()
        }
        binding.imageViewPassport3.setOnClickListener{
            setVisibleNavigationBottomView(true)
            binding.passport3Layout.requestFocus()
        }
        binding.textView1.setOnClickListener{
            setVisibleNavigationBottomView(true)
            binding.passport3Layout.requestFocus()
        }
        binding.textView2.setOnClickListener{
            setVisibleNavigationBottomView(true)
            binding.passport3Layout.requestFocus()
        }

        binding.textboxPostIndexActual.setOnTouchListener { _, motionEvent -> showYesNoDialog(motionEvent) }
        binding.textboxSubjectActual.setOnTouchListener { _, motionEvent -> showYesNoDialog(motionEvent) }
        binding.textboxCityActual.setOnTouchListener { _, motionEvent -> showYesNoDialog(motionEvent) }
        binding.textboxResidenceStreetActual.setOnTouchListener { _, motionEvent -> showYesNoDialog(motionEvent) }
    }


    private suspend fun comebackAfterOnBackPressed() {
        return coroutineScope {
            Handler(Looper.getMainLooper()).post {
                wrapper(KEY_POST_INDEX_REG, binding.textboxPostIndexReg::setText)
                wrapper(KEY_SUBJECT_REG, binding.textboxSubjectReg::setText)
                wrapper(KEY_CITY_REG, binding.textboxCityReg::setText)
                wrapper(KEY_RESIDENCE_STREET_REG, binding.textboxResidenceStreetReg::setText)
                wrapper(KEY_POST_INDEX_ACTUAL, binding.textboxPostIndexActual::setText)
                wrapper(KEY_SUBJECT_ACTUAL, binding.textboxSubjectActual::setText)
                wrapper(KEY_CITY_ACTUAL, binding.textboxCityActual::setText)
                wrapper(KEY_RESIDENCE_STREET_ACTUAL, binding.textboxResidenceStreetActual::setText)
            }
            val str: String? = sharedPreferences.getString(KEY_IMAGE_PASSPORT3, null)
            if (str != null && str != "") {
                val bitmap = ConvertClass.convertStringToBitmap(str)
                Handler(Looper.getMainLooper()).post {
                    Glide.with(this@Passport3Fragment)
                        .load(bitmap)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .fitCenter()
                        .into(binding.imageViewPassport3)
                }
            }
            else{
                val bitmap = ConvertClass.decodeSampledBitmapFromResource(resources, R.drawable.passport_registration, 100, 100)
                Handler(Looper.getMainLooper()).post { binding.imageViewPassport3.setImageBitmap(bitmap) }
            }

            Handler(Looper.getMainLooper()).post{
                RegistrationActivity.next.isEnabled = true
                RegistrationActivity.previous.isEnabled = true
                RegistrationActivity.info.isChecked = true
                RegistrationActivity.info.title = "4/8"
            }
        }
    }

    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences.getString(key, null))
                .ifPresent(editText)
    }

    private fun showYesNoDialog(motionEvent: MotionEvent): Boolean {
        //если не первый раз нажимаем на одну это же поля или уже сказали "Да"
        if (motionEvent.action != MotionEvent.ACTION_UP || isYes) return false
        ShowCustomDialog()
            .showDialog(this.activity, this.activity?.getDrawable(R.drawable.ic_baseline_room_24),
            "Место жительства", "Место прописки соответствует фактическому месту проживания?",
            "Да", "Нет")
            .setOnYes {
                binding.textboxSubjectActual.text = binding.textboxSubjectReg.text
                binding.textboxPostIndexActual.text = binding.textboxPostIndexReg.text
                binding.textboxCityActual.text = binding.textboxCityReg.text
                binding.textboxResidenceStreetActual.text = binding.textboxResidenceStreetReg.text
            }
        isYes = true
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var bitmap : Bitmap? = null
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = BitmapFactory.decodeFile(
                    SelectImageClass.currentPhotoPath)
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }

            if(bitmap != null) binding.imageViewPassport3.setImageBitmap(bitmap)
        }
    }

    private suspend fun saveLastState() {
        return coroutineScope {
            sharedPreferences.edit().putString(KEY_POST_INDEX_REG, binding.textboxPostIndexReg.text.toString())
                    .putString(KEY_SUBJECT_REG, binding.textboxSubjectReg.text.toString())
                    .putString(KEY_CITY_REG, binding.textboxCityReg.text.toString())
                    .putString(KEY_RESIDENCE_STREET_REG, binding.textboxResidenceStreetReg.text.toString())
                    .putString(KEY_POST_INDEX_ACTUAL, binding.textboxPostIndexActual.text.toString())
                    .putString(KEY_SUBJECT_ACTUAL, binding.textboxSubjectActual.text.toString())
                    .putString(KEY_CITY_ACTUAL, binding.textboxCityActual.text.toString())
                    .putString(KEY_RESIDENCE_STREET_ACTUAL, binding.textboxResidenceStreetActual.text.toString())
                    .putString(KEY_IS_YES, isYes.toString())
                    .putString(KEY_IMAGE_PASSPORT3, ConvertClass.convertBitmapToString(binding.imageViewPassport3.drawable.toBitmap()))
                    .apply()
        }
    }
    private fun setVisibleNavigationBottomView(status : Boolean){
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }

    override fun onStop() {
        lifecycleScope.launch { saveLastState() }
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}