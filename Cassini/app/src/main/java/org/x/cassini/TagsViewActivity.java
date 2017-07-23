package org.x.cassini;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class TagsViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context mContext;
    private char[] mAlphabet;
    private int[] mAlphabetIndex;
    private ArrayList<String> mTagList;
    private String TAG = "TagsViewActivity";
    private TagsViewAdapter mAdapter;
    private RecyclerView mtagListLayout;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagsview);

        mContext = getApplication();
        initToolbar();
        loadConfig();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");


        BroadcastReceiver br = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                loadConfig();
                initTagLists();
                Log.d(TAG,"On receive Broadcast");
            }
        };

        IntentFilter filter = new IntentFilter("org.x.cassini.DB_UPDATE");
        this.registerReceiver(br,filter);

        initTagLists();
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initTagLists() {
        mAlphabet = new char[26];
        mTagList = new ArrayList<>();
        mAlphabetIndex = new int[26];
        for(int i=0; i<26; i++) {
            mAlphabetIndex[i] = i;
            mAlphabet[i] = (char) ('A' + i);
            mTagList.add(String.valueOf(mAlphabet[i]));
        }

        for(int i=0; i<26; i++) {
            Log.d(TAG,"For letter " + mAlphabet[i]);
            Cursor res = db.getTagList(mAlphabet[i]);

            int temp = mAlphabetIndex[i];
            while (res.moveToNext()) {
                mTagList.add(temp+1, res.getString(0));
                temp++;
            }
            for(int j = i+1; j<26; j++) {
                mAlphabetIndex[j] = mAlphabetIndex[j] + (temp - mAlphabetIndex[i]);
//                Log.d(TAG,"Letter " + mAlphabet[j] + " now has index " + mAlphabetIndex[j]);
            }
        }

    }

    private void loadConfig() {
        File sdCard = Environment.getExternalStorageDirectory();
        File savedFile = new File (sdCard.getAbsolutePath() + "/Cassini/config.txt");
        Log.d(TAG, "loadConfig: config file path " + savedFile.getAbsolutePath() );
        if (!savedFile.exists()) {
            Log.e(TAG, "loadConfig: config file not found");
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
                Log.e(TAG, "File not found: " + e.toString());
            } catch (IOException e) {
                Log.e(TAG, "Can not read file: " + e.toString());
            }
        }
    }


    private void initTagBlocks() {
        mtagListLayout = (RecyclerView) findViewById(R.id.taglist_layout);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        mtagListLayout.setLayoutManager(layoutManager);
        mAdapter = new TagsViewAdapter(mTagList,mContext);
        mAdapter.setmAlphabetIndex(mAlphabetIndex);
        mtagListLayout.setAdapter(mAdapter);
        ItemClickSupport.addTo(mtagListLayout)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(mContext,TagEntriesActivity.class);
                        intent.putExtra("Tag",mTagList.get(position));
                        startActivity(intent);
                    }
                });
    }
}
