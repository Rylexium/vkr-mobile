package com.example.vkr.utils.dialogs;

import android.app.Activity;
import android.app.Dialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.vkr.R;

public class ShowBottomDialog {
    private LinearLayout firstItem, secondItem;
    private Dialog dialog;

    public ShowBottomDialog showDialog(Activity activity, String textTitle,
                                       Drawable firstIcon, String firstText,
                                       Drawable secondIcon, String secondText) {
        dialog = new Dialog(activity);

        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.bottomsheetlayout);

        TextView title = dialog.findViewById(R.id.chooseTitle);
        title.setText(textTitle);

        ImageView icon1 = dialog.findViewById(R.id.first_icon);
        icon1.setImageDrawable(firstIcon);

        ImageView icon2 = dialog.findViewById(R.id.second_icon);
        icon2.setImageDrawable(secondIcon);

        TextView text1 = dialog.findViewById(R.id.first_textView);
        text1.setText(firstText);

        TextView text2 = dialog.findViewById(R.id.second_textView);
        text2.setText(secondText);

        firstItem = dialog.findViewById(R.id.first_item);
        secondItem = dialog.findViewById(R.id.second_item);


        dialog.show();
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().getAttributes().windowAnimations = R.style.DialogAnimation;
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        return this;
    }
    public ShowBottomDialog setOnFirstItem(Runnable runnable){
        firstItem.setOnClickListener(view -> {
            dialog.dismiss();
            runnable.run();
        });
        return this;
    }

    public ShowBottomDialog setOnSecondItem(Runnable runnable){
        secondItem.setOnClickListener(view -> {
            dialog.dismiss();
            runnable.run();
        });
        return this;
    }

}
