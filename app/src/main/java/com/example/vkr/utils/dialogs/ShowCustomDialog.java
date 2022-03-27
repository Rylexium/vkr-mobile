package com.example.vkr.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vkr.R;

public class ShowCustomDialog {
    private Dialog dialog;
    private Button yes, no;

    public ShowCustomDialog showDialog(Activity activity, Drawable icon,
                                       String textTitle, String textBody,
                                       String positiveText, String negativeText) {
        dialog = new Dialog(activity);
        dialog.setContentView(R.layout.custom_dialog);
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(true); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.CustomDialogAnimation; //Setting the animations to dialog

        ImageView image = dialog.findViewById(R.id.custom_dialog_image);

        if(icon == null) image.setImageDrawable(activity.getDrawable(R.drawable.questionmark_115537));
        else image.setImageDrawable(icon);

        TextView title = dialog.findViewById(R.id.custom_dialog_title);
        title.setText(textTitle);

        TextView text = dialog.findViewById(R.id.custom_dialog_text);
        text.setText(textBody);

        no = dialog.findViewById(R.id.custom_dialog_no);
        yes = dialog.findViewById(R.id.custom_dialog_yes);

        no.setText(negativeText);
        yes.setText(positiveText);

        yes.setOnClickListener(view -> dialog.dismiss());
        no.setOnClickListener(view -> dialog.dismiss());

        dialog.show();

        return this;
    }

    public ShowCustomDialog setOnYes(Runnable runnable){
        yes.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler(Looper.getMainLooper()).postDelayed(runnable, 250);
        });
        return this;
    }

    public ShowCustomDialog setOnNo(Runnable runnable){
        no.setOnClickListener(view -> {
            dialog.dismiss();
            new Handler(Looper.getMainLooper()).postDelayed(runnable, 250);
        });
        return this;
    }

    public ShowCustomDialog setOnDismiss(Runnable runnable){
        dialog.setOnDismissListener(view -> new Handler(Looper.getMainLooper()).postDelayed(runnable, 250));
        return this;
    }
}
