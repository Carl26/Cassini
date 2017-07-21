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
    private LinearLayout dimensionsHolder, plusHolder;
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
        plusHolder = (LinearLayout) findViewById(R.id.edit_dimension_plus_holder);
        plusHolder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), EditDimensionActivity.class);
                Bundle bundle = new Bundle();
                bundle.putBoolean("isExist", false);
                intent.putExtras(bundle);
                startActivityForResult(intent, 1);
            }
        });
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
                    final boolean isActivated;

                    // the dimension is active
                    int index = receiveString.indexOf(":");
                    String dimensionId = receiveString.substring(2, index);
                    final String dimensionString = receiveString.substring(index + 1);
                    Log.d(TAG, "loadResources: id is " + dimensionId + " and dimension is " + dimensionString);
                    Dimension temp = new Dimension(getApplicationContext());
                    if (isActive.equals("T")) {
                        temp.setColor(1);
                        isActivated = true;
                        Log.d(TAG, "loadConfig: tis an active dimension");
                    } else if (isActive.equals("F")) {
                        temp.setColor(0);
                        isActivated = false;
                        Log.d(TAG, "loadConfig: tis anot active dimension");
                    } else {
                        isActivated = false;
                        Log.e(TAG, "loadConfig: wrong activation state");
                    }
                    temp.setHeader(dimensionString);
                    temp.setDimensionId(dimensionId);
                    temp.setBackgroundColor(Color.WHITE);
                    temp.setLayoutParams(params);
                    final int tempId = View.generateViewId();
                    temp.setId(tempId);
                    final int tempType;
                    String type = receiveString.substring(1, 2);
                    if (type.equals("T")) {
                        tempType = 0;
                    } else if (type.equals("I")) {
                        tempType = 1;
                    } else {
                        Log.e(TAG, "loadConfig: no such type");
                        tempType = -1;
                    }
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

                    indexHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), EditDimensionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isExist", true);
                            bundle.putString("header", dimensionString);
                            // this is view id
                            bundle.putInt("id", tempId);
                            bundle.putInt("type", tempType);
                            bundle.putBoolean("isActivated", isActivated);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 1);
                        }
                    });
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

//        // loaded all dimensions, time to add in + text view
//        TextView plusButton = new TextView(getApplicationContext());
//        int pixel = (int) (25 * scale + 0.5f);
//        LinearLayout.LayoutParams plusButtonParams = new LinearLayout.LayoutParams(pixel, pixel);
//        int pixelMargin = (int) (20*scale+0.5f);
//        plusButtonParams.setMargins(pixelMargin, pixelMargin, pixelMargin, pixelMargin);
//        plusButtonParams.gravity = Gravity.CENTER;
//        plusButton.setLayoutParams(plusButtonParams);
//        plusButton.setText("+");
//        plusButton.setBackgroundResource(R.drawable.circle);
//        plusButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
//        plusButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
//        plusButton.setTypeface(Typeface.DEFAULT_BOLD);
//        LinearLayout plusHolder = new LinearLayout(getApplicationContext());
//        plusHolder.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
//        plusHolder.setMinimumHeight((int) (180 * scale + 0.5f));
//        plusHolder.addView(plusButton);
//        plusHolder.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent intent = new Intent(getApplicationContext(), EditDimensionActivity.class);
//                Bundle bundle = new Bundle();
//                bundle.putBoolean("isExist", false);
//                intent.putExtras(bundle);
//                startActivityForResult(intent, 1);
//            }
//        });
//        dimensionsHolder.addView(plusHolder);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: received edit entry result");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                // TODO set value
                Bundle bundle = data.getExtras();
                boolean isExist = bundle.getBoolean("isExist");
                if (isExist) {
                    int id = bundle.getInt("id");
                    Log.d(TAG, "onActivityResult: id is " + id);
                }
                boolean isActivated = bundle.getBoolean("isActivated");
                String newTitle = bundle.getString("newTitle");
                int type = bundle.getInt("type");
                Log.d(TAG, "onActivityResult: is exist " + isExist + " is activated " + isActivated + " new title " + newTitle + " type " + type);
            }
        }
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
