package com.example.vkr.personal_cabinet.moreAbout;

import static java.util.Arrays.asList;

import android.animation.LayoutTransition;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
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
import java.util.List;
import java.util.Objects;

public class MoreAboutTheInstitutActivity extends AppCompatActivity {
    private int id;
    private TextView nameOfInstitut;
    private TextView directorOfInstitut;
    private TextView contactPhoneOfInstitut;
    private TextView discriptionOfInstitut;
    private LinearLayout layoutSpecialityInfo;
    private LinearLayout layoutSpeciality;
    private List<List<String>> specialitys;
    private boolean isPressed = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more_about_the_institut);
        initComponents();
        applyEvents();
        setSupportActionBar(findViewById(R.id.toolbar_institut));
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.menu_institut, menu);
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
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void applyEvents(){
        layoutSpeciality.setOnClickListener(view -> {
            isPressed = !isPressed;
            fillSpeciality(isPressed, findViewById(R.id.arrow_downward4));
        });
    }
    private void fillSpeciality(boolean isPress, ImageView status) {
        TransitionManager.beginDelayedTransition(layoutSpecialityInfo, new AutoTransition());
        layoutSpecialityInfo.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        if(isPress) {
            for (int i = 0; i < specialitys.size(); i++) {
                LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                View rowView = inflater.inflate(R.layout.field_for_text, null);
                TextView text = rowView.findViewById(R.id.text_question);
                text.setText(String.format("%s %s", specialitys.get(i).get(0), specialitys.get(i).get(1)));
                layoutSpecialityInfo.addView(rowView);
            }
            status.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_back_24));
        }
        else {
            layoutSpecialityInfo.removeAllViews();
            status.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.ic_baseline_arrow_downward_24));
        }
    }


    private void initComponents() {
        id = Integer.parseInt(getIntent().getStringExtra("id"));

        nameOfInstitut = findViewById(R.id.name_of_institut);
        directorOfInstitut = findViewById(R.id.director_of_institut);
        contactPhoneOfInstitut = findViewById(R.id.contact_phone_of_institut);
        discriptionOfInstitut = findViewById(R.id.discription_of_institut);
        layoutSpeciality = findViewById(R.id.layout_of_specialitys);
        layoutSpecialityInfo = findViewById(R.id.layout_of_specialitys_info);

        downloadInfoInstituts();
        downloadSpecialitys();
    }

    private void downloadSpecialitys(){
        String url;
        if(Integer.parseInt(PersonalCabinetActivity.idEducation) < 5)
            url = "https://vkr1-app.herokuapp.com/speciality/abit/info?id_institut=" + getIntent().getStringExtra("id");
        else
            url = "https://vkr1-app.herokuapp.com/speciality/magistr/info?id_institut=" + getIntent().getStringExtra("id");
        Log.e("", url);
        AndroidNetworking.get(url)
            .setPriority(Priority.IMMEDIATE)
            .build()
            .getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    new Thread(()->{
                        try {
                            specialitys = new ArrayList<>();
                            JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                            jsonNode.forEach(item -> specialitys.add(asList(item.get("id").asText(), item.get("name").asText())));
                            new Handler(Looper.getMainLooper()).post(()->{
                                fillSpeciality(isPressed, findViewById(R.id.arrow_downward4));
                                layoutSpeciality.setEnabled(true);
                            });

                        } catch (Exception e) {
                            Log.e("", "error catch");
                            Log.e("", e.getMessage());
                        }
                    }).start();
                }

                @Override
                public void onError(ANError anError) {
                    Log.e("", "error");
                }
            });
    }
    private void downloadInfoInstituts(){
        Activity activity = this;
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/institutions?id=" + getIntent().getStringExtra("id"))
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                            new Handler(Looper.getMainLooper()).post(() -> {
                                nameOfInstitut.setText(getIntent().getStringExtra("name_institut"));
                                discriptionOfInstitut.setText(jsonNode.get("institution").get("description").asText());
                                contactPhoneOfInstitut.setText(jsonNode.get("institution").get("contact_phone").asText());
                                directorOfInstitut.setText(String.format("Директор института: %s", jsonNode.get("director").get("family").asText() + " " +
                                        jsonNode.get("director").get("name").asText() + " " + jsonNode.get("director").get("patronymic").asText()));

                                CardView card = findViewById(R.id.cardview_institut);
                                switch (id){
                                    case 1:
                                        findViewById(R.id.more_about_the_institut_main_layout).setBackground(ContextCompat.getDrawable(activity, R.drawable.cradient1));
                                        card.setCardBackgroundColor(Color.parseColor("#191970"));
                                        break;
                                    case 2:
                                        findViewById(R.id.more_about_the_institut_main_layout).setBackground(ContextCompat.getDrawable(activity, R.drawable.cradient2));
                                        card.setCardBackgroundColor(Color.parseColor("#CD5C5C"));
                                        break;
                                    case 3:
                                        findViewById(R.id.more_about_the_institut_main_layout).setBackground(ContextCompat.getDrawable(activity, R.drawable.cradient3));
                                        card.setCardBackgroundColor(Color.parseColor("#8A2BE2"));
                                        break;
                                    case 4:
                                        findViewById(R.id.more_about_the_institut_main_layout).setBackground(ContextCompat.getDrawable(activity, R.drawable.cradient4));
                                        card.setCardBackgroundColor(Color.parseColor("#013220"));
                                        break;
                                    case 5:
                                        findViewById(R.id.more_about_the_institut_main_layout).setBackground(ContextCompat.getDrawable(activity, R.drawable.cradient5));
                                        card.setCardBackgroundColor(Color.parseColor("#660066"));
                                        break;
                                    case 6:
                                        findViewById(R.id.more_about_the_institut_main_layout).setBackground(ContextCompat.getDrawable(activity, R.drawable.cradient6));
                                        card.setCardBackgroundColor(Color.parseColor("#660033"));
                                        break;
                                    case 7:
                                        findViewById(R.id.more_about_the_institut_main_layout).setBackground(ContextCompat.getDrawable(activity, R.drawable.cradient7));
                                        card.setCardBackgroundColor(Color.parseColor("#ff6600"));
                                        break;
                                }
                            });

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) { }
                });
    }

}