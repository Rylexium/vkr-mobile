package com.example.vkr.activity.registration.ui.passport2

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
import androidx.core.content.ContextCompat
import androidx.appcompat.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import com.example.vkr.R
import com.example.vkr.databinding.FragmentPassport2Binding
import com.example.vkr.utils.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import java.util.*
import java.util.function.Consumer
import kotlin.coroutines.suspendCoroutine


class Passport2Fragment : Fragment() {

    private var _binding: FragmentPassport2Binding? = null

    private val binding get() = _binding!!

    var sharedPreferences: SharedPreferences? = null

    val KEY_PASSPORT_SERIES = "passport_series"
    val KEY_DATE_ISSUING = "date_issuing"
    val KEY_PASSPORT_NUMBER = "passport_number"
    val KEY_CODE_UNIT = "code_unit"
    val KEY_IMAGE_PASSPORT2 = "imagePassport2"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassport2Binding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = requireActivity().getPreferences(MODE_PRIVATE)
        applyEvents()
        GlobalScope.launch { comebackAfterOnBackPressed() }
        return root
    }

    private suspend fun comebackAfterOnBackPressed() {
        return suspendCoroutine {
            Handler(Looper.getMainLooper()).post {
                wrapper(KEY_PASSPORT_SERIES, binding.textboxPassportSeries::setText)
                wrapper(KEY_DATE_ISSUING, binding.textboxDateIssuingOfPassport::setText)
                wrapper(KEY_PASSPORT_NUMBER, binding.textboxPassportNumber::setText)
                wrapper(KEY_CODE_UNIT, binding.textboxCodeUnit::setText)
            }
            val str : String? = sharedPreferences!!.getString(KEY_IMAGE_PASSPORT2, null)
            if (str != null){
                val bitmap = ConvertClass.convertStringToBitmap(str)
                Handler(Looper.getMainLooper()).post{ binding.imageViewPassport2.setImageBitmap(bitmap) }
            }
            Handler(Looper.getMainLooper()).post{
                if (binding.textboxPassportSeries.text?.length!! < 5) binding.textboxPassportSeries.setTextColor(Color.RED)
                else binding.textboxPassportSeries.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                if (binding.textboxPassportNumber.text?.length!! < 6) binding.textboxPassportNumber.setTextColor(Color.RED)
                else binding.textboxPassportNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))

                if (binding.textboxCodeUnit.text?.length!! < 7) binding.textboxCodeUnit.setTextColor(Color.RED)
                else binding.textboxCodeUnit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            }
        }
    }
    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences!!.getString(key, null))
                .ifPresent(editText)
    }

    private fun applyEvents() {
        binding.makePassport2Photo.setOnClickListener { SelectImageClass.showMenu(requireActivity(), this, false) }
        binding.textboxDateIssuingOfPassport.setOnClickListener {
            SelectDateClass.showDatePickerDialogForDateIssuing(activity as AppCompatActivity?, binding.textboxDateIssuingOfPassport)
        }
        binding.textboxPassportSeries.addTextChangedListener(CorrectText(binding.textboxPassportSeries, "## ##"))
        binding.textboxCodeUnit.addTextChangedListener(CorrectText(binding.textboxCodeUnit, "###-###"))

        binding.passport2Layout.setOnClickListener{ setVisibleNavigationBottomView(true) }
        binding.textboxPassportSeries.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxPassportSeries.text?.length!! < 5) binding.textboxPassportSeries.setTextColor(Color.RED)
            else binding.textboxPassportSeries.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        binding.textboxPassportNumber.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxPassportNumber.text?.length!! < 6) binding.textboxPassportNumber.setTextColor(Color.RED)
            else binding.textboxPassportNumber.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
        binding.textboxCodeUnit.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxCodeUnit.text?.length!! < 7) binding.textboxCodeUnit.setTextColor(Color.RED)
            else binding.textboxCodeUnit.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var bitmap: Bitmap? = null
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = BitmapFactory.decodeFile(SelectImageClass.currentPhotoPath)
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if(bitmap != null)
                binding.imageViewPassport2.setImageBitmap(bitmap)
        }
    }

    private fun saveLastState() {
        sharedPreferences!!.edit()
                .putString(KEY_PASSPORT_SERIES, binding.textboxPassportSeries.text.toString())
                .putString(KEY_DATE_ISSUING, binding.textboxDateIssuingOfPassport.text.toString())
                .putString(KEY_PASSPORT_NUMBER, binding.textboxPassportNumber.text.toString())
                .putString(KEY_CODE_UNIT, binding.textboxCodeUnit.text.toString())
                .putString(KEY_IMAGE_PASSPORT2, ConvertClass.convertBitmapToString(binding.imageViewPassport2.drawable?.toBitmap()))
                .apply()
    }

    private fun setVisibleNavigationBottomView(status : Boolean){
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }

    override fun onStop() {
        saveLastState()
        super.onStop()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}