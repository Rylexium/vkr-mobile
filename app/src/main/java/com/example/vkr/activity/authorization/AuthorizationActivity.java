package com.example.vkr.activity.authorization;

import android.app.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.vkr.R;
import com.example.vkr.splash_screen.SplashScreen;
import com.example.vkr.utils.ConvertClass;
import com.example.vkr.utils.HashPass;
import com.example.vkr.utils.HideKeyboardClass;
import com.example.vkr.utils.OpenActivity;
import com.example.vkr.utils.dialogs.ShowToast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.textfield.TextInputEditText;

import org.json.JSONObject;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;


public class AuthorizationActivity extends AppCompatActivity {
    private TextInputEditText textBoxLogin;
    private TextInputEditText textBoxPassword;
    private Button singInBtn;
    private TextView labelQuestions;
    private TextView labelRememberPassword;
    private TextView labelRegistration;
    private ImageView logo;

    final String KEY_LOGIN = "login";

    private Integer tryToConnect = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.authorization_activity);
        if(getSupportActionBar() != null) getSupportActionBar().hide();
        initComponents();
        comebackAfterOnBackPressed();
        ApplyEvents();
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (getCurrentFocus() != null) {
            HideKeyboardClass.hideKeyboard(this);
            LinearLayout ll = findViewById(R.id.authorization_layout);
            ll.requestFocus();
        }
        return super.dispatchTouchEvent(ev);
    }

    @Override
    public void onBackPressed() {
        saveLastState();
        finishAffinity();
    }

    @Override
    public void onResume(){
        textBoxPassword.setText("");
        SplashScreen.sharedPreferences.edit().remove("password").apply();
        super.onResume();
    }

    private void comebackAfterOnBackPressed() {
        String restoredText = getPreferences(MODE_PRIVATE).getString("login", null);
        if (!TextUtils.isEmpty(restoredText)) {
            textBoxLogin.setText(restoredText);
        }
    }

    private void ApplyEvents(){
        labelRememberPassword.setOnClickListener(view -> {
            labelRememberPassword.setEnabled(false);
            OpenActivity.openSupport(this, textBoxLogin.getText().toString());
            new Handler(Looper.getMainLooper()).postDelayed(() -> labelRememberPassword.setEnabled(true), 1000);
        });
        labelRegistration.setOnClickListener(view -> {
            labelRegistration.setEnabled(false);
            OpenActivity.openRegistration(this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> labelRegistration.setEnabled(true), 1000);
        });
        labelQuestions.setOnClickListener(view -> {
            labelQuestions.setEnabled(false);
            OpenActivity.openPageWithQuestion(this);
            new Handler(Looper.getMainLooper()).postDelayed(() -> labelQuestions.setEnabled(true), 1000);
        });

        singInBtn.setOnClickListener(view -> {
            if(textBoxLogin.getText().length() == 0 || textBoxPassword.getText().length() == 0) return;
            if(!singInBtn.isEnabled()) return;
            authorization(textBoxLogin.getText().toString(), textBoxPassword.getText().toString());
            saveLastState();
        });
    }

    private void saveLastState(){
        getPreferences(MODE_PRIVATE)
                .edit()
                .putString(KEY_LOGIN, textBoxLogin.getText().toString())
                .apply();
        SplashScreen.sharedPreferences.edit()
                .putString(KEY_LOGIN, textBoxLogin.getText().toString())
                .putString("password", HashPass.sha256(textBoxPassword.getText().toString() + HashPass.STATIC_SALT))
                .apply();
    }

    private void initComponents() {
        logo = findViewById(R.id.ssau_logo);
        Glide.with(this)
                .load(BitmapFactory.decodeResource(getResources(), R.drawable.ssau_logo_auth))
                .format(DecodeFormat.PREFER_ARGB_8888)
                .into(logo);


        textBoxLogin = findViewById(R.id.textbox_login);
        textBoxPassword = findViewById(R.id.textbox_password);
        singInBtn = findViewById(R.id.button_singIn);
        labelQuestions = findViewById(R.id.questions);
        labelRememberPassword = findViewById(R.id.remember_password);
        labelRegistration = findViewById(R.id.registration);
    }

    private void authorization(String login, String password) {
        singInBtn.setEnabled(false);
        String previousText = singInBtn.getText().toString();
        singInBtn.setText("Входим...");
        Activity activity = this;
        AndroidNetworking.get(String.format("https://vkr1-app.herokuapp.com/authorization?login=%s&password=%s",
                login, HashPass.sha256(password + HashPass.STATIC_SALT)))
                .setPriority(Priority.IMMEDIATE)
                .setOkHttpClient(new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                            ShowToast.show(getApplicationContext(), "Успешно");
                            if(jsonNode.get("idEducation") == null){
                                ShowToast.show(getApplicationContext(), "Неверный логин или пароль");
                            }
                            else if (Integer.parseInt(jsonNode.get("idEducation").toString()) < 5
                                    && !Boolean.parseBoolean(jsonNode.get("user").get("is_entry").toString())) {
                                OpenActivity.openExamsResult(activity,
                                        jsonNode.get("user").get("id_abit").toString(),
                                        jsonNode.get("user").get("login").toString().replace("\"", ""),
                                        jsonNode.get("idEducation").toString());
                            } else{
                                OpenActivity.openPersonalCabinet(activity,
                                        jsonNode.get("user").get("id_abit").toString(),
                                        jsonNode.get("user").get("login").toString().replace("\"", ""),
                                        jsonNode.get("idEducation").toString());
                            }

                            singInBtn.setEnabled(true);
                            singInBtn.setText(previousText);
                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError error) {
                        if(error.getErrorDetail().equals("parseError")) ShowToast.show(getApplicationContext(), "Неверный логин или пароль");
                        else ShowToast.show(getApplicationContext(), "Произошла ошибка при соединении с сервером");
                        singInBtn.setEnabled(true);
                        singInBtn.setText(previousText);
                        tryToConnect += 1;
                        if(tryToConnect < 3)
                            authorization(login, password);
                    }
                });
    }

}