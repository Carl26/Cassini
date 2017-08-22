package org.x.cassini;

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
import android.view.MenuItem;
import android.view.View;
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
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/8/18 0018.
 */

public class StoriesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button deleteBtn;
    private Context mContext;
    private ListView list;
    private String TAG = "StoriesActivity";
    private DBBroadcastReceiver receiver;
    private DatabaseHelper db;
    private ArrayList<String> titleList, idList, infoList, adapterTitleList;
    private boolean isMultiple = false;
    private ArrayList<Boolean> selectionList;
    private StoriesListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stories);
        mContext = this;
        initComponents();
    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.stories_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        deleteBtn = (Button) findViewById(R.id.stories_delete_button);
        list = (ListView) findViewById(R.id.stories_list);
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
        if (infoList != null) {
            initList();
        }
        if (receiver == null) {
            receiver = new StoriesActivity.DBBroadcastReceiver();
            IntentFilter filter = new IntentFilter();
            filter.addAction("org.x.cassini.DB_UPDATE");
            filter.addAction("org.x.cassini.DB_DELETE");
            registerReceiver(receiver, filter);
        }
    }

    public ArrayList<Boolean> getSelectedItems() {
        return selectionList;
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
            deleteBtn.setVisibility(View.GONE);
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
                    titleList = new ArrayList<>();
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        Log.d(TAG, "loadResources: read dimension: " + receiveString);
                        int index = receiveString.indexOf(":");
                        String dimensionString = receiveString.substring(index + 1);
                        Log.d(TAG, "loadResources: dimension is " + dimensionString);
                        titleList.add(dimensionString);
                    }
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
        Cursor res = db.getAllStoriesData();

        if (res.getCount() == 0) {
            Toast.makeText(mContext, "No record found!", Toast.LENGTH_SHORT).show();
            return ;
        }
        infoList = new ArrayList<>();
        idList = new ArrayList<>();
        adapterTitleList = new ArrayList<>();
        String dimensionId, infoString, titleListForAdapter;
        while (res.moveToNext()) {
            dimensionId = res.getString(1);
            infoString = res.getString(2);
            int dId = Integer.valueOf(dimensionId) - 1;
            switch (dId) {
                case -4: titleListForAdapter = "Weather";
                    break;
                case -3: titleListForAdapter = "Emotion";
                    break;
                case -2: titleListForAdapter = "Exercise";
                    break;
                case -1: titleListForAdapter = "Tag";
                    break;
                default: titleListForAdapter = titleList.get(dId);
                    break;
            }
            adapterTitleList.add(titleListForAdapter);
            idList.add(dimensionId);
            infoList.add(infoString);
        }
        res.close();
    }

    private void generateTimeline(String title, String id, String info) {
        Bundle bundle = new Bundle();
        int dimensionId = Integer.valueOf(id);
        bundle.putString("title", title);
        Log.d(TAG, "generateTimeline: title is " + title);
        int startDay = Integer.valueOf(info.substring(4, 6));
        int startMonth = Integer.valueOf(info.substring(2, 4));
        int startYear = Integer.valueOf(info.substring(0, 2));
        int endDay = Integer.valueOf(info.substring(10));
        int endMonth = Integer.valueOf(info.substring(8, 10));
        int endYear = Integer.valueOf(info.substring(6, 8));
        bundle.putInt("startDay", startDay);
        bundle.putInt("startMonth", startMonth);
        bundle.putInt("startYear", startYear);
        bundle.putInt("endDay", endDay);
        bundle.putInt("endMonth", endMonth);
        bundle.putInt("endYear", endYear);
        bundle.putInt("dimensionId", dimensionId);
        bundle.putBoolean("stories", true);
        Log.d(TAG, "generateTimeline: dimension id is " + dimensionId);
        Log.e(TAG, "generateTimeline: " + startDay + startMonth + startYear + " " + endDay + endMonth + endYear);
        Intent intent = new Intent(this, TimelinePreviewActivity.class);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    private void initList() {
        list = (ListView) findViewById(R.id.stories_list);
        listAdapter = new StoriesListAdapter(mContext, infoList, adapterTitleList);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String selectedTitle = adapterTitleList.get(position);
                String selectedDimensionId = idList.get(position);
                String selectedInfo = infoList.get(position);
                if (!isMultiple) {
                    Log.d(TAG, "onItemClick: position is " + position);
                    generateTimeline(selectedTitle, selectedDimensionId, selectedInfo);
                } else {
                    CheckBox checkBox = (CheckBox) view.findViewById(R.id.stories_row_checkbox);
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
                    deleteBtn.setVisibility(View.VISIBLE);
                    deleteBtn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Log.d(TAG, "onClick: deleting selected items");
                            for (int i = 0; i < selectionList.size(); i++) {
                                if (selectionList.get(i)) {
                                    Log.d(TAG, "onClick: deleting item " + i);
//                                    Storie tempDelete = stories.get(i);
//                                    String dateDelete = tempDelete.getmDateTime();
//                                    ArrayList<String> tagListDelete = tempDelete.getmTagList();
//                                    boolean isDeleted = db.deleteData(dateDelete, tagListDelete);
//                                    Log.d(TAG, "onClick: is deleted " + isDeleted);
////                                    // refresh list
////                                    loadData();
//                                    if (isDeleted) {
//                                        Log.d(TAG, "onClick: sending delete intent");
//                                        Intent intentForUpdate = new Intent();
//                                        intentForUpdate.setAction("org.x.cassini.DB_DELETE");
//                                        sendBroadcast(intentForUpdate);
//                                    }
                                }
                            }
                            onBackPressed();
                        }
                    });
                    selectionList = new ArrayList<Boolean>();
                    for (int i = 0; i < infoList.size(); i++) {
                        selectionList.add(false);
                    }
                    isMultiple = true;
                    listAdapter.setIsMultiple(isMultiple);
                    CheckBox box = (CheckBox) view.findViewById(R.id.stories_row_checkbox);
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
            } else if (intent.getAction().equals("org.x.cassini.DB_DELETE")) {
                Log.d(TAG, "onReceive: received db delete intent");
                loadData();
            }
        }
    }

}
