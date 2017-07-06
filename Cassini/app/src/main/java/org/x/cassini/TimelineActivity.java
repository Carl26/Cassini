package org.x.cassini;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;

/**
 * Created by Guo Mingxuan on 2017/7/5 0005.
 */

public class TimelineActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private Button toolbarConfirm;
    private TextView startDate, endDate;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);

        setContentView(R.layout.activity_timeline);

        initToolbar();

        initComponents();
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.timeline_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    private void initComponents() {
        toolbarConfirm = (Button) findViewById(R.id.timeline_toolbar_button);
        startDate = (TextView) findViewById(R.id.timeline_from_date);
        endDate = (TextView) findViewById(R.id.timeline_to_date);
    }
}
