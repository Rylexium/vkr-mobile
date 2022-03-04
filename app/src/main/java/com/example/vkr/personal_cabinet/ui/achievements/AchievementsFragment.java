package com.example.vkr.personal_cabinet.ui.achievements;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.idAbit;
import static com.example.vkr.utils.EditLinearLayout.newSize;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.utils.ConvertClass;
import com.example.vkr.utils.ShowToast;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public class AchievementsFragment extends Fragment {

    private View binding;
    private LinearLayout mainLayout;
    private TextView supportTextView;
    private static final List<String> achievements = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_achievements, container, false);
        mainLayout = binding.findViewById(R.id.fragment_achievements_layout);

        new Thread(()->{
            if(achievements.isEmpty()) downloadPrivileges();
            else achievements.forEach(item -> onAddField(ConvertClass.convertStringToBitmap(item)));

            new Handler(Looper.getMainLooper()).postDelayed(()-> {
                if (mainLayout.getChildCount() == 0) {
                    supportTextView = new TextView(getActivity());
                    supportTextView.setText("Нет фотографий с достижениями");
                    supportTextView.setTypeface(supportTextView.getTypeface(), Typeface.BOLD);
                    supportTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                    supportTextView.setGravity(Gravity.CENTER);
                    supportTextView.setTextColor(Color.RED);
                    mainLayout.addView(supportTextView);
                } else {
                    mainLayout.removeView(supportTextView);
                    supportTextView = null;
                }
            }, 1000);
        }).start();

        return binding.getRootView();
    }


    private void downloadPrivileges(){
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/achievements?id=" + idAbit)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                            jsonNode.forEach(item -> {
                                if(!item.asText().equals("null") && !item.asText().equals("")) {
                                    achievements.add(item.asText());
                                    onAddField(ConvertClass.convertStringToBitmap(item.asText()));
                                }
                            });
                        }
                        catch(Exception ignored){ }
                    }

                    @Override
                    public void onError(ANError anError) {
                        Log.e("error", "privileges");
                    }
                });
    }

    public void onAddField(Bitmap v) {
        LayoutInflater inflater= (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field, null);
        LinearLayout ll = rowView.findViewById(R.id.field_for_image);
        ll.removeView(rowView.findViewById(R.id.delete_button));

        ImageView view = rowView.findViewById(R.id.image_edit);
        newSize(view, getActivity());
        view.setImageBitmap(v);
        mainLayout.addView(rowView);
    }

    public static void clearData(){
        achievements.clear();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}