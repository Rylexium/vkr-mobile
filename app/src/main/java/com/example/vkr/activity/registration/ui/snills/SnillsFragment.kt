package com.example.vkr.activity.registration.ui.snills

import com.example.vkr.R

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.Color
import android.os.Bundle
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.support.v4.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vkr.databinding.FragmentSnillsBinding
import com.example.vkr.utils.ConvertClass
import com.example.vkr.utils.CorrectText
import com.example.vkr.utils.SelectImageClass
import java.util.regex.Pattern

class SnillsFragment : Fragment() {

    private var _binding: FragmentSnillsBinding? = null
    private val binding get() = _binding!!
    private var bitmap : Bitmap? = null
    var sharedPreferences : SharedPreferences? = null

    val KEY_SNILLS = "snills"
    val KEY_PHOTO_SNILLS = "photoSnills"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSnillsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        binding.imageViewSnills.setImageBitmap(ConvertClass.decodeSampledBitmapFromResource(resources, R.drawable.snills, 100, 100));
        ApplyEvents()
        comebackAfterOnBackPressed()
        return root
    }

    private fun ApplyEvents() {
        binding.makeSnillsPhoto.setOnClickListener {
            SelectImageClass.showMenu(activity!!, this, false)
        }
        binding.textboxSnills.addTextChangedListener(CorrectText(binding.textboxSnills, "###-###-### ##"))
        binding.textboxSnills.setOnFocusChangeListener { _, isFocus -> setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && !isCorrectSnills(binding.textboxSnills.text.toString())) binding.textboxSnills.setTextColor(Color.RED)
            else binding.textboxSnills.setTextColor(ContextCompat.getColor(context!!, R.color.white)) }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = data?.extras!!["data"] as Bitmap?
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if(bitmap != null)
                binding.imageViewSnills.setImageBitmap(bitmap)
        }
    }

    private fun comebackAfterOnBackPressed() {
        var restoredText = sharedPreferences!!.getString(KEY_SNILLS, null)
        if (!TextUtils.isEmpty(restoredText)) {
            binding.textboxSnills.setText(restoredText)
        }
        restoredText = sharedPreferences!!.getString(KEY_PHOTO_SNILLS, null)
        if (!TextUtils.isEmpty(restoredText)) {
            binding.imageViewSnills.setImageBitmap(ConvertClass.convertStringToBitmap(restoredText))
        }
        binding.textboxSnills.setTextColor(if (!isCorrectSnills(binding.textboxSnills.text.toString())) Color.RED
                                           else ContextCompat.getColor(context!!, R.color.white))
    }

    fun clearComponents() {
        if (sharedPreferences == null) return
        bitmap = null
        sharedPreferences!!.edit().clear().apply()
    }

    override fun onPause() {
        saveLastState()
        super.onPause()
    }

    fun isCorrectSnills(text: String): Boolean {
        return text.length == 14 && Pattern.matches("^\\d{3}-\\d{3}-\\d{3} \\d{2}$", text)
    }

    private fun saveLastState() {
        sharedPreferences!!.edit()
                .putString(KEY_SNILLS, binding.textboxSnills.text.toString())
                .putString(KEY_PHOTO_SNILLS, ConvertClass.convertBitmapToString(bitmap))
                .apply()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    fun setVisibleNavigationBottomView(status : Boolean){
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }
}