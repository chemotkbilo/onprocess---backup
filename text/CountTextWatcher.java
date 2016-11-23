package com.ideationdesignservices.txtbook.text;

import android.text.Editable;
import android.text.TextWatcher;
import android.widget.TextView;

public class CountTextWatcher implements TextWatcher {
    public TextView countView;
    public int maxLength;

    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    public void onTextChanged(CharSequence s, int start, int before, int count) {
        this.countView.setText(String.valueOf(this.maxLength - s.length()));
    }

    public void afterTextChanged(Editable s) {
    }
}
