package com.example.vkr.activity.registration.ui.passport3

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.vkr.R
import com.example.vkr.databinding.FragmentPassport3Binding
import com.example.vkr.utils.ConvertClass
import com.example.vkr.utils.SelectImageClass
import java.util.*
import java.util.function.Consumer


class Passport3Fragment : Fragment() {
    private var _binding: FragmentPassport3Binding? = null
    private val binding get() = _binding!!

    private var bitmap: Bitmap? = null
    var sharedPreferences: SharedPreferences? = null
    private var isYes = false

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

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPassport3Binding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        ApplyEvents()
        comebackAfterOnBackPressed()

        val listOfSubject = listOf(resources.getString(R.string.subject_of_russia))[0].split(";")
                .map { str -> str.replaceFirst(" ", "") }


        binding.textboxSubjectReg.setAdapter(ArrayAdapter<Any?>(context!!, R.layout.list_item, listOfSubject))
        binding.textboxSubjectActual.setAdapter(ArrayAdapter<Any?>(context!!, R.layout.list_item, listOfSubject))
        if(bitmap == null)
            binding.imageViewPassport3.setImageBitmap(ConvertClass.decodeSampledBitmapFromResource(resources, R.drawable.passport_registration, 100, 100))
        return root
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun ApplyEvents() {
        binding.makePassport3Photo.setOnClickListener { SelectImageClass.showMenu(activity!!,this, false) }

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


    private fun comebackAfterOnBackPressed() {
        wrapper(KEY_POST_INDEX_REG, binding.textboxPostIndexReg::setText)
        wrapper(KEY_SUBJECT_REG, binding.textboxSubjectReg::setText)
        wrapper(KEY_CITY_REG, binding.textboxCityReg::setText)
        wrapper(KEY_RESIDENCE_STREET_REG, binding.textboxResidenceStreetReg::setText)
        wrapper(KEY_POST_INDEX_ACTUAL, binding.textboxPostIndexActual::setText)
        wrapper(KEY_SUBJECT_ACTUAL, binding.textboxSubjectActual::setText)
        wrapper(KEY_CITY_ACTUAL, binding.textboxCityActual::setText)
        wrapper(KEY_RESIDENCE_STREET_ACTUAL, binding.textboxResidenceStreetActual::setText)
        val str : String? = sharedPreferences!!.getString(KEY_IMAGE_PASSPORT3, null)
        if (str != null) {
            bitmap = ConvertClass.convertStringToBitmap(str)
            binding.imageViewPassport3.setImageBitmap(bitmap)
        }
    }

    private fun wrapper(key: String, editText: Consumer<String>) {
        Optional.ofNullable(sharedPreferences?.getString(key, null))
                .ifPresent(editText)
    }

    private fun showYesNoDialog(motionEvent: MotionEvent): Boolean {
        //если не первый раз нажимаем на одну это же поля или уже сказали "Да"
        if (motionEvent.action != MotionEvent.ACTION_UP || isYes) return false
        val dialogView: View = LayoutInflater.from(context).inflate(R.layout.yes_no_dialog, null)
        val dialogBuilder: android.app.AlertDialog.Builder? = android.app.AlertDialog.Builder(context)
                .setView(dialogView)
        val alertDialog: android.app.AlertDialog? = dialogBuilder!!.show()
        dialogView.findViewById<View>(R.id.button_yes).setOnClickListener {
            binding.textboxSubjectActual.text = binding.textboxSubjectReg.text
            binding.textboxPostIndexActual.text = binding.textboxPostIndexReg.text
            binding.textboxCityActual.text = binding.textboxCityReg.text
            binding.textboxResidenceStreetActual.text = binding.textboxResidenceStreetReg.text
            alertDialog!!.dismiss()
        }
        dialogView.findViewById<View>(R.id.button_no).setOnClickListener { alertDialog!!.dismiss() }
        isYes = true
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        bitmap = null
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = data?.extras!!["data"] as Bitmap?
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if(bitmap != null) binding.imageViewPassport3.setImageBitmap(bitmap)
        }
    }

    private fun saveLastState() {
        sharedPreferences!!.edit().putString(KEY_POST_INDEX_REG, binding.textboxPostIndexReg.text.toString())
                .putString(KEY_SUBJECT_REG, binding.textboxSubjectReg.text.toString())
                .putString(KEY_CITY_REG, binding.textboxCityReg.text.toString())
                .putString(KEY_RESIDENCE_STREET_REG, binding.textboxResidenceStreetReg.text.toString())
                .putString(KEY_POST_INDEX_ACTUAL, binding.textboxPostIndexActual.text.toString())
                .putString(KEY_SUBJECT_ACTUAL, binding.textboxSubjectActual.text.toString())
                .putString(KEY_CITY_ACTUAL, binding.textboxCityActual.text.toString())
                .putString(KEY_RESIDENCE_STREET_ACTUAL, binding.textboxResidenceStreetActual.text.toString())
                .putString(KEY_IS_YES, isYes.toString())
                .putString(KEY_IMAGE_PASSPORT3, ConvertClass.convertBitmapToString(bitmap))
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