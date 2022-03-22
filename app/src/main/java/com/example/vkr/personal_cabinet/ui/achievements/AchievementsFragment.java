package com.example.vkr.personal_cabinet.ui.achievements;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.idAbit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.utils.AnimationHideFab;
import com.example.vkr.utils.ShowToast;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.utils.ConvertClass;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;

public class AchievementsFragment extends Fragment {

    private View binding;
    private LinearLayout mainLayout;
    private static final List<String> achievements = new ArrayList<>();

    private float mTouchPosition;
    private float mReleasePosition;

    private int countTry = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_achievements, container, false);
        mainLayout = binding.findViewById(R.id.fragment_achievements_layout);

        if(achievements.isEmpty()) downloadPrivileges();
        else achievements.forEach(item -> onAddField(ConvertClass.convertStringToBitmap(item)));

        applyEvents();
        return binding.getRootView();
    }


    private void applyEvents(){
        binding.getRootView().setOnTouchListener((view, event) -> {
            if (event.getAction() == MotionEvent.ACTION_DOWN) {
                mTouchPosition = event.getY();
            }
            if (event.getAction() == MotionEvent.ACTION_UP) {
                mReleasePosition = event.getY();

                if (mTouchPosition - mReleasePosition > 0) // user scroll down
                    AnimationHideFab.hide(PersonalCabinetActivity.fab);
                else //user scroll up
                    AnimationHideFab.show(PersonalCabinetActivity.fab);
            }
            return false;
        });
    }


    private void downloadPrivileges(){
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/achievements?id=" + idAbit)
                .setPriority(Priority.HIGH)
                .setOkHttpClient(new OkHttpClient.Builder()
                                        .connectTimeout(2, TimeUnit.SECONDS)
                                        .build())
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            new ObjectMapper().readTree(response.toString()).forEach(item -> {
                                if(!item.asText().equals("null") && !item.asText().equals("")) {
                                    achievements.add(item.asText());
                                    onAddField(ConvertClass.convertStringToBitmap(item.asText()));
                                }
                                else{
                                    ShowToast.show(getContext(), "Нет изображений с вашими достижениями");
                                    return;
                                }
                            });
                        }
                        catch(Exception ignored){ }
                    }

                    @Override
                    public void onError(ANError anError) {
                        countTry += 1;
                        if(countTry % 10 == 0) ShowToast.show(getContext(), "Не удалось загрузить изображения");
                        new Handler().postDelayed(() -> downloadPrivileges(), 1000);
                    }
                });
    }

    public void onAddField(Bitmap v) {
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field_for_image, null);

        ImageView image = rowView.findViewById(R.id.field_image);
        Glide.with(this)
                .load(v)
                .format(DecodeFormat.PREFER_RGB_565)
                .fitCenter()
                .into(image);
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