package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
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
    private File dir;

    @Override
    protected void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_all_entries);
        mContext = getApplication();

        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        formStoriesArray();
        initList();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.all_entries_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, "Title is " + getSupportActionBar().getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void formStoriesArray() {
        File sdCard = Environment.getExternalStorageDirectory();
        dir = new File (sdCard.getAbsolutePath() + "/Cassini/");
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
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Storie selected = stories.get(position);
                String filename = selected.getmDateTime().substring(0, 10).replaceAll("\\/", "");
                Intent intent = new Intent(mContext, NewEntryActivity.class);
                intent.putExtra("date", filename);
                startActivity(intent);
            }
        });
    }
}
