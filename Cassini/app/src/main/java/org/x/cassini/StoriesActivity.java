package org.x.cassini;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.ListView;

/**
 * Created by Guo Mingxuan on 2017/8/18 0018.
 */

public class StoriesActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button deleteBtn;
    private Context mContext;
    private ListView list;

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
}
