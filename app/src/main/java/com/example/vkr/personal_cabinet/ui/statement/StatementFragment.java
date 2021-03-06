package com.example.vkr.personal_cabinet.ui.statement;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.specialitysAbit;
import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.typeOfStudy;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import androidx.annotation.NonNull;

import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.personal_cabinet.moreAbout.MoreAboutTheInstitutActivity;
import com.example.vkr.personal_cabinet.moreAbout.MoreAboutTheSpecialityActivity;
import com.example.vkr.utils.dialogs.ShowToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class StatementFragment extends Fragment {

    private LinearLayout linearLayout;
    private View binding;

    private Button buttonSubmitStatement;
    private TextView supportTextView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        PersonalCabinetActivity.selectedPage = 4;
        binding = inflater.inflate(R.layout.fragment_statement, container, false);
        initComponents();
        applyEvents();
        buttonSubmitStatement.setEnabled(false);
        return binding.getRootView();
    }


    private void applyEvents(){
        buttonSubmitStatement.setOnClickListener(this::onClick);
    }

    private void initComponents(){
        linearLayout = binding.findViewById(R.id.layout_of_statement);
        buttonSubmitStatement = binding.findViewById(R.id.button_submit_statement);
        fillSpeciality();
    }

    private boolean isValidPriority(){
        if(linearLayout.getChildCount() == 0) return false;
        try {
            for (int i = 0; i < linearLayout.getChildCount(); ++i) {
                Spinner spinnerPriority = linearLayout.getChildAt(i).findViewById(R.id.spinner_priority);
                spinnerPriority.getSelectedItemId();
                for (int j = i + 1; j < linearLayout.getChildCount(); ++j) {
                    Spinner spinnerPriority2 = linearLayout.getChildAt(j).findViewById(R.id.spinner_priority);
                    if (spinnerPriority.getSelectedItemId() == spinnerPriority2.getSelectedItemId()) {
                        ShowToast.show(getActivity(), "???????????????????? ?????????????????????? ???????? ???? ??????????");
                        return false;
                    }
                }
            }
        }catch (Exception e){
            return false;
        }
        return true;
    }

    private void onAddField(String idSpeciality, String nameSpeciality,
                            String nameInstitut, String nameTypeOfStudy,
                            String valueOfDateOfStatement, String valueFinancing, String valuePriority) {
        if(PersonalCabinetActivity.listFinancing == null) return;

        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field_for_statement, null);

        LinearLayout mainLayout = rowView.findViewById(R.id.main_statement_layout);

        TextView name = rowView.findViewById(R.id.textview_speciality); //02.03.03 ??????????
        TextView institute = rowView.findViewById(R.id.textview_institut); //???????????????????????????????????? ????????????????
        TextView typeOfStudy = rowView.findViewById(R.id.textview_type_of_study); //??????????
        TextView dateOfStatement = rowView.findViewById(R.id.date_of_statement); //22.04.2022

        Spinner spinnerFinancing = rowView.findViewById(R.id.spinner_financing);
        Spinner spinnerPriority = rowView.findViewById(R.id.spinner_priority);

        name.setOnClickListener(view-> {
            name.setEnabled(false);
            new Handler().postDelayed(() -> name.setEnabled(true),2000); //?????????? 2-?? ???????? ?????????? ???????????????? ?? ?????????????????? ?????????? 2 ????????
            startActivity(new Intent(binding.getContext(), MoreAboutTheSpecialityActivity.class)
                    .putExtra("id", idSpeciality)
                    .putExtra("type_of_study", nameTypeOfStudy));
        });

        if(nameInstitut.equals("null"))
            mainLayout.removeView(institute);
        else {
            institute.setText(nameInstitut);
            institute.setPaintFlags(institute.getPaintFlags() | Paint.UNDERLINE_TEXT_FLAG);
            institute.setOnClickListener(view -> {
                institute.setEnabled(false);
                new Handler().postDelayed(() -> institute.setEnabled(true),2000);
                startActivity(new Intent(binding.getContext(), MoreAboutTheInstitutActivity.class)
                        .putExtra("name_institut", nameInstitut)
                        .putExtra("id", PersonalCabinetActivity.instituts.get(nameInstitut)));
            });
        }

        spinnerFinancing.setAdapter(new ArrayAdapter<>(getActivity(), android.R.layout.simple_spinner_dropdown_item, PersonalCabinetActivity.listFinancing));
        spinnerFinancing.setSelection(valueFinancing != null && !valueFinancing.equals("null") ? Integer.parseInt(valueFinancing) - 1: 0);

        spinnerPriority.setSelection(valuePriority != null && !valuePriority.equals("null")? Integer.parseInt(valuePriority) - 1 : 0);

        name.setText(String.format("%s %s", idSpeciality, nameSpeciality));

        typeOfStudy.setText(nameTypeOfStudy);
        dateOfStatement.setText(valueOfDateOfStatement != null && !valueOfDateOfStatement.equals("null")? valueOfDateOfStatement :  "-");
        linearLayout.addView(rowView);
    }

    @Override
    public void onResume(){
        linearLayout.removeAllViews();
        fillSpeciality();
        super.onResume();
    }

    public static void clearData(){
        if(specialitysAbit != null) specialitysAbit.clear();
        specialitysAbit = null;
        if(typeOfStudy != null) typeOfStudy.clear();
        typeOfStudy = null;
    }

    private void fillSpeciality(){
        if(specialitysAbit == null) return;
        new Thread(()->{
            //?????????????????? ???? ????????????????????
            specialitysAbit.sort(Comparator.comparing(
                    map -> Integer.parseInt(map.get("priority") == null || Objects.equals(map.get("priority"), "null") ? "0" : map.get("priority"))));
            new Handler(Looper.getMainLooper()).post(() -> {
                for (int i = 0; i < specialitysAbit.size(); ++i)
                    onAddField(specialitysAbit.get(i).get("id_spec"), specialitysAbit.get(i).get("name_spec"),
                            specialitysAbit.get(i).get("institution"), specialitysAbit.get(i).get("typeOfStudy"),
                            specialitysAbit.get(i).get("date_filing"), specialitysAbit.get(i).get("id_financing"),
                            specialitysAbit.get(i).get("priority"));
                buttonSubmitStatement.setEnabled(true);
            });
        }).start();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    private void onClick(View view) {
        if (linearLayout.getChildCount() == 0 && supportTextView == null) {
            supportTextView = new TextView(getActivity());
            supportTextView.setText("???????????????????? ?????????????? ???? ?????????????? \"?????????????????????? ????????????????????\" ?? ?????????????? ??????????????????????");
            supportTextView.setTypeface(supportTextView.getTypeface(), Typeface.BOLD);
            supportTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
            supportTextView.setGravity(Gravity.CENTER);
            supportTextView.setTextColor(Color.RED);
            linearLayout.addView(supportTextView);
        } else if (isValidPriority()) {
            supportTextView = null;
            buttonSubmitStatement.setEnabled(false);

            List<JSONObject> res = new ArrayList<>();
            for (int i = 0; i < linearLayout.getChildCount(); ++i) {
                TextView name = linearLayout.getChildAt(i).findViewById(R.id.textview_speciality);
                TextView typeOfStudy = linearLayout.getChildAt(i).findViewById(R.id.textview_type_of_study);
                Spinner spinnerPriority = linearLayout.getChildAt(i).findViewById(R.id.spinner_priority);
                Spinner spinnerFinancing = linearLayout.getChildAt(i).findViewById(R.id.spinner_financing);

                try {
                    res.add(new JSONObject()
                            .put("id_abit", PersonalCabinetActivity.idAbit)
                            .put("id_spec", name.getText().toString().split(" ")[0])
                            .put("type_of_study", PersonalCabinetActivity.typeOfStudy.get(typeOfStudy.getText().toString()))
                            .put("priority", (spinnerPriority.getSelectedItemId() + 1))
                            .put("id_financing", (spinnerFinancing.getSelectedItemId() + 1))
                            .put("date_filing", new SimpleDateFormat("dd-MM-yyyy").format(new Date())));
                    specialitysAbit.get(i).put("date_filing", new SimpleDateFormat("dd-MM-yyyy").format(new Date()));
                    specialitysAbit.get(i).put("id_financing", String.valueOf((spinnerFinancing.getSelectedItemId() + 1)));
                    specialitysAbit.get(i).put("priority", String.valueOf((spinnerPriority.getSelectedItemId() + 1)));
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
                            buttonSubmitStatement.setEnabled(true);
                            onResume();
                        }

                        @Override
                        public void onError(ANError anError) {
                            Log.e("", "?????? ?????????? ???? ??????");
                        }
                    });

            ShowToast.show(getActivity(), "??????????????");
        }
    }
}