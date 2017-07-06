package org.x.cassini;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.widget.DatePicker;

import java.util.Calendar;

/**
 * Created by Guo Mingxuan on 2017/7/6 0006.
 */

public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
    private OnDateSelectedListener mCallback;
    private int mTextViewId;
    private String TAG = "DatePicker";
    private boolean isDateSet = false;
    private String setDate;

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        mCallback = (OnDateSelectedListener) getActivity();
        Bundle bundle = this.getArguments();
        mTextViewId = bundle.getInt("textViewId");
        isDateSet = bundle.getBoolean("isDateSet");
        if (isDateSet) {
            setDate = bundle.getString("date");
            Log.d(TAG, "onCreateDialog: date is set to be " + setDate);
            int day = Integer.valueOf(setDate.substring(0, 2));
            int month = Integer.valueOf(setDate.substring(3, 5));
            int year = Integer.valueOf(setDate.substring(6));
            Log.d(TAG, "onCreateDialog: ddmmyy " + day + month + year);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        } else {
            // Use the current date as the default if not set
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);
            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }
    }

    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        if (mCallback != null) {
            mCallback.onDateSelected(mTextViewId, year, month + 1, dayOfMonth);
        }
    }

    public void onAttach(Context activity) {
        super.onAttach(activity);
        try {
            mCallback = (OnDateSelectedListener) activity;
        } catch (ClassCastException e) {
            Log.e(TAG, "onAttach: ", e);
        }
    }

    public interface OnDateSelectedListener {
        public void onDateSelected(int textViewId, int year, int month, int dayOfMonth);
    }
}
