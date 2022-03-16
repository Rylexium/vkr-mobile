package com.example.vkr.activity.registration.ui.achievements

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import com.example.vkr.R
import com.example.vkr.databinding.FragmentAchievBinding
import com.example.vkr.utils.ConvertClass
import com.example.vkr.utils.EditLinearLayout
import com.example.vkr.utils.SelectImageClass
import com.example.vkr.utils.ShowToast


class AchievFragment: Fragment() {
    private var _binding: FragmentAchievBinding? = null
    private val binding get() = _binding!!
    private var sharedPreferences : SharedPreferences? = null
    val KEY_ACHIEVEMENT = "achievement_bitmap"

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAchievBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)
        ApplyEvents()
        comebackAfterOnBackPressed()
        return root
    }
    private fun ApplyEvents() {
        binding.buttonAddAchievements.setOnClickListener {
            if(binding.layoutForImagesAchievements.childCount < 5)
                SelectImageClass.showMenu(activity!!, this, false)
            else ShowToast.show(context, "Максимум 5 фотографий")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        var bitmap: Bitmap? = null
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = data?.extras!!["data"] as Bitmap?
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if (bitmap != null) {
                EditLinearLayout.onAddField(bitmap, binding.layoutForImagesAchievements, activity)
            }
        }
    }

    fun comebackAfterOnBackPressed() {
        var restoredText = sharedPreferences!!.getString(KEY_ACHIEVEMENT + "0", null)
        var i = 1
        while (restoredText != null && restoredText != "" && i < 5) {
            EditLinearLayout.onAddField(ConvertClass.convertStringToBitmap(restoredText), binding.layoutForImagesAchievements, activity)
            restoredText = sharedPreferences!!.getString(KEY_ACHIEVEMENT + i, null)
            i++
        }
    }
    @SuppressLint("CommitPrefEdits")
    private fun saveLastState() {
        for(i in 0 until 5)
            sharedPreferences!!.edit().putString(KEY_ACHIEVEMENT + i, "").apply()
        for (i in 0 until binding.layoutForImagesAchievements.childCount) {
            val v: ImageView = binding.layoutForImagesAchievements.getChildAt(i).findViewById(R.id.image_edit)
            sharedPreferences!!.edit()
                    .putString(KEY_ACHIEVEMENT + i, ConvertClass.convertBitmapToString((v.drawable as BitmapDrawable).bitmap))
                    .apply()
        }
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