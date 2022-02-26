package com.example.vkr.utils;

import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class MySpinnerAdapter extends ArrayAdapter {

    public MySpinnerAdapter(@NonNull Context context, int textViewResourceId, @NonNull List objects) {
        super(context, textViewResourceId, objects);
    }

    @Override
    public boolean isEnabled(int position) { // Отключаем первый итем у спиннера и делаем его как hint
        return position != 0;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                @NonNull ViewGroup parent) {
        View view = super.getDropDownView(position, convertView, parent);
        TextView tv = (TextView) view;
        tv.setTextColor(position == 0? Color.GRAY : Color.BLACK);

        return view;
    }
}
