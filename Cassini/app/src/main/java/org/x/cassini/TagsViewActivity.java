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
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class TagsViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context mContext;
    private ArrayList<TagBlock> mTagBlocks;
    private char[] mAlphabet;
    private ArrayList<ArrayList<String>> mTagLists;
    private String TAG = "TagsViewActivity";
    private LinearLayout tagBlockLayout;
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
//        formTagBlocksArray();
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

    private void initTagLists() {
        mAlphabet = new char[26];
        mTagLists = new ArrayList<>(26);
        mTagBlocks = new ArrayList<>(26);


        for(int i=0; i<26; i++) {
            mAlphabet[i] = (char) ('A' + i);
            Log.d(TAG, "initAlphabet " + mAlphabet[i]);

            db = new DatabaseHelper(mContext, 1);
            Cursor res = db.getTagList(mAlphabet[i]);
//            Log.d(TAG, "getTagFromDb");

            ArrayList<String> placeholder = new ArrayList<>(100);
            mTagLists.add(placeholder);

            while (res.moveToNext()) {
                mTagLists.get(i).add(res.getString(0));
//                Log.d(TAG,"got Tag with Name " + mTagLists.get(i));
            }
//            Log.d(TAG, "WriteToStringArray");


                TagBlock temp = new TagBlock(mAlphabet[i], mTagLists.get(i));
//            Log.d(TAG, "TagBlock generated with " + temp.getmTagNames());

                mTagBlocks.add(temp);
                Log.d(TAG, "Write " + mTagBlocks.get(i).getmAlphabet() + " To TagBlock Array");

        }
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

//    private void formTagBlocksArray() {
//        mAlphabet = new char[26];
//        mTagBlocks = new ArrayList<TagBlock>(1000);
//
//        for(int i=0; i<26; i++) {
//            mAlphabet[i] = (char) ('A' + i);
//            Log.d(TAG, "initAlphabet " + mAlphabet[i]);
//
//            db = new DatabaseHelper(mContext,1);
//            Cursor res = db.getTagList(mAlphabet[i]);
//            Log.d(TAG, "getTagFromDb");
//
//            ArrayList<String> mTagNames = new ArrayList<String>(1000);
//            while (res.moveToNext()) {
//                mTagNames.add(res.getString(0));
//                Log.d(TAG,"got Tag with Name " + mTagNames);
//            }
//            Log.d(TAG, "WriteToStringArray");
//
//            TagBlock temp = new TagBlock(mAlphabet[i],mTagNames);
//            Log.d(TAG,"TagBlock generated with " + temp.getmTagNames());
//
//            mTagBlocks.add(temp);
//            Log.d(TAG, "Write " + mTagBlocks.get(i).getmAlphabet() + " To TagBlock Array");
//        }

//        TagBlock.setOnClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//                Intent intent = new Intent(mContext, NewEntryActivity.class);
//                startActivity(intent);
//            }
//        });

//    }


    private void initTagBlocks() {
        tagBlockLayout = (LinearLayout) findViewById(R.id.tagsview_list);
        LayoutInflater mInflator = (LayoutInflater) getSystemService(mContext.LAYOUT_INFLATER_SERVICE);

        if (tagBlockLayout.getChildCount() == 0) {
            for (int i = 0; i < 26; i++) {

                TagBlock tagBlock = mTagBlocks.get(i);
                if(!tagBlock.isEmpty()) {
//            LinearLayout templayout = (LinearLayout) inflate(mContext,R.layout.tag_block,this);
//            setContentView(R.layout.tag_block);
                    View templayout = mInflator.inflate(R.layout.tag_block, null);
//            View templayout = LayoutInflater.from(mContext).inflate(R.layout.tag_block,);
                    TextView tagAlphabet = (TextView) templayout.findViewById(R.id.tag_alphabet);
                    View divider = (View) templayout.findViewById(R.id.tag_divider);
                    RecyclerView tagList = (RecyclerView) templayout.findViewById(R.id.tag_list);


                    tagAlphabet.setText(String.valueOf(tagBlock.getmAlphabet()));
//            Log.d(TAG,"Bind Tags with Alphabet" + tagBlock.getmAlphabet());
                    LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
                    tagList.setLayoutManager(layoutManager);
                    final TagBlockAdapter mTagListAdapter = new TagBlockAdapter(tagBlock.getmTagNames());
//            Log.d(TAG,"Created new TagBlockAdapter with " + tagBlock.getmTagNames());
                    tagList.setAdapter(mTagListAdapter);
                    Log.d(TAG, "Set Adapter for tags with Alphabet " + tagBlock.getmAlphabet() + " and is_empty = " + tagBlock.isEmpty());
//                if (tagBlock.isEmpty()) {
//                    tagAlphabet.setVisibility(View.GONE);
//                    divider.setVisibility(View.GONE);
//                    tagList.setVisibility(View.GONE);
//                }
                    ItemClickSupport.addTo(tagList)
                            .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                                @Override
                                public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                                    Intent intent = new Intent(mContext, TagEntriesActivity.class);
                                    intent.putExtra("Tag", mTagListAdapter.getTag(position));
                                    startActivity(intent);
                                }
                            });
//            ((ViewGroup)templayout.getParent()).removeView(templayout);
                    tagBlockLayout.addView(templayout);
                    Log.d(TAG, "added view #" + tagBlockLayout.getChildCount());
                }
            }
        }
    }
}
