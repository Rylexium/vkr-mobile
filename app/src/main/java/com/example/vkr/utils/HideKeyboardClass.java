package com.example.vkr.utils;

import android.app.Activity;
import androidx.appcompat.app.AppCompatActivity;
import android.view.inputmethod.InputMethodManager;

public class HideKeyboardClass {

    public static void hideKeyboard(AppCompatActivity activity) { //метод убирает клавиатуру
        final InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && inputMethodManager.isActive()) {
            if (activity.getCurrentFocus() != null) {
                inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
            }
        }
    }
}
