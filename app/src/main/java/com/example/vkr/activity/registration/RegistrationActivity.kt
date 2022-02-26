package com.example.vkr.activity.registration

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.SharedPreferences
import android.os.Bundle
import android.support.design.internal.BottomNavigationItemView
import android.support.v4.app.Fragment
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.view.MotionEvent

import com.example.vkr.R
import com.example.vkr.activity.registration.ui.achievements.AchievFragment
import com.example.vkr.activity.registration.ui.education.EducationFragment
import com.example.vkr.activity.registration.ui.finish.FinishFragment
import com.example.vkr.activity.registration.ui.passport1.Passport1Fragment
import com.example.vkr.activity.registration.ui.passport2.Passport2Fragment
import com.example.vkr.activity.registration.ui.passport3.Passport3Fragment
import com.example.vkr.activity.registration.ui.privileges.PrivilegesFragment
import com.example.vkr.activity.registration.ui.registration.RegistrationFragment
import com.example.vkr.activity.registration.ui.snills.SnillsFragment
import com.example.vkr.databinding.RegistrationActivityBinding
import com.example.vkr.utils.HideKeyboardClass


class RegistrationActivity : AppCompatActivity() {

    private lateinit var binding: RegistrationActivityBinding

    private var fragments = listOf(RegistrationFragment(), Passport1Fragment(), Passport2Fragment(), Passport3Fragment(),
                                   SnillsFragment(), EducationFragment(), AchievFragment(), PrivilegesFragment(), FinishFragment())

    var sharedPreferences : SharedPreferences? = null
    private var step = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = RegistrationActivityBinding.inflate(layoutInflater)
        sharedPreferences = getPreferences(MODE_PRIVATE)
        replaceFragment(fragments[0])
        setTitleStep("${step + 1}/${fragments.size - 1}")
        applyEvents()
        setContentView(binding.root)
    }

    private fun applyEvents() {
        binding.bottomNavigation.setOnNavigationItemSelectedListener {
            when (it.itemId) {
                R.id.button_next -> {
                    if ((step + 1) < fragments.size - 1) {
                        step += 1
                        replaceFragment(fragments[step])
                    }
                    else if((step + 2) == fragments.size){
                        step += 1
                        setTitleStep("Финиш")
                        replaceFragment(fragments[step])
                        return@setOnNavigationItemSelectedListener false
                    }
                }
                R.id.button_previous -> {
                    if ((step - 1) >= 0) {
                        step -= 1
                        replaceFragment(fragments[step])
                    }
                }
            }
            if((step+1) < 9) setTitleStep("${step + 1}/${fragments.size - 1}")
            false
        }
    }

    private fun replaceFragment(fragment: Fragment){
        val transaction = supportFragmentManager.beginTransaction()
        transaction.replace(R.id.fragment_container, fragment)
        transaction.commit()
    }

    override fun dispatchTouchEvent(ev: MotionEvent?): Boolean {
        if(currentFocus != null){
            HideKeyboardClass.hideKeyboard(this)
            currentFocus!!.clearFocus()
        }
        return super.dispatchTouchEvent(ev)
    }

    @SuppressLint("RestrictedApi")
    private fun setTitleStep(text : CharSequence){
        binding.bottomNavigation.menu.getItem(1).isChecked = true
        binding.bottomNavigation.findViewById<BottomNavigationItemView>(R.id.step_info)
                .setTitle(text)
    }

    override fun onBackPressed() {
        AlertDialog.Builder(this)
                .setTitle("Внимание")
                .setMessage("Вы действительно хотите выйти? Все введённые данные будут утеряны")
                .setCancelable(false)
                .setPositiveButton("Да") {
                    dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                    sharedPreferences!!.edit().clear().apply()
                    super.onBackPressed()
                }
                .setNegativeButton("Нет") {
                    dialog: DialogInterface, _: Int ->
                    dialog.dismiss()
                }
                .show()
                .setCanceledOnTouchOutside(true)
    }

}