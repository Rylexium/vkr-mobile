package com.example.vkr.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.example.vkr.R;
import com.example.vkr.activity.admission.AdmissionActivity;
import com.example.vkr.activity.authorization.QuestionsActivity;
import com.example.vkr.activity.maps.GoogleMapsActivity;
import com.example.vkr.activity.maps.YandexMapsActivity;
import com.example.vkr.activity.registration.ExamsResultActivity;
import com.example.vkr.activity.registration.RegistrationActivity;
import com.example.vkr.activity.support.ChangePasswordActivity;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.activity.support.SupportActivity;
import com.example.vkr.personal_cabinet.ui.statement.ViewPdfActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;

import java.io.File;

public class OpenActivity {

    public static boolean openPageDeveloper(Activity activity){
        activity.startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://vk.com/rylexium")));
        return true;
    }

    public static boolean openPageWithQuestion(Activity activity){
        activity.startActivity(new Intent(activity, QuestionsActivity.class));
        return true;
    }

    public static boolean openMapsWhereWe(Activity activity){
        GoogleApiAvailability googleApiAvailability = GoogleApiAvailability.getInstance();
        int resultCode = googleApiAvailability.isGooglePlayServicesAvailable(activity);
        if(resultCode == ConnectionResult.SUCCESS)
            activity.startActivity(new Intent(activity, GoogleMapsActivity.class));
        else
            new ShowBottomDialog().showDialog(activity, "Что открыть?",
                    activity.getDrawable(R.drawable.ic_baseline_map_24), "Яндекс.Карты",
                    activity.getDrawable(R.drawable.ic_baseline_open_in_browser_24), "Браузер")
                    .setOnFirstItem(() -> activity.startActivity(new Intent(activity, YandexMapsActivity.class)))
                    .setOnSecondItem(() -> activity.startActivity(new Intent(Intent.ACTION_VIEW)
                            .setData(Uri.parse("https://www.google.ru/maps/place/53%C2%B012'49.3%22N+50%C2%B010'37.3%22E/@53.2137071," +
                                    "50.1762502,18z/data=!3m1!4b1!4m6!3m5!1s0x0:0xeeb6557ca0cc4e6b!7e2!8m2!3d53.2137058!4d50.1770238?hl=ru"))));
        return true;
    }

    public static void openPersonalCabinet(Activity activity, String idAbit, String login, String idEducation){
        activity.startActivity(new Intent(activity, PersonalCabinetActivity.class)
                                                            .putExtra("id_abit", idAbit)
                                                            .putExtra("login", login)
                                                            .putExtra("id_education", idEducation));
    }

    public static void openExamsResult(Activity activity, String idAbit, String login, String idEducation){
        activity.startActivity(new Intent(activity, ExamsResultActivity.class)
                                .putExtra("id_abit", idAbit)
                                .putExtra("login", login)
                                .putExtra("id_education", idEducation));
    }

    public static void openRegistration(Activity activity){
        activity.startActivity(new Intent(activity, RegistrationActivity.class));
    }

    public static boolean openSupport(Activity activity, String login){
        activity.startActivity(new Intent(activity, SupportActivity.class)
                .putExtra("login", login));
        return true;
    }

    public static boolean openChangePassword(Activity activity, String login){
        activity.startActivity(new Intent(activity, ChangePasswordActivity.class)
                .putExtra("login", login));
        return true;
    }

    public static boolean openAdmissionSteps(Activity activity){
        activity.startActivity(new Intent(activity, AdmissionActivity.class));
        return true;
    }

    public static boolean openMissedMan(Activity activity){
        activity.startActivity(new Intent(Intent.ACTION_VIEW).setData(Uri.parse("https://vk.com/id378508088"))); //tmd63
        return true;
    }

    public static boolean openPDF(Activity activity, File file) {
        new ShowBottomDialog().showDialog(activity, "Что открыть?",
                activity.getDrawable(R.drawable.ic_baseline_screenshot_24), "В приложении",
                activity.getDrawable(R.drawable.ic_baseline_exit_to_app_24), "В проводнике")
                .setOnFirstItem(() ->{
                    ViewPdfActivity.file = file;
                    activity.startActivity(new Intent(activity, ViewPdfActivity.class));
                })
                .setOnSecondItem(() -> {
                    Intent target = new Intent(Intent.ACTION_VIEW);
                    target.setDataAndType(Uri.parse(file.getAbsolutePath()), "application/pdf");
                    target.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                    target.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                    target.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                    activity.startActivity(Intent.createChooser(target, "Open File"));
                });
        return true;
    }


}
