package com.example.vkr.activity.registration.ui.passport2

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vkr.R
import com.example.vkr.databinding.FragmentPassport2Binding
import com.example.vkr.utils.*
import java.util.*
import java.util.function.Consumer


class Passport2Fragment : Fragment() {

    private var _binding: FragmentPassport2Binding? = null

    private val binding get() = _binding!!

    private var bitmap: Bitmap? = null
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
        sharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        ApplyEvents()
        comebackAfterOnBackPressed()
        return root
    }

    private fun comebackAfterOnBackPressed() {
        wrapper(KEY_PASSPORT_SERIES, binding.textboxPassportSeries::setText)
        wrapper(KEY_DATE_ISSUING, binding.textboxDateIssuingOfPassport::setText)
        wrapper(KEY_PASSPORT_NUMBER, binding.textboxPassportNumber::setText)
        wrapper(KEY_CODE_UNIT, binding.textboxCodeUnit::setText)
        val str : String? = sharedPreferences!!.getString(KEY_IMAGE_PASSPORT2, null)
        if (str != null){
            bitmap = ConvertClass.convertStringToBitmap(str)
            binding.imageViewPassport2.setImageBitmap(bitmap)
        }

        if (binding.textboxPassportSeries.text.length < 5) binding.textboxPassportSeries.setTextColor(Color.RED)
        else binding.textboxPassportSeries.setTextColor(ContextCompat.getColor(context!!, R.color.white))

        if (binding.textboxPassportNumber.text.length < 6) binding.textboxPassportNumber.setTextColor(Color.RED)
        else binding.textboxPassportNumber.setTextColor(ContextCompat.getColor(context!!, R.color.white))

        if (binding.textboxCodeUnit.text.length < 7) binding.textboxCodeUnit.setTextColor(Color.RED)
        else binding.textboxCodeUnit.setTextColor(ContextCompat.getColor(context!!, R.color.white))
    }
    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences!!.getString(key, null))
                .ifPresent(editText)
    }

    private fun ApplyEvents() {
        binding.makePassport2Photo.setOnClickListener { SelectImageClass.showMenu(activity!!, this, false) }
        binding.textboxDateIssuingOfPassport.setOnClickListener {
            SelectDateClass.showDatePickerDialogForDateIssuing(activity as AppCompatActivity?, binding.textboxDateIssuingOfPassport)
        }
        binding.textboxPassportSeries.addTextChangedListener(CorrectText(binding.textboxPassportSeries, "## ##"))
        binding.textboxCodeUnit.addTextChangedListener(CorrectText(binding.textboxCodeUnit, "###-###"))

        binding.passport2Layout.setOnClickListener{ setVisibleNavigationBottomView(true) }
        binding.textboxPassportSeries.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxPassportSeries.text.length < 5) binding.textboxPassportSeries.setTextColor(Color.RED)
            else binding.textboxPassportSeries.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        }
        binding.textboxPassportNumber.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxPassportNumber.text.length < 6) binding.textboxPassportNumber.setTextColor(Color.RED)
            else binding.textboxPassportNumber.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        }
        binding.textboxCodeUnit.setOnFocusChangeListener { _, isFocus ->
            setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && binding.textboxCodeUnit.text.length < 7) binding.textboxCodeUnit.setTextColor(Color.RED)
            else binding.textboxCodeUnit.setTextColor(ContextCompat.getColor(context!!, R.color.white))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bitmap = null
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = data?.extras!!["data"] as Bitmap?
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if(bitmap != null){
                binding.imageViewPassport2.setImageBitmap(bitmap)
            }
        }
    }

    private fun saveLastState() {
        sharedPreferences!!.edit()
                .putString(KEY_PASSPORT_SERIES, binding.textboxPassportSeries.text.toString())
                .putString(KEY_DATE_ISSUING, binding.textboxDateIssuingOfPassport.text.toString())
                .putString(KEY_PASSPORT_NUMBER, binding.textboxPassportNumber.text.toString())
                .putString(KEY_CODE_UNIT, binding.textboxCodeUnit.text.toString())
                .putString(KEY_IMAGE_PASSPORT2, ConvertClass.convertBitmapToString(bitmap))
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