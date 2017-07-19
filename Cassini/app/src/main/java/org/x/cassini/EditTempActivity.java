package org.x.cassini;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.os.Environment;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutCompat;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class EditTempActivity extends AppCompatActivity {
    private LinearLayout dimensionsHolder;
    private Toolbar toolbar;
    private String TAG = "Settings";
    private boolean isSaving = false;
    private ArrayList<Dimension> dimensionList;
    private ArrayList<Integer> dimensionIdList;
    private File file;
    private DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_temp);

        initViews();
        loadConfig();

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.edit_temp_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        dimensionsHolder = (LinearLayout) findViewById(R.id.edit_temp_bottom_linear);
    }

    private void loadConfig() {
        final float scale = getResources().getDisplayMetrics().density;
        // preset layoutparams for dimensions
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        dimensionList = new ArrayList<>();
        dimensionIdList = new ArrayList<>();
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cassini/config.txt");
            FileInputStream inputStream = new FileInputStream(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                receiveString = bufferedReader.readLine();
                inputStream.close();
                int version = Integer.valueOf(receiveString);
                db = new DatabaseHelper(this, version);
                Log.d(TAG, "loadConfig: db version is " + version);

                // only init dimensions if not in edit mode
                while ((receiveString = bufferedReader.readLine()) != null) {
                    Log.d(TAG, "loadResources: read dimension: " + receiveString);
                    // check if the dimension will be used
                    String isActive = receiveString.substring(0, 1);

                    // the dimension is active
                    int index = receiveString.indexOf(":");
                    String dimensionId = receiveString.substring(1, index);
                    String dimensionString = receiveString.substring(index + 1);
                    Log.d(TAG, "loadResources: id is " + dimensionId + " and dimension is " + dimensionString);
                    Dimension temp = new Dimension(getApplicationContext());
                    if (isActive.equals("T")) {
                        temp.setColor(1);
                        Log.d(TAG, "loadConfig: tis an active dimension");
                    } else if (isActive.equals("F")) {
                        temp.setColor(0);
                        Log.d(TAG, "loadConfig: tis anot active dimension");
                    }
                    temp.setHeader(dimensionString);
                    temp.setDimensionId(dimensionId);
                    temp.setBackgroundColor(Color.WHITE);
                    temp.setLayoutParams(params);
                    int tempId = View.generateViewId();
                    temp.setId(tempId);
                    // use a linearlayout to contain both dimension and textview button
                    TextView indexButton = new TextView(getApplicationContext());
                    int pixel = (int) (25 * scale + 0.5f);
                    LinearLayout.LayoutParams plusButtonParams = new LinearLayout.LayoutParams(pixel, pixel);
                    int pixelMargin = (int) (20*scale+0.5f);
                    plusButtonParams.setMargins(pixelMargin, pixelMargin, pixelMargin, pixelMargin);
                    plusButtonParams.gravity = Gravity.CENTER;
                    indexButton.setLayoutParams(plusButtonParams);
                    indexButton.setText(dimensionId);
                    indexButton.setBackgroundResource(R.drawable.circle);
                    indexButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    indexButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    indexButton.setTypeface(Typeface.DEFAULT_BOLD);
                    LinearLayout indexHolder = new LinearLayout(getApplicationContext());
                    indexHolder.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    indexHolder.setOrientation(LinearLayout.HORIZONTAL);
                    indexHolder.addView(indexButton);
                    indexHolder.addView(temp);

                    // add view into linear layout
                    dimensionsHolder.addView(indexHolder);
                    // store dimension info into lists
                    dimensionList.add(temp);
                    dimensionIdList.add(tempId);
                }

            }
        }
        catch (FileNotFoundException e) {
            Log.e("main activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("main activity", "Can not read file: " + e.toString());
        }

        // loaded all dimensions, time to add in + text view
        TextView plusButton = new TextView(getApplicationContext());
        int pixel = (int) (25 * scale + 0.5f);
        LinearLayout.LayoutParams plusButtonParams = new LinearLayout.LayoutParams(pixel, pixel);
        int pixelMargin = (int) (20*scale+0.5f);
        plusButtonParams.setMargins(pixelMargin, pixelMargin, pixelMargin, pixelMargin);
        plusButtonParams.gravity = Gravity.CENTER;
        plusButton.setLayoutParams(plusButtonParams);
        plusButton.setText("+");
        plusButton.setBackgroundResource(R.drawable.circle);
        plusButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
        plusButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        plusButton.setTypeface(Typeface.DEFAULT_BOLD);
        LinearLayout plusHolder = new LinearLayout(getApplicationContext());
        plusHolder.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        plusHolder.setMinimumHeight((int) (180 * scale + 0.5f));
        plusHolder.addView(plusButton);
        dimensionsHolder.addView(plusHolder);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_edit_temp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.save_button) {
            Log.d(TAG, "onOptionsItemSelected: exiting with changes saved");
            isSaving = true;
        }
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isSaving) {
            Log.d(TAG, "onDestroy: saving to config file");
        } else {
            Log.d(TAG, "onDestroy: changes not saved");
        }
    }
}
