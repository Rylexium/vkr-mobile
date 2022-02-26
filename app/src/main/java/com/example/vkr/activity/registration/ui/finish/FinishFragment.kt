package com.example.vkr.activity.registration.ui.finish

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.support.v4.app.Fragment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.example.vkr.activity.registration.ui.education.EducationFragment
import com.example.vkr.activity.registration.ui.passport1.Passport1Fragment
import com.example.vkr.activity.registration.ui.passport2.Passport2Fragment
import com.example.vkr.activity.registration.ui.passport3.Passport3Fragment
import com.example.vkr.activity.registration.ui.privileges.PrivilegesFragment
import com.example.vkr.activity.registration.ui.registration.RegistrationFragment
import com.example.vkr.activity.registration.ui.snills.SnillsFragment
import com.example.vkr.databinding.FragmentFinishBinding
import com.example.vkr.utils.ShowToast

class FinishFragment : Fragment() {
    private var _binding: FragmentFinishBinding? = null

    private val binding get() = _binding!!
    var sharedPreferences : SharedPreferences? = null

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {

        _binding = FragmentFinishBinding.inflate(inflater, container, false)
        sharedPreferences = activity!!.getPreferences(Context.MODE_PRIVATE)

        Log.e("", sharedPreferences?.all.toString());
        fillStep1()
        fillStep2()
        fillStep3()
        fillStep4()
        fillStep5()
        fillStep6()
        fillStep8()
        Log.e("isValidRegistrationFragment", isValidRegistrationFragment().toString())
        Log.e("isValidPassport1Fragment", isValidPassport1Fragment().toString())
        Log.e("isValidPassport2Fragment", isValidPassport2Fragment().toString())
        //Log.e("isValidPassport3Fragment", isValidPassport3Fragment().toString()) //коментирую, потому что нужно вводить русские буквы
        Log.e("isValidSnillsFragment", isValidSnillsFragment().toString())
        Log.e("isValidEducationFragment", isValidEducationFragment().toString())
        //привелегии не проверяю, потому что их может и не быть

        // проверка валидности всех данных с каждого фрагмента
        // затем сборка всех данных для каждого отношения





        return binding.root
    }

    private fun fillStep1() {
        binding.finishLoginReg.text = sharedPreferences?.all?.get(RegistrationFragment().KEY_LOGIN).toString()
        binding.finishPhoneReg.text = sharedPreferences?.all?.get(RegistrationFragment().KEY_PHONE).toString()
        binding.finishEmailReg.text = sharedPreferences?.all?.get(RegistrationFragment().KEY_EMAIL).toString()
        if(sharedPreferences?.all?.get(RegistrationFragment().KEY_PASS) == sharedPreferences?.all?.get(RegistrationFragment().KEY_PASS2))
            binding.finishPassReg.text = "Совпадают"
        else binding.finishPassReg.text = "Не совпадают"
    }
    private fun fillStep2() {
        binding.finishFamilyReg.text = sharedPreferences?.all?.get(Passport1Fragment().KEY_FAMILY).toString()
        binding.finishNameReg.text = sharedPreferences?.all?.get(Passport1Fragment().KEY_NAME).toString()
        binding.finishPatronymicReg.text = sharedPreferences?.all?.get(Passport1Fragment().KEY_PATRONYMIC).toString()
        binding.finishDateOfBirthdayReg.text = sharedPreferences?.all?.get(Passport1Fragment().KEY_DATE_OF_BIRTH).toString()
        binding.finishNationalityReg.text = sharedPreferences?.all?.get(Passport1Fragment().KEY_NAME_NATIONALITY).toString()

        val sex = sharedPreferences?.all?.get(Passport1Fragment().KEY_SEX).toString().split(" ")
        if(sex.size == 1) binding.finishSexReg.text = "Не указано"
        else binding.finishSexReg.text = sex[1]
    }
    private fun fillStep3() {
        binding.finishNumberPassportReg.text = sharedPreferences?.all?.get(Passport2Fragment().KEY_PASSPORT_NUMBER).toString()
        binding.finishSeriesPassportReg.text = sharedPreferences?.all?.get(Passport2Fragment().KEY_PASSPORT_SERIES).toString()
        binding.finishDateOfIssuePassportReg.text = sharedPreferences?.all?.get(Passport2Fragment().KEY_DATE_ISSUING).toString()
        binding.finishCodeUnitReg.text = sharedPreferences?.all?.get(Passport2Fragment().KEY_CODE_UNIT).toString()
    }
    private fun fillStep4() {
        binding.finishConstAddressReg.text = sharedPreferences?.all?.get(Passport3Fragment().KEY_POST_INDEX_REG).toString() + ", " +
                sharedPreferences?.all?.get(Passport3Fragment().KEY_SUBJECT_REG).toString() + ", " +
                sharedPreferences?.all?.get(Passport3Fragment().KEY_CITY_REG).toString() + ", " +
                sharedPreferences?.all?.get(Passport3Fragment().KEY_RESIDENCE_STREET_REG).toString()
        binding.finishActualAddressReg.text = sharedPreferences?.all?.get(Passport3Fragment().KEY_POST_INDEX_ACTUAL).toString() + ", " +
                sharedPreferences?.all?.get(Passport3Fragment().KEY_SUBJECT_ACTUAL).toString() + ", " +
                sharedPreferences?.all?.get(Passport3Fragment().KEY_CITY_ACTUAL).toString() + ", " +
                sharedPreferences?.all?.get(Passport3Fragment().KEY_RESIDENCE_STREET_ACTUAL).toString()
    }
    private fun fillStep5() {
        binding.finishSnillsReg.text = sharedPreferences?.all?.get(SnillsFragment().KEY_SNILLS).toString()
    }
    private fun fillStep6() {
        binding.finishTypeOfEducationReg.text = sharedPreferences?.all?.get(EducationFragment().KEY_NAME_TYPE_EDUCATION).toString()
        binding.finishNumberEducationReg.text = sharedPreferences?.all?.get(EducationFragment().KEY_ID_EDUCATION).toString()
        binding.finishDateOfIssueEducationReg.text = sharedPreferences?.all?.get(EducationFragment().KEY_DATE_OF_ISSUE_OF_EDUCATION).toString()
    }
    private fun fillStep8() {
        binding.finishPrivilegesReg.text = sharedPreferences?.all?.get(PrivilegesFragment().KEY_NAME_PRIVILEGES).toString()
    }





    private fun patternForEmpty(field:String) : Boolean{
        ShowToast.show(context, "Поле \"$field\" не может быть пустым")
        return false
    }
    private fun patternForInvalidData(field : String) : Boolean{
        ShowToast.show(context, "В поле \"$field\" некорректные данные")
        return false
    }

    private fun isValidRegistrationFragment() : Boolean {
        return when {
            sharedPreferences?.all?.get(RegistrationFragment().KEY_LOGIN) == "" ->  patternForEmpty("Логин")
            sharedPreferences?.all?.get(RegistrationFragment().KEY_EMAIL) == "" && sharedPreferences?.all?.get(RegistrationFragment().KEY_PHONE) == "" -> {
                ShowToast.show(context, "Поля \"email\" или \"номер\" телефона не могут быть пустым")
                false
            }
            sharedPreferences?.all?.get(RegistrationFragment().KEY_PASS) != sharedPreferences?.all?.get(RegistrationFragment().KEY_PASS2) -> {
                ShowToast.show(context, "Пароли не совпадают")
                false
            }
            sharedPreferences?.all?.get(RegistrationFragment().KEY_EMAIL)?.toString() != ""
                    && !RegistrationFragment().isCorrectEmail(sharedPreferences?.all?.get(RegistrationFragment().KEY_EMAIL)?.toString()) ->
                patternForInvalidData("Почта")
            sharedPreferences?.all?.get(RegistrationFragment().KEY_PHONE)?.toString() != ""
                    && !RegistrationFragment().isCorrectPhone(sharedPreferences?.all?.get(RegistrationFragment().KEY_PHONE)?.toString()) ->
                patternForInvalidData("Номер телефона")
            else -> true
        }
    }
    private fun isValidPassport1Fragment() : Boolean {
        return when {
            sharedPreferences?.all?.get(Passport1Fragment().KEY_FAMILY) == "" -> patternForEmpty("Фамилия")
            sharedPreferences?.all?.get(Passport1Fragment().KEY_FAMILY)?.toString()?.length!! < 2 -> patternForInvalidData("Фамилия")
            sharedPreferences?.all?.get(Passport1Fragment().KEY_NAME) == "" -> patternForEmpty("Имя")
            sharedPreferences?.all?.get(Passport1Fragment().KEY_NAME)?.toString()?.length!! < 2 -> patternForInvalidData("Имя")

            sharedPreferences?.all?.get(Passport1Fragment().KEY_PATRONYMIC)?.toString() != ""
                    && sharedPreferences?.all?.get(Passport1Fragment().KEY_PATRONYMIC)?.toString()?.length!! < 2 -> patternForInvalidData("Отчество")

            sharedPreferences?.all?.get(Passport1Fragment().KEY_DATE_OF_BIRTH) == "" -> patternForEmpty("Дата рождения")
            sharedPreferences?.all?.get(Passport1Fragment().KEY_SEX) == "" -> patternForEmpty("Пол")
            sharedPreferences?.all?.get(Passport1Fragment().KEY_NATIONALITY).toString() == "0" -> {
                ShowToast.show(context, "Вы не выбрали своё гражданство")
                false
            }
            else -> true
        }
    }
    private fun isValidPassport2Fragment() : Boolean {
        return when {
            sharedPreferences?.all?.get(Passport2Fragment().KEY_PASSPORT_SERIES) == "" -> patternForEmpty("Серия паспорта")
            sharedPreferences?.all?.get(Passport2Fragment().KEY_PASSPORT_SERIES).toString().length < 5 -> patternForInvalidData("Серия паспорта")
            sharedPreferences?.all?.get(Passport2Fragment().KEY_PASSPORT_NUMBER) == "" -> patternForEmpty("Номер паспорта")
            sharedPreferences?.all?.get(Passport2Fragment().KEY_PASSPORT_NUMBER).toString().length < 6 -> patternForInvalidData("Номер паспорта")
            sharedPreferences?.all?.get(Passport2Fragment().KEY_CODE_UNIT) == "" -> patternForEmpty("Код подразделения")
            sharedPreferences?.all?.get(Passport2Fragment().KEY_CODE_UNIT).toString().length < 7 -> patternForInvalidData("Код подразделения")
            sharedPreferences?.all?.get(Passport2Fragment().KEY_DATE_ISSUING) == "" -> {
                ShowToast.show(context, "Вы не выбрали дату выдачи паспорта")
                false
            }
            else -> true
        }
    }
    private fun isValidPassport3Fragment() : Boolean{
        return when {
            sharedPreferences?.all?.get(Passport3Fragment().KEY_POST_INDEX_REG) == "" -> patternForEmpty("Почтовый индекс")
            sharedPreferences?.all?.get(Passport3Fragment().KEY_SUBJECT_REG) == "" -> patternForEmpty("Субъект")
            sharedPreferences?.all?.get(Passport3Fragment().KEY_CITY_REG) == "" -> patternForEmpty("Населённый пункт")
            sharedPreferences?.all?.get(Passport3Fragment().KEY_RESIDENCE_STREET_REG) == "" -> patternForEmpty("Улица")
            else -> true
        }

    }
    private fun isValidSnillsFragment() : Boolean{
        return when {
            sharedPreferences?.all?.get(SnillsFragment().KEY_SNILLS) == "" -> patternForEmpty("СНИЛС")
            !SnillsFragment().isCorrectSnills(sharedPreferences?.all?.get(SnillsFragment().KEY_SNILLS).toString()) -> patternForInvalidData("СНИЛС")
            else -> true
        }

    }
    private fun isValidEducationFragment() : Boolean {
        return when {
            sharedPreferences?.all?.get(EducationFragment().KEY_TYPE_EDUCATION_POSITION).toString() == "0" -> patternForEmpty("Тип обучения")
            sharedPreferences?.all?.get(EducationFragment().KEY_ID_EDUCATION) == "" -> patternForEmpty("Номер документа")
            sharedPreferences?.all?.get(EducationFragment().KEY_DATE_OF_ISSUE_OF_EDUCATION) == "" -> patternForEmpty("Дата выдачи")
            else -> true
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}