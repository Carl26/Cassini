package org.x.cassini;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TagEntriesActivity extends AppCompatActivity {

    private Context mContext;
    private String TAG = "TagEntriesActivity";
    private ArrayList<Storie>  mTagEntries;
    private AllEntriesListAdapter mTagEntriesAdapter;
    private Toolbar toolbar;
    private DatabaseHelper db;
    private String mTag;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_entries);
        mContext = getApplication();
        mTag = getIntent().getStringExtra("Tag");

        initToolbar();
    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "OnResume");

        formEntriesList(mTag);
        initEntriesList();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tag_entries_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        TextView title = (TextView) toolbar.findViewById(R.id.tag_entries_toolbar_title);
        title.setText(mTag);

        Log.d(TAG, "initToolbar");
    }

    private void formEntriesList(String tag) {

    }

    private void initEntriesList(){

    }



}
