package org.x.cassini;

import android.app.IntentService;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.media.Image;
import android.os.Bundle;
import android.os.Environment;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.ResultReceiver;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import org.x.cassini.AddressResultReceiver.Receiver;

/**
 * Created by Guo Mingxuan on 2017/6/7 0007.
 */

public class NewEntryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String TAG = "NewEntry";
    private TextView time, location, mainText, toolbarTitle;
    private ImageView BWeather, BEmotion, BExercise, BStar, BTag;
    private LinearLayout newEntryLayout;
    private ArrayList<Dimension> dimensionList;
    private ArrayList<String> dimensionInput;
    private Calendar calendar;
    private final int UNSET = -1;
    private final int SUNNY = 0, CLOUDY = 1, RAINY = 2, HEAVYRAIN = 3, THUNDERSTORM = 4, SNOW = 5;
    private final int HAPPY = 0, SAD = 1, NEUTRAL = 2, ANGRY = 3, EMBARRASSED = 4, KISS = 5;
    private final int WALK = 0, RUN = 1, BALL = 2, CYCLING = 3, SWIN = 4;
    private int intWeather = UNSET; // -1 - not selected, 0 - sunny, 1 - cloudy, 2 - rainy, 3 - heavy rain, 4 - thunderstorm, 5 - snow
    private int intEmotion = UNSET; // -1 - not selected, 0 - happy, 1 - sad, 2 - neutral, 3 - angry, 4 - embarrassed, 5 - kiss
    private int intExercise = UNSET; // -1 - not selected, 0 - walk, 1 - run, 2 - ball, 3 - cycling, 4 - swim
    private int isStar = 0; // 0 - false/ not starred, 1 - true/ starred
    private String sTag = ""; // if sTag = null, tag is not set
    private ArrayList<String> tagList;
    private GridView grid, tagGrid;
    private NewEntryGridAdapter weatherAdapter, emojiAdapter, exerciseAdapter;
    private int[] weatherIcons, emojiIcons, exerciseIcons;
    private EditText tagField;
    private NewEntryTagAdapter tagAdapter;
    private Context mContext;
    private String sDate;
    private boolean isResult = false;
    private Date currentDateInfo;
    private Storie holder;
    private DatabaseHelper db;
    private ArrayList<Integer> dimensionIdList;
    private boolean isEditMode = false;
    private static String COL_DATE = "DATE";
    private static String TABLE_ENTRY_NAME = "entry_table";
    private File file;
    private ArrayList<String> dbTagList;
    private String dbId, toolbarTitleString;
    private EditText editTagField;
    private int focusedId = -1;
    private FusedLocationProviderClient mFusedLocationClient;
    protected Location mLastLocation;
    private AddressResultReceiver mResultReceiver;
    private String mLocation;


    @Override
    protected void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_new_entry);

        Log.d(TAG, "Entered onCreate");
        mContext = getApplication();

        // initialize various components
        initIntArrays();
        initTextView();
        initButtons();
        initBottomPart();
        mainText.requestFocus();

        // load other dates if needed
        if (getIntent().getStringExtra("date") != null) {
            sDate = getIntent().getStringExtra("date");
            isEditMode = true;
            Log.d(TAG, "onCreate: requested date is " + sDate);
        }
//        else {
//            sDate = new SimpleDateFormat("yyyyMMddHHmmss").format(currentDateInfo);
//            isEditMode = false;
//            Log.d(TAG, "onCreate: Today is " + sDate);
//        }

        initToolbar();
        // establish database and read dimensions
        loadResources();
//        db = new DatabaseHelper(mContext,1);
    }

    private void loadResources() {
        // preset layoutparams for dimensions
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(10, 10, 10, 10);
        dimensionList = new ArrayList<>();
        dimensionIdList = new ArrayList<>();
        try {
            file = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + "/Cassini/config.txt");
            FileInputStream inputStream = new FileInputStream(file);

            if (inputStream != null) {
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                String receiveString = "";
                receiveString = bufferedReader.readLine();
                inputStream.close();
                int version = Integer.valueOf(receiveString);
                db = new DatabaseHelper(this, version);
                Log.d(TAG, "loadConfig: db version is " + version);
                if (!isEditMode) {
                    // only init dimensions if not in edit mode
                    while ((receiveString = bufferedReader.readLine()) != null) {
                        Log.d(TAG, "loadResources: read dimension: " + receiveString);
                        // check if the dimension will be used
                        String isActive = receiveString.substring(0, 1);
                        if (isActive.equals("T")) {
                            String type = receiveString.substring(1, 2);
                            Log.d(TAG, "loadResources: type is " + type);
                            // the dimension is active
                            int index = receiveString.indexOf(":");
                            String dimensionId = receiveString.substring(2, index);
                            String dimensionString = receiveString.substring(index + 1);
                            Log.d(TAG, "loadResources: id is " + dimensionId + " and dimension is " + dimensionString);
                            Dimension temp = new Dimension(mContext);
//                    temp.inflate(this, R.layout.dimension, null);
                            temp.setHeader(dimensionString);
                            temp.setDimensionId(dimensionId);
                            temp.setBackgroundColor(Color.WHITE);
                            temp.setLayoutParams(params);
                            if (type.equals("T")) {
                                temp.setType(0);
                            } else if (type.equals("I")) {
                                temp.setType(1);
                            } else {
                                Log.e(TAG, "loadResources: no such input type");
                            }
                            int tempId = View.generateViewId();
                            temp.setId(tempId);
                            // add view into linear layout
                            newEntryLayout.addView(temp);
                            // store dimension info into lists
                            dimensionList.add(temp);
                            dimensionIdList.add(tempId);
                        } else {
                            Log.d(TAG, "loadResources: inactive dimension: " + receiveString);
                        }
                    }
                }
            }
        } catch (FileNotFoundException e) {
            Log.e("main activity", "File not found: " + e.toString());
        } catch (IOException e) {
            Log.e("main activity", "Can not read file: " + e.toString());
        }
    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.new_entry_toolbar);
        setSupportActionBar(toolbar);
        toolbarTitle = (TextView) findViewById(R.id.new_entry_toolbar_title);
        toolbarTitleString = new SimpleDateFormat("MMMM dd, yyyy").format(currentDateInfo);
        toolbarTitle.setText(toolbarTitleString);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, "Title is " + getSupportActionBar().getTitle());
    }

    private void initTextView() {
        calendar = Calendar.getInstance();
        time = (TextView) findViewById(R.id.new_entry_time);
        location = (TextView) findViewById(R.id.new_entry_location);
        currentDateInfo = calendar.getTime();
        if (!isEditMode) {
            time.setText(new SimpleDateFormat("dd/MM/yyyy HH:mm:ss").format(currentDateInfo));
        }

        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mFusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, new OnSuccessListener<Location>() {
                    @Override
                    public void onSuccess(Location location) {
                        mLastLocation = location;
//                        Log.d(TAG, "latitude is " + mLastLocation.getLatitude());

                        // In some rare cases the location returned can be null
                        if (location == null) {
                            return;
                        }

                        // Start service and update UI to reflect new location
//                        startIntentService();
                    }
                });
//        Log.d(TAG, "location is " + mLocation);
        location.setText(mLocation);
    }

    private void initButtons() {
        BWeather = (ImageView) findViewById(R.id.new_entry_weather);
        BEmotion = (ImageView) findViewById(R.id.new_entry_emotion);
        BExercise = (ImageView) findViewById(R.id.new_entry_exercise);
        BStar = (ImageView) findViewById(R.id.new_entry_star);
        BTag = (ImageView) findViewById(R.id.new_entry_tag);
        grid = (GridView) findViewById(R.id.new_entry_grid);
//        weatherAdapter = new NewEntryGridAdapter(mContext, weatherIcons);
//        grid.setAdapter(weatherAdapter);
//        weatherAdapter.notifyDataSetChanged();
        tagGrid = (GridView) findViewById(R.id.new_entry_tag_grid);
//        if (tagList != null && !tagList.isEmpty()) {
//            tagGrid.setVisibility(View.VISIBLE);
//        }
        tagField = (EditText) findViewById(R.id.new_entry_tag_field);
        tagField.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    sTag = tagField.getText().toString();
                    if (!sTag.equals("")) { // user has entered something
                        tagList.add(sTag);
                        Log.d(TAG, "onFocusChange: added tag " + sTag);
                        BTag.setImageResource(R.drawable.ic_tag_filled);
                    } else {
                        Log.d(TAG, "onFocusChange: nothing to be added");
                    }
                    finishEditingTag();
                    // return to unfocused state
                    mainText.requestFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(mContext.INPUT_METHOD_SERVICE);
                    imm.hideSoftInputFromWindow(mainText.getWindowToken(), 0);
                } else {
                    tagField.setText("");
                    Log.d(TAG, "onFocusChange: focused");
                    grid.setVisibility(View.GONE);
                }
            }
        });

        BWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid.getVisibility() == View.GONE) {
                    grid.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: Grid is visible");
                    initGrid(0);
                    focusedId = 0;
                } else {
                    if (focusedId == 0 || focusedId == -1) {
                        grid.setVisibility(View.GONE);
                    } else {
                        Log.e(TAG, "onClick: init grid with id " + focusedId);
                        initGrid(0);
                    }
                }
                tagField.setVisibility(View.GONE);
            }
        });

        BEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid.getVisibility() == View.GONE) {
                    grid.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: Grid is visible");
                    initGrid(1);
                    focusedId = 1;
                } else {
                    if (focusedId == 1 || focusedId == -1) {
                        grid.setVisibility(View.GONE);
                    } else {
                        Log.e(TAG, "onClick: init grid with id " + focusedId);
                        initGrid(1);
                    }
                }
                tagField.setVisibility(View.GONE);
            }
        });

        BExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid.getVisibility() == View.GONE) {
                    grid.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: Grid is visible");
                    initGrid(2);
                    focusedId = 2;
                } else {
                    if (focusedId == 2 || focusedId == -1) {
                        grid.setVisibility(View.GONE);
                    } else {
                        Log.e(TAG, "onClick: init grid with id " + focusedId);
                        initGrid(2);
                    }
                }
                tagField.setVisibility(View.GONE);
            }
        });

        BStar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isStar == 0) { // not starred
                    BStar.setImageResource(R.drawable.ic_star_full);
                    isStar = 1;
                } else {
                    BStar.setImageResource(R.drawable.ic_star_empty);
                    isStar = 0;
                }
            }
        });

        BTag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tagField.setVisibility(View.VISIBLE);
                tagField.requestFocus();
                if (!tagList.isEmpty()) {
                    tagGrid.setVisibility(View.VISIBLE);
                    tagAdapter = new NewEntryTagAdapter(mContext, tagList);
                    tagGrid.setAdapter(tagAdapter);
                    tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                            AlertDialog.Builder alert = new AlertDialog.Builder(NewEntryActivity.this);
                            String existingTag = tagList.get(position);
                            alert.setTitle("Edit tag");
                            editTagField = new EditText(NewEntryActivity.this);
                            editTagField.setMaxLines(1);
                            editTagField.setInputType(InputType.TYPE_CLASS_TEXT);
                            editTagField.setText(existingTag);
                            editTagField.setFocusable(true);
                            editTagField.setFocusableInTouchMode(true);
                            alert.setView(editTagField);

                            alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    tagList.remove(position);
                                    Toast.makeText(mContext, "Tag deleted!", Toast.LENGTH_SHORT).show();
                                    finishEditingTag();
                                }
                            });

                            alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int whichButton) {
                                    String modifiedTag = editTagField.getText().toString();
                                    if (!modifiedTag.equals("")) {
                                        tagList.set(position, modifiedTag);
                                        Toast.makeText(mContext, "Tag updated!", Toast.LENGTH_SHORT).show();
                                    } else {
                                        tagList.remove(position);
                                        Toast.makeText(mContext, "Tag deleted!", Toast.LENGTH_SHORT).show();
                                    }
                                    finishEditingTag();
                                }
                            });
                            alert.show();
                        }
                    });
                }
            }
        });
    }

    private void finishEditingTag() {
        if (tagList.isEmpty()) {
            BTag.setImageResource(R.drawable.ic_tag_empty);
        }
//        tagGrid.setVisibility(View.GONE);
        tagField.setVisibility(View.GONE);
        if (tagAdapter == null) {
            if (!tagList.isEmpty()) {
                tagGrid.setVisibility(View.VISIBLE);
                tagAdapter = new NewEntryTagAdapter(mContext, tagList);
                tagGrid.setAdapter(tagAdapter);
                tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                        AlertDialog.Builder alert = new AlertDialog.Builder(NewEntryActivity.this);
                        String existingTag = tagList.get(position);
                        alert.setTitle("Edit tag");
                        editTagField = new EditText(NewEntryActivity.this);
                        editTagField.setMaxLines(1);
                        editTagField.setInputType(InputType.TYPE_CLASS_TEXT);
                        editTagField.setText(existingTag);
                        editTagField.setFocusable(true);
                        editTagField.setFocusableInTouchMode(true);
                        alert.setView(editTagField);

                        alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                tagList.remove(position);
                                Toast.makeText(mContext, "Tag deleted!", Toast.LENGTH_SHORT).show();
                                finishEditingTag();
                            }
                        });

                        alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int whichButton) {
                                String modifiedTag = editTagField.getText().toString();
                                if (!modifiedTag.equals("")) {
                                    tagList.set(position, modifiedTag);
                                    Toast.makeText(mContext, "Tag updated!", Toast.LENGTH_SHORT).show();
                                } else {
                                    tagList.remove(position);
                                    Toast.makeText(mContext, "Tag deleted!", Toast.LENGTH_SHORT).show();
                                }
                                finishEditingTag();
                            }
                        });
                        alert.show();
                    }
                });
                tagAdapter.notifyDataSetChanged();
            }
        }
    }

    private void initBottomPart() {
        newEntryLayout = (LinearLayout) findViewById(R.id.new_entry_bottom_linear);
        mainText = (TextView) findViewById(R.id.new_entry_main_text);
        mainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("title", toolbarTitleString);
                bundle.putString("mainText", (String) mainText.getText());
                Intent mainTextAct = new Intent(mContext, MainTextActivity.class);
                mainTextAct.putExtras(bundle);
                startActivityForResult(mainTextAct, 1);
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        isResult = true;
        Log.d(TAG, "onActivityResult: isResult is true");
        super.onActivityResult(requestCode, resultCode, data);
//        Log.e(TAG, "onActivityResult: result here");
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String modifiedText = data.getStringExtra("mainText");
                mainText.setText(modifiedText);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        // save new entry data
//        dimensionInput = new ArrayList<>();
//        if (dimensionList != null) {
//            for (Dimension d : dimensionList) {
//                dimensionInput.add(d.getInput());
//                Log.d(TAG, "onPause: edittext input is " + d.getInput());
//            }
//        }
//        saveDiary();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        // restore data back to new entry activity
//        if (dimensionInput != null) {
//            for (int i = 0; i < dimensionInput.size(); i++) {
//                dimensionList.get(i).setInput(dimensionInput.get(i));
//            }
//        }
        if (isEditMode && !isResult) {
            loadDiary();
        } else {
            Log.e(TAG, "onResume: not loading diary");
        }
        mainText.requestFocus();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.e(TAG, "onDestroy: exiting activity");
        // save diary here
        saveDiary();
    }

    private void saveDiary() {
        // grab dimension input if any
        boolean hasInput = false;
        dimensionInput = new ArrayList<>();
        if (!dimensionIdList.isEmpty()) {
            for (int id : dimensionIdList) {
                Dimension tempDimension = (Dimension) findViewById(id);
                String tempInput = tempDimension.getInput();
                if (!tempInput.equals("")) {
                    hasInput = true;
                }
                dimensionInput.add(tempInput);
                Log.d(TAG, "saveDiary: added input " + tempInput + " to " + tempDimension.getHeader());
            }
            Log.d(TAG, "saveDiary: dimension id list size is " + dimensionIdList);
        } else {
            Log.e(TAG, "saveDiary: dimension list is empty");
        }
        Log.d(TAG, "saveDiary: dimension list size " + dimensionList.size());
        // create a new diary only if there is any input and not in edit mode
        if (!isEditMode) {
            if (intWeather != UNSET || intEmotion != UNSET || intExercise != UNSET ||
                    !tagList.isEmpty() || !mainText.getText().toString().equals("") || hasInput) {
                Log.d(TAG, "saveDiary: should save diary here");
                String dbDate = time.getText().toString();
                String dbLocation = location.getText().toString();
                String dbMainText = mainText.getText().toString();
                ArrayList<ArrayList<String>> dbDimensionInfo = new ArrayList<>();
                for (int i = 0; i < dimensionList.size(); i++) {
                    String questionId = "D" + dimensionList.get(i).getDimensionId();
                    String answer = dimensionInput.get(i);
                    Log.d(TAG, "saveDiary: q&a: " + questionId + " && " + answer);
                    ArrayList<String> tempList = new ArrayList<>();
                    tempList.add(questionId);
                    tempList.add(answer);
                    dbDimensionInfo.add(tempList);
                }
                boolean isInserted = db.insertData(dbDate, dbLocation, intWeather, intEmotion, intExercise, isStar, tagList, dbMainText, dbDimensionInfo);
                Log.e(TAG, "saveDiary: is inserted " + isInserted);
            }
        } else {
            // update data to existing row
            Log.d(TAG, "saveDiary: update database");
            String dbDate = time.getText().toString();
            String dbLocation = location.getText().toString();
            String dbMainText = mainText.getText().toString();
            ArrayList<ArrayList<String>> dbDimensionInfo = new ArrayList<>();
            Log.d(TAG, "saveDiary: dimension list size in else " + dimensionList.size());
            for (int i = 0; i < dimensionList.size(); i++) {
                String questionId = "D" + dimensionList.get(i).getDimensionId();
                String answer = dimensionInput.get(i);
                Log.d(TAG, "saveDiary: q&a: " + questionId + " && " + answer);
                ArrayList<String> tempList = new ArrayList<>();
                tempList.add(questionId);
                tempList.add(answer);
                dbDimensionInfo.add(tempList);
            }
            // provide old tag list for tag table update
            boolean isUpdated = db.updateData(dbId, dbDate, dbLocation, intWeather, intEmotion, intExercise, isStar, tagList, dbTagList, dbMainText, dbDimensionInfo);
            Log.d("DB", "update data: loaded info id " + dbId + " date " + dbDate + " location " + dbLocation +
                    " weather " + intWeather + " emotion " + intEmotion + " exercise " + intExercise + " star " + isStar +
                    " tags " + tagList + " old tagList " + dbTagList + " maintext " + dbMainText + " dimension indicators " + dbDimensionInfo);
            Log.e(TAG, "saveDiary: is updated " + isUpdated);
//            Intent intent = new Intent();
//            setResult(RESULT_OK, intent);

            Intent intentForUpdate = new Intent();
            intentForUpdate.setAction("org.x.cassini.DB_UPDATE");
            sendBroadcast(intentForUpdate);
        }
        db.close();
    }

    private void initIntArrays() {
        // weather array
        weatherIcons = new int[6];
        weatherIcons[0] = 0;
        weatherIcons[1] = 1;
        weatherIcons[2] = 2;
        weatherIcons[3] = 3;
        weatherIcons[4] = 4;
        weatherIcons[5] = 5;

        // emoji array
        emojiIcons = new int[6];
        emojiIcons[0] = 6;
        emojiIcons[1] = 7;
        emojiIcons[2] = 8;
        emojiIcons[3] = 9;
        emojiIcons[4] = 10;
        emojiIcons[5] = 11;

        // exercise array
        exerciseIcons = new int[5];
        exerciseIcons[0] = 12;
        exerciseIcons[1] = 13;
        exerciseIcons[2] = 14;
        exerciseIcons[3] = 15;
        exerciseIcons[4] = 16;

        // tag list
        tagList = new ArrayList<>();
    }

    private void initGrid(int type) {

        if (type == 0) { // 0 for weather
            weatherAdapter = new NewEntryGridAdapter(mContext, weatherIcons);
            grid.setAdapter(weatherAdapter);
            weatherAdapter.notifyDataSetChanged();
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO save the selected weather profile to disk
                    switch (position) {
                        case 0:
                            BWeather.setImageResource(R.drawable.ic_sunny);
                            grid.setVisibility(View.GONE);
                            intWeather = SUNNY;
                            break;
                        case 1:
                            BWeather.setImageResource(R.drawable.ic_cloudy);
                            grid.setVisibility(View.GONE);
                            intWeather = CLOUDY;
                            break;
                        case 2:
                            BWeather.setImageResource(R.drawable.ic_rainy);
                            grid.setVisibility(View.GONE);
                            intWeather = RAINY;
                            break;
                        case 3:
                            BWeather.setImageResource(R.drawable.ic_heavy_rain);
                            grid.setVisibility(View.GONE);
                            intWeather = HEAVYRAIN;
                            break;
                        case 4:
                            BWeather.setImageResource(R.drawable.ic_thunderstorm);
                            grid.setVisibility(View.GONE);
                            intWeather = THUNDERSTORM;
                            break;
                        case 5:
                            BWeather.setImageResource(R.drawable.ic_snow);
                            grid.setVisibility(View.GONE);
                            intWeather = SNOW;
                            break;
                    }
                }
            });
        } else if (type == 1) {
            emojiAdapter = new NewEntryGridAdapter(mContext, emojiIcons);
            grid.setAdapter(emojiAdapter);
            emojiAdapter.notifyDataSetChanged();
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO save the selected weather profile to disk
                    switch (position) {
                        case 0:
                            BEmotion.setImageResource(R.drawable.ic_happy);
                            grid.setVisibility(View.GONE);
                            intEmotion = HAPPY;
                            break;
                        case 1:
                            BEmotion.setImageResource(R.drawable.ic_sad);
                            grid.setVisibility(View.GONE);
                            intEmotion = SAD;
                            break;
                        case 2:
                            BEmotion.setImageResource(R.drawable.ic_neutral);
                            grid.setVisibility(View.GONE);
                            intEmotion = NEUTRAL;
                            break;
                        case 3:
                            BEmotion.setImageResource(R.drawable.ic_angry);
                            grid.setVisibility(View.GONE);
                            intEmotion = ANGRY;
                            break;
                        case 4:
                            BEmotion.setImageResource(R.drawable.ic_embarrassed);
                            grid.setVisibility(View.GONE);
                            intEmotion = EMBARRASSED;
                            break;
                        case 5:
                            BEmotion.setImageResource(R.drawable.ic_kiss);
                            grid.setVisibility(View.GONE);
                            intEmotion = KISS;
                            break;
                    }
                }
            });
        } else if (type == 2) {
            exerciseAdapter = new NewEntryGridAdapter(mContext, exerciseIcons);
            grid.setAdapter(exerciseAdapter);
            exerciseAdapter.notifyDataSetChanged();
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO save the selected weather profile to disk
                    switch (position) {
                        case 0:
                            BExercise.setImageResource(R.drawable.ic_walk);
                            grid.setVisibility(View.GONE);
                            intExercise = WALK;
                            break;
                        case 1:
                            BExercise.setImageResource(R.drawable.ic_run);
                            grid.setVisibility(View.GONE);
                            intExercise = RUN;
                            break;
                        case 2:
                            BExercise.setImageResource(R.drawable.ic_ball);
                            grid.setVisibility(View.GONE);
                            intExercise = BALL;
                            break;
                        case 3:
                            BExercise.setImageResource(R.drawable.ic_cycling);
                            grid.setVisibility(View.GONE);
                            intExercise = CYCLING;
                            break;
                        case 4:
                            BExercise.setImageResource(R.drawable.ic_swim);
                            grid.setVisibility(View.GONE);
                            intExercise = SWIN;
                            break;
                    }
                }
            });
        }
    }

    private void loadDiary() {
        Log.d(TAG, "loadDiary: load diary of " + sDate);
        if (db == null) {
            Log.e(TAG, "loadDiary: DB is not initialized");
            return;
        }
        String findEntryQuery = "SELECT * FROM " + TABLE_ENTRY_NAME + " WHERE " + COL_DATE + "=" + "'" + sDate + "'";
        Cursor cursor = db.getEntry(findEntryQuery);
        if (cursor != null) {
            cursor.moveToNext();
            dbId = cursor.getString(0);
            String dbDate = cursor.getString(1);
            String dbLocation = cursor.getString(2);
            int dbWeather = cursor.getInt(3);
            int dbEmotion = cursor.getInt(4);
            int dbExercise = cursor.getInt(5);
            int dbStar = cursor.getInt(6);
            String dbTags = cursor.getString(7);
            String dbMainText = cursor.getString(8);
            String dbDimensionIndicator = cursor.getString(9);
            Log.d(TAG, "loadDiary: loaded info " + "id " + dbId + " date " + dbDate + " location " + dbLocation +
                    " weather " + dbWeather + " emotion " + dbEmotion + " exercise " + dbExercise + " star " + dbStar +
                    " tags " + dbTags + " maintext " + dbMainText + " dimension indicators " + dbDimensionIndicator);
            time.setText(dbDate);
            location.setText(dbLocation);
            // parse tags Json into ArrayList
            Gson gson = new Gson();
            Type type = new TypeToken<ArrayList<String>>() {
            }.getType();
            dbTagList = gson.fromJson(dbTags, type);
            // parse dimension indicators json to arraylist
            ArrayList<String> dbIndicatorList = gson.fromJson(dbDimensionIndicator, type);
            // get dimension inputs by reading corresponding columns
            dimensionInput = new ArrayList<>();
            ArrayList<Integer> dimensionPosition = new ArrayList<>();
            if (dbIndicatorList != null && !dbIndicatorList.get(0).equals("")) {
                for (String columnName : dbIndicatorList) {
                    int position = Integer.valueOf(columnName.substring(1));
                    int index = 9 + position;
                    Log.d(TAG, "loadDiary: dimension position is " + index);
                    dimensionPosition.add(position);
                    dimensionInput.add(cursor.getString(index));
                }
            }
            // fill in data into empty new entry template
            // weather
            switch (dbWeather) {
                case UNSET:
                    break;
                case SUNNY:
                    intWeather = SUNNY;
                    BWeather.setImageResource(R.drawable.ic_sunny);
                    break;
                case CLOUDY:
                    intWeather = CLOUDY;
                    BWeather.setImageResource(R.drawable.ic_cloudy);
                    break;
                case RAINY:
                    intWeather = RAINY;
                    BWeather.setImageResource(R.drawable.ic_rainy);
                    break;
                case HEAVYRAIN:
                    intWeather = HEAVYRAIN;
                    BWeather.setImageResource(R.drawable.ic_heavy_rain);
                    break;
                case THUNDERSTORM:
                    intWeather = THUNDERSTORM;
                    BWeather.setImageResource(R.drawable.ic_thunderstorm);
                    break;
                case SNOW:
                    intWeather = SNOW;
                    BWeather.setImageResource(R.drawable.ic_snow);
                    break;
            }
            // emotion
            switch (dbEmotion) {
                case UNSET:
                    break;
                case HAPPY:
                    intEmotion = HAPPY;
                    BEmotion.setImageResource(R.drawable.ic_happy);
                    break;
                case SAD:
                    intEmotion = SAD;
                    BEmotion.setImageResource(R.drawable.ic_sad);
                    break;
                case NEUTRAL:
                    intEmotion = NEUTRAL;
                    BEmotion.setImageResource(R.drawable.ic_neutral);
                    break;
                case ANGRY:
                    intEmotion = ANGRY;
                    BEmotion.setImageResource(R.drawable.ic_angry);
                    break;
                case EMBARRASSED:
                    intEmotion = EMBARRASSED;
                    BEmotion.setImageResource(R.drawable.ic_embarrassed);
                    break;
                case KISS:
                    intEmotion = KISS;
                    BEmotion.setImageResource(R.drawable.ic_kiss);
                    break;
            }
            // exercise
            switch (dbExercise) {
                case UNSET:
                    break;
                case WALK:
                    intExercise = WALK;
                    BExercise.setImageResource(R.drawable.ic_walk);
                    break;
                case RUN:
                    intExercise = RUN;
                    BExercise.setImageResource(R.drawable.ic_run);
                    break;
                case BALL:
                    intExercise = BALL;
                    BExercise.setImageResource(R.drawable.ic_ball);
                    break;
                case CYCLING:
                    intExercise = CYCLING;
                    BExercise.setImageResource(R.drawable.ic_cycling);
                    break;
                case SWIN:
                    intExercise = SWIN;
                    BExercise.setImageResource(R.drawable.ic_swim);
                    break;
            }
            // star
            if (dbStar == 1) {
                isStar = 1;
                BStar.setImageResource(R.drawable.ic_star_full);
            }
            // tag
            if (dbTagList != null && !dbTagList.isEmpty()) {
                // tag list is not empty
                BTag.setImageResource(R.drawable.ic_tag_full);
                for (String tagItem : dbTagList) {
                    tagList.add(tagItem);
                }
                tagGrid.setVisibility(View.VISIBLE);
                if (tagAdapter == null) {
                    if (!tagList.isEmpty()) {
                        tagGrid.setVisibility(View.VISIBLE);
                        tagAdapter = new NewEntryTagAdapter(mContext, tagList);
                        tagGrid.setAdapter(tagAdapter);
                        tagGrid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                AlertDialog.Builder alert = new AlertDialog.Builder(NewEntryActivity.this);
                                String existingTag = tagList.get(position);
                                alert.setTitle("Edit tag");
                                editTagField = new EditText(NewEntryActivity.this);
                                editTagField.setMaxLines(1);
                                editTagField.setInputType(InputType.TYPE_CLASS_TEXT);
                                editTagField.setText(existingTag);
                                editTagField.setFocusable(true);
                                editTagField.setFocusableInTouchMode(true);
                                alert.setView(editTagField);

                                alert.setNegativeButton("Delete", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        tagList.remove(position);
                                        Toast.makeText(mContext, "Tag deleted!", Toast.LENGTH_SHORT).show();
                                        finishEditingTag();
                                    }
                                });

                                alert.setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int whichButton) {
                                        String modifiedTag = editTagField.getText().toString();
                                        if (!modifiedTag.equals("")) {
                                            tagList.set(position, modifiedTag);
                                            Toast.makeText(mContext, "Tag updated!", Toast.LENGTH_SHORT).show();
                                        } else {
                                            tagList.remove(position);
                                            Toast.makeText(mContext, "Tag deleted!", Toast.LENGTH_SHORT).show();
                                        }
                                        finishEditingTag();
                                    }
                                });
                                alert.show();
                            }
                        });
                    }
                }
                tagAdapter.notifyDataSetChanged();
            }
            // main text
            mainText.setText(dbMainText);
            // dimension
            if (!dimensionPosition.isEmpty()) {
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(10, 10, 10, 10);
                dimensionIdList = new ArrayList<>();
                try {
                    FileInputStream fis = new FileInputStream(file);
                    BufferedReader br = new BufferedReader(new InputStreamReader(fis));
                    String line = br.readLine();
                    Log.d(TAG, "loadDiary: db version is " + line);
                    int count = 0;
                    int inputIndex = 0;
                    for (int position : dimensionPosition) {
//                        count = position;
                        Log.d(TAG, "loadDiary: position is " + position + " count is " + count + " inputindex is " + inputIndex);
                        for (int i = 0; i < position - count - 1; i++) {
                            // read unneeded lines
                            br.readLine();
                            Log.e(TAG, "loadDiary: skipped one line");
                        }
                        line = br.readLine();
                        Log.e(TAG, "loadDiary: dimension line is " + line);
                        int index = line.indexOf(":");
                        String dimensionId = line.substring(2, index);
                        String dimensionString = line.substring(index + 1);
                        Log.d(TAG, "loadResources: id is " + dimensionId + " and dimension is " + dimensionString);
                        Dimension temp = new Dimension(mContext);
//                    temp.inflate(this, R.layout.dimension, null);
                        temp.setHeader(dimensionString);
                        temp.setDimensionId(dimensionId);
                        temp.setInput(dimensionInput.get(inputIndex));
                        temp.setBackgroundColor(Color.WHITE);
                        temp.setLayoutParams(params);
                        int tempId = View.generateViewId();
                        temp.setId(tempId);
                        // add view into linear layout
                        newEntryLayout.addView(temp);
                        // store dimension info into lists
                        dimensionIdList.add(tempId);
                        dimensionList.add(temp);
                        inputIndex++;
                        count = position;
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "loadDiary: dimension list after loading is " + dimensionList.size());
            }
        } else {
            Log.e(TAG, "loadDiary: unable to find entry" + sDate);
        }
    }

    protected void startIntentService() {
        Intent intent = new Intent(this, FetchAddressIntentService.class);
        mResultReceiver = new AddressResultReceiver(new Handler());
//        mResultReceiver.setReceiver(this);
        intent.putExtra(FetchAddressIntentService.Constants.RECEIVER, mResultReceiver);
        Log.d(TAG,"location is " + mLocation);
        intent.putExtra(FetchAddressIntentService.Constants.LOCATION_DATA_EXTRA, mLastLocation);
        startService(intent);
        Log.d(TAG,"intent sent");
    }

//    class AddressResultReceiver extends ResultReceiver {
//        public AddressResultReceiver(Handler handler) {
//            super(handler);
//        }
//
//        @Override
//        protected void onReceiveResult(int resultCode, Bundle resultData) {
//            Log.d(TAG,"On Receive Result");
//            mLocation = resultData.getString(FetchAddressIntentService.Constants.RESULT_DATA_KEY);
//            Log.d(TAG,"mlocation is " + mLocation);
//        }
//    }


    public static class FetchAddressIntentService extends IntentService {

        protected ResultReceiver mReceiver;

        /**
         * Creates an IntentService.  Invoked by your subclass's constructor.
         *
         * @param name Used to name the worker thread, important only for debugging.
         */
        public FetchAddressIntentService(String name) {
            super(name);
        }

        public FetchAddressIntentService() {
            super("FetchAddressIntentService");
        }


        public final class Constants {
            public static final int SUCCESS_RESULT = 0;
            public static final int FAILURE_RESULT = 1;
            public static final String PACKAGE_NAME =
                    "org.x.cassini";
            public static final String RECEIVER = PACKAGE_NAME + ".RECEIVER";
            public static final String RESULT_DATA_KEY = PACKAGE_NAME +
                    ".RESULT_DATA_KEY";
            public static final String LOCATION_DATA_EXTRA = PACKAGE_NAME +
                    ".LOCATION_DATA_EXTRA";
        }

        @Override
        protected void onHandleIntent(@Nullable Intent intent) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());

            String errorMessage = "";

            // Get the location passed to this service through an extra.
            Location location = intent.getParcelableExtra(
                    Constants.LOCATION_DATA_EXTRA);

            // ...

            List<Address> addresses = null;

            try {
                addresses = geocoder.getFromLocation(
                        location.getLatitude(),
                        location.getLongitude(),
                        // In this sample, get just a single address.
                        1);
            } catch (IOException ioException) {
                // Catch network or other I/O problems.
                errorMessage = getString(R.string.service_not_available);
//                Log.e(TAG, errorMessage, ioException);
            } catch (IllegalArgumentException illegalArgumentException) {
                // Catch invalid latitude or longitude values.
                errorMessage = getString(R.string.invalid_lat_long_used);
//                Log.e(TAG, errorMessage + ". " +
//                        "Latitude = " + location.getLatitude() +
//                        ", Longitude = " +
//                        location.getLongitude(), illegalArgumentException);
//            }

            // Handle case where no address was found.
            if (addresses == null || addresses.size() == 0) {
                if (errorMessage.isEmpty()) {
                    errorMessage = getString(R.string.no_address_found);
//                    Log.e(TAG, errorMessage);
                }
                deliverResultToReceiver(Constants.FAILURE_RESULT, errorMessage);
            } else {
                Address address = addresses.get(0);
                ArrayList<String> addressFragments = new ArrayList<String>();

                // Fetch the address lines using getAddressLine,
                // join them, and send them to the thread.
                for (int i = 0; i <= address.getMaxAddressLineIndex(); i++) {
                    addressFragments.add(address.getAddressLine(i));
                }
//                Log.i(TAG, getString(R.string.address_found));
                deliverResultToReceiver(Constants.SUCCESS_RESULT,
                        TextUtils.join(System.getProperty("line.separator"),
                                addressFragments));
            }
        }}


        private void deliverResultToReceiver(int resultCode, String message) {
            Bundle bundle = new Bundle();
            bundle.putString(Constants.RESULT_DATA_KEY, message);
            mReceiver.send(resultCode, bundle);
        }
    }
}