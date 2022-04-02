package com.example.vkr.activity.registration;

import static android.widget.AdapterView.OnItemSelectedListener;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.utils.HideKeyboardClass;
import com.example.vkr.utils.MySpinnerAdapter;
import com.example.vkr.utils.dialogs.ShowToast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

public class ExamsResultActivity extends AppCompatActivity {

    private String idAbit;
    private LinearLayout examsLayout;
    private List<String> exams;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getSupportActionBar() != null) getSupportActionBar().hide(); //убираем action bar
        setContentView(R.layout.exams_result_activity);
        initComponents();
        appleEvents();
    }


    public void onAddField(View v){
        LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field_for_exam, null);
        EditText pointsExam = rowView.findViewById(R.id.text_points_of_exam);
        Spinner spinnerExams = rowView.findViewById(R.id.exam);

        Spinner spinnerYear = rowView.findViewById(R.id.spinner_date_exam);
        spinnerYear.setAdapter(new MySpinnerAdapter(this, R.layout.spinner_item, Arrays.asList(getResources().getStringArray(R.array.date_of_exams))));
        spinnerYear.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { //выбран какой
                    TextView text = (TextView) view;
                    Optional.ofNullable(text).ifPresent(e -> text.setTextColor(Color.BLACK));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
        spinnerExams.setOnItemSelectedListener(new OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position > 0) { //выбран какой
                    TextView text = (TextView) view;
                    Optional.ofNullable(text).ifPresent(e -> text.setTextColor(Color.BLACK));
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });

        spinnerExams.setAdapter(new MySpinnerAdapter(this, R.layout.spinner_item, exams));
        pointsExam.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) { }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                try {
                    if (Integer.parseInt(charSequence.toString()) < 0)
                        pointsExam.setText("0");
                    else if (Integer.parseInt(charSequence.toString()) > 100) {
                        pointsExam.setText("100");
                        pointsExam.setSelection(3);
                    }
                } catch (Exception ignored){}
            }

            @Override
            public void afterTextChanged(Editable editable) { }
        });

        rowView.findViewById(R.id.delete_button).setOnClickListener(view -> examsLayout.removeView(rowView));

        examsLayout.addView(rowView, examsLayout.getChildCount() - 1);
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            HideKeyboardClass.hideKeyboard(this);
            examsLayout.clearFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    private void appleEvents(){
        findViewById(R.id.button_next_to_lk).setOnClickListener(view -> {

            if(examsLayout.getChildCount() > 0){
                if(isCorrectData()){
                    List<JSONObject> res = new ArrayList<>();
                    for(int i=0; i<examsLayout.getChildCount() - 1; ++i){
                        Spinner exam = examsLayout.getChildAt(i).findViewById(R.id.exam);
                        Spinner year = examsLayout.getChildAt(i).findViewById(R.id.spinner_date_exam);
                        EditText points = examsLayout.getChildAt(i).findViewById(R.id.text_points_of_exam);
                        try {
                            res.add(new JSONObject()
                                    .put("id_abit", idAbit)
                                    .put("id_exam", exam.getSelectedItemPosition())
                                    .put("points", points.getText().toString())
                                    .put("year", year.getSelectedItem().toString()));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    Activity activity = this;
                    AndroidNetworking.post("https://vkr1-app.herokuapp.com/abit/exams/add")
                            .addJSONArrayBody(new JSONArray(res))
                            .setPriority(Priority.IMMEDIATE)
                            .build()
                            .getAsJSONObject(new JSONObjectRequestListener() {
                                @Override
                                public void onResponse(JSONObject response) {
                                    finish();
                                    startActivity(new Intent(activity, PersonalCabinetActivity.class)
                                            .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK)
                                            .putExtra("login", getIntent().getStringExtra("login"))
                                            .putExtra("id_abit", idAbit)
                                            .putExtra("id_education", getIntent().getStringExtra("id_education")));

                                    AndroidNetworking.put("https://vkr1-app.herokuapp.com/users?id_abit=" + idAbit)
                                            .setPriority(Priority.IMMEDIATE)
                                            .build()
                                            .getAsJSONObject(new JSONObjectRequestListener() {
                                                @Override
                                                public void onResponse(JSONObject response) {}
                                                @Override
                                                public void onError(ANError anError) {}
                                            });
                                }
                                @Override
                                public void onError(ANError anError) {
                                    Log.e("", "Ошибка при отправление данных (экзамены)");
                                }
                            });
                }
            }
            else
                ShowToast.show(this, "Экзаменов не может быть меньше 3");
        });
    }

    private boolean isCorrectData(){
        for(int i=0; i<examsLayout.getChildCount() - 1; ++i){
            Spinner exam = examsLayout.getChildAt(i).findViewById(R.id.exam);
            Spinner year = examsLayout.getChildAt(i).findViewById(R.id.spinner_date_exam);
            EditText points = examsLayout.getChildAt(i).findViewById(R.id.text_points_of_exam);
            if(exam.getSelectedItemPosition() == 0 //проверка на пустоту полей
                    || year.getSelectedItemPosition() == 0
                    || points.getText().toString().equals("")){
                ShowToast.show(this, "Есть пустые поля");
                return false;
            }
            else{
                for(int j=i+1; j<examsLayout.getChildCount() - 1; ++j){
                    Spinner exam1 = examsLayout.getChildAt(j).findViewById(R.id.exam);
                    if(exam.getSelectedItemPosition() == exam1.getSelectedItemPosition()){
                        ShowToast.show(this, "Одинаковые экзамены не могут быть");
                        return false;
                    }
                }
            }

        }
        return true;
    }

    private void initComponents(){
        idAbit = getIntent().getStringExtra("id_abit");
        examsLayout = findViewById(R.id.exams_layout);
        new Thread(()->{
            exams = new ArrayList<>();
            exams.add("Выберите экзамен");
            AndroidNetworking.get("https://vkr1-app.herokuapp.com/exams")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                jsonNode.forEach(e -> exams.add(e.get("name").toString().replace("\"", "")));
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        }
                        @Override
                        public void onError(ANError error) { }
                    });
        }).start();
    }


}