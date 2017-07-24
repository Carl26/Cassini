package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {

    private Context mContext;
    private String TAG = "SettingsActivity";
    private Toolbar toolbar;
    private LinearLayout editTemplate, myAccount, setPassword, setWallpaper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        mContext = getApplication();
        Log.d(TAG, "onCreate");

        initToolbar();

    }

    @Override
    protected void onResume(){
        super.onResume();
        Log.d(TAG, "onResume");

        initOptions();

    }

    private void initToolbar() {

        toolbar = (Toolbar) findViewById(R.id.settings_toolbar);
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

    private void initOptions(){
        editTemplate = (LinearLayout) findViewById(R.id.settings_edit_temp);
        editTemplate.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent editTempAct = new Intent(mContext,EditTempActivity.class);
                startActivity(editTempAct);
            }
        });

        myAccount = (LinearLayout) findViewById(R.id.settings_account);
        myAccount.setOnClickListener(new View.OnClickListener(){
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

        setPassword = (LinearLayout) findViewById(R.id.settings_password);
        setPassword.setOnClickListener(new View.OnClickListener(){
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

        setWallpaper = (LinearLayout) findViewById(R.id.settings_wallpaper);
        setWallpaper.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Toast myToast = Toast.makeText(
                        getApplicationContext(),
                        R.string.unavailable_feature,
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });
    }
}
