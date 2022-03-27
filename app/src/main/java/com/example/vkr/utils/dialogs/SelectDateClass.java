package com.example.vkr.utils.dialogs;

import android.app.DatePickerDialog;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.DatePicker;
import android.widget.EditText;

import com.example.vkr.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Locale;

public class SelectDateClass {
    private static DatePickerDialog datePickerDialog(AppCompatActivity activity, EditText editDate){
        if(editDate == null || activity == null) return null;

        Calendar calendar = new GregorianCalendar();
        Date date = new Date();
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd.MM.yyyy", Locale.getDefault());
        try {
            date = simpleDateFormat.parse(editDate.getText().toString()); // parse the String according to the desired format
        } catch (ParseException ignored) { }

        if(date != null) calendar.setTime(date);

        return new DatePickerDialog(
                activity,
                R.style.datePickerDialogTheme,
                new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                        editDate.setText(new String(
                                        (day<9 ? "0" + day : day) + "." +
                                              (month<9 ? "0" + (month + 1): month + 1 ) + "." + year));
                    }
                },
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH));
    }

    public static void showDatePickerDialogForBirthday(AppCompatActivity activity, EditText editDate) {
        DatePickerDialog dialog = datePickerDialog(activity, editDate);
        dialog.getDatePicker().setMinDate((long) (System.currentTimeMillis() - 3.154e+12)); //сегодня - 100 лет
        dialog.getDatePicker().setMaxDate((long) (System.currentTimeMillis() - 4.73e+11));  //сегодня - 15 лет
        dialog.show();
    }
    public static void showDatePickerDialogForDateIssuing(AppCompatActivity activity, EditText editDate) {
        DatePickerDialog dialog = datePickerDialog(activity, editDate);
        dialog.getDatePicker().setMinDate((long) (System.currentTimeMillis() - 1.734e+12)); //сегодня - 55 лет
        dialog.getDatePicker().setMaxDate((long)  System.currentTimeMillis());  //сегодня - 15 лет
        dialog.show();
    }
}
