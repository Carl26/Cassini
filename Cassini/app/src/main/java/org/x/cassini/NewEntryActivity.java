package org.x.cassini;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Guo Mingxuan on 2017/6/7 0007.
 */

public class NewEntryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String TAG = "NewEntry";
    private TextView time, location, mainText;
    private Button BWeather, BEmotion, BStar, BTag;
    private LinearLayout LBottom;

    @Override
    protected void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_new_entry);

        Log.d(TAG, "Entered onCreate");

        // initialize various components
        initToolbar();
        initTextView();
        initButtons();
        initBottomPart();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.new_entry_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Entry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, "Title is " + getSupportActionBar().getTitle());
    }

    private void initTextView() {
        time = (TextView) findViewById(R.id.new_entry_time);
        location = (TextView) findViewById(R.id.new_entry_location);
    }

    private void initButtons() {
        BWeather = (Button) findViewById(R.id.new_entry_weather);
        BEmotion = (Button) findViewById(R.id.new_entry_emotion);
        BStar = (Button) findViewById(R.id.new_entry_star);
        BTag = (Button) findViewById(R.id.new_entry_tag);
    }

    private void initBottomPart() {
        LBottom = (LinearLayout) findViewById(R.id.new_entry_bottom_linear);
        mainText = (TextView) findViewById(R.id.new_entry_main_text);
    }
}
