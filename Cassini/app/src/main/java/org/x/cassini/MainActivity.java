package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
    private TextView  newEntry, allEntries, timelineView, tags, stories, settings;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        clearDb();
        loadConfig();
//        db = new DatabaseHelper(getApplicationContext(),1);

        Log.d(TAG, "Entered onCreate");
        // initialize various components
        initTextViews();
        findViewById(R.id.mainpage_relative_layout).requestFocus();
//        testing();
    }

    private void clearDb() {
        // used for deleting/ downgrading db
        Log.d(TAG, "onCreate: " + this.deleteDatabase("Storie.db"));
    }

//    private void testing() {
//        Button viewAll = (Button) findViewById(R.id.button_view_db);
//        viewAll.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Cursor res = db.getAllEntryData();
//                if (res.getCount() == 0) {
//                    showMessage("Error", "No data found!");
//                    return ;
//                }
//
//                StringBuffer buffer = new StringBuffer();
//                while (res.moveToNext()) {
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("Date: " + res.getString(1) + "\n");
//                    buffer.append("location: " + res.getString(2) + "\n");
//                    buffer.append("weather: " + res.getString(3) + "\n");
//                    buffer.append("emotion: " + res.getString(4) + "\n");
//                    buffer.append("exercise: " + res.getString(5) + "\n");
//                    buffer.append("star: " + res.getString(6) + "\n");
//                    buffer.append("tag: " + res.getString(7) + "\n");
//                    buffer.append("maintext: " + res.getString(8) + "\n");
//                    buffer.append("dimensionIndicator: " + res.getString(9) + "\n");
//                    buffer.append("d1: " + res.getString(10) + "\n\n");
//                }
//
//                // show all data
//                showMessage("Data", buffer.toString());
//            }
//        });
//
//        Button viewTags = (Button) findViewById(R.id.button_tag);
//        viewTags.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Cursor res = db.getAllTagData();
//                if (res.getCount() == 0) {
//                    showMessage("Error", "No data found!");
//                    return ;
//                }
//
//                StringBuffer buffer = new StringBuffer();
//                while (res.moveToNext()) {
//                    buffer.append("ID: " + res.getString(0) + "\n");
//                    buffer.append("Tag: " + res.getString(1) + "\n");
//                    buffer.append("Entry ID: " + res.getString(2) + "\n\n");
//                }
//
//                // show all data
//                showMessage("Data", buffer.toString());
//            }
//        });
//    }

    public void showMessage(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setCancelable(true);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.show();
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
                builder.append("TT1:What is the one thing I learnt today?");
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

    private void initTextViews(){

        //all entries
        allEntries = (TextView) findViewById(R.id.main_all_entries);
        allEntries.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent allEntriesAct = new Intent(getApplication(),AllEntriesActivity.class);
                startActivity(allEntriesAct);
            }
        });

        //timeline view
        timelineView = (TextView) findViewById(R.id.main_timeline_view);
        timelineView.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent timelineAct = new Intent(getApplication(),TimelineActivity.class);
                startActivity(timelineAct);
            }
        });

        //tags
        tags = (TextView) findViewById(R.id.main_tags);
        tags.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplication(),TagsViewActivity.class);
                startActivity(intent);
            }
        });

//        //starred
//        starred = (TextView) findViewById(R.id.main_layout_starred);
//        starred.setOnClickListener(new View.OnClickListener(){
//            @Override
//            public void onClick(View v){
//                Toast myToast = Toast.makeText(3
//                        getApplicationContext(),
//                        "The best things are yet to come.",
//                        Toast.LENGTH_LONG
//                );
//                myToast.show();
//            }
//        });

        //stories
        stories = (TextView) findViewById(R.id.main_stories);
        stories.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        "This feature will be available in the next release. The best things are yet to come!",
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });

        //settings
        settings = (TextView) findViewById(R.id.main_settings);
        settings.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent intent = new Intent(getApplication(),SettingsActivity.class);
                startActivity(intent);
            }
        });


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

    @Override
    protected void onPause() {
        super.onPause();
        db.close();
    }

}
