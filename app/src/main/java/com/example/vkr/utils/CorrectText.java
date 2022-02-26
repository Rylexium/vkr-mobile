package com.example.vkr.utils;

import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.widget.EditText;

public class CorrectText implements TextWatcher {

    private final String TAG = this.getClass().getSimpleName();

    private EditText mEditText;
    private String mPattern;

    public CorrectText(EditText editText, String pattern) {
        mEditText = editText;
        mPattern = pattern;
        //set max length of string
        int maxLength = pattern.length();
        mEditText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(maxLength)});
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        StringBuilder phone = new StringBuilder(s);
        if (count > 0 && !isValid(phone.toString())) {
            try {
                for (int i = 0; i < phone.length(); i++) {
                    char c = mPattern.charAt(i);
                    if ((c != '#') &&  c != phone.charAt(i)) {
                        phone.insert(i, c);
                    }
                }
            }
            catch(IndexOutOfBoundsException ignored){

            }

            mEditText.setText(phone);
            mEditText.setSelection(mEditText.getText().length());
        }
    }

    @Override
    public void afterTextChanged(Editable s) { }

    private boolean isValid(String phone)
    {
        for (int i = 0; i < phone.length(); i++) {
            char c = mPattern.charAt(i);

            if (c == '#') continue;

            if (c != phone.charAt(i)) {
                return false;
            }
        }

        return true;
    }
}

