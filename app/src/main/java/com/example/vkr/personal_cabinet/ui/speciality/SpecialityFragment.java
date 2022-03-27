package com.example.vkr.personal_cabinet.ui.speciality;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.idEducation;
import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.sendSpeciality;
import static java.util.Arrays.asList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;

import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.utils.dialogs.ShowToast;
import com.google.android.material.snackbar.Snackbar;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.example.vkr.R;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.personal_cabinet.moreAbout.MoreAboutTheInstitutActivity;
import com.example.vkr.personal_cabinet.moreAbout.MoreAboutTheSpecialityActivity;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class SpecialityFragment extends Fragment {

    private View binding;
    private LinearLayout specialityLayout;
    private ScrollView scrollView;

    private static List<List<String>> speciality = new ArrayList<>(); //инициализация;
    private static Integer start = 0;
    private static Integer end = 26;
    private final Integer next = 26;

    private static Integer countSpeciality = 0;

    private ProgressBar progressBar;

    private static boolean isBottom = false; // дошли до конца
    private static boolean isAllSpecialityDownload = false;

    private static Integer scrollY = 0;

    private int countTry = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        PersonalCabinetActivity.selectedPage = 3;
        binding = inflater.inflate(R.layout.fragment_speciality, container, false);
        initComponents();
        applyEvents();
        return binding.getRootView();
    }

    private void onAddField(String idSpeciality, String nameSpeciality,
                            String nameInstitut, String nameTypeOfStudy,
                            String valueGeneralCompetition, String valueContract) {
        if(getActivity() == null) return;
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView = inflater.inflate(R.layout.field_for_speciality, null);

        TextView name = rowView.findViewById(R.id.textview_speciality); //02.03.03 МОАИС
        TextView institut = rowView.findViewById(R.id.textview_institut); //Естественнонаучный институт
        TextView typeOfStudy = rowView.findViewById(R.id.textview_type_of_study); //Очная
        TextView generalCompetition = rowView.findViewById(R.id.textview_general_competition); // 253 / 25
        TextView contract = rowView.findViewById(R.id.textview_сontract); // 123 / 75

        LinearLayout mainLayout = rowView.findViewById(R.id.main_speciality_layout);
        mainLayout.setOnClickListener(view -> {
            mainLayout.setEnabled(false);
            new Handler().postDelayed(() -> mainLayout.setEnabled(true),2000); //иначе 2-й клик будет доступен и откроется сразу 2 окна
            startActivity(new Intent(binding.getContext(), MoreAboutTheSpecialityActivity.class)
                    .putExtra("id", idSpeciality)
                    .putExtra("type_of_study", nameTypeOfStudy));
        });

        institut.setOnClickListener(view ->{
            institut.setEnabled(false);
            new Handler().postDelayed(() -> institut.setEnabled(true),2000);
            startActivity(new Intent(binding.getContext(), MoreAboutTheInstitutActivity.class)
                    .putExtra("name_institut", nameInstitut)
                    .putExtra("id", PersonalCabinetActivity.instituts.get(nameInstitut)));
        });


        name.setText(String.format("%s %s", idSpeciality, nameSpeciality));
        institut.setText(nameInstitut.equals("null")? "" : nameInstitut);
        typeOfStudy.setText(nameTypeOfStudy);
        generalCompetition.setText(valueGeneralCompetition);
        contract.setText(valueContract);

        specialityLayout.addView(rowView);
    }

    private void applyEvents(){
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            int bottom = (scrollView.getChildAt(scrollView.getChildCount() - 1)).getHeight() - scrollView.getHeight() - scrollY;
            if (bottom == 0 && !isBottom) {
                if (!isAllSpecialityDownload) {
                    isBottom = true;
                    downloadSpeciality();
                } else
                    Snackbar.make(scrollView, "Все специальности были загружены", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
            }
            else{
                isBottom = false;
                SpecialityFragment.scrollY = scrollView.getScrollY();
            }
        });
    }

    private void initComponents() {
        scrollView = binding.findViewById(R.id.scrollview_speciality_fragment);
        specialityLayout = binding.findViewById(R.id.layout_speciality);

        scrollView = binding.findViewById(R.id.scrollview_speciality_fragment);

        if(speciality.size() == 0)  //первый раз зашли сюда
            downloadSpeciality(); //подгружаем
        else {
            for(int i = 0; i < speciality.size(); i++) //уже были данные
                onAddField(speciality.get(i).get(0), speciality.get(i).get(1),
                        speciality.get(i).get(2), speciality.get(i).get(3),
                        speciality.get(i).get(4), speciality.get(i).get(5));
        }

        scrollView.post(() -> scrollView.scrollTo(0, scrollY)); //возвращаем предыдущую позицию в scrollView
    }

    public static void clearTable() {
        speciality.clear();
        start = 0;
        end = 26;
        countSpeciality = 0;
        isBottom = false;
        isAllSpecialityDownload = false;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        sendSpeciality();
        binding = null;
    }

    private void downloadSpeciality(){
        if(isAllSpecialityDownload) return;
        if(countSpeciality != 0 && countSpeciality == speciality.size()){
            isAllSpecialityDownload = true;
            return;
        }
        String url;
        if(Integer.parseInt(idEducation) < 5)
            url = "https://vkr1-app.herokuapp.com/speciality/abit/min";
        else if(Integer.parseInt(idEducation) < 7)
            url = "https://vkr1-app.herokuapp.com/speciality/magistr/min";
        else
            url = "https://vkr1-app.herokuapp.com/speciality/aspirant/min";

        if(progressBar == null) {
            progressBar = new ProgressBar(getContext());
            progressBar.setPadding(0,30,0, 0);
            specialityLayout.addView(progressBar);
        }

        AndroidNetworking.get(url + "?start=" + start.toString() + "&next=" + end.toString())
                .setPriority(Priority.IMMEDIATE)
                .setOkHttpClient(new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        specialityLayout.removeView(progressBar);
                        progressBar = null;
                        try {
                            JsonNode jsonNode = new ObjectMapper().readTree(response.toString());

                            if(speciality.size() != 0)
                                for(JsonNode item : jsonNode.get("speciality")) {
                                    Log.e("item", item.get("specialityMinInfo").get("id").asText() + " " + item.get("typeOfStudy").asText());
                                    Log.e("speciality", speciality.get(start - 1).get(0) + " " + speciality.get(start - 1).get(3));
                                    if (item.get("specialityMinInfo").get("id").asText().equals(speciality.get(start - 1).get(0)) &&
                                            item.get("typeOfStudy").asText().equals(speciality.get(start - 1).get(3)))
                                        return;

                                }

                            countSpeciality = jsonNode.get("len").asInt();
                            jsonNode.get("speciality").forEach(item -> speciality.add(asList(
                                    item.get("specialityMinInfo").get("id").asText(),
                                    item.get("specialityMinInfo").get("name").asText(),
                                    item.get("institut").asText(),
                                    item.get("typeOfStudy").asText(),
                                    item.get("specialityMinInfo").get("budget").toString(),
                                    item.get("specialityMinInfo").get("pay").toString())));
                            for(int i = start; i < speciality.size(); i++)
                                onAddField(speciality.get(i).get(0), speciality.get(i).get(1),
                                        speciality.get(i).get(2), speciality.get(i).get(3),
                                        speciality.get(i).get(4), speciality.get(i).get(5));
                            start += next;
                            end += next;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onError(ANError anError) {
                        if (speciality.size() != countSpeciality) {
                            countTry += 1;
                            if(countTry % 8 == 0) ShowToast.show(getContext(), "Проверьте подключение к интернету");
                            new Handler().postDelayed(() -> downloadSpeciality(), 1000);
                        }
                        else  {
                            isAllSpecialityDownload = true;
                            Snackbar.make(scrollView, "Все специальности были загружены", Snackbar.LENGTH_LONG)
                                    .setAction("Action", null).show();
                        }
                    }
                });
    }
}