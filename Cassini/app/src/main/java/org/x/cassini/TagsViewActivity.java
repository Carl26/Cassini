package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.nfc.Tag;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;

public class TagsViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context mContext;
    private ArrayList<TagBlock> mTagBlocks;
    private char[] mAlphabet;
    private String TAG = "TagsViewActivity";
    private TagsViewAdapter mTagsViewAdapter;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagsview);

        mContext = getApplication();
        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

//        loadConfig();
        formTagBlocksArray();
        initTagBlocks();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tagsview_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, "initToolbar");

    }

//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        onBackPressed();
//        return super.onOptionsItemSelected(item);
//    }
//    private void loadConfig() {
//        File sdCard = Environment.getExternalStorageDirectory();
//        File savedFile = new File (sdCard.getAbsolutePath() + "/Cassini/config.txt");
//        Log.d(TAG, "loadConfig: config file path " + savedFile.getAbsolutePath() );
//        if (!savedFile.exists()) {
//            Log.e(TAG, "loadConfig: config file not found");
//        } else {
//            Log.d(TAG, "loadConfig: read config file");
//            try {
//                InputStream inputStream = new FileInputStream(savedFile);
//                if ( inputStream != null ) {
//                    InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
//                    BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
//                    String receiveString = "";
//                    receiveString = bufferedReader.readLine();
//                    inputStream.close();
//                    int version = Integer.valueOf(receiveString);
//                    db = new DatabaseHelper(this, version);
//                    Log.d(TAG, "loadConfig: db version is " + version);
//                    bufferedReader.close();
//                    inputStreamReader.close();
//                }
//                inputStream.close();
//            }
//            catch (FileNotFoundException e) {
//                Log.e(TAG, "File not found: " + e.toString());
//            } catch (IOException e) {
//                Log.e(TAG, "Can not read file: " + e.toString());
//            }
//        }
//    }

    private void formTagBlocksArray() {
        mAlphabet = new char[26];
        mTagBlocks = new ArrayList<TagBlock>(1000);

        for(int i=0; i<26; i++) {
            mAlphabet[i] = (char) ('A' + i);
            Log.d(TAG, "initAlphabet " + mAlphabet[i]);

            db = new DatabaseHelper(mContext,1);
            Cursor res = db.getTagList(mAlphabet[i]);
            Log.d(TAG, "getTagFromDb");

            ArrayList<String> mTagNames = new ArrayList<String>(1000);
            while (res.moveToNext()) {
                mTagNames.add(res.getString(0));
                Log.d(TAG,"got Tag with Name " + mTagNames);
            }
            Log.d(TAG, "WriteToStringArray");

            TagBlock temp = new TagBlock(mAlphabet[i],mTagNames);
            Log.d(TAG,"TagBlock generated with " + temp.getmTagNames());

            mTagBlocks.add(temp);
            Log.d(TAG, "Write " + mTagBlocks.get(i).getmAlphabet() + " To TagBlock Array");
        }

//        TagBlock.setOnClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(mContext, NewEntryActivity.class);
//                startActivity(intent);
//            }
//        });

    }

    private void initTagBlocks() {
        RecyclerView tagBlocksList = (RecyclerView) findViewById(R.id.tagsview_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        tagBlocksList.setLayoutManager(layoutManager);
        Log.d(TAG,"setLayoutManager");
        mTagsViewAdapter = new TagsViewAdapter(mTagBlocks,mContext);
        Log.d(TAG,"newTagsViewAdapter");
        tagBlocksList.setAdapter(mTagsViewAdapter);
        Log.d(TAG,"setTagsViewAdapter");
    }

}
