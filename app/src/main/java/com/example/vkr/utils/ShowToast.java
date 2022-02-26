package com.example.vkr.utils;

import android.content.Context;
import android.widget.Toast;

public class ShowToast {

    private static Toast toast;

    public static void show(Context mcontext, String text) {
        if (toast != null)
            toast.cancel();
        toast = Toast.makeText(mcontext, text, Toast.LENGTH_SHORT);
        toast.show();
    }
}