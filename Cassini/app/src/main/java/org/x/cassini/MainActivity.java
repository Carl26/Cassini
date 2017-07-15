package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {

    private String TAG = "MainPage";
    private TextView  newEntry;
    private LinearLayout allEntries, timelineView, tags, starred, stories, settings;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        // used for deleting/ downgrading db
//        Log.d(TAG, "onCreate: " + this.deleteDatabase("Storie.db"));

        Log.d(TAG, "Entered onCreate");
        loadConfig();
        // initialize various components
        initLinearLayout();
        initTextView();
        findViewById(R.id.mainpage_relative_layout).requestFocus();
    }

    private void loadConfig() {
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Cassini/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        Log.d(TAG, "loadConfig: folder created/ found");
        File savedFile = new File(dir, "config.txt");
        Log.d(TAG, "loadConfig: config file path " + savedFile.getAbsolutePath() );
        if (!savedFile.exists()) {
            Log.d(TAG, "loadConfig: create a new config file");
            FileOutputStream fos;
            try {
                fos = new FileOutputStream(savedFile);
                StringBuilder builder = new StringBuilder();
                // database version
                builder.append("1");
                builder.append(System.lineSeparator());
                // default dimension
                builder.append("T1:What is the one thing I learnt today?");
                builder.append(System.lineSeparator());
                fos.write(builder.toString().getBytes());
                db = new DatabaseHelper(this, 1);
                fos.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Log.d(TAG, "loadConfig: read config file");
            try {
                InputStream inputStream = new FileInputStream(savedFile);

                if ( inputStream != null ) {
                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                    String receiveString = "";
                    receiveString = bufferedReader.readLine();
                    inputStream.close();
                    int version = Integer.valueOf(receiveString);
                    db = new DatabaseHelper(this, version);
                    Log.d(TAG, "loadConfig: db version is " + version);
                    bufferedReader.close();
                    inputStreamReader.close();
                }
                inputStream.close();
            }
            catch (FileNotFoundException e) {
                Log.e("main activity", "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e("main activity", "Can not read file: " + e.toString());
            }
        }
    }

    private void initLinearLayout(){

        //all entries
        allEntries = (LinearLayout) findViewById(R.id.main_layout_all_entries);
        allEntries.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent allEntriesAct = new Intent(getApplication(),AllEntriesActivity.class);
                startActivity(allEntriesAct);
            }
        });

        //timeline view
        timelineView = (LinearLayout) findViewById(R.id.main_layout_timeline_view);
        timelineView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
//                Toast myToast = Toast.makeText(
//                        getApplicationContext(),
//                        "The best things are yet to come.",
//                        Toast.LENGTH_LONG
//                );
//                myToast.show();
                Intent timelineAct = new Intent(getApplication(),TimelineActivity.class);
                startActivity(timelineAct);
            }
        });

        //tags
        tags = (LinearLayout) findViewById(R.id.main_layout_tags);
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
        starred = (LinearLayout) findViewById(R.id.main_layout_starred);
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
        stories = (LinearLayout) findViewById(R.id.main_layout_stories);
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
        settings = (LinearLayout) findViewById(R.id.main_layout_settings);
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
        newEntry = (TextView) findViewById(R.id.main_new_entry);
        newEntry.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent newEntryAct = new Intent(getApplication(),NewEntryActivity.class);
                startActivity(newEntryAct);
            }
        });
    }


}
