package com.example.vkr.personal_cabinet.ui.home;

import android.animation.LayoutTransition;
import androidx.lifecycle.ViewModelProvider;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import android.transition.AutoTransition;
import android.transition.TransitionManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.androidnetworking.AndroidNetworking;
import com.androidnetworking.common.Priority;
import com.androidnetworking.error.ANError;
import com.androidnetworking.interfaces.JSONObjectRequestListener;
import com.example.vkr.R;
import com.example.vkr.databinding.FragmentMainBinding;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.utils.ConvertClass;
import com.example.vkr.utils.ShowToast;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.util.Objects;

public class MainFragment extends Fragment {
    private FragmentMainBinding binding;
    private FloatingActionButton fab;

    private static String loginString;
    private static String sexString;
    private static String passportString;
    private static String nationalityString;
    private static String departamentCode;
    private static String dateOfIssingPassport;
    private static String constAddress;
    private static String actualAddress;
    private static String idEducation;
    private static String numberEducation;
    private static String regNumberEducation;
    private static String dateOfIssingEducation;
    private static String dateOfBirthday;
    private static String privilege;

    private static Bitmap bitmapPassport1, bitmapPassport2; //своеобразный кэш картинок
    private static Boolean isDownloadImagesPassport = null;

    private static Bitmap bitmapEducation1, bitmapEducation2; //своеобразный кэш картинок
    private static Boolean isDownloadImagesEducation = null;

    private static Bitmap bitmapPrivilege; //своеобразный кэш картинок
    private static Boolean isDownloadImagePrivilege = null;

    private static MainViewModel mainViewModel;

    private static Integer scrollY = 0;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMainBinding.inflate(inflater, container, false);

        mainViewModel = new ViewModelProvider(this,  new ViewModelProvider.NewInstanceFactory()).get(MainViewModel.class);
        initHomeViewModel();
        initComponents();
        ApplyEvents();
        return binding.getRoot();
    }

    public static MainViewModel getHomeViewModel(){ return mainViewModel; }

    private void setLogin(String text){
        loginString = text;
        binding.textviewLogin.setText(text);
    }
    private void setSnills(String text){
        binding.textviewSnills.setText(text);
    }
    private void setSex(String text){
        sexString = text;
        binding.textviewSex.setText(text);
    }
    private void setPassport(String text){
        passportString = text;
        binding.textviewPassport.setText(text);
    }
    private void setNationality(String text){
        nationalityString = text;
        binding.textviewNationality.setText(text);
    }
    private void setDepartamentCode(String text){
        departamentCode = text;
        binding.textviewDepartamentCode.setText(text);
    }
    private void setConstAddress(String text){
        constAddress = text;
        binding.textviewConstAddress.setText(text);
    }
    private void setActualAddress(String text){
        actualAddress = text;
        binding.textviewActualAddress.setText(text);
    }
    private void setIdEducation(String text){
        idEducation = text;
        binding.textviewIdEducation.setText(text);
    }
    private void setNumberEducation(String text){
        numberEducation = text;
        binding.textviewNumberEducation.setText(text);
    }
    private void setRegNumberEducation(String text){
        regNumberEducation = text;
        binding.textviewRegNumberEducation.setText(text);
    }
    private void setDateOfIssingEducation(String text){
        dateOfIssingEducation = text;
        binding.textviewDateOfIssingEducation.setText(text);
    }
    private void setDateOfIssingPassport(String text){
        dateOfIssingPassport = text;
        binding.textviewDateOfIssingPassport.setText(text);
    }
    private void setDateOfBirthday(String text){
        dateOfBirthday = text;
        binding.textviewDateOfBirthday.setText(text);
    }
    private void setPrivilege(String text){
        privilege = text;
        binding.textviewPrivilege.setText(text);
    }

    private void ApplyEvents() {
        binding.buttonGetImagesPassport.setOnClickListener(view -> {
            if(isDownloadImagesPassport == null) downloadImagesPassport();
            else {
                if (isDownloadImagesPassport) setImages(bitmapPassport1, bitmapPassport2, binding.layoutForPagesPassport);
                else{
                    binding.scrollviewHomeFragment.scrollTo(0, binding.textviewDateOfIssingPassport.getScrollY());
                    removeAllItem(binding.layoutForPagesPassport);
                }
                isDownloadImagesPassport = !isDownloadImagesPassport;
            }

        });
        binding.buttonGetImagesEducation.setOnClickListener(view -> {
            if(isDownloadImagesEducation == null) downloadImagesEducation();
            else {
                if(isDownloadImagesEducation) setImages(bitmapEducation1, bitmapEducation2, binding.layoutForPagesEducation);
                else removeAllItem(binding.layoutForPagesEducation);
                isDownloadImagesEducation = !isDownloadImagesEducation;
            }
        });
        binding.buttonGetImagePrivilege.setOnClickListener(view -> {
            if(isDownloadImagePrivilege == null) downloadImagePrivilege();
            else {
                if(isDownloadImagePrivilege) setImages(bitmapPrivilege, null, binding.layoutForPagePrivilege);
                else removeAllItem(binding.layoutForPagePrivilege);
                isDownloadImagePrivilege = !isDownloadImagePrivilege;
            }
        });


        binding.scrollviewHomeFragment.setOnScrollChangeListener((v, scrollX, scrollY, oldScrollX, oldScrollY) -> {
            //work with fab
            if (scrollY == 0 || (scrollY < oldScrollY && !fab.isShown()))
                fab.show();
            else if (scrollY > oldScrollY && fab.isShown())
                fab.hide();
        });
    }

    private void downloadImagesPassport(){
        binding.buttonGetImagesPassport.setEnabled(false);
        String previousText = (String) binding.buttonGetImagesPassport.getText();
        binding.buttonGetImagesPassport.setText("Загрузка фото...");
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/passport?id=" + PersonalCabinetActivity.idAbit)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new Thread(()->{
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                bitmapPassport1 = ConvertClass.convertStringToBitmap(jsonNode.get("passport1").asText());
                                bitmapPassport2 = ConvertClass.convertStringToBitmap(jsonNode.get("passport2").asText());

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    setImages(bitmapPassport1, bitmapPassport2, binding.layoutForPagesPassport);
                                    isDownloadImagesPassport = false; //данные отобразились, поэтому след состояние будет не загружено на форму
                                });
                            } catch (Exception e) {
                                Log.e("", e.getMessage());
                                new Handler(Looper.getMainLooper()).post(()-> ShowToast.show(binding.getRoot().getContext(), "Изображений нет"));
                            }
                            new Handler(Looper.getMainLooper()).post(()-> {
                                binding.buttonGetImagesPassport.setEnabled(true); //данные загрузились кнопка включена
                                binding.buttonGetImagesPassport.setText(previousText);
                            });
                        }).start();
                    }
                    @Override
                    public void onError(ANError error) {
                        ShowToast.show(binding.getRoot().getContext(), "Изображений нет");
                        binding.buttonGetImagesPassport.setEnabled(false);
                        binding.buttonGetImagesPassport.setText(previousText);
                    }
                });
    }
    private void downloadImagesEducation(){
        binding.buttonGetImagesEducation.setEnabled(false);
        String previousText = (String) binding.buttonGetImagesEducation.getText();
        binding.buttonGetImagesEducation.setText("Загрузка фото...");
        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/education?id=" + PersonalCabinetActivity.idAbit)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new Thread(()->{
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                bitmapEducation1 = ConvertClass.convertStringToBitmap(jsonNode.get("education1").asText());
                                bitmapEducation2 = ConvertClass.convertStringToBitmap(jsonNode.get("education2").asText());

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    setImages(bitmapEducation1, bitmapEducation1, binding.layoutForPagesEducation);
                                    isDownloadImagesEducation = false; //данные отобразились, поэтому след состояние будет не загружено на форму
                                });
                            } catch (Exception e) {
                                Log.e("", e.getMessage());
                                new Handler(Looper.getMainLooper()).post(()-> ShowToast.show(binding.getRoot().getContext(), "Изображений нет"));
                            }
                            new Handler(Looper.getMainLooper()).post(()-> {
                                binding.buttonGetImagesEducation.setEnabled(true);
                                binding.buttonGetImagesEducation.setText(previousText);
                            });
                        }).start();

                    }
                    @Override
                    public void onError(ANError error) {
                        ShowToast.show(binding.getRoot().getContext(), "Изображений нет");
                        binding.buttonGetImagesEducation.setEnabled(true); //данные загрузились кнопка включена
                        binding.buttonGetImagesEducation.setText(previousText);
                    }
                });
    }
    private void downloadImagePrivilege(){
        binding.buttonGetImagePrivilege.setEnabled(false);
        String previousText = (String) binding.buttonGetImagePrivilege.getText();
        binding.buttonGetImagePrivilege.setText("Загрузка фото...");

        AndroidNetworking.get("https://vkr1-app.herokuapp.com/abit/privileges?id=" + PersonalCabinetActivity.idAbit)
                .setPriority(Priority.HIGH)
                .build()
                .getAsJSONObject(new JSONObjectRequestListener() {
                    @Override
                    public void onResponse(JSONObject response) {
                        new Thread(()->{
                            try {
                                JsonNode jsonNode = new ObjectMapper().readTree(response.toString());
                                bitmapPrivilege = ConvertClass.convertStringToBitmap(jsonNode.get("privileges").asText());

                                new Handler(Looper.getMainLooper()).post(() -> {
                                    setImages(bitmapPrivilege, null, binding.layoutForPagePrivilege);
                                    isDownloadImagePrivilege = false; //данные отобразились, поэтому след состояние будет не загружено на форму
                                });
                            } catch (Exception e) {
                                new Handler(Looper.getMainLooper()).post(()-> ShowToast.show(binding.getRoot().getContext(), "Изображений нет"));
                            }
                            new Handler(Looper.getMainLooper()).post(()-> {
                                binding.buttonGetImagePrivilege.setEnabled(true); //данные загрузились кнопка включена
                                binding.buttonGetImagePrivilege.setText(previousText);
                            });
                        }).start();

                    }
                    @Override
                    public void onError(ANError error) {
                        ShowToast.show(binding.getRoot().getContext(), "Изображений нет");
                        binding.buttonGetImagePrivilege.setEnabled(true); //данные загрузились кнопка включена
                        binding.buttonGetImagePrivilege.setText(previousText);
                    }
                });
    }

    private void setImages(Bitmap bitmap1, Bitmap bitmap2, LinearLayout linearLayout){
        TransitionManager.beginDelayedTransition(binding.fragmentMainMainLayout, new AutoTransition());
        binding.fragmentMainMainLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        TransitionManager.beginDelayedTransition(linearLayout, new AutoTransition());
        linearLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        LayoutInflater inflater = (LayoutInflater) Objects.requireNonNull(getActivity()).getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View rowView1, rowView2;
        if(bitmap1 == null && bitmap2 == null){
            ShowToast.show(getContext(), "Изображений нет");
            return;
        }
        if(bitmap1 != null) {
            rowView1 = inflater.inflate(R.layout.field_for_image, null);
            ImageView image1 = rowView1.findViewById(R.id.field_image);
            image1.setImageBitmap(bitmap1);
            image1.setMinimumHeight(1000);
            image1.setMinimumWidth(1000);
            linearLayout.addView(rowView1, 0);
        }
        if(bitmap2 != null) {
            rowView2 = inflater.inflate(R.layout.field_for_image, null);
            ImageView image2 = rowView2.findViewById(R.id.field_image);
            image2.setImageBitmap(bitmap2);
            image2.setMinimumHeight(1000);
            image2.setMinimumWidth(1000);
            linearLayout.addView(rowView2, 1);
        }
    }

    private void removeAllItem(LinearLayout linearLayout){
        TransitionManager.beginDelayedTransition(binding.fragmentMainMainLayout, new AutoTransition());
        binding.fragmentMainMainLayout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        TransitionManager.beginDelayedTransition(binding.layoutForPagesEducation, new AutoTransition());
        binding.layoutForPagesEducation.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        while(linearLayout.getChildCount() != 1){
            linearLayout.removeViewAt(0);
        }
    }

    private void initHomeViewModel(){
        mainViewModel.getTextLogin().observe(getViewLifecycleOwner(), this::setLogin);
        mainViewModel.getTextSex().observe(getViewLifecycleOwner(), this::setSex);
        mainViewModel.getTextSnills().observe(getViewLifecycleOwner(), this::setSnills);
        mainViewModel.getTextNationality().observe(getViewLifecycleOwner(), this::setNationality);
        mainViewModel.getTextPassport().observe(getViewLifecycleOwner(), this::setPassport);
        mainViewModel.getDepartamentCode().observe(getViewLifecycleOwner(), this::setDepartamentCode);
        mainViewModel.getDateOfIssingPassport().observe(getViewLifecycleOwner(), this::setDateOfIssingPassport);
        mainViewModel.getConstAddress().observe(getViewLifecycleOwner(), this::setConstAddress);
        mainViewModel.getActualAddress().observe(getViewLifecycleOwner(), this::setActualAddress);
        mainViewModel.getIdEducation().observe(getViewLifecycleOwner(), this::setIdEducation);
        mainViewModel.getNumberEducation().observe(getViewLifecycleOwner(), this::setNumberEducation);
        mainViewModel.getRegNumberEducation().observe(getViewLifecycleOwner(), this::setRegNumberEducation);
        mainViewModel.getDateOfIssingEducation().observe(getViewLifecycleOwner(), this::setDateOfIssingEducation);
        mainViewModel.getDateOfBirthday().observe(getViewLifecycleOwner(), this::setDateOfBirthday);
        mainViewModel.getPrivilege().observe(getViewLifecycleOwner(), this::setPrivilege);
    }

    private void initComponents() {
        binding.textviewLogin.setText(loginString);
        binding.textviewSnills.setText(PersonalCabinetActivity.idAbit);
        binding.textviewSex.setText(sexString);
        binding.textviewNationality.setText(nationalityString);
        binding.textviewPassport.setText(passportString);
        binding.textviewDepartamentCode.setText(departamentCode);
        binding.textviewDateOfIssingPassport.setText(dateOfIssingPassport);
        binding.textviewConstAddress.setText(constAddress);
        binding.textviewActualAddress.setText(actualAddress);
        binding.textviewIdEducation.setText(idEducation);
        binding.textviewNumberEducation.setText(numberEducation);
        binding.textviewRegNumberEducation.setText(regNumberEducation);
        binding.textviewDateOfIssingEducation.setText(dateOfIssingEducation);
        binding.textviewDateOfBirthday.setText(dateOfBirthday);
        binding.textviewPrivilege.setText(privilege);
        if(isDownloadImagesPassport != null) isDownloadImagesPassport = true;
        if(isDownloadImagesEducation != null) isDownloadImagesEducation = true;
        binding.scrollviewHomeFragment.post(()->binding.scrollviewHomeFragment.scrollTo(0, scrollY));
        fab = Objects.requireNonNull(getActivity()).findViewById(R.id.fab);
    }
    public static void clearData() {
        loginString = null;
        sexString = null;
        passportString = null;
        nationalityString = null;
        departamentCode = null;
        dateOfIssingPassport = null;
        constAddress = null;
        actualAddress = null;
        idEducation = null;
        numberEducation = null;
        regNumberEducation = null;
        dateOfIssingEducation = null;
        dateOfBirthday = null;
        privilege = null;

        bitmapPassport1 = null;
        bitmapPassport2 = null;
        isDownloadImagesPassport = null;

        bitmapEducation1 = null;
        bitmapEducation2 = null;
        isDownloadImagesEducation = null;

        isDownloadImagePrivilege = null;
        bitmapPrivilege = null;
    }
    @Override
    public void onDestroyView() {
        MainFragment.scrollY = binding.scrollviewHomeFragment.getScrollY();
        super.onDestroyView();
        binding = null;
    }

}