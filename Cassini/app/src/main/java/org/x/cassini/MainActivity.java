package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainPage";
    private TextView  newEntry;
    private LinearLayout allEntries, timelineView, tags, starred, stories, settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(TAG, "Entered onCreate");

        // initialize various components
        initLinearLayout();
        initTextView();
        findViewById(R.id.mainpage_relative_layout).requestFocus();

    }

    private void initLinearLayout(){

        //all entries
        allEntries = (LinearLayout) findViewById(R.id.main_layout_all_entries);
        allEntries.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent allEntriesAct = new Intent(getApplication(),AllEntriesActivity.class);
            }
        });

        //timeline view
        timelineView = (LinearLayout) findViewById(R.id.main_layout_timeline_view);
        timelineView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "The best things are yet to come.",
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });

        //tags
        tags = (LinearLayout) findViewById(R.id.main_tags);
        tags.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "The best things are yet to come.",
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });

        //starred
        starred = (LinearLayout) findViewById(R.id.main_starred);
        starred.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "The best things are yet to come.",
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });

        //stories
        stories = (LinearLayout) findViewById(R.id.main_stories);
        stories.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "The best things are yet to come.",
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });

        //settings
        settings = (LinearLayout) findViewById(R.id.main_all_entries);
        settings.setOnClickListener(new View.OnClickListener(){


            @Override
            public void onClick(View v){
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "The best things are yet to come.",
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });

    }

    private void initTextView(){

        //new Entry
        newEntry = (TextView) findViewById(R.id.main_all_entries);
        newEntry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent newEntryAct = new Intent(getApplication(),NewEntryActivity.class);
            }
        });
    }


}
