package com.example.vkr.activity.registration.ui.privileges

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.support.design.widget.BottomNavigationView
import android.support.v4.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONArrayRequestListener
import com.example.vkr.R
import com.example.vkr.databinding.FragmentPrivilegesBinding
import com.example.vkr.utils.*
import com.fasterxml.jackson.core.JsonProcessingException
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import okhttp3.OkHttpClient
import org.json.JSONArray
import java.util.concurrent.TimeUnit
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class PrivilegesFragment : Fragment() {
    private var _binding: FragmentPrivilegesBinding? = null
    private val binding get() = _binding!!
    var sharedPreferences: SharedPreferences? = null
    var listPrivileges: MutableList<String> = ArrayList()

    val KEY_SELECTED_PRIVILEGES = "selectedItemPositionPrivilege"
    val KEY_NAME_PRIVILEGES = "name_privileges"
    val KEY_PRIVILIGE = "privilege_bitmap"
    private var bitmap: Bitmap? = null


    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivilegesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        downloadPrivileges()
        applyEvents()
        comebackAfterOnBackPressed()
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
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == RESULT_OK) {
            when (requestCode) {
                SelectImageClass.CAMERA -> bitmap = data?.extras!!["data"] as Bitmap?
                SelectImageClass.GALLERY -> bitmap = ConvertClass.decodeUriToBitmap(context, data?.data)
            }
            if (bitmap != null) {
                EditLinearLayout.onAddField(bitmap, binding.layoutForImagesPrivileges, activity)
            }
        }
    }

    fun comebackAfterOnBackPressed () {
        val restoredText: String? = activity!!.getPreferences(MODE_PRIVATE).getString(KEY_PRIVILIGE + "0", null)
        if(!restoredText.isNullOrEmpty())
            EditLinearLayout.onAddField(ConvertClass.convertStringToBitmap(restoredText), binding.layoutForImagesPrivileges, activity)
    }

    @SuppressLint("CommitPrefEdits")
    private fun saveLastState() {
        for(i in 0 until 5)
            sharedPreferences!!.edit().putString(KEY_PRIVILIGE + i, "").apply()

        for (i in 0 until binding.layoutForImagesPrivileges.childCount) {
            val v: ImageView = binding.layoutForImagesPrivileges.getChildAt(i).findViewById(R.id.image_edit)
            sharedPreferences!!.edit()
                    .putString(KEY_PRIVILIGE + i, ConvertClass.convertBitmapToString((v.drawable as BitmapDrawable).bitmap)).apply()
        }

        var selectedItem = binding.listboxPrivileges.selectedItem?.toString()
        if(binding.listboxPrivileges.selectedItem == null) selectedItem = ""

        sharedPreferences!!.edit()
            .putString(KEY_SELECTED_PRIVILEGES, binding.listboxPrivileges.selectedItemPosition.toString())
            .putString(KEY_NAME_PRIVILEGES, selectedItem)
            .apply()
    }


    private fun downloadPrivileges() {
        GlobalScope.launch {
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
        saveLastState()
        super.onStop()
    }


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

}