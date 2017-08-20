package org.x.cassini;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

import static android.R.id.list;

public class TagEntriesActivity extends AppCompatActivity {

    private Context mContext;
    private String TAG = "TagEntriesActivity";
    private ArrayList<Storie> mTagEntries;
    private Toolbar toolbar;
    private DatabaseHelper db;
    private String mTag;
    private ListView list;
    private TagEntriesListAdapter listAdapter;
    private ArrayList<Boolean> selectionList;
    private Button btnDelete;
    private boolean isMultiple = false;
    private DBBroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_entries);
        mContext = this;
        mTag = getIntent().getStringExtra("Tag");

        initToolbar();
    }

    public ArrayList<Boolean> getSelectedItems() {
        return selectionList;
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "OnResume");
        loadData();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(receiver);
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.tag_entries_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        btnDelete = (Button) findViewById(R.id.tag_entries_delete_button);
        TextView title = (TextView) toolbar.findViewById(R.id.tag_entries_toolbar_title);
        title.setText(mTag);

        Log.d(TAG, "initToolbar");
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

    private void loadData() {
        formEntriesList(mTag);
        if (mTagEntries != null) {
            initEntriesList();
        }
        if (receiver == null) {
            receiver = new TagEntriesActivity.DBBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("org.x.cassini.DB_UPDATE");
            filter.addAction("org.x.cassini.DB_DELETE");
            registerReceiver(receiver, filter);
        }
    }

    private void formEntriesList(String tag) {
        loadConfig();
//        db = new DatabaseHelper(mContext, 1);
        Cursor res = db.getTagEntries(tag);

        if (res.getCount() == 0) {
            Toast.makeText(mContext, "No record found!", Toast.LENGTH_SHORT).show();
            return;
        }

        Storie temp;
        String date, location, mainText, tagJson;
        ArrayList<String> tagList;
        Gson gson = new Gson();
        Type type = new TypeToken<ArrayList<String>>() {
        }.getType();
        mTagEntries = new ArrayList<>();
        while (res.moveToNext()) {
            date = res.getString(1);
            location = res.getString(2);
            tagJson = res.getString(7);
            tagList = gson.fromJson(tagJson, type);
            mainText = res.getString(8);
            temp = new Storie(date, location, tagList, mainText);
            mTagEntries.add(0,temp);
        }

    }

    private void initEntriesList() {
        list = (ListView) findViewById(R.id.tag_entries_list);
        listAdapter = new TagEntriesListAdapter(mContext, mTagEntries);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Storie selected = mTagEntries.get(position);
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
                                    Storie tempDelete = mTagEntries.get(i);
                                    String dateDelete = tempDelete.getmDateTime();
                                    ArrayList<String> tagListDelete = tempDelete.getmTagList();
                                    boolean isDeleted = db.deleteData(dateDelete, tagListDelete);
                                    Log.d(TAG, "onClick: is deleted " + isDeleted);
//                                    // refresh list
//                                    loadData();
                                    if (isDeleted) {
                                        Log.d(TAG, "onClick: sending delete intent");
                                        Intent intentForUpdate = new Intent();
                                        intentForUpdate.setAction("org.x.cassini.DB_DELETE");
                                        sendBroadcast(intentForUpdate);
                                    }
                                }
                            }
                            onBackPressed();
                        }
                    });
                    selectionList = new ArrayList<Boolean>();
                    for (int i = 0; i < mTagEntries.size(); i++) {
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

    public class DBBroadcastReceiver extends BroadcastReceiver {
        private String TAG = "DBBR";

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction().equals("org.x.cassini.DB_UPDATE")) {
                Log.d(TAG, "onReceive: received db update intent");
                loadData();
            } else if (intent.getAction().equals("org.x.cassini.DB_DELETE")) {
                Log.d(TAG, "onReceive: received db delete intent");
                loadData();
            }
        }
    }
}
