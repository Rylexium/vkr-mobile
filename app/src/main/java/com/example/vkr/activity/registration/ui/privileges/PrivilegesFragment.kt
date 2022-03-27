package com.example.vkr.activity.registration.ui.privileges

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.material.bottomnavigation.BottomNavigationView
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.lifecycle.lifecycleScope
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.vkr.R
import com.example.vkr.activity.registration.RegistrationActivity
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.databinding.FragmentPrivilegesBinding
import com.example.vkr.utils.*
import com.example.vkr.utils.dialogs.SelectImageClass
import com.example.vkr.utils.dialogs.ShowToast
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import org.json.JSONArray
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class PrivilegesFragment : Fragment() {
    private var _binding: FragmentPrivilegesBinding? = null
    private val binding get() = _binding!!
    private var listPrivileges: MutableList<String> = ArrayList()
    private var bitmap: Bitmap? = null
    companion object {
        val KEY_SELECTED_PRIVILEGES = "selectedItemPositionPrivilege"
        val KEY_NAME_PRIVILEGES = "name_privileges"
        val KEY_PRIVILIGE = "privilege_bitmap"
    }

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        RegistrationActivity.next.isEnabled = false
        RegistrationActivity.previous.isEnabled = false
        _binding = FragmentPrivilegesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        downloadPrivileges()
        applyEvents()
        lifecycleScope.launch { comebackAfterOnBackPressed() }
        return root
    }

    fun applyEvents() {
        binding.buttonAddPrivileges.setOnClickListener {
            if(binding.layoutForImagesPrivileges.childCount < 1)
                SelectImageClass.showMenu(activity!!, this, false)
            else ShowToast.show(context, "Максимум 1 фотография")
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = BitmapFactory.decodeFile(
                    SelectImageClass.currentPhotoPath)
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }

            if (bitmap != null)
                EditLinearLayout.onAddField(bitmap, binding.layoutForImagesPrivileges, activity)
        }
    }

    private suspend fun comebackAfterOnBackPressed () {
        return coroutineScope {
            val restoredText: String? = activity!!.getPreferences(MODE_PRIVATE).getString(KEY_PRIVILIGE, null)
            if(!restoredText.isNullOrEmpty()){
                bitmap = ConvertClass.convertStringToBitmap(restoredText)
                Handler(Looper.getMainLooper()).post {
                    EditLinearLayout.onAddField(bitmap, binding.layoutForImagesPrivileges, activity)
                }
            }
            Handler(Looper.getMainLooper()).post{
                RegistrationActivity.next.isEnabled = true
                RegistrationActivity.previous.isEnabled = true
                RegistrationActivity.info.isChecked = true
                RegistrationActivity.info.title = "8/8"
            }
        }
    }

    @SuppressLint("CommitPrefEdits")
    private suspend fun saveLastState() {
        var selectedItem = binding.listboxPrivileges.selectedItem?.toString()
        if(binding.listboxPrivileges.selectedItem == null) selectedItem = ""

        sharedPreferences!!.edit()
            .putString(KEY_SELECTED_PRIVILEGES, binding.listboxPrivileges.selectedItemPosition.toString())
            .putString(KEY_NAME_PRIVILEGES, selectedItem)
            .apply()

        return coroutineScope {
            sharedPreferences!!.edit()
                .putString(KEY_PRIVILIGE, ConvertClass.convertBitmapToString(bitmap))
                .apply()
        }
    }

    private fun downloadPrivileges() {
        lifecycleScope.launch {
            run tryToDownload@{
                repeat(1001) { if (isDownloadPrivileges()) return@tryToDownload }
            }

            if(_binding == null) return@launch

            Handler(Looper.getMainLooper()).post {
                binding.listboxPrivileges.adapter = ArrayAdapter<Any?>(
                    context!!,
                    android.R.layout.simple_spinner_dropdown_item,
                    listPrivileges as List<Any?>
                )
                activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility =
                    View.VISIBLE
                val restoredText: String? = activity!!.getPreferences(MODE_PRIVATE)
                    .getString(KEY_SELECTED_PRIVILEGES, null)
                if (restoredText != null) {
                    binding.listboxPrivileges.setSelection(restoredText.toInt())
                }
            }
        }
    }

    private suspend fun isDownloadPrivileges() : Boolean {
        return suspendCoroutine {
            if (listPrivileges.size == 0) {
                AndroidNetworking.get("https://vkr1-app.herokuapp.com/privileges")
                    .setPriority(Priority.IMMEDIATE)
                    .build()
                    .getAsJSONArray(object : JSONArrayRequestListener {
                        override fun onResponse(response: JSONArray) {
                            ObjectMapper().readTree(response.toString()).forEach { item -> listPrivileges.add(item["name"].asText()) }
                            it.resume(true)
                        }

                        override fun onError(error: ANError) {
                            it.resume(false)
                        }
                    })
            } else it.resume(true)
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