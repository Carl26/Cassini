package org.x.cassini;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/7/9 0009.
 */

public class TimelinePreviewActivity extends AppCompatActivity {
    private int startDay, startMonth, startYear, endDay, endMonth, endYear, dimensionId;
    private String title;
    private String TAG = "TimelinePreview";
    private DatabaseHelper db;
    private File file;
    private int version;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        setContentView(R.layout.activity_timeline_preview);
        Bundle data = getIntent().getExtras();
        startDay = data.getInt("startDay");
        startMonth = data.getInt("startMonth");
        startYear = data.getInt("startYear");
        endDay = data.getInt("endDay");
        endMonth = data.getInt("endMonth");
        endYear = data.getInt("endYear");
        dimensionId = data.getInt("dimensionId");
        title = data.getString("title");
        String startDate, endDate, startMonthStr, endMonthStr, startDayStr, endDayStr;
        if (startMonth < 10) {
            startMonthStr = "0" + startMonth;
        } else {
            startMonthStr = "" + startMonth;
        }
        if (endMonth < 10) {
            endMonthStr = "0" + endMonth;
        } else {
            endMonthStr = "" + endMonth;
        }
        if (startDay < 10) {
            startDayStr = "0" + startDay;
        } else {
            startDayStr = "" + startDay;
        }
        if (endDay < 10) {
            endDayStr = "0" + endDay;
        } else {
            endDayStr = "" + endDay;
        }
        startDate = "" + startYear + startMonthStr + startDayStr;
        endDate = "" + endYear + endMonthStr + endDayStr;
        Log.d(TAG, "onCreate: from " + startDate + " to " + endDate + " with id " + dimensionId + " of " + title);
        // init db
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cassini/config.txt");
            FileInputStream inputStream = new FileInputStream(file);
            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                receiveString = bufferedReader.readLine();
                inputStream.close();
                version = Integer.valueOf(receiveString);
                db = new DatabaseHelper(this, version);
                Log.d(TAG, "loadConfig: db version is " + version);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        ArrayList<String> result = db.getTimeline(startDate, endDate, dimensionId);
        if (result.isEmpty()) {
            Log.e(TAG, "onCreate: no record found");
            Toast.makeText(getApplicationContext(), "No data found for the selected!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d(TAG, "onCreate: " + result);
        }
    }
}
