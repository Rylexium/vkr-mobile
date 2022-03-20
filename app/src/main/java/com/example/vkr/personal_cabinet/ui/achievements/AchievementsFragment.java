package com.example.vkr.personal_cabinet.ui.achievements;

import static com.example.vkr.personal_cabinet.PersonalCabinetActivity.idAbit;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import androidx.annotation.NonNull;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.fragment.app.Fragment;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.utils.ConvertClass;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class AchievementsFragment extends Fragment {

    private View binding;
    private LinearLayout mainLayout;
    private TextView supportTextView;
    private static final List<String> achievements = new ArrayList<>();
    private FloatingActionButton fab;
    private ScrollView scrollView;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = inflater.inflate(R.layout.fragment_achievements, container, false);
        mainLayout = binding.findViewById(R.id.fragment_achievements_layout);
        fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
        scrollView = binding.findViewById(R.id.scrollview_achievements_fragment);

        if(achievements.isEmpty()) downloadPrivileges();
        else achievements.forEach(item -> onAddField(ConvertClass.convertStringToBitmap(item)));

        new Handler().postDelayed(()-> {
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

        applyEvents();
        return binding.getRootView();
    }


    private void applyEvents(){
        scrollView.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            if (scrollY == 0 || (scrollY < oldScrollY && !fab.isShown()))
                fab.show();
            else if (scrollY > oldScrollY && fab.isShown())
                fab.hide();
        });
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
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field_for_image, null);

        ImageView image = rowView.findViewById(R.id.field_image);
        image.setImageBitmap(v);

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