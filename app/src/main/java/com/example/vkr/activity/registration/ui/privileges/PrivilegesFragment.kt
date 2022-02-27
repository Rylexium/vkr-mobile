package com.example.vkr.activity.registration.ui.privileges

import android.annotation.SuppressLint
import android.app.Activity.RESULT_OK
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.os.Bundle
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
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import okhttp3.OkHttpClient
import org.json.JSONArray
import java.util.concurrent.TimeUnit


class PrivilegesFragment : Fragment() {
    private var _binding: FragmentPrivilegesBinding? = null
    private val binding get() = _binding!!
    var sharedPreferences: SharedPreferences? = null
    var listPrivileges: MutableList<String> = ArrayList()

    val KEY_SELECTED_PRIVILEGES = "selectedItemPositionPrivilege"
    val KEY_NAME_PRIVILEGES = "name_privileges"
    val KEY_PRIVILIGE = "privilege_bitmap"
    private var bitmap: Bitmap? = null

    var tryToConnect : Int = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPrivilegesBinding.inflate(inflater, container, false)
        val root: View = binding.root
        sharedPreferences = activity!!.getPreferences(MODE_PRIVATE)
        ApplyEvents()
        comebackAfterOnBackPressed()
        downloadPrivileges()
        return root
    }

    fun ApplyEvents() {
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
        sharedPreferences!!.edit()
            .putString(KEY_SELECTED_PRIVILEGES, binding.listboxPrivileges.selectedItemPosition.toString())
            .putString(KEY_NAME_PRIVILEGES, binding.listboxPrivileges.selectedItem?.toString())
            .apply()
    }


    private fun downloadPrivileges() {
        if(listPrivileges.isEmpty()) {
            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.GONE
            AndroidNetworking.get("https://vkr1-app.herokuapp.com/privileges")
                .setPriority(Priority.IMMEDIATE)
                .setOkHttpClient(OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build())
                .build()
                .getAsJSONArray(object : JSONArrayRequestListener {
                    override fun onResponse(response: JSONArray) {
                        try {
                            val jsonNode = ObjectMapper().readTree(response.toString())
                            jsonNode.forEach{ listPrivileges.add(it["name"].toString().replace("\"", ""))}

                            binding.listboxPrivileges.adapter = ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_dropdown_item, listPrivileges as List<Any?>)
                            activity?.findViewById<BottomNavigationView>(R.id.bottom_navigation)!!.visibility = View.VISIBLE
                            val restoredText : String? = activity!!.getPreferences(MODE_PRIVATE).getString(KEY_SELECTED_PRIVILEGES, null)
                            if (restoredText != null) {
                                binding.listboxPrivileges.setSelection(restoredText.toInt())
                            }
                        } catch (e: JsonProcessingException) {
                            e.printStackTrace()
                        }
                    }

                    override fun onError(error: ANError) {
                        tryToConnect += 1
                        if (tryToConnect < 3)
                            downloadPrivileges()
                    }
                })
        }
        else{
            binding.listboxPrivileges.adapter = ArrayAdapter<Any?>(context!!, android.R.layout.simple_spinner_dropdown_item, listPrivileges as List<Any?>)
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