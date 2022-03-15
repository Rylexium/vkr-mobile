package com.example.vkr.personal_cabinet.moreAbout;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.specialitysAbit;
import static java.util.Arrays.asList;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.design.widget.Snackbar;
import android.support.v4.content.ContextCompat;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.view.menu.ActionMenuItemView;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.utils.OpenActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MoreAboutTheSpecialityActivity extends AppCompatActivity {

    private String idSpec;

    private TextView institutOfSpeciality;
    private TextView facultOfSpeciality;
    private TextView nameOfSpeciality;
    private TextView idOfSpeciality;
    private TextView fioCuratorOfSpeciality;
    private TextView phoneOfSpeciality;
    private TextView studyingTimeOfSpeciality;
    private TextView typeOfStudyOfSpeciality;
    private TextView budgetOfSpeciality;
    private TextView payPerYearOfSpeciality;
    private TextView payOfSpeciality;
    private TextView descriptionOfSpeciality;
    private TextView passingScoreOfSpeciality;

    private LinearLayout mainLayout;
    private LinearLayout layoutOfExamsForSpeciality;

    private List<List<String>> exams;

    private LinearLayout competenciesOfSpeciality;
    private LinearLayout professionsOfSpeciality;
    private LinearLayout partnersOfSpeciality;

    private boolean favorite = false;
    public static String typeOfStudy;

    private String competencies, professions, partners;
    private boolean isCompetencies = false, isProfessions = false, isPartners = false;
    private MenuItem favoriteItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_about_the_speciality);
        setSupportActionBar(findViewById(R.id.toolbar_speciality));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
        initComponents();
        applyEvents();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_speciality, menu);
        favoriteItem = menu.findItem(R.id.add_speciality);
        favoriteItem.setEnabled(false);
        favoriteItem.setIcon(DrawableCompat.wrap(Objects.requireNonNull(favorite ? ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24) :
                                                                               ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24))));
        return true;
    }

    @SuppressLint("RestrictedApi")
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_contact_with_developer:
                return OpenActivity.openPageDeveloper(this);
            case R.id.action_faq:
                return OpenActivity.openPageWithQuestion(this);
            case R.id.action_we_on_maps:
                return OpenActivity.openMapsWhereWe(this);
            case android.R.id.home:
                finish();
                return true;
            case R.id.add_speciality:
                ActionMenuItemView it = findViewById(R.id.add_speciality);
                if(favorite){
                    favorite = false;
                    for(int i=0; i<specialitysAbit.size(); ++i) {
                        if (Objects.equals(specialitysAbit.get(i).get("id_spec"), idSpec)
                                && Objects.equals(specialitysAbit.get(i).get("id_abit"), PersonalCabinetActivity.idAbit)
                                && Objects.equals(specialitysAbit.get(i).get("type_of_study"), typeOfStudy)) {
                            specialitysAbit.remove(i);
                            break;
                        }
                    }
                    it.setIcon(DrawableCompat.wrap(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_border_24))));

                    AndroidNetworking.delete("https://vkr1-app.herokuapp.com/abit/spec/" +
                            "delete?id_abit=" + PersonalCabinetActivity.idAbit + "&id_spec=" + idSpec + "&type_of_study=" + typeOfStudy)
                            .setPriority(Priority.HIGH)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) { }

                                @Override
                                public void onError(ANError anError) {
                                    Log.e("", "error");
                                }
                            });
                }
                else if(specialitysAbit.size() < 3) {
                    favorite = true;
                    specialitysAbit.add(new HashMap<String, String>(){
                        {
                            put("id_abit", PersonalCabinetActivity.idAbit);
                            put("id_spec", idSpec);
                            put("type_of_study", typeOfStudy);
                            put("typeOfStudy", typeOfStudyOfSpeciality.getText().toString());
                            put("priority", null);
                            put("id_financing", null);
                            put("date_filing", null);
                            put("name_spec", nameOfSpeciality.getText().toString());
                            put("institution", institutOfSpeciality.getText().toString());
                        }});
                    it.setIcon(DrawableCompat.wrap(Objects.requireNonNull(ContextCompat.getDrawable(this, R.drawable.ic_baseline_favorite_24))));
                }
                else {
                    Snackbar.make(it, "Вы не можете выбрать больше 3 специальностей...", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void applyEvents() {
        competenciesOfSpeciality.setOnClickListener(view-> {
            setTextForTitle(isCompetencies, findViewById(R.id.competencies_of_speciality_info),
                    competencies, view.findViewById(R.id.arrow_downward1));
            isCompetencies = !isCompetencies;
        });

        professionsOfSpeciality.setOnClickListener(view-> {
            setTextForTitle(isProfessions, findViewById(R.id.professions_of_speciality_info),
                    professions, view.findViewById(R.id.arrow_downward2));
            isProfessions = !isProfessions;
        });

        partnersOfSpeciality.setOnClickListener(view-> {
            setTextForTitle(isPartners, findViewById(R.id.partners_of_speciality_info),
                    partners, view.findViewById(R.id.arrow_downward3));
            isPartners = !isPartners;
        });
    }

    private void setTextForTitle(boolean isPressed, LinearLayout linearLayout, String text, ImageView status){
        TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGE_APPEARING);
        if(!isPressed){
            LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView=inflater.inflate(R.layout.field_for_text, null);
            rowView.setVisibility(View.VISIBLE);
            TextView textQuestion = rowView.findViewById(R.id.text_question);
            textQuestion.setText(text);
            linearLayout.addView(rowView);
            status.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24));
        }
        else {
            linearLayout.getChildAt(linearLayout.getChildCount() - 1).setVisibility(View.GONE);
            linearLayout.removeViewAt(linearLayout.getChildCount() - 1);
            status.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_downward_24));
        }
    }

    private void initComponents() {
        idSpec = getIntent().getStringExtra("id");
        institutOfSpeciality = findViewById(R.id.institut_of_speciality);
        facultOfSpeciality = findViewById(R.id.facult_of_speciality);
        nameOfSpeciality = findViewById(R.id.name_of_speciality);
        idOfSpeciality = findViewById(R.id.id_of_speciality);
        fioCuratorOfSpeciality = findViewById(R.id.fio_curator_of_speciality);
        phoneOfSpeciality = findViewById(R.id.phone_of_speciality);
        studyingTimeOfSpeciality = findViewById(R.id.studying_time_of_speciality);
        typeOfStudyOfSpeciality = findViewById(R.id.type_of_study_of_speciality);
        budgetOfSpeciality = findViewById(R.id.budget_of_speciality);
        payPerYearOfSpeciality = findViewById(R.id.pay_per_year_of_speciality);
        payOfSpeciality = findViewById(R.id.pay_of_speciality);
        descriptionOfSpeciality = findViewById(R.id.description_of_speciality);
        passingScoreOfSpeciality = findViewById(R.id.passing_score_number);

        mainLayout = findViewById(R.id.layout_more_about_the_speciality);
        layoutOfExamsForSpeciality = findViewById(R.id.layout_of_exams_for_speciality);

        competenciesOfSpeciality = findViewById(R.id.competencies_of_speciality);
        professionsOfSpeciality = findViewById(R.id.professions_of_speciality);
        partnersOfSpeciality = findViewById(R.id.partners_of_speciality);

        typeOfStudy = PersonalCabinetActivity.typeOfStudy.get(getIntent().getStringExtra("type_of_study"));
        downloadSpeciality();
        downloadMinExams();

        for(int i=0; i<specialitysAbit.size() && !favorite; ++i)
            if(Objects.equals(specialitysAbit.get(i).get("id_spec"), idSpec)
                    && Objects.equals(specialitysAbit.get(i).get("id_abit"), PersonalCabinetActivity.idAbit)
                    && Objects.equals(specialitysAbit.get(i).get("type_of_study"), typeOfStudy))
                favorite = true;
    }

    private void downloadSpeciality(){
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/speciality?id=" + idSpec +"&type_of_study=" + typeOfStudy)
                .setPriority(Priority.IMMEDIATE)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new Thread(()->{
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                competencies = !jsonNode.get("speciality").get("competencies").toString().equals("null")?
                                        jsonNode.get("speciality").get("competencies").asText() : "-";

                                professions = !jsonNode.get("speciality").get("professions").toString().equals("null")?
                                        jsonNode.get("speciality").get("professions").asText() : "-";

                                partners = !jsonNode.get("speciality").get("partners").toString().equals("null")?
                                        jsonNode.get("speciality").get("partners").asText() : "-";

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    idOfSpeciality.setText(String.format("Код: %s", jsonNode.get("speciality").get("id").asText()));
                                    nameOfSpeciality.setText(jsonNode.get("speciality").get("name").asText());
                                    institutOfSpeciality.setText(jsonNode.get("institution").get("name") == null? "-" :jsonNode.get("institution").get("name").asText());

                                    if (jsonNode.get("speciality").get("facult") == null)
                                        mainLayout.removeView(facultOfSpeciality);
                                    else facultOfSpeciality.setText(jsonNode.get("speciality").get("facult").get("name").asText());

                                    fioCuratorOfSpeciality.setText(String.format("Куратор: %s", jsonNode.get("speciality").get("curator").toString().equals("null")? '-' :
                                            jsonNode.get("curator").get("family").asText() + " " + jsonNode.get("curator").get("name").asText() + " "
                                                    + jsonNode.get("curator").get("patronymic").asText()));

                                    phoneOfSpeciality.setText(String.format("Номер для связи: %s",
                                            jsonNode.get("speciality").get("contact_number").toString().equals("null")?
                                                    '-' : jsonNode.get("speciality").get("contact_number").asText()));

                                    if (!jsonNode.get("speciality").get("studying_time").toString().equals("null"))
                                        studyingTimeOfSpeciality.setText(String.format("%s %s", jsonNode.get("speciality").get("studying_time"), //tyt
                                                jsonNode.get("speciality").get("studying_time").asInt() < 5 ? "года" : "лет"));

                                    typeOfStudyOfSpeciality.setText(jsonNode.get("typeOfStudy").get("name").asText());
                                    budgetOfSpeciality.setText(jsonNode.get("speciality").get("budget").toString());

                                    if (!jsonNode.get("speciality").get("pay_per_year").toString().equals("null"))
                                        payPerYearOfSpeciality.setText(jsonNode.get("speciality").get("pay_per_year").toString().length() == 5 ?
                                                new StringBuilder(jsonNode.get("speciality").get("pay_per_year").toString()).insert(2, ' ').toString() :
                                                new StringBuilder(jsonNode.get("speciality").get("pay_per_year").toString()).insert(3, ' ').toString());

                                    payOfSpeciality.setText(jsonNode.get("speciality").get("pay").toString());

                                    descriptionOfSpeciality.setText(jsonNode.get("speciality").get("description").toString().equals("null")?
                                            "-" : jsonNode.get("speciality").get("description").asText());

                                    passingScoreOfSpeciality.setText(jsonNode.get("speciality").get("passing_score").toString().equals("null")?
                                            "-" : jsonNode.get("speciality").get("passing_score").toString());

                                    favoriteItem.setEnabled(true);
                                });
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    @Override
                    public void onError(ANError anError) { }
                });
    }

    private void downloadMinExams(){
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/speciality_exams?id_spec=" + idSpec)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        new Thread(()->{
                            try {
                                exams = new ArrayList<>();
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                jsonNode.forEach(item -> exams.add(asList(item.get("exams").get("name").asText(),
                                                                          item.get("specialityExams").get("min_points").toString())));
                                new Handler(Looper.getMainLooper()).post(() -> {
                                    for(int i=0; i<exams.size(); i++){
                                        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                                        View rowView = inflater.inflate(R.layout.field_for_min_exam, null);
                                        TextView text = rowView.findViewById(R.id.name_exam);
                                        TextView points = rowView.findViewById(R.id.min_points_exam);
                                        text.setText(exams.get(i).get(0));
                                        points.setText(exams.get(i).get(1));
                                        layoutOfExamsForSpeciality.addView(rowView);
                                    }
                                });


                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }).start();
                    }

                    @Override
                    public void onError(ANError anError) { }
                });
    }

}