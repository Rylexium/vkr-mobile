package com.example.vkr.personal_cabinet.ui.result_egu;

import static java.util.Arrays.asList;

import androidx.lifecycle.ViewModel;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONArrayRequestListener;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
public class ResultEguViewModel extends ViewModel {
    public static List<List<String>> exams;
    public static Map<String, String> minPointsExams;

    public ResultEguViewModel() {
        downloadExams();
        downloadMinPointsExams();
    }


    public List<List<String>> getExams(){
        return exams;
    }

    public static void clearExams(){
        if(exams == null) return;
        exams.clear();
        exams = null;
    }

    public Map<String, String> getMinPointsExams(){
        return minPointsExams;
    }

    private void downloadExams(){
        if(exams == null) {
            AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/exams?id_abit=" + PersonalCabinetActivity.idAbit)
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            exams = new ArrayList<>();
                            new Thread(() -> {
                                try {
                                    JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                    for (int i = jsonNode.size() - 1; i >= 0; --i) {
                                        exams.add(asList(jsonNode.get(i).get("exams").get("name").toString().replace("\"", ""),
                                                jsonNode.get(i).get("abitExams").get("points").toString(),
                                                jsonNode.get(i).get("abitExams").get("year").toString()));
                                    }

                                } catch (JsonProcessingException e) {
                                    e.printStackTrace();
                                }
                            }).start();
                        }

                        @Override
                        public void onError(ANError error) { }
                    });
        }
    }

    private void downloadMinPointsExams(){
        if(minPointsExams == null) {
            AndroidNetworking.get("https://vkr1-app.herokuapp.com/speciality_exams/min")
                    .setPriority(Priority.HIGH)
                    .build()
                    .getAsJSONArray(new JSONArrayRequestListener() {
                        @Override
                        public void onResponse(JSONArray response) {
                            minPointsExams = new HashMap<>();
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
                        }
                    });
        }
    }

}
