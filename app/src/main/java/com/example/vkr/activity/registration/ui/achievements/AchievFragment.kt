package com.example.vkr.activity.registration.ui.achievements

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.core.graphics.drawable.toBitmap
import androidx.core.view.get
import androidx.lifecycle.lifecycleScope
import com.example.vkr.R
import com.example.vkr.activity.registration.RegistrationActivity
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.databinding.FragmentAchievBinding
import com.example.vkr.utils.ConvertClass
import com.example.vkr.utils.EditLinearLayout
import com.example.vkr.utils.dialogs.SelectImageClass
import com.example.vkr.utils.dialogs.ShowToast
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch


class AchievFragment: Fragment() {
    private var _binding: FragmentAchievBinding? = null
    private val binding get() = _binding!!

    companion object {
        val KEY_ACHIEVEMENT = "achievement_bitmap"
    }
    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievBinding.inflate(inflater, container, false)
        val root: View = binding.root
        applyEvents()
        RegistrationActivity.next.isEnabled = false
        RegistrationActivity.previous.isEnabled = false
        lifecycleScope.launch { comebackAfterOnBackPressed() }
        return root
    }
    private fun applyEvents() {
        binding.buttonAddAchievements.setOnClickListener {
            if(binding.layoutForImagesAchievements.childCount < 5)
                SelectImageClass.showMenu(activity!!, this, false)
            else ShowToast.show(context, "Максимум 5 фотографий")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        var bitmap: Bitmap? = null
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = BitmapFactory.decodeFile(
                    SelectImageClass.currentPhotoPath)
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if (bitmap != null)
                EditLinearLayout.onAddField(bitmap, binding.layoutForImagesAchievements, activity)
        }
    }

    private suspend fun comebackAfterOnBackPressed() {
        return coroutineScope {
            val restoredText1 = sharedPreferences.getString(KEY_ACHIEVEMENT + "0", null)
            if(restoredText1 != null && restoredText1 != ""){
                val bitmap = ConvertClass.convertStringToBitmap(restoredText1)
                Handler(Looper.getMainLooper()).post {
                    EditLinearLayout.onAddField(bitmap, binding.layoutForImagesAchievements, activity)
                }
            }

            val restoredText2 = sharedPreferences.getString(KEY_ACHIEVEMENT + "1", null)
            if(restoredText2 != null && restoredText2 != ""){
                val bitmap = ConvertClass.convertStringToBitmap(restoredText2)
                Handler(Looper.getMainLooper()).post {
                    EditLinearLayout.onAddField(bitmap, binding.layoutForImagesAchievements, activity)
                }
            }

            val restoredText3 = sharedPreferences.getString(KEY_ACHIEVEMENT + "2", null)
            if(restoredText3 != null && restoredText3 != ""){
                val bitmap = ConvertClass.convertStringToBitmap(restoredText3)
                Handler(Looper.getMainLooper()).post {
                    EditLinearLayout.onAddField(bitmap, binding.layoutForImagesAchievements, activity)
                }
            }

            val restoredText4 = sharedPreferences.getString(KEY_ACHIEVEMENT + "3", null)
            if(restoredText4 != null && restoredText4 != ""){
                val bitmap = ConvertClass.convertStringToBitmap(restoredText4)
                Handler(Looper.getMainLooper()).post {
                    EditLinearLayout.onAddField(bitmap, binding.layoutForImagesAchievements, activity)
                }
            }

            val restoredText5 = sharedPreferences.getString(KEY_ACHIEVEMENT + "4", null)
            if(restoredText5 != null && restoredText5 != ""){
                val bitmap = ConvertClass.convertStringToBitmap(restoredText5)
                Handler(Looper.getMainLooper()).post {
                    EditLinearLayout.onAddField(bitmap, binding.layoutForImagesAchievements, activity)
                }
            }
            Handler(Looper.getMainLooper()).post{
                RegistrationActivity.next.isEnabled = true
                RegistrationActivity.previous.isEnabled = true
                RegistrationActivity.info.isChecked = true
                RegistrationActivity.info.title = "7/8"
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private suspend fun saveLastState() {
        for(i in 0 until 5)
            sharedPreferences.edit().putString(KEY_ACHIEVEMENT + i, "").apply()

        return coroutineScope {
            if(binding.layoutForImagesAchievements.childCount == 0) return@coroutineScope
            for(i in 0 until binding.layoutForImagesAchievements.childCount)
                sharedPreferences.edit()
                    .putString(KEY_ACHIEVEMENT + i, ConvertClass.convertBitmapToString(binding.layoutForImagesAchievements[i]
                        .findViewById<ImageView>(R.id.image_edit).drawable.toBitmap())).apply()
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
}
