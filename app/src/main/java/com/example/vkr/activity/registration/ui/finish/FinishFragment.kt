package com.example.vkr.activity.registration.ui.finish

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.androidnetworking.AndroidNetworking
import com.androidnetworking.common.Priority
import com.androidnetworking.error.ANError
import com.androidnetworking.interfaces.JSONObjectRequestListener
import com.example.vkr.activity.registration.RegistrationActivity.Companion.sharedPreferences
import com.example.vkr.activity.registration.ui.achievements.AchievFragment
import com.example.vkr.activity.registration.ui.education.EducationFragment
import com.example.vkr.activity.registration.ui.passport1.Passport1Fragment
import com.example.vkr.activity.registration.ui.passport2.Passport2Fragment
import com.example.vkr.activity.registration.ui.passport3.Passport3Fragment
import com.example.vkr.activity.registration.ui.privileges.PrivilegesFragment
import com.example.vkr.activity.registration.ui.registration.RegistrationFragment
import com.example.vkr.activity.registration.ui.snills.SnillsFragment
import com.example.vkr.databinding.FragmentFinishBinding
import com.example.vkr.utils.HashPass
import com.example.vkr.utils.dialogs.ShowToast
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper

import org.json.JSONObject

class FinishFragment : Fragment() {
    private var _binding: FragmentFinishBinding? = null

    private val binding get() = _binding!!

    var messageAboutUnique : String? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFinishBinding.inflate(inflater, container, false)

        fillStep1()
        fillStep2()
        fillStep3()
        fillStep4()
        fillStep5()
        fillStep6()
        fillStep8()
        checkUnique()
        applyEvents()
        return binding.root
    }

    private fun applyEvents(){
        binding.postInfoReg.setOnClickListener{
            if(messageAboutUnique != null){
                ShowToast.show(context, messageAboutUnique)
                return@setOnClickListener
            }
            if(isValidRegistrationFragment() && isValidPassport1Fragment()
                && isValidPassport2Fragment()
                    && isValidSnillsFragment() && isValidEducationFragment()
            ){
                postAbit()
            }
        }
    }

    private fun fillStep1() {
        binding.finishLoginReg.text = sharedPreferences.all[RegistrationFragment.KEY_LOGIN].toString()
        binding.finishPhoneReg.text = sharedPreferences.all[RegistrationFragment.KEY_PHONE].toString()
        binding.finishEmailReg.text = sharedPreferences.all[RegistrationFragment.KEY_EMAIL].toString()
        when {
            sharedPreferences.all[RegistrationFragment.KEY_PASS] == "" -> binding.finishPassReg.text = "???????????? ????????"
            sharedPreferences.all[RegistrationFragment.KEY_PASS] == sharedPreferences.all[RegistrationFragment.KEY_PASS2] -> binding.finishPassReg.text = "??????????????????"
            else -> binding.finishPassReg.text = "???? ??????????????????"
        }
    }
    private fun fillStep2() {
        binding.finishFamilyReg.text = sharedPreferences.all[Passport1Fragment.KEY_FAMILY].toString()
        binding.finishNameReg.text = sharedPreferences.all[Passport1Fragment.KEY_NAME].toString()
        binding.finishPatronymicReg.text = sharedPreferences.all[Passport1Fragment.KEY_PATRONYMIC].toString()
        binding.finishDateOfBirthdayReg.text = sharedPreferences.all[Passport1Fragment.KEY_DATE_OF_BIRTH].toString()
        binding.finishNationalityReg.text = sharedPreferences.all[Passport1Fragment.KEY_NAME_NATIONALITY].toString()

        val sex = sharedPreferences.all[Passport1Fragment.KEY_SEX].toString().split(" ")
        if(sex.size == 1) binding.finishSexReg.text = "???? ??????????????"
        else binding.finishSexReg.text = sex[1]
    }
    private fun fillStep3() {
        binding.finishNumberPassportReg.text = sharedPreferences.all[Passport2Fragment.KEY_PASSPORT_NUMBER].toString()
        binding.finishSeriesPassportReg.text = sharedPreferences.all[Passport2Fragment.KEY_PASSPORT_SERIES].toString()
        binding.finishDateOfIssuePassportReg.text = sharedPreferences.all[Passport2Fragment.KEY_DATE_ISSUING].toString()
        binding.finishCodeUnitReg.text = sharedPreferences.all[Passport2Fragment.KEY_CODE_UNIT].toString()
    }

    private fun fillStep4() {
        binding.finishConstAddressReg.text = sharedPreferences.all[Passport3Fragment.KEY_POST_INDEX_REG].toString() + ", " +
                sharedPreferences.all[Passport3Fragment.KEY_SUBJECT_REG].toString() + ", " +
                sharedPreferences.all[Passport3Fragment.KEY_CITY_REG].toString() + ", " +
                sharedPreferences.all[Passport3Fragment.KEY_RESIDENCE_STREET_REG].toString()
        binding.finishActualAddressReg.text = sharedPreferences.all[Passport3Fragment.KEY_POST_INDEX_ACTUAL].toString() + ", " +
                sharedPreferences.all[Passport3Fragment.KEY_SUBJECT_ACTUAL].toString() + ", " +
                sharedPreferences.all[Passport3Fragment.KEY_CITY_ACTUAL].toString() + ", " +
                sharedPreferences.all[Passport3Fragment.KEY_RESIDENCE_STREET_ACTUAL].toString()
    }
    private fun fillStep5() {
        binding.finishSnillsReg.text = sharedPreferences.all[SnillsFragment.KEY_SNILLS].toString()
    }
    private fun fillStep6() {
        binding.finishTypeOfEducationReg.text = sharedPreferences.all[EducationFragment.KEY_NAME_TYPE_EDUCATION].toString()
        binding.finishNumberEducationReg.text = sharedPreferences.all[EducationFragment.KEY_ID_EDUCATION].toString()
        binding.finishDateOfIssueEducationReg.text = sharedPreferences.all[EducationFragment.KEY_DATE_OF_ISSUE_OF_EDUCATION].toString()
    }
    private fun fillStep8() {
        binding.finishPrivilegesReg.text = sharedPreferences.all[PrivilegesFragment.KEY_NAME_PRIVILEGES].toString()
    }


    private fun checkUnique(){
        val passport = binding.finishSeriesPassportReg.text.toString().replace(" ", "") + binding.finishNumberPassportReg.text

        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/check_unique" +
                "?id=" + binding.finishSnillsReg.text.toString().replace("-", "").replace(" ", "") +

                "&phone=" + binding.finishPhoneReg.text.toString().replace("(", "").replace(")", "")
                                                                  .replace(" ", "").replace("-", "") +

                "&email=" + binding.finishEmailReg.text + "&passport=" + passport +
                "&number_education=" + binding.finishNumberEducationReg.text + "&login=" + binding.finishLoginReg.text)
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener {
                override fun onResponse(response: JSONObject) {
                    val jsonNode : JsonNode = ObjectMapper().readTree(response.toString())
                    messageAboutUnique = when (jsonNode["error"].asText()){
                        "id" -> "?????????? ?????????? ?????? ????????????????????"
                        "phone" -> "?????????? ?????????? ???????????????? ?????? ????????????????????"
                        "email" -> "?????????? ?????????? ?????? ????????????????????"
                        "passport" -> "?????????? ?????????????? ?????? ????????????????????"
                        "number_education" -> "?????????? ?????????? ???? ?????????????????????? ?????? ????????????????????"
                        "login" -> "?????????? ?????????? ?????? ????????????????????"
                        else -> null
                    }
                }
                override fun onError(error: ANError) {
                    Log.e("", "chechUnique")
                }
            });
    }

    private fun postUser(){
        val jsonObject = JSONObject()
        jsonObject.put("login", binding.finishLoginReg.text)
            .put("password", HashPass.sha256(sharedPreferences.all["pass"].toString() + HashPass.STATIC_SALT))
            .put("salt1", "")
            .put("salt2", "")
            .put("id_abit", binding.finishSnillsReg.text.toString().replace("-", "").replace(" ", "").toLong())
            .put("is_entry", false)
        Log.e("", jsonObject.toString());
        AndroidNetworking.post("https://vkr1-app.herokuapp.com/users/add")
            .setPriority(Priority.IMMEDIATE)
            .addJSONObjectBody(jsonObject)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject) {
                    Log.e("", response.toString())
                }

                override fun onError(anError: ANError) {
                    Log.e("", "error post user")
                }
            })
    }

    private fun postAbit(){

        val jsonObject = JSONObject()

        var sex = true
        if(binding.finishSexReg.text == "??????????????") sex = false

        jsonObject
            .put("id",
                binding.finishSnillsReg.text.toString().replace("-", "").replace(" ", "").toLong()
            )
            .put("phone", binding.finishPhoneReg.text.toString().replace("(", "").replace(")", "")
                                                                      .replace(" ", "").replace("-", "").toLongOrNull())
            .put("email", binding.finishEmailReg.text)
            .put("family", binding.finishFamilyReg.text)
            .put("name", binding.finishNameReg.text)
            .put("patronymic", binding.finishPatronymicReg.text)
            .put("sex", sex)
            .put("id_nationality", sharedPreferences.all["nationality"].toString().toInt())

            .put("passport", (binding.finishSeriesPassportReg.text.toString().replace(" ", "")
                    + binding.finishNumberPassportReg.text).toLong())

            .put("departament_code", binding.finishCodeUnitReg.text.toString().replace("-", "").toInt())
            .put("const_address", binding.finishConstAddressReg.text)
            .put("actual_address", binding.finishActualAddressReg.text)
            .put("id_education", sharedPreferences.all[EducationFragment.KEY_TYPE_EDUCATION_POSITION].toString().toInt())
            .put("number_education", binding.finishNumberEducationReg.text.toString().toLongOrNull())
            .put("reg_number_education", sharedPreferences.all[EducationFragment.KEY_REGISTRATION_NUMBER]?.toString()?.toIntOrNull())
            .put("date_of_issing_passport", binding.finishDateOfIssuePassportReg.text)
            .put("date_of_issing_education", binding.finishDateOfIssueEducationReg.text)
            .put("date_of_birthday", binding.finishDateOfBirthdayReg.text)
            .put("id_privileges", sharedPreferences.all[PrivilegesFragment.KEY_SELECTED_PRIVILEGES].toString().toInt() + 1)
            .put("passport1", sharedPreferences.all[Passport2Fragment.KEY_IMAGE_PASSPORT2].toString())
            .put("passport2", sharedPreferences.all[Passport3Fragment.KEY_IMAGE_PASSPORT3].toString())
            .put("snills", sharedPreferences.all[SnillsFragment.KEY_PHOTO_SNILLS].toString())
            .put("education1", sharedPreferences.all[EducationFragment.KEY_EDUCATION_PICTURE1].toString())
            .put("education2", sharedPreferences.all[EducationFragment.KEY_EDUCATION_PICTURE2].toString())
            .put("achievement1", sharedPreferences.all[AchievFragment.KEY_ACHIEVEMENT + "0"].toString())
            .put("achievement2", sharedPreferences.all[AchievFragment.KEY_ACHIEVEMENT + "1"].toString())
            .put("achievement3", sharedPreferences.all[AchievFragment.KEY_ACHIEVEMENT + "2"].toString())
            .put("achievement4", sharedPreferences.all[AchievFragment.KEY_ACHIEVEMENT + "3"].toString())
            .put("achievement5", sharedPreferences.all[AchievFragment.KEY_ACHIEVEMENT + "4"].toString())
            .put("privileges", sharedPreferences.all[PrivilegesFragment.KEY_PRIVILIGE].toString())


        Log.e("", jsonObject.toString())
        AndroidNetworking.post("https://vkr1-app.herokuapp.com/abit/add")
            .setPriority(Priority.IMMEDIATE)
            .addJSONObjectBody(jsonObject)
            .build()
            .getAsJSONObject(object : JSONObjectRequestListener{
                override fun onResponse(response: JSONObject) {
                    Log.e("", response.toString())
                    postUser()
                }

                override fun onError(anError: ANError?) {
                    Log.e("", "error post abit")
                }
            })
    }



    private fun patternForEmpty(field:String) : Boolean {
        ShowToast.show(context, "???????? \"$field\" ???? ?????????? ???????? ????????????")
        return false
    }
    private fun patternForInvalidData(field : String) : Boolean {
        ShowToast.show(context, "?? ???????? \"$field\" ???????????????????????? ????????????")
        return false
    }

    private fun isValidRegistrationFragment() : Boolean {
        return when {
            sharedPreferences.all[RegistrationFragment.KEY_LOGIN] == "" ->  patternForEmpty("??????????")
            sharedPreferences.all[RegistrationFragment.KEY_EMAIL] == "" -> patternForEmpty("Email")
            sharedPreferences.all[RegistrationFragment.KEY_PHONE] == "" -> patternForEmpty("??????????????")
            sharedPreferences.all[RegistrationFragment.KEY_PASS] != sharedPreferences.all[RegistrationFragment.KEY_PASS2] -> {
                ShowToast.show(context, "???????????? ???? ??????????????????")
                false
            }
            sharedPreferences.all[RegistrationFragment.KEY_EMAIL]?.toString() != ""
                    && !RegistrationFragment.isCorrectEmail(sharedPreferences.all[RegistrationFragment.KEY_EMAIL].toString()) ->
                patternForInvalidData("??????????")
            sharedPreferences.all[RegistrationFragment.KEY_PHONE]?.toString() != ""
                    && !RegistrationFragment.isCorrectPhone(sharedPreferences.all[RegistrationFragment.KEY_PHONE].toString()) ->
                patternForInvalidData("?????????? ????????????????")
            else -> true
        }
    }
    private fun isValidPassport1Fragment() : Boolean {
        return when {
            sharedPreferences.all[Passport1Fragment.KEY_FAMILY] == "" -> patternForEmpty("??????????????")
            sharedPreferences.all[Passport1Fragment.KEY_FAMILY]?.toString()?.length!! < 2 -> patternForInvalidData("??????????????")
            sharedPreferences.all[Passport1Fragment.KEY_NAME] == "" -> patternForEmpty("??????")
            sharedPreferences.all[Passport1Fragment.KEY_NAME].toString().length < 2 -> patternForInvalidData("??????")

            sharedPreferences.all[Passport1Fragment.KEY_PATRONYMIC]?.toString() != ""
                    && sharedPreferences.all[Passport1Fragment.KEY_PATRONYMIC]?.toString()?.length!! < 2 -> patternForInvalidData("????????????????")

            sharedPreferences.all[Passport1Fragment.KEY_DATE_OF_BIRTH] == "" -> patternForEmpty("???????? ????????????????")
            sharedPreferences.all[Passport1Fragment.KEY_SEX] == "" -> patternForEmpty("??????")
            sharedPreferences.all[Passport1Fragment.KEY_NATIONALITY].toString() == "0" -> {
                ShowToast.show(context, "???? ???? ?????????????? ???????? ??????????????????????")
                false
            }
            else -> true
        }
    }
    private fun isValidPassport2Fragment() : Boolean {
        return when {
            sharedPreferences.all[Passport2Fragment.KEY_PASSPORT_SERIES] == "" -> patternForEmpty("?????????? ????????????????")
            sharedPreferences.all[Passport2Fragment.KEY_PASSPORT_SERIES].toString().length < 5 -> patternForInvalidData("?????????? ????????????????")
            sharedPreferences.all[Passport2Fragment.KEY_PASSPORT_NUMBER] == "" -> patternForEmpty("?????????? ????????????????")
            sharedPreferences.all[Passport2Fragment.KEY_PASSPORT_NUMBER].toString().length < 6 -> patternForInvalidData("?????????? ????????????????")
            sharedPreferences.all[Passport2Fragment.KEY_CODE_UNIT] == "" -> patternForEmpty("?????? ??????????????????????????")
            sharedPreferences.all[Passport2Fragment.KEY_CODE_UNIT].toString().length < 7 -> patternForInvalidData("?????? ??????????????????????????")
            sharedPreferences.all[Passport2Fragment.KEY_DATE_ISSUING] == "" -> {
                ShowToast.show(context, "???? ???? ?????????????? ???????? ???????????? ????????????????")
                false
            }
            else -> true
        }
    }
    private fun isValidPassport3Fragment() : Boolean {
        return when {
            sharedPreferences.all[Passport3Fragment.KEY_POST_INDEX_REG] == "" -> patternForEmpty("???????????????? ????????????")
            sharedPreferences.all[Passport3Fragment.KEY_SUBJECT_REG] == "" -> patternForEmpty("??????????????")
            sharedPreferences.all[Passport3Fragment.KEY_CITY_REG] == "" -> patternForEmpty("???????????????????? ??????????")
            sharedPreferences.all[Passport3Fragment.KEY_RESIDENCE_STREET_REG] == "" -> patternForEmpty("??????????")
            else -> true
        }

    }
    private fun isValidSnillsFragment() : Boolean {
        return when {
            sharedPreferences.all[SnillsFragment.KEY_SNILLS] == "" -> patternForEmpty("??????????")
            !SnillsFragment.isCorrectSnills(sharedPreferences.all[SnillsFragment.KEY_SNILLS].toString()) -> patternForInvalidData("??????????")
            else -> true
        }

    }
    private fun isValidEducationFragment() : Boolean {
        return when {
            sharedPreferences.all[EducationFragment.KEY_TYPE_EDUCATION_POSITION].toString() == "0" -> patternForEmpty("?????? ????????????????")
            sharedPreferences.all[EducationFragment.KEY_ID_EDUCATION] == "" -> patternForEmpty("?????????? ??????????????????")
            sharedPreferences.all[EducationFragment.KEY_DATE_OF_ISSUE_OF_EDUCATION] == "" -> patternForEmpty("???????? ????????????")
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}