package com.example.vkr.activity.registration.ui.snills

import com.example.vkr.R

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import androidx.core.content.ContextCompat
import android.text.TextUtils
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.example.vkr.activity.registration.RegistrationActivity
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.databinding.FragmentSnillsBinding
import com.example.vkr.utils.ConvertClass
import com.example.vkr.utils.CorrectText
import com.example.vkr.utils.dialogs.SelectImageClass
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import java.util.regex.Pattern

class SnillsFragment : Fragment() {

    private var _binding: FragmentSnillsBinding? = null
    private val binding get() = _binding!!

    companion object {
        val KEY_SNILLS = "snills"
        val KEY_PHOTO_SNILLS = "photoSnills"

        fun isCorrectSnills(text: String): Boolean {
            return text.length == 14 && Pattern.matches("^\\d{3}-\\d{3}-\\d{3} \\d{2}$", text)
        }
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        RegistrationActivity.next.isEnabled = false
        RegistrationActivity.previous.isEnabled = false
        _binding = FragmentSnillsBinding.inflate(inflater, container, false)
        val root: View = binding.root
        applyEvents()
        lifecycleScope.launch { comebackAfterOnBackPressed() }
        return root
    }

    private fun applyEvents() {
        binding.makeSnillsPhoto.setOnClickListener {
            SelectImageClass.showMenu(activity!!, this, false)
        }
        binding.textboxSnills.addTextChangedListener(CorrectText(binding.textboxSnills, "###-###-### ##"))
        binding.textboxSnills.setOnFocusChangeListener { _, isFocus -> setVisibleNavigationBottomView(!isFocus)
            if (!isFocus && !isCorrectSnills(binding.textboxSnills.text.toString())) binding.textboxSnills.setTextColor(Color.RED)
            else binding.textboxSnills.setTextColor(ContextCompat.getColor(context!!, R.color.white)) }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            var bitmap : Bitmap? = null
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = BitmapFactory.decodeFile(
                    SelectImageClass.currentPhotoPath)
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if(bitmap != null)
                Glide.with(this)
                    .load(bitmap)
                    .format(DecodeFormat.PREFER_RGB_565)
                    .fitCenter()
                    .into(binding.imageViewSnills)
        }
    }

    private suspend fun comebackAfterOnBackPressed() {
        return coroutineScope {
            val restoredText1 = sharedPreferences.getString(KEY_SNILLS, null)
            if (!TextUtils.isEmpty(restoredText1))
                Handler(Looper.getMainLooper()).post { binding.textboxSnills.setText(restoredText1) }

            val restoredText2 = sharedPreferences.getString(KEY_PHOTO_SNILLS, null)
            if (!TextUtils.isEmpty(restoredText2)) {
                val bitmap = ConvertClass.convertStringToBitmap(restoredText2)
                Handler(Looper.getMainLooper()).post {
                    Glide.with(this@SnillsFragment)
                        .load(bitmap)
                        .format(DecodeFormat.PREFER_RGB_565)
                        .fitCenter()
                        .into(binding.imageViewSnills)
                }
            }
            else {
                val bitmap = ConvertClass.decodeSampledBitmapFromResource(resources, R.drawable.snills, 100, 100)
                Handler(Looper.getMainLooper()).post { binding.imageViewSnills.setImageBitmap(bitmap) }
            }

            Handler(Looper.getMainLooper()).post {
                binding.textboxSnills.setTextColor(if (!isCorrectSnills(binding.textboxSnills.text.toString())) Color.RED else ContextCompat.getColor(context!!, R.color.white))
            }

            Handler(Looper.getMainLooper()).post{
                RegistrationActivity.next.isEnabled = true
                RegistrationActivity.previous.isEnabled = true
                RegistrationActivity.info.isChecked = true
                RegistrationActivity.info.title = "5/8"
            }
        }
    }

    override fun onStop() {
        lifecycleScope.launch { saveLastState() }
        super.onStop()
    }

    private suspend fun saveLastState() {
        return coroutineScope {
            sharedPreferences.edit()
                    .putString(KEY_SNILLS, binding.textboxSnills.text.toString())
                    .putString(KEY_PHOTO_SNILLS, ConvertClass.convertBitmapToString(binding.imageViewSnills.drawable?.toBitmap()))
                    .apply()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setVisibleNavigationBottomView(status : Boolean){
        if(status) activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
        else activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
    }
}