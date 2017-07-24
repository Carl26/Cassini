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
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.util.ArrayList;

import static java.security.AccessController.getContext;

public class EditTempActivity extends AppCompatActivity {
    private LinearLayout dimensionsHolder, plusHolder;
    private Toolbar toolbar;
    private String TAG = "EditTemp";
    private boolean isSaving = false;
    private ArrayList<Dimension> dimensionList;
    private ArrayList<Integer> dimensionIdList;
    private File file;
    private DatabaseHelper db;
    private int lastIndex = -1;
    private Dimension temp;
    private boolean isUpgradingNeeded = false;
    private int version = -1;
    private int initialListSize = -1;

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
        // preset layoutparams for dimensions
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        dimensionList = new ArrayList<>();
        dimensionIdList = new ArrayList<>();
        final float scale = getResources().getDisplayMetrics().density;
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cassini/config.txt");
            FileInputStream inputStream = new FileInputStream(file);

            if ( inputStream != null ) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                receiveString = bufferedReader.readLine();
                inputStream.close();
                version = Integer.valueOf(receiveString);
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
                    lastIndex = Integer.valueOf(dimensionId);
                    temp = new Dimension(getApplicationContext());
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
                    temp.setIsActivated(isActivated);
                    temp.setHeader(dimensionString);
                    temp.setDimensionId(dimensionId);
                    temp.setBackgroundColor(Color.WHITE);
                    temp.setLayoutParams(params);
                    final int tempId = View.generateViewId();
                    temp.setId(tempId);
                    int tempType;
                    String type = receiveString.substring(1, 2);
                    if (type.equals("T")) {
                        tempType = 0;
                        Log.d(TAG, "loadConfig: type is text");
                    } else if (type.equals("I")) {
                        tempType = 1;
                        Log.d(TAG, "loadConfig: type is number");
                    } else {
                        Log.e(TAG, "loadConfig: no such type");
                        tempType = -1;
                    }
//                    Log.e(TAG, "loadConfig: temptype is " + tempType);
//                    Log.e(TAG, "loadConfig: temp type before: " + temp.getType() );
                    temp.setType(tempType);
//                    Log.e(TAG, "loadConfig: temp type now " + temp.getType());
                    // use a linearlayout to contain both dimension and textview button
                    TextView indexButton = new TextView(getApplicationContext());
                    int pixel = (int) (25 * scale + 0.5f);
                    LinearLayout.LayoutParams indexButtonParams = new LinearLayout.LayoutParams(pixel, pixel);
                    int pixelMargin = (int) (20*scale+0.5f);
                    indexButtonParams.setMargins(pixelMargin, pixelMargin, pixelMargin, pixelMargin);
                    indexButtonParams.gravity = Gravity.CENTER;
                    indexButton.setLayoutParams(indexButtonParams);
                    indexButton.setText(dimensionId);
                    indexButton.setBackgroundResource(R.drawable.circle);
                    indexButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    indexButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    indexButton.setTypeface(Typeface.DEFAULT_BOLD);
                    indexButton.setGravity(Gravity.CENTER);
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
                    initialListSize = dimensionList.size();
                    Log.d(TAG, "loadConfig: loaded " + initialListSize + " dimensions");
                    int position = dimensionList.size() - 1;
                    final Dimension listenerTemp = dimensionList.get(position);

                    indexHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), EditDimensionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isExist", true);
                            bundle.putString("header", listenerTemp.getHeader());
                            // this is view id
                            bundle.putInt("id", tempId);
                            bundle.putInt("type", listenerTemp.getType());
                            bundle.putBoolean("isActivated", isActivated);
                            Log.d(TAG, "onClick: sent bundle contains " + listenerTemp.getHeader() + " " + tempId + " " + listenerTemp.getType() + " " + isActivated);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 1);
                        }
                    });
                }
            }
        }
        catch (FileNotFoundException e) {
            Log.e("main activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("main activity", "Can not read file: " + e.toString());
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d(TAG, "onActivityResult: received edit entry result");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                final float scale = getResources().getDisplayMetrics().density;
                Bundle bundle = data.getExtras();
                boolean isExist = bundle.getBoolean("isExist");
                final boolean isActivated = bundle.getBoolean("isActivated");
                final String newTitle = bundle.getString("newTitle");
                final int type = bundle.getInt("type");
                LinearLayout indexHolder = new LinearLayout(getApplicationContext());
                int id = -1;
                if (isExist) {
                    id = bundle.getInt("id");
                    Log.d(TAG, "onActivityResult: id is " + id);
                    temp = (Dimension) findViewById(id);
                } else {
                    isUpgradingNeeded = true;
                    temp = new Dimension(getApplicationContext());
                    lastIndex = lastIndex + 1;
                    temp.setDimensionId(String.valueOf(lastIndex));
                    LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                    params.setMargins(10, 10, 10, 10);
                    temp.setBackgroundColor(Color.WHITE);
                    temp.setLayoutParams(params);
                    TextView indexButton = new TextView(getApplicationContext());
                    int pixel = (int) (25 * scale + 0.5f);
                    LinearLayout.LayoutParams indexButtonParams = new LinearLayout.LayoutParams(pixel, pixel);
                    int pixelMargin = (int) (20*scale+0.5f);
                    indexButtonParams.setMargins(pixelMargin, pixelMargin, pixelMargin, pixelMargin);
                    indexButtonParams.gravity = Gravity.CENTER;
                    indexButton.setLayoutParams(indexButtonParams);
                    indexButton.setText("" + lastIndex);
                    indexButton.setBackgroundResource(R.drawable.circle);
                    indexButton.setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimary));
                    indexButton.setGravity(Gravity.CENTER_HORIZONTAL);
                    indexButton.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
                    indexButton.setTypeface(Typeface.DEFAULT_BOLD);
                    indexButton.setGravity(Gravity.CENTER);
                    indexHolder = new LinearLayout(getApplicationContext());
                    indexHolder.setLayoutParams(new LinearLayoutCompat.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                    indexHolder.setOrientation(LinearLayout.HORIZONTAL);
                    indexHolder.addView(indexButton);
                    indexHolder.addView(temp);
                    final int tempId = View.generateViewId();
                    temp.setId(tempId);
                    indexHolder.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(getApplicationContext(), EditDimensionActivity.class);
                            Bundle bundle = new Bundle();
                            bundle.putBoolean("isExist", true);
                            bundle.putString("header", newTitle);
                            // this is view id
                            bundle.putInt("id", tempId);
                            bundle.putInt("type", type);
                            bundle.putBoolean("isActivated", isActivated);
                            Log.d(TAG, "onClick: sent bundle contains " + newTitle + " " + tempId + " " + type + " " + isActivated);
                            intent.putExtras(bundle);
                            startActivityForResult(intent, 1);
                        }
                    });
                }
                if (isActivated) {
                    temp.setColor(1);
                } else {
                    temp.setColor(0);
                }
                temp.setIsActivated(isActivated);
                temp.setHeader(newTitle);
                temp.setType(type);
                Log.d(TAG, "onActivityResult: is exist " + isExist + " is activated " + isActivated + " new title " + newTitle + " type " + type);
                Log.d(TAG, "onActivityResult: temp type is " + temp.getType());
                if (!isExist) {
                    // add to layout
                    dimensionsHolder.addView(indexHolder);
                    // store dimension info into lists
                    dimensionList.add(temp);
                    dimensionIdList.add(lastIndex);
                    Log.d(TAG, "onActivityResult: last index is " + lastIndex);
                }
                Log.d(TAG, "onActivityResult: now dimension list size is " + dimensionList.size());
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
//            isSaving = true;
            int count = dimensionList.size() - initialListSize;
            Log.d(TAG, "onDestroy: added " + count + " new more dimensions");
//            if (isUpgradingNeeded) {
//                upgradeDb(count);
//            }
            saveConfig(count);
            return true;
//            Intent intentForUpdate = new Intent();
//            intentForUpdate.setAction("org.x.cassini.DB_UPGRADE");
//            sendBroadcast(intentForUpdate);
//            return true;
//        } else {
        } else {
            onBackPressed();
            return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        if (isSaving) {
//            Log.d(TAG, "onDestroy: saving to config file");
//            int count = dimensionList.size() - initialListSize;
//            Log.d(TAG, "onDestroy: added " + count + " new more dimensions");
//            if (isUpgradingNeeded) {
//                upgradeDb(count);
//            }
//            saveConfig(count);
//        } else {
//            Log.d(TAG, "onDestroy: changes not saved");
//        }
        Log.e(TAG, "onDestroy: destroyed");
    }

    private void saveConfig(int count) {
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cassini/config.txt");
            FileOutputStream fos = new FileOutputStream(file);
            if (version == -1) {
                Log.e(TAG, "saveConfig: version is incorrect");
                return;
            }
            StringBuilder builder = new StringBuilder();
            // database version
            int newVersion = version + count;
            builder.append(newVersion);
            builder.append(System.lineSeparator());
            // write all dimensions to file
            for (int i = 0; i < dimensionList.size(); i++) {
                Dimension temp = dimensionList.get(i);
                int dimensionId = i + 1;
                String dimensionTitle = temp.getHeader();
                int dimensionType = temp.getType();
                String typeString = "";
                if (dimensionType == 0) {
                    typeString = "T";
                } else if (dimensionType == 1) {
                    typeString = "I";
                } else {
                    Log.e(TAG, "saveConfig: wrong type");
                    return;
                }
                boolean dimensionActive = temp.getIsActivated();
                String activeString;
                if (dimensionActive) {
                    activeString = "T";
                } else {
                    activeString = "F";
                }
                String toBeWrittenIn = activeString + typeString + dimensionId + ":" + dimensionTitle;
                Log.d(TAG, "saveConfig: new line is " + toBeWrittenIn);
                builder.append(toBeWrittenIn);
                builder.append(System.lineSeparator());
            }
            fos.write(builder.toString().getBytes());
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.e(TAG, "saveConfig: saved config");
        finish();
    }

//    private void upgradeDb(int count) {
//        int newVersion = version + count;
//        Log.d(TAG, "upgradeDb: new version is " + newVersion);
//        db = new DatabaseHelper(getApplicationContext(), newVersion);
//    }
}
