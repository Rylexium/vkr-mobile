package com.example.vkr.utils.dialogs;

import android.content.Context;

import com.example.vkr.R;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class ShowToast {

    public static void show(Context mcontext, String text) {
        StyleableToast.makeText(mcontext, text, R.style.CustomToast).show();
    }
}