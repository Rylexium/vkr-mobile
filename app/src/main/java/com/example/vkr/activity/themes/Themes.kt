package com.example.vkr.activity.themes

import android.app.Activity
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.content.res.Resources
import com.example.vkr.R

class Themes {
    companion object {
        lateinit var sharedPreferences: SharedPreferences
        fun init(activity : Activity){
            sharedPreferences = activity.getPreferences(MODE_PRIVATE)
        }
        fun getCustomTheme(): Int {
            return if(sharedPreferences.all["color"] == null) R.style.Theme_VKR
            else when(sharedPreferences.all["color"] as Int) {
                0 -> R.style.Theme_VKR
                1 -> R.style.Theme_VKR_Red
                else -> R.style.Theme_VKR
            }
        }
        fun setColor(color : Int) {
            sharedPreferences.edit()
                .putInt("color", color)
                .apply()
        }
    }
}