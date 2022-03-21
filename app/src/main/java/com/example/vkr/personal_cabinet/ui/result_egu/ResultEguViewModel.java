package com.example.vkr.personal_cabinet.ui.result_egu;

import static java.util.Arrays.asList;

import android.os.Handler;
import android.view.View;

import androidx.lifecycle.ViewModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.utils.ShowToast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class ResultEguViewModel extends ViewModel {
    public static List<List<String>> exams = new ArrayList<>();
    public static Map<String, String> minPointsExams = new HashMap<>();

    private int countTry1 = 0;
    private int countTry2 = 0;

    public ResultEguViewModel() {
        new Thread(this::downloadMinPointsExams).start();
        new Thread(this::downloadExams).start();
    }


    public List<List<String>> getExams(){ return exams; }

    public static void clearExams(){
        exams.clear();
    }

    public Map<String, String> getMinPointsExams(){
        return minPointsExams;
    }

    public void downloadExams(){
        if(exams.size() != 0) return;
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/exams?id_abit=" + PersonalCabinetActivity.idAbit)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(new OkHttpClient.Builder()
                        .connectTimeout(2, TimeUnit.SECONDS)
                        .build())
                .build()
                .getAsJSONArray(new JSONArrayRequestListener() {
                    @Override
                    public void onResponse(JSONArray response) {
                        if(exams.size() != 0) return;
                        try {
                            JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                            for (int i = jsonNode.size() - 1; i >= 0; --i) {
                                exams.add(asList(jsonNode.get(i).get("exams").get("name").asText(),
                                        jsonNode.get(i).get("abitExams").get("points").toString(),
                                        jsonNode.get(i).get("abitExams").get("year").toString()));
                            }

                        } catch (JsonProcessingException e) {
                            e.printStackTrace();
                        };
                    }

                    @Override
                    public void onError(ANError error) {
                        countTry1 += 1;
                        if(countTry1 % 8 == 0) ShowToast.show(PersonalCabinetActivity.fab.getContext(), "Проверьте подключение к интернету");
                        new Handler().postDelayed(() -> downloadExams(), 1000);
                    }
                });
    }

    public void downloadMinPointsExams(){
        if(minPointsExams.size() != 0) return;
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/speciality_exams/min")
            .setPriority(Priority.HIGH)
            .setOkHttpClient(new OkHttpClient.Builder()
                    .connectTimeout(2, TimeUnit.SECONDS)
                    .build())
            .build()
            .getAsJSONArray(new JSONArrayRequestListener() {
                @Override
                public void onResponse(JSONArray response) {
                    if(minPointsExams.size() != 0) return;
                    try {
                        JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                        for (int i = 0; i < jsonNode.size(); i++)
                            minPointsExams.put(jsonNode.get(i).get("name").toString().replace("\"", ""),
                                    jsonNode.get(i).get("min_points").toString());

                    } catch (JsonProcessingException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onError(ANError error) {
                    countTry2 += 1;
                    if(countTry2 % 8 == 0) ShowToast.show(PersonalCabinetActivity.fab.getContext(), "Проверьте подключение к интернету");
                    new Handler().postDelayed(() -> downloadMinPointsExams(), 1000);
                }
            });
    }

}
