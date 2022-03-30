package com.example.vkr.utils.dialogs;

import android.content.Context;
import android.graphics.PorterDuff;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.ContextCompat;

import com.example.vkr.R;

public class ShowToast {
    private static Toast toast;
    public static void show(Context mcontext, String text) {
        if(toast != null)
            toast.cancel();
        toast = Toast.makeText(mcontext, text, Toast.LENGTH_SHORT);
        View view = toast.getView();
        view.getBackground().setColorFilter(ContextCompat.getColor(mcontext, R.color.black_grey), PorterDuff.Mode.SRC_IN);
        TextView textView = view.findViewById(android.R.id.message);
        textView.setTextColor(ContextCompat.getColor(mcontext, R.color.white));
        toast.show();
    }
}