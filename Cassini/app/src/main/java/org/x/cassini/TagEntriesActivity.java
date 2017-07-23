package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

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
    private AllEntriesListAdapter listAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tag_entries);
        mContext = getApplication();
        mTag = getIntent().getStringExtra("Tag");

        initToolbar();
    }

    @Override
    protected void onResume() {
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

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

    private void formEntriesList(String tag) {
//        loadConfig();
        db = new DatabaseHelper(mContext, 1);
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
        listAdapter = new AllEntriesListAdapter(mContext, mTagEntries);
        list.setAdapter(listAdapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Storie selected = mTagEntries.get(position);
                String sDate = selected.getmDateTime();
                Log.d(TAG, "onItemClick: sent date is " + sDate);
                Intent intent = new Intent(mContext, NewEntryActivity.class);
                intent.putExtra("date", sDate);
                startActivity(intent);
            }
        });
        list.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemLongClick: long click received");
                list.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
                list.setItemsCanFocus(false);
                list.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
                    @Override
                    public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {
                        final int count = list.getCheckedItemCount();
                        getSupportActionBar().setTitle(count + " selected");
                        listAdapter.toggleSelection(position);
                    }

                    @Override
                    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                        mode.getMenuInflater().inflate(R.menu.menu_all_entries, menu);
                        return true;
                    }

                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }

                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        if (item.getItemId() == R.id.menu_delete) {
                            SparseBooleanArray selection = listAdapter.getmSelectedItems();
                            for (int i = 0; i < selection.size(); i++) {
                                if (selection.get(i)) {
                                    Storie toRemove = listAdapter.getItem(i);
                                    listAdapter.remove(toRemove);
                                }
                            }
                            mode.finish();
                            return true;
                        } else {
                            return false;
                        }
                    }

                    @Override
                    public void onDestroyActionMode(ActionMode mode) {
                        listAdapter.clearSelection();
                    }
                });
                return true;
            }
        });
    }
}
