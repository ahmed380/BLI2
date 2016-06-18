package com.smehsn.compassinsurance.view.dialog;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.widget.DatePicker;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class DateDialog extends DialogFragment
        implements DatePickerDialog.OnDateSetListener {

    private OnDateSetListener listener;


    public void setOnDateSetListener(OnDateSetListener listener) {
        this.listener = listener;
    }

    public interface OnDateSetListener{
        void onDateSet(String date);
    }


    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        final Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), this, year, month, day);
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day) {
        Calendar c = Calendar.getInstance();
        c.set(year, month, day);

        SimpleDateFormat sdf = new SimpleDateFormat("EEE, dd MMM yyyy", Locale.US);
        String formattedDate = sdf.format(c.getTime());
        if(listener != null){
            listener.onDateSet(formattedDate);
        }
    }
}