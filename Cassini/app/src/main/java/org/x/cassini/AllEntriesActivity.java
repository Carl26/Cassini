package org.x.cassini;

import android.content.Context;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.ListView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/6/15 0015.
 */

public class AllEntriesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Context mContext;
    private String TAG = "AllEntries";
    private ArrayList<Storie> stories;
    private ListView list;

    @Override
    protected void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_all_entries);
        mContext = getApplication();

        formStoriesArray();
        initToolbar();

        initList();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.all_entries_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("All Entries");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, "Title is " + getSupportActionBar().getTitle());
    }

    private void formStoriesArray() {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Cassini/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File[] files = dir.listFiles();
        if (files == null) {
            Toast.makeText(mContext, "No record found!", Toast.LENGTH_SHORT).show();
        } else {
            stories = new ArrayList<>();
            for (File file : files) {
                String dateTime, mainText, location;
                ArrayList<String> tagList = new ArrayList<>();
                int count;
                try {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    // read date and time
                    dateTime = br.readLine();
                    Log.d(TAG, "formStoriesArray: " + dateTime);
                    // read location;
                    location = br.readLine();
                    Log.d(TAG, "formStoriesArray: " + location);
                    // skip weather to star
                    for (int i = 0; i < 4; i++) {
                        br.readLine();
                    }
                    // read tags
                    count = Integer.valueOf(br.readLine());
                    Log.d(TAG, "formStoriesArray: " + count);
                    for (int j = 0; j < count; j++) {
                        tagList.add(br.readLine());
                        Log.d(TAG, "formStoriesArray: " + tagList.get(j));
                    }
                    // read main text
                    count = Integer.valueOf(br.readLine());
                    char[] textChar = new char[count];
                    br.read(textChar, 0, count);
                    mainText = new String(textChar);
                    Log.d(TAG, "formStoriesArray: " + mainText);
                    Storie temp = new Storie(mainText, location, dateTime, tagList);
                    stories.add(temp);
                    Log.d(TAG, "formStoriesArray: storie added");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                // TODO close streams
            }
        }
    }

    private void initList() {
        list = (ListView) findViewById(R.id.all_entries_list);
        list.setAdapter(new AllEntriesListAdapter(mContext, stories));
    }
}
