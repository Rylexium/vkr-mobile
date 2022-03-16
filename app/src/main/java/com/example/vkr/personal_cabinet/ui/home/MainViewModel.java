package com.example.vkr.personal_cabinet.ui.home;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import androidx.annotation.NonNull;

public class MainViewModel extends ViewModel {
    private MutableLiveData<String> login;
    private MutableLiveData<String> sex;
    private MutableLiveData<String> snills;
    private MutableLiveData<String> nationality;
    private MutableLiveData<String> passport;
    private MutableLiveData<String> departament_code;
    private MutableLiveData<String> date_of_issing_passport;
    private MutableLiveData<String> const_address;
    private MutableLiveData<String> actual_address;
    private MutableLiveData<String> id_education;
    private MutableLiveData<String> number_education;
    private MutableLiveData<String> reg_number_education;
    private MutableLiveData<String> date_of_issing_education;
    private MutableLiveData<String> date_of_birthday;
    private MutableLiveData<String> privilege;

    public MainViewModel() {
        login = new MutableLiveData<>();
        sex = new MutableLiveData<>();
        snills = new MutableLiveData<>();
        nationality = new MutableLiveData<>();
        passport = new MutableLiveData<>();
        departament_code = new MutableLiveData<>();
        date_of_issing_passport = new MutableLiveData<>();
        const_address = new MutableLiveData<>();
        actual_address = new MutableLiveData<>();
        id_education = new MutableLiveData<>();
        number_education = new MutableLiveData<>();
        reg_number_education = new MutableLiveData<>();
        date_of_issing_education = new MutableLiveData<>();
        date_of_birthday = new MutableLiveData<>();
        privilege = new MutableLiveData<>();
    }


    public LiveData<String> getTextLogin() { return login; }
    public LiveData<String> getTextSex() { return sex; }
    public LiveData<String> getTextSnills() { return snills; }
    public LiveData<String> getTextNationality() { return nationality; }
    public LiveData<String> getTextPassport() { return passport; }
    public LiveData<String> getDepartamentCode() { return departament_code; };
    public LiveData<String> getDateOfIssingPassport() { return date_of_issing_passport; };
    public LiveData<String> getConstAddress() { return const_address; };
    public LiveData<String> getActualAddress() {return actual_address; };
    public LiveData<String> getIdEducation() { return id_education; };
    public LiveData<String> getNumberEducation() { return number_education; };
    public LiveData<String> getRegNumberEducation() { return reg_number_education; };
    public LiveData<String> getDateOfIssingEducation() { return date_of_issing_education; };
    public LiveData<String> getDateOfBirthday() { return date_of_birthday; };
    public LiveData<String> getPrivilege() { return privilege; }

    public void postTextLogin(String text) { login.postValue(text); }
    public void postTextSex(String text) { sex.postValue(text);}
    public void postTextSnills(String text) {
        if(text.length() < 11)
            snills.postValue(text);
        else{
            StringBuilder textBuilder = new StringBuilder(text);
            textBuilder.insert(3, '-');
            textBuilder.insert(7, '-');
            textBuilder.insert(11, ' ');
            snills.postValue(textBuilder.toString());
        }
    }
    public void postTextNationality(String text) { nationality.postValue(text); }
    public void postTextPassport(String text) {
        if(text.equals("-")) passport.postValue(text);
        else {
            StringBuilder textBuilder = new StringBuilder(text);
            textBuilder.insert(2, ' ');
            textBuilder.insert(5, ' ');
            passport.postValue(textBuilder.toString());
        }
    }
    public void postDepartamentCode(String text) {
        if(text.equals("-")) departament_code.postValue(text);
        else {
            StringBuilder textBuilder = new StringBuilder(text);
            textBuilder.insert(3, '-');
            departament_code.postValue(textBuilder.toString());
        }
    }
    public void postDateOfIssingPassport(String text) { date_of_issing_passport.postValue(doCorrectDate(text));  }
    public void postConstAddress(String text) { const_address.postValue(text);  }
    public void postActualAddress(String text) { actual_address.postValue(text);  }
    public void postIdEducation(String text) {  id_education.postValue(text);  }
    public void postNumberEducation(String text) {  number_education.postValue(text);  }
    public void postRegNumberEducation(String text) {  reg_number_education.postValue(text.equals("0")? "-": text);  }
    public void postDateOfIssingEducation(String text) {  date_of_issing_education.postValue(doCorrectDate(text));  }
    public void postDateOfBirthday(String text) {  date_of_birthday.postValue(doCorrectDate(text));  }
    public void postPrivilege(String text) { privilege.postValue(text); }


    private String doCorrectDate(@NonNull String text){
        int index = text.indexOf('+');
        return index == -1 ? text : text.substring(0, index);
    }
}
