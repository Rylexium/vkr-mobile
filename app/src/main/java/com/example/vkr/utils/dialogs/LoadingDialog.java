package com.example.vkr.utils.dialogs;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.KeyEvent;
import android.view.LayoutInflater;

import com.example.vkr.R;

public class LoadingDialog {
    private final Activity activity;
    private AlertDialog dialog;

    public LoadingDialog(Activity activity) {
        this.activity = activity;
    }

    public void startLoadingDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);

        LayoutInflater inflater = activity.getLayoutInflater();
        builder.setView(inflater.inflate(R.layout.loading_dialog, null));
        builder.setCancelable(false);
        builder.setOnKeyListener((DialogInterface.OnKeyListener) (dialogInterface, keyCode, keyEvent) -> {
            if (keyCode == KeyEvent.KEYCODE_BACK)
                if (dialog.isShowing()) {
                    dialog.dismiss();
                    activity.finish();
                    return true;
                }
            return false;
        });

        dialog = builder.create();
        dialog.show();
    }

    public void dismissDialog() {
       dialog.dismiss();
    }
}
