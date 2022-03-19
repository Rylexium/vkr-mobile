package com.example.vkr.personal_cabinet;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.appcompat.app.AlertDialog;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Menu;

import com.example.vkr.utils.ShowCustomDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.activity.authorization.AuthorizationActivity;
import com.example.vkr.databinding.PersonalCabinetActivityBinding;
import com.example.vkr.personal_cabinet.ui.achievements.AchievementsFragment;
import com.example.vkr.personal_cabinet.ui.home.MainFragment;
import com.example.vkr.personal_cabinet.ui.result_egu.ResultEguFragment;
import com.example.vkr.personal_cabinet.ui.speciality.SpecialityFragment;
import com.example.vkr.personal_cabinet.ui.statement.StatementFragment;
import com.example.vkr.utils.OpenActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PersonalCabinetActivity extends AppCompatActivity {
    private NavigationView navigationView;
    private TextView fio;
    private TextView emailPhone;

    private AppBarConfiguration mAppBarConfiguration;
    private PersonalCabinetActivityBinding binding;

    public static String idAbit;
    public static String idEducation;

    public static List<Map<String, String>> specialitysAbit; //кэш
    public static Map<String, String> typeOfStudy;
    public static Map<String, String> instituts;

    public static FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = PersonalCabinetActivityBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.appBarPersonalCabinet.toolbar);
        binding.appBarPersonalCabinet.fab.setOnClickListener(view -> startActivity(new Intent(Intent.ACTION_VIEW)
                                                                                .setData(Uri.parse("https://vk.com/moais_samara"))));

        fab = binding.appBarPersonalCabinet.fab;
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home,
                R.id.nav_achievement,
                R.id.nav_agreement,
                R.id.nav_egu,
                R.id.nav_speciality,
                R.id.nav_statement,
                R.id.nav_exit) //перечислить layout
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_personal_cabinet);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);
        initComponents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.personal_cabinet, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact_with_developer:
                return OpenActivity.openPageDeveloper(this);
            case R.id.action_faq:
                return OpenActivity.openPageWithQuestion(this);
            case R.id.action_we_on_maps:
                return OpenActivity.openMapsWhereWe(this);
            case R.id.action_steps_admission:
                return OpenActivity.openAdmissionSteps(this);
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_personal_cabinet);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    @Override
    public void onBackPressed(){
        new ShowCustomDialog().showDialog(this, null,
                "Выйти", "Вы действительно хотите выйти?",
                "Да", "Нет")
                .setOnYes(() -> {
                    startActivity(new Intent(this, AuthorizationActivity.class)
                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                    finishAffinity();
                    sendSpeciality();
                    clearData();
                    MainFragment.clearData();
                    ResultEguFragment.clearTable();
                    SpecialityFragment.clearTable();
                    StatementFragment.clearData();
                    AchievementsFragment.clearData();
                })
                .setOnDismiss(() -> navigationView.getMenu().findItem(R.id.nav_home).setChecked(true));
    }


    public static void sendSpeciality() {
        if(PersonalCabinetActivity.specialitysAbit == null) return;

        List<JSONObject> res = new ArrayList<>();
        for (int i = 0; i < specialitysAbit.size(); ++i) {
            try {
                res.add(new JSONObject()
                        .put("id_abit", specialitysAbit.get(i).get("id_abit"))
                        .put("id_spec", specialitysAbit.get(i).get("id_spec"))
                        .put("type_of_study", specialitysAbit.get(i).get("type_of_study"))
                        .put("priority", specialitysAbit.get(i).get("priority"))
                        .put("id_financing", specialitysAbit.get(i).get("id_financing"))
                        .put("date_filing", specialitysAbit.get(i).get("date_filing")));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }


        AndroidNetworking.post("https://vkr1-app.herokuapp.com/abit/spec/add")
                .setPriority(Priority.HIGH)
                .addJSONArrayBody(new JSONArray(res))
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.e("", response.toString());
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("", "Чёт пошло не так");
                    }
                });
    }

    private void clearData(){
        idAbit = null;
        if(specialitysAbit != null)specialitysAbit.clear();
        specialitysAbit = null;
    }

    private void initComponents(){
        navigationView = findViewById(R.id.nav_view);
        navigationView.getMenu().findItem(R.id.nav_exit).setOnMenuItemClickListener(menuItem -> {
            onBackPressed();
            return true;
        });
        View headerView = navigationView.getHeaderView(0);
        fio = headerView.findViewById(R.id.header_textView_FIO);
        emailPhone = headerView.findViewById(R.id.header_textView_email_phone);
        idAbit = getIntent().getStringExtra("id_abit");
        idEducation = getIntent().getStringExtra("id_education");

        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit?id=" + idAbit)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new Thread(()->{
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                String resFio =  jsonNode.get("abit").get("family").asText()
                                        + " " +  jsonNode.get("abit").get("name").asText()
                                        + " " + (!jsonNode.get("abit").get("patronymic").toString().equals("null")? jsonNode.get("abit").get("patronymic").asText() : "");
                                String resEmailPhone = "Почта: " + (!jsonNode.get("abit").get("email").toString().equals("null")?
                                        jsonNode.get("abit").get("email").asText() : "-")
                                        + '\n' + "Телефон: " + (!jsonNode.get("abit").get("phone").toString().equals("null") ?
                                        doNicePhone(jsonNode.get("abit").get("phone").asText()) : "-");

                                new Handler(Looper.getMainLooper()).post(()->{
                                    fio.setText(resFio);
                                    emailPhone.setText(resEmailPhone);
                                });

                                sendDataToHomeFragment(getIntent().getStringExtra("login"),
                                        jsonNode.get("abit").get("id").asText(),
                                        jsonNode.get("sex").get("name").asText(),
                                        jsonNode.get("nationality").get("name").asText(),
                                        jsonNode.get("abit").get("passport").asText(),
                                        jsonNode.get("abit").get("departament_code").asText(),
                                        jsonNode.get("abit").get("date_of_issing_education").asText(),
                                        jsonNode.get("abit").get("date_of_issing_passport").asText(),
                                        jsonNode.get("abit").get("const_address").asText(),
                                        jsonNode.get("abit").get("actual_address").asText(),
                                        jsonNode.get("education").get("name").asText(),
                                        jsonNode.get("abit").get("number_education").asText(),
                                        jsonNode.get("abit").get("reg_number_education").asText(),
                                        jsonNode.get("abit").get("date_of_birthday").asText(),
                                        jsonNode.get("privilege").get("name").asText());
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    @Override
                    public void onError(ANError error) { }
                });

        downloadTypeOfStudy();
        downloadSpecialitysAbit();
        downloadInstituts();
    }

    private void sendDataToHomeFragment(String login, String snills, String sex, String nationality,
                                        String passport, String departament_code, String date_of_issing_education,
                                        String date_of_issing_passport, String const_address, String actual_address,
                                        String id_education, String number_education, String reg_number_education,
                                        String date_of_birthday, String privileges) {
        MainFragment.getHomeViewModel().postTextLogin(login.equals("null")? "-" : login);
        MainFragment.getHomeViewModel().postTextSnills(snills.equals("null")? "-" : snills);
        MainFragment.getHomeViewModel().postTextSex(sex.equals("null")? "-" : sex);
        MainFragment.getHomeViewModel().postTextNationality(nationality.equals("null")? "-" : nationality);
        MainFragment.getHomeViewModel().postTextPassport(passport.equals("null")? "-" : passport);
        MainFragment.getHomeViewModel().postDepartamentCode(departament_code.equals("null")? "-" : departament_code);
        MainFragment.getHomeViewModel().postDateOfIssingEducation(date_of_issing_education.equals("null")? "-" : date_of_issing_education);
        MainFragment.getHomeViewModel().postDateOfIssingPassport(date_of_issing_passport.equals("null")? "-" : date_of_issing_passport);
        MainFragment.getHomeViewModel().postConstAddress(const_address.equals("null")? "-" : const_address);
        MainFragment.getHomeViewModel().postActualAddress(actual_address.equals("null")? "-" : actual_address);
        MainFragment.getHomeViewModel().postIdEducation(id_education.equals("null")? "-" : id_education);
        MainFragment.getHomeViewModel().postNumberEducation(number_education.equals("null")? "-" : number_education);
        MainFragment.getHomeViewModel().postRegNumberEducation(reg_number_education.equals("null") ? "-" : reg_number_education);
        MainFragment.getHomeViewModel().postDateOfBirthday(date_of_birthday.equals("null")? "-" : date_of_birthday);
        MainFragment.getHomeViewModel().postPrivilege(privileges);

    }

    private StringBuilder doNicePhone(String phone){ // 89371727345 -> 8 (937) 17-27-345
        StringBuilder res = new StringBuilder();
        for(int i = 0; i<phone.length(); ++i){
            if (i == 1) res.append(" (");
            else if (i == 4) res.append(") ");
            else if (i == 6 || i == 8) res.append("-");
            res.append(phone.charAt(i));
        }
        return res;
    }

    private void downloadSpecialitysAbit(){
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/spec?id_abit=" + idAbit)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        new Thread(()->{
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                specialitysAbit = new ArrayList<>();
                                jsonNode.forEach(item -> specialitysAbit.add(new HashMap<String, String>() {
                                    {
                                        put("id_abit", item.get("abitSpec").get("id_abit").toString());
                                        put("id_spec", item.get("abitSpec").get("id_spec").asText());
                                        put("type_of_study", item.get("abitSpec").get("type_of_study").toString());
                                        put("priority", item.get("abitSpec").get("priority").toString());
                                        put("id_financing", item.get("abitSpec").get("id_financing").toString());
                                        put("date_filing", item.get("abitSpec").get("date_filing").asText());
                                        put("name_spec", item.get("nameSpec").asText());
                                        put("typeOfStudy", item.get("typeOfStudy").get("name").asText());
                                        put("institution", item.get("nameInstitution").asText());
                                    }
                                }));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    @Override
                    public void onError(ANError anError){
                        Log.e("", "tyt");
                        specialitysAbit = new ArrayList<>();
                    }

                });

    }

    private void downloadInstituts(){
        if(instituts != null) return;
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/institutions")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        new Thread(() -> {
                            try {
                                instituts = new HashMap<>();
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                jsonNode.forEach(e-> instituts.put(e.get("name").asText(), e.get("id").toString()));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    @Override
                    public void onError(ANError anError){ }
                });
    }

    private void downloadTypeOfStudy(){
        if(typeOfStudy != null) return;
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/type_of_study")
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        new Thread(() -> {
                            try {
                                typeOfStudy = new HashMap<>();
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                jsonNode.forEach(e-> typeOfStudy.put(e.get("name").asText(), e.get("id").toString()));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    @Override
                    public void onError(ANError anError){ }
                });
    }
}