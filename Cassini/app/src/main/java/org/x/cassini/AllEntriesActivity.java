package org.x.cassini;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

/**
 * Created by Guo Mingxuan on 2017/6/15 0015.
 */

public class AllEntriesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Context mContext;
    private String TAG = "AllEntries";
    private ArrayList<Storie> stories;
    private ListView list;
    private AllEntriesListAdapter listAdapter;
    private DatabaseHelper db;
    private boolean isResult = false;
    private DBBroadcastReceiver receiver;
    private boolean isMultiple = false;
    private ArrayList<Boolean> selectionList;
    private Button btnDelete;

    @Override
    protected void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_all_entries);
        mContext = this;
        initToolbar();
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.e(TAG, "onResume");
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void loadData() {
        formStoriesArray();
        if (stories != null) {
            initList();
        }
        if (receiver == null) {
            receiver = new DBBroadcastReceiver();
            IntentFilter filter = new IntentFilter("org.x.cassini.DB_UPDATE");
            registerReceiver(receiver, filter);
        }
    }

    public ArrayList<Boolean> getSelectedItems() {
        return selectionList;
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.all_entries_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnDelete = (Button) findViewById(R.id.all_entries_delete);
        Log.d(TAG, "Title is " + getSupportActionBar().getTitle());
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (isMultiple) {
            isMultiple = false;
            listAdapter.setIsMultiple(isMultiple);
            btnDelete.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
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

    private void formStoriesArray() {
        loadConfig();
//        db = new DatabaseHelper(mContext,1);
        Cursor res = db.getAllEntryData();

        if (res.getCount() == 0) {
            Toast.makeText(mContext, "No record found!", Toast.LENGTH_SHORT).show();
            return ;
        }
        Storie temp;
        String date, location, mainText, tagJson;
        ArrayList<String> tagList;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        stories = new ArrayList<>();
        while (res.moveToNext()) {
            date = res.getString(1);
            location = res.getString(2);
            tagJson = res.getString(7);
            tagList = gson.fromJson(tagJson, type);
            mainText = res.getString(8);
            temp = new Storie(date, location, tagList, mainText);
            Log.d(TAG, "formStoriesArray: storie is " + date + " " + location + " " + tagList + " " + mainText);
            stories.add(0,temp);
        }
    }

    private void initList() {
        list = (ListView) findViewById(R.id.all_entries_list);
        listAdapter = new AllEntriesListAdapter(mContext, stories);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Storie selected = stories.get(position);
                String sDate = selected.getmDateTime();
                if (!isMultiple) {
                    Log.d(TAG, "onItemClick: sent date is " + sDate);
                    Intent intent = new Intent(mContext, NewEntryActivity.class);
                    intent.putExtra("date", sDate);
                    startActivity(intent);
                } else {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.all_entries_checkbox);
                    if (checkBox.isChecked()) {
                        Log.d(TAG, "onItemClick: unchecking the item");
                        checkBox.setChecked(false);
                        selectionList.set(position, false);
                        Log.d(TAG, "onItemClick: uncheck position " + position);
                    } else {
                        Log.d(TAG, "onItemClick: checking the item");
                        checkBox.setChecked(true);
                        selectionList.set(position, true);
                        Log.d(TAG, "onItemClick: check position " + position);
                    }
                    listAdapter.notifyDataSetChanged();
                }
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemLongClick: long click received");
                if (!isMultiple) {
                    btnDelete.setVisibility(View.VISIBLE);
                    btnDelete.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: deleting selected items");
                            for (int i = 0; i < selectionList.size(); i++) {
                                if (selectionList.get(i)) {
                                    Log.d(TAG, "onClick: deleting item " + i);
                                }
                            }
                            onBackPressed();
                        }
                    });
                    selectionList = new ArrayList<Boolean>();
                    for (int i = 0; i < stories.size(); i++) {
                        selectionList.add(false);
                    }
                    isMultiple = true;
                    listAdapter.setIsMultiple(isMultiple);
                    CheckBox box = (CheckBox) view.findViewById(R.id.all_entries_checkbox);
                    box.setChecked(true);
                    selectionList.set(position, true);
                } else {
                    Log.d(TAG, "onItemLongClick: no effect since alr in multiple mode");
                }
                return true;
            }
        });
    }

//    @Override
//    public void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//        Log.d(TAG, "onActivityResult: received edit entry result");
//        if (requestCode == 1) {
//            if (resultCode == RESULT_OK) {
//                isResult = true;
//            }
//        }
//    }

    public class DBBroadcastReceiver extends BroadcastReceiver {
        private String TAG = "DBBR";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("org.x.cassini.DB_UPDATE")) {
                Log.d(TAG, "onReceive: received db update intent");
                loadData();
            }
        }
    }

}
