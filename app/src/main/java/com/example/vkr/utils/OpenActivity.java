package com.example.vkr.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;

import com.example.vkr.activity.authorization.QuestionsActivity;
import com.example.vkr.activity.registration.ExamsResultActivity;
import com.example.vkr.activity.registration.RegistrationActivity;
import com.example.vkr.activity.support.ChangePasswordActivity;
import com.example.vkr.personal_cabinet.PersonalCabinetActivity;
import com.example.vkr.activity.support.SupportActivity;

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
        activity.startActivity(new Intent(Intent.ACTION_VIEW)
                .setData(Uri.parse("https://www.google.ru/maps/place/53%C2%B012'49.3%22N+50%C2%B010'37.3%22E/@53.2137071,50.1762502,18z/data=!3m1!4b1!4m6!3m5!1s0x0:0xeeb6557ca0cc4e6b!7e2!8m2!3d53.2137058!4d50.1770238?hl=ru")));
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


}
