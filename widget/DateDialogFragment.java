package com.ideationdesignservices.txtbook.widget;

import android.app.DatePickerDialog;
import android.app.DatePickerDialog.OnDateSetListener;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.Context;
import android.os.Bundle;
import android.widget.DatePicker;
import java.util.Calendar;

public class DateDialogFragment extends DialogFragment {
    private Context mContext;
    private OnDateSetListener mDateSetListener = new OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            DateDialogFragment.this.mYear = year;
            DateDialogFragment.this.mMonth = monthOfYear;
            DateDialogFragment.this.mDay = dayOfMonth;
            DateDialogFragment.this.mListener.updateChangedDate(year, monthOfYear, dayOfMonth);
        }
    };
    private int mDay;
    private long mEndDate;
    private DateDialogFragmentListener mListener;
    private int mMonth;
    private long mStartDate;
    private String mTitle;
    private int mYear;

    public static DateDialogFragment newInstance(Context context, DateDialogFragmentListener listener, Calendar curCal, long startDate, long endDate, String title) {
        DateDialogFragment dialog = new DateDialogFragment();
        dialog.mContext = context;
        dialog.mListener = listener;
        Calendar startCal = Calendar.getInstance();
        startCal.setTimeInMillis(startDate);
        startCal.set(10, 0);
        startCal.set(12, 0);
        startCal.set(13, 0);
        startCal.set(14, 0);
        startCal.set(9, 0);
        dialog.mStartDate = startCal.getTimeInMillis();
        Calendar endCal = Calendar.getInstance();
        endCal.setTimeInMillis(endDate);
        endCal.set(10, 11);
        endCal.set(12, 59);
        endCal.set(13, 59);
        endCal.set(14, 999);
        endCal.set(9, 1);
        dialog.mEndDate = endCal.getTimeInMillis();
        if (curCal == null) {
            curCal = startCal;
        }
        dialog.mYear = curCal.get(1);
        dialog.mMonth = curCal.get(2);
        dialog.mDay = curCal.get(5);
        dialog.mTitle = title;
        return dialog;
    }

    public Dialog onCreateDialog(Bundle savedInstanceState) {
        DatePickerDialog dialog = new DatePickerDialog(this.mContext, this.mDateSetListener, this.mYear, this.mMonth, this.mDay);
        dialog.setTitle(this.mTitle);
        DatePicker picker = dialog.getDatePicker();
        if (this.mEndDate >= 0) {
            picker.setMaxDate(this.mEndDate);
        }
        if (this.mStartDate >= 0) {
            picker.setMinDate(this.mStartDate);
        }
        return dialog;
    }
}
