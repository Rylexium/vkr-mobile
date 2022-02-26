package com.example.vkr.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.vkr.R;

public class EditLinearLayout {

    public static int dpToPx(int dp, Context context) {
        float density = context.getResources().getDisplayMetrics().density;
        return Math.round((float) dp * density);
    }
    public static void onAddField(Bitmap v, LinearLayout linearLayout, Activity activity) {
        LayoutInflater inflater= (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        final View rowView=inflater.inflate(R.layout.field, null);
        ImageView view = rowView.findViewById(R.id.image_edit);
        newSize(view, activity);
        view.setImageBitmap(v);
        Button deleteButton= rowView.findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(view1 -> linearLayout.removeView(rowView));
        linearLayout.addView(rowView);
    }

    public static void newSize(ImageView imageView, Activity activity){
        imageView.getLayoutParams().width = EditLinearLayout.dpToPx(580, activity);//580
        imageView.getLayoutParams().height = EditLinearLayout.dpToPx(300, activity);//300
    }

}
