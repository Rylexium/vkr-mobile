package com.example.vkr.activity.themes

import android.content.Intent
import android.content.SharedPreferences
import android.content.res.Resources.Theme
import android.os.Bundle
import android.util.TypedValue
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import androidx.appcompat.app.AppCompatActivity
import com.example.vkr.R
import com.example.vkr.splash_screen.SplashScreen
import com.example.vkr.utils.dialogs.ShowToast


class ChangeThemeActivity : AppCompatActivity() {

    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var colorPicker : Spinner
    private lateinit var viewColorSecondary : View
    private lateinit var viewColorPrimary : View
    private lateinit var viewColorPrimaryDark : View
    private lateinit var btnAccept : ImageView
    private var beginTheme : Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.change_theme_activity)
        initComponents()
        applyEvents()
        comebackAfterOnBackPressed()
        beginTheme = colorPicker.selectedItemPosition
    }

    override fun getTheme(): Theme? {
        val theme = super.getTheme()
        theme.applyStyle(Themes.getCustomTheme(), true)
        return theme
    }

    private fun saveLastState() {
        sharedPreferences.edit().putInt("position", colorPicker.selectedItemPosition).apply()
    }

    override fun onBackPressed() {
        if(beginTheme != colorPicker.selectedItemPosition)
            startActivity(Intent(this, SplashScreen::class.java))
        saveLastState()
        super.onBackPressed()
    }

    private fun comebackAfterOnBackPressed() {
        if(sharedPreferences.all["position"] != null)
            colorPicker.setSelection(sharedPreferences.all["position"] as Int)
    }

    private fun applyEvents() {
        colorPicker.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                acceptTheme()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun acceptTheme() {
        Themes.setColor(colorPicker.selectedItemPosition)
        val typedValue = TypedValue()

        theme!!.resolveAttribute(R.attr.colorSecondary, typedValue, true)
        viewColorSecondary.setBackgroundColor(typedValue.data)

        theme!!.resolveAttribute(R.attr.colorPrimary, typedValue, true)
        viewColorPrimary.setBackgroundColor(typedValue.data)

        theme!!.resolveAttribute(R.attr.colorPrimaryDark, typedValue, true)
        viewColorPrimaryDark.setBackgroundColor(typedValue.data)

        btnAccept.setColorFilter(typedValue.data)
    }

    private fun initComponents() {
        sharedPreferences = getPreferences(MODE_PRIVATE)
        viewColorSecondary = findViewById(R.id.color_colorSecondary)
        viewColorPrimary = findViewById(R.id.color_colorPrimary)
        viewColorPrimaryDark = findViewById(R.id.color_colorPrimaryDark)
        colorPicker = findViewById(R.id.color_picker)
        btnAccept = findViewById(R.id.accept)
    }
}