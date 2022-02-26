package com.example.vkr.activity.authorization;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vkr.R;

public class QuestionsActivity extends AppCompatActivity {

    private LinearLayout documentsNeededLayout;
    private Button btnDocumentsNeeded;
    private boolean isPressedBtnDocumentsNeeded = false;


    private LinearLayout howToApplyLayout;
    private Button btnHowHowToApply;
    private boolean isPressedBtnHowToApply = false;

    private LinearLayout passingScoreLayout;
    private Button btnPassingScore;
    private boolean isPressedBtnPassingScore = false;

    private LinearLayout medicalCardLayout;
    private Button btnMedicalCard;
    private boolean isPressedBtnMedicalCard = false;

    private LinearLayout сopyOfDocumentsLayout;
    private Button btnCopyOfDocuments;
    private boolean isPressedBtnCopyOfDocuments = false;

    private LinearLayout digitalSignatureLayout;
    private Button btnDigitalSignature;
    private boolean isPressedBtnDigitalSignature = false;

    private LinearLayout neededExamsLayout;
    private Button btnNeededExamsLayout;
    private boolean isPressedBtnNeededExams = false;

    private LinearLayout dormitoryLayout;
    private Button btnDormitory;
    private boolean isPressedBtnDormitory = false;

    private LinearLayout needToBeEnrolledLayout;
    private Button btnNeedToBeEnrolled;
    private boolean isPressedBtnNeedToBeEnrolled = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.questions_activity);
        if(getSupportActionBar() != null) getSupportActionBar().hide(); //убираем action bar
        getWindow().setStatusBarColor(ContextCompat.getColor(this, R.color.blue_500));
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        initComponents();
        applyEvents();
    }

    private void setTextForQuestion(boolean isPressed, LinearLayout linearLayout, String text){
        if(!isPressed){
            LayoutInflater inflater=(LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            final View rowView=inflater.inflate(R.layout.field_for_questions, null);
            TextView textQuestion = rowView.findViewById(R.id.text_question);
            textQuestion.setText(text);
            linearLayout.addView(rowView);
        }
        else linearLayout.removeViewAt(linearLayout.getChildCount() - 1);
    }

    private void applyEvents(){
        btnDocumentsNeeded.setOnClickListener(view-> {
            setTextForQuestion(isPressedBtnDocumentsNeeded, documentsNeededLayout, getResources().getString(R.string.question1));
            isPressedBtnDocumentsNeeded = !isPressedBtnDocumentsNeeded;
        });

        btnHowHowToApply.setOnClickListener(view->{
            setTextForQuestion(isPressedBtnHowToApply, howToApplyLayout, getResources().getString(R.string.question2));
            isPressedBtnHowToApply = !isPressedBtnHowToApply;
        });

        btnPassingScore.setOnClickListener(view -> {
            setTextForQuestion(isPressedBtnPassingScore, passingScoreLayout, getResources().getString(R.string.question3));
            isPressedBtnPassingScore = !isPressedBtnPassingScore;
        });

        btnMedicalCard.setOnClickListener(view->{
            setTextForQuestion(isPressedBtnMedicalCard, medicalCardLayout, getResources().getString(R.string.question4));
            isPressedBtnMedicalCard = !isPressedBtnMedicalCard;
        });

        btnCopyOfDocuments.setOnClickListener(view->{
            setTextForQuestion(isPressedBtnCopyOfDocuments, сopyOfDocumentsLayout, getResources().getString(R.string.question5));
            isPressedBtnCopyOfDocuments = !isPressedBtnCopyOfDocuments;
        });

        btnDigitalSignature.setOnClickListener(view->{
            setTextForQuestion(isPressedBtnDigitalSignature, digitalSignatureLayout, getResources().getString(R.string.question6));
            isPressedBtnDigitalSignature = !isPressedBtnDigitalSignature;
        });

        btnNeededExamsLayout.setOnClickListener(view->{
            setTextForQuestion(isPressedBtnNeededExams, neededExamsLayout, getResources().getString(R.string.question7));
            isPressedBtnNeededExams = !isPressedBtnNeededExams;
        });

        btnDormitory.setOnClickListener(view->{
            setTextForQuestion(isPressedBtnDormitory, dormitoryLayout, getResources().getString(R.string.question8));
            isPressedBtnDormitory = !isPressedBtnDormitory;
        });

        btnNeedToBeEnrolled.setOnClickListener(view->{
            setTextForQuestion(isPressedBtnNeedToBeEnrolled, needToBeEnrolledLayout, getResources().getString(R.string.question9));
            isPressedBtnNeedToBeEnrolled = !isPressedBtnNeedToBeEnrolled;
        });
    }

    private void initComponents(){
        documentsNeededLayout = findViewById(R.id.documents_needed_layout);
        btnDocumentsNeeded = findViewById(R.id.btn_documents_needed);

        howToApplyLayout = findViewById(R.id.how_to_apply_layout);
        btnHowHowToApply = findViewById(R.id.btn_how_to_apply);

        passingScoreLayout = findViewById(R.id.passing_score_layout);
        btnPassingScore = findViewById(R.id.btn_passing_score);

        medicalCardLayout = findViewById(R.id.medical_card_layout);
        btnMedicalCard = findViewById(R.id.btn_medical_card);

        сopyOfDocumentsLayout = findViewById(R.id.сopy_of_documents_layout);
        btnCopyOfDocuments = findViewById(R.id.btn_сopy_of_documents);

        digitalSignatureLayout = findViewById(R.id.digital_signature_layout);
        btnDigitalSignature = findViewById(R.id.btn_digital_signature);

        neededExamsLayout = findViewById(R.id.needed_exams_layout);
        btnNeededExamsLayout = findViewById(R.id.btn_needed_exams);

        dormitoryLayout = findViewById(R.id.dormitory_layout);
        btnDormitory = findViewById(R.id.btn_dormitory);

        needToBeEnrolledLayout = findViewById(R.id.need_to_be_enrolled_layout);
        btnNeedToBeEnrolled = findViewById(R.id.btn_need_to_be_enrolled);
    }
}