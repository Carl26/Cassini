package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.IdRes;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

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
    private int startYear, startMonth, startDay, endYear, endMonth, endDay;
    private Context mContext;
    private RadioGroup rgHorizontal, rgVertical;
    private int checkedButtonHorizontal = -1, checkedButtonVertical = -1;
    private RadioButton rbWeather, rbEmotion, rbExercise, rbTag;
    private File file;
    private int version;
    private DatabaseHelper db;
    private ArrayList<String> titleList, idList;
    private ArrayList<Integer> viewIdList;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        mContext = getApplication();

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

    private void generateTimeline() {
        Bundle bundle = new Bundle();
        String title = "";
        int dimensionId = -10;
        if (checkedButtonHorizontal == -1) {
            int position = checkedButtonVertical - 1;
            title = titleList.get(position);
            dimensionId = Integer.valueOf(idList.get(position));
        } else {
            switch (checkedButtonHorizontal) {
                case 0: title = "Weather";
                    dimensionId = -4;
                    break;
                case 1: title = "Emotion";
                    dimensionId = -3;
                    break;
                case 2: title = "Exercise";
                    dimensionId = -2;
                    break;
                case 3: title = "Tag";
                    dimensionId = -1;
                    break;
            }
        }
        bundle.putString("title", title);
        Log.d(TAG, "generateTimeline: title is " + title);
        bundle.putInt("startDay", startDay);
        bundle.putInt("startMonth", startMonth);
        bundle.putInt("startYear", startYear);
        bundle.putInt("endDay", endDay);
        bundle.putInt("endMonth", endMonth);
        bundle.putInt("endYear", endYear);
        bundle.putInt("dimensionId", dimensionId);
        Log.d(TAG, "generateTimeline: dimension id is " + dimensionId);
        Log.e(TAG, "generateTimeline: " + startDay + startMonth + startYear + " " + endDay + endMonth + endYear);
        Intent intent = new Intent(this, TimelinePreviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void loadDimensions() {
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cassini/config.txt");
            FileInputStream inputStream = new FileInputStream(file);
            titleList = new ArrayList<>();
            idList = new ArrayList<>();
            viewIdList = new ArrayList<>();
            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                receiveString = bufferedReader.readLine();
                inputStream.close();
                version = Integer.valueOf(receiveString);
                db = new DatabaseHelper(this, version);
                Log.d(TAG, "loadConfig: db version is " + version);

                // only init dimensions if not in edit mode
                while ((receiveString = bufferedReader.readLine()) != null) {
                    Log.d(TAG, "loadResources: read dimension: " + receiveString);
                    int index = receiveString.indexOf(":");
                    String dimensionId = receiveString.substring(2, index);
                    String dimensionString = receiveString.substring(index + 1);
                    Log.d(TAG, "loadResources: id is " + dimensionId + " and dimension is " + dimensionString);
                    titleList.add(dimensionString);
                    idList.add(dimensionId);
                    RadioButton dimensionButton = new RadioButton(getApplicationContext());
                    dimensionButton.setText(dimensionString);
                    dimensionButton.setTextSize(14);
                    dimensionButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.black));
                    int viewId = View.generateViewId();
                    dimensionButton.setId(viewId);
                    viewIdList.add(viewId);
                    rgVertical.addView(dimensionButton);
//                    Log.d(TAG, "loadDimensions: dimension list size is " + titleList.size() + " view id list size is " + viewIdList.size());
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e(TAG, "Can not read file: " + e.toString());
        }
    }

    private void initComponents() {
        toolbarConfirm = (Button) findViewById(R.id.timeline_toolbar_button);
        startDate = (TextView) findViewById(R.id.timeline_from_date);
        endDate = (TextView) findViewById(R.id.timeline_to_date);
        rbWeather = (RadioButton) findViewById(R.id.timeline_rb_weather);
        rbEmotion = (RadioButton) findViewById(R.id.timeline_rb_emotion);
        rbExercise = (RadioButton) findViewById(R.id.timeline_rb_exercise);
        rbTag = (RadioButton) findViewById(R.id.timeline_rb_tag);

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

        toolbarConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isStartDateSet) {
                    Toast.makeText(mContext, "Please select starting date!", Toast.LENGTH_SHORT).show();
                } else if (!isEndDateSet) {
                    Toast.makeText(mContext, "Please select ending date!", Toast.LENGTH_SHORT).show();
                } else if (checkedButtonHorizontal == -1 && checkedButtonVertical == -1) {
                    Toast.makeText(mContext, "Please select one dimension!", Toast.LENGTH_SHORT).show();
                } else {
                    // get time range
                    if (startYear < endYear) {
                        // no need to check month and day
                        generateTimeline();
                    } else if (startYear == endYear) {
                        if (startMonth < endMonth) {
                            // no need to check month and day
                            generateTimeline();
                        } else if (startMonth == endMonth) {
                            if (startDay <= endDay) {
                                // no need to check month and day
                                generateTimeline();
                            } else {
                                Toast.makeText(mContext, "Please select correct time span!", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            Toast.makeText(mContext, "Please select correct time span!", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(mContext, "Please select correct time span!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        rgHorizontal = (RadioGroup) findViewById(R.id.timeline_radio_group_horizontal);
        rgVertical = (RadioGroup) findViewById(R.id.timeline_radio_group_vertical);

        loadDimensions();

        // only allow one selection from both radiogroups
        rgHorizontal.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Log.d(TAG, "horizontal onCheckedChanged: checked button id is " + checkedId);
                if (checkedButtonVertical != -1) {
                    rgVertical.clearCheck();
                    checkedButtonVertical = -1;
                    Log.e(TAG, "onCheckedChanged: cleared vertical check");
                }
                switch (checkedId) {
                    case R.id.timeline_rb_weather: checkedButtonHorizontal = 0;
                        Log.d(TAG, "onCheckedChanged: weather button clicked");
                        break;
                    case R.id.timeline_rb_emotion: checkedButtonHorizontal = 1;
                        Log.d(TAG, "onCheckedChanged: emotion button clicked");
                        break;
                    case R.id.timeline_rb_exercise: checkedButtonHorizontal = 2;
                        Log.d(TAG, "onCheckedChanged: exercise button clicked");
                        break;
                    case R.id.timeline_rb_tag: checkedButtonHorizontal = 3;
                        Log.d(TAG, "onCheckedChanged: tag button clicked");
                        break;
                }
            }
        });

        rgVertical.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                Log.d(TAG, "vertical onCheckedChanged: checked button id is " + checkedId);
                if (checkedButtonHorizontal != -1) {
                    rgHorizontal.clearCheck();
                    checkedButtonHorizontal = -1;
                    Log.e(TAG, "onCheckedChanged: cleared horizontal check");
                }
                Log.d(TAG, "onCheckedChanged: checked id is " + checkedId);
                if (checkedId != -1) {
                    int position = checkedId - 1;
                    checkedButtonVertical = viewIdList.get(position);
                }
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
            startYear = year;
            startMonth = month;
            startDay = dayOfMonth;
            Log.d(TAG, "onDateSelected: start date set " + startYear + startMonth + startDay);
        } else if (textViewId == R.id.timeline_to_date) {
            isEndDateSet = true;
            endYear = year;
            endMonth = month;
            endDay = dayOfMonth;
            Log.d(TAG, "onDateSelected: end date set " + endYear + endMonth + endDay);
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
