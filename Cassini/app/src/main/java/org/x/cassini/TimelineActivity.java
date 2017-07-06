package org.x.cassini;

import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Guo Mingxuan on 2017/7/5 0005.
 */

public class TimelineActivity extends AppCompatActivity implements DatePickerFragment.OnDateSelectedListener{
    private Toolbar toolbar;
    private Button toolbarConfirm;
    private TextView startDate, endDate;
    private FragmentManager fm;
    private final int STARTDATEFRAGMENT = 0, ENDDATEFRAGMENT = 1;
    private boolean isStartDateSet = false, isEndDateSet = false;
    private String TAG = "Timeline";

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_timeline);

        fm = getSupportFragmentManager();

        initToolbar();

        initComponents();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        toolbarConfirm = (Button) findViewById(R.id.timeline_toolbar_button);
        startDate = (TextView) findViewById(R.id.timeline_from_date);
        endDate = (TextView) findViewById(R.id.timeline_to_date);

        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               startFragment(STARTDATEFRAGMENT);
            }
        });

        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startFragment(ENDDATEFRAGMENT);
            }
        });
    }

    @Override
    public void onDateSelected(int textViewId, int year, int month, int dayOfMonth) {
        TextView temp = (TextView) findViewById(textViewId);
        String mon = String.format("%02d", month);
        String day = String.format("%02d", dayOfMonth);
        String selectedDate = day + "|" + mon + "|" + year;
        temp.setText(selectedDate);
        if (textViewId == R.id.timeline_from_date) {
            isStartDateSet = true;
            Log.d(TAG, "onDateSelected: start date set");
        } else if (textViewId == R.id.timeline_to_date) {
            isEndDateSet = true;
            Log.d(TAG, "onDateSelected: end date set");
        }
    }

    private void startFragment(int type) {
        DialogFragment startDateFragment = new DatePickerFragment();
        Bundle bundle = new Bundle();
        if (type == STARTDATEFRAGMENT) {
            Log.d(TAG, "startFragment: type is start");
            bundle.putInt("textViewId", R.id.timeline_from_date);
            if (isStartDateSet) {
                // only changing start date
                bundle.putBoolean("isDateSet", true);
                bundle.putString("date", startDate.getText().toString());
                Log.d(TAG, "startFragment: start date sent is " + startDate.getText().toString());
            } else {
                bundle.putBoolean("isDateSet", false);
                Log.d(TAG, "startFragment: no start date sent");
            }
        } else if (type == ENDDATEFRAGMENT) {
            Log.d(TAG, "startFragment: type is end");
            bundle.putInt("textViewId", R.id.timeline_to_date);
            if (isEndDateSet) {
                // only changing end date
                bundle.putBoolean("isDateSet", true);
                bundle.putString("date", endDate.getText().toString());
                Log.d(TAG, "startFragment: end date sent is " + endDate.getText().toString());
            } else {
                bundle.putBoolean("isDateSet", false);
                Log.d(TAG, "startFragment: no end date sent");
            }
        }
        startDateFragment.setArguments(bundle);
        startDateFragment.show(fm, "DatePicker");
    }
}
