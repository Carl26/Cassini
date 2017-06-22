package org.x.cassini;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by Guo Mingxuan on 2017/6/7 0007.
 */

public class NewEntryActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private String TAG = "NewEntry";
    private TextView time; private TextView location; private TextView mainText;
    private Button BWeather, BEmotion, BExercise, BStar, BTag;
    private LinearLayout LBottom;
    private Dimension learn, problem;
    private ArrayList<Dimension> dimensionList;
    private ArrayList<String> dimensionInput;
    private Calendar calendar;
    private final int SUNNY = 0, CLOUDY = 1, RAINY = 2, HEAVYRAIN = 3, THUNDERSTORM = 4, SNOW = 5;
    private int intWeather = -1; // -1 - not selected, 0 - sunny, 1 - cloudy, 2 - rainy, 3 - heavy rain, 4 - thunderstorm, 5 - snow
    private int intEmotion = -1;
    private int intStar = 0; // 0 - false/ not starred, 1 - true/ starred
    private String sTag = ""; // if sTag = null, tag is not set
    private GridView grid;
    private NewEntryGridAdapter weatherAdapter, emojiAdapter, exerciseAdapter;
    private int[] weatherIcons, emojiIcons, exerciseIcons;

    @Override
    protected void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_new_entry);

        Log.d(TAG, "Entered onCreate");

        // initialize various components
        initIntArrays();

        initToolbar();
        initTextView();
        initButtons();
        initBottomPart();
        findViewById(R.id.new_entry_relative_layout).requestFocus();

    }

    private void initToolbar() {
        toolbar = (Toolbar) findViewById(R.id.new_entry_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("New Entry");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        Log.d(TAG, "Title is " + getSupportActionBar().getTitle());
    }

    private void initTextView() {
        calendar = Calendar.getInstance();
        time = (TextView) findViewById(R.id.new_entry_time);
        location = (TextView) findViewById(R.id.new_entry_location);
        time.setText(new SimpleDateFormat("dd/MM/yyyy").format(calendar.getTime()));
    }

    private void initButtons() {
        BWeather = (Button) findViewById(R.id.new_entry_weather);
        BEmotion = (Button) findViewById(R.id.new_entry_emotion);
        BExercise = (Button) findViewById(R.id.new_entry_exercise);
        BStar = (Button) findViewById(R.id.new_entry_star);
        BTag = (Button) findViewById(R.id.new_entry_tag);
        grid = (GridView) findViewById(R.id.new_entry_grid);
//        weatherAdapter = new NewEntryGridAdapter(getApplication(), weatherIcons);
//        grid.setAdapter(weatherAdapter);
//        weatherAdapter.notifyDataSetChanged();
        grid.setVisibility(View.GONE);

        BWeather.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid.getVisibility() == View.GONE) {
                    grid.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: Grid is visible");
                    initGrid(0);
                } else {
                    // TODO test click weather then click other buttons like exercise
                    grid.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: Grid is gone");
                }
            }
        });

        BEmotion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid.getVisibility() == View.GONE) {
                    grid.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: Grid is visible");
                    initGrid(1);
                } else {
                    // TODO test click weather then click other buttons like exercise
                    grid.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: Grid is gone");
                }
            }
        });

        BExercise.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (grid.getVisibility() == View.GONE) {
                    grid.setVisibility(View.VISIBLE);
                    Log.d(TAG, "onClick: Grid is visible");
                    initGrid(2);
                } else {
                    // TODO test click weather then click other buttons like exercise
                    grid.setVisibility(View.GONE);
                    Log.d(TAG, "onClick: Grid is gone");
                }
            }
        });
    }

    private void initBottomPart() {
        LBottom = (LinearLayout) findViewById(R.id.new_entry_bottom_linear);
        mainText = (TextView) findViewById(R.id.new_entry_main_text);
        mainText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();
                bundle.putString("mainText", (String) mainText.getText());
                Intent mainTextAct = new Intent(getApplication(), MainTextActivity.class);
                mainTextAct.putExtras(bundle);
                startActivityForResult(mainTextAct, 1);
            }
        });
        // for testing purpose only
//        mainText.setText("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444dddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444444ddddddddddddddddd");

        // Dimensions related
        dimensionList = new ArrayList<Dimension>();
        learn = (Dimension) findViewById(R.id.new_entry_dimension_learn);
        problem = (Dimension) findViewById(R.id.new_entry_dimension_problem);
        learn.setHeader("What did I learn today?");
        problem.setHeader("What problem did I face today?");
        dimensionList.add(learn);
        dimensionList.add(problem);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
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
        // save edittext data
        dimensionInput = new ArrayList<>();
        if (dimensionList != null) {
            for (Dimension d : dimensionList) {
                dimensionInput.add(d.getInput());
                Log.d(TAG, "onPause: edittext input is " + d.getInput());
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // restore data back to edittext
        if (dimensionInput != null) {
            for (int i = 0; i < dimensionInput.size(); i++) {
                dimensionList.get(i).setInput(dimensionInput.get(i));
            }
        }
        findViewById(R.id.new_entry_relative_layout).requestFocus();
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_new_entry, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        if (item.getItemId() == R.id.new_entry_finish_button) {
//            // save entry to local dir
//            saveDiary();
//
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        saveDiary();
    }

    private void saveDiary() {
        String filename = time.getText().toString().replaceAll("\\/", "");
//        File savedFile = new File(getApplication().getFilesDir(), filename);
        File sdCard = Environment.getExternalStorageDirectory();
        File dir = new File (sdCard.getAbsolutePath() + "/Cassini/");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File savedFile = new File(dir, filename + ".txt");
        String toWrite = formDiary();
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(savedFile);
            // if file does not exist
            if (!savedFile.exists()) {
                savedFile.createNewFile();
            }
            fos.write(toWrite.getBytes());
            fos.flush();
            fos.close();
            Log.d(TAG, "saveDiary: Finished saving diary");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        finish();
    }

    private String formDiary() {
        // each section is stored in a new line for easy reading afterwards
        StringBuilder sb = new StringBuilder();
        // date
        sb.append(time.getText().toString());
        sb.append(System.lineSeparator());
        // location
        sb.append(location.getText().toString());
        sb.append(System.lineSeparator());
        // main text
        String mainDiary = mainText.getText().toString();
        int textLen = mainDiary.length();
        sb.append(textLen);
        sb.append(System.lineSeparator());
        sb.append(mainDiary);
        sb.append(System.lineSeparator());
        // dimensions
        sb.append(learn.getHeader());
        sb.append(System.lineSeparator());
        sb.append(learn.getInput());

        return sb.toString();
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
    }

    private void initGrid(int type) {
        if (type == 0) { // 0 for weather
            weatherAdapter = new NewEntryGridAdapter(getApplication(), weatherIcons);
            grid.setAdapter(weatherAdapter);
            weatherAdapter.notifyDataSetChanged();
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO save the selected weather profile to disk
                    switch (position) {
                        case 0: BWeather.setBackgroundResource(R.drawable.ic_sunny);
                            grid.setVisibility(View.GONE);
                            break;
                        case 1: BWeather.setBackgroundResource(R.drawable.ic_cloudy);
                            grid.setVisibility(View.GONE);
                            break;
                        case 2: BWeather.setBackgroundResource(R.drawable.ic_rainy);
                            grid.setVisibility(View.GONE);
                            break;
                        case 3: BWeather.setBackgroundResource(R.drawable.ic_heavy_rain);
                            grid.setVisibility(View.GONE);
                            break;
                        case 4: BWeather.setBackgroundResource(R.drawable.ic_thunderstorm);
                            grid.setVisibility(View.GONE);
                            break;
                        case 5: BWeather.setBackgroundResource(R.drawable.ic_snow);
                            grid.setVisibility(View.GONE);
                            break;
                    }
                }
            });
        } else if (type == 1) {
            emojiAdapter = new NewEntryGridAdapter(getApplication(), emojiIcons);
            grid.setAdapter(emojiAdapter);
            emojiAdapter.notifyDataSetChanged();
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO save the selected weather profile to disk
                    switch (position) {
                        case 0: BEmotion.setBackgroundResource(R.drawable.ic_happy);
                            grid.setVisibility(View.GONE);
                            break;
                        case 1: BEmotion.setBackgroundResource(R.drawable.ic_sad);
                            grid.setVisibility(View.GONE);
                            break;
                        case 2: BEmotion.setBackgroundResource(R.drawable.ic_neutral);
                            grid.setVisibility(View.GONE);
                            break;
                        case 3: BEmotion.setBackgroundResource(R.drawable.ic_angry);
                            grid.setVisibility(View.GONE);
                            break;
                        case 4: BEmotion.setBackgroundResource(R.drawable.ic_crying);
                            grid.setVisibility(View.GONE);
                            break;
                        case 5: BEmotion.setBackgroundResource(R.drawable.ic_shocked);
                            grid.setVisibility(View.GONE);
                            break;
                    }
                }
            });
        } else if (type == 2) {
            exerciseAdapter = new NewEntryGridAdapter(getApplication(), exerciseIcons);
            grid.setAdapter(exerciseAdapter);
            exerciseAdapter.notifyDataSetChanged();
            grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    // TODO save the selected weather profile to disk
                    switch (position) {
                        case 0:
                            BExercise.setBackgroundResource(R.drawable.ic_walk);
                            grid.setVisibility(View.GONE);
                            break;
                        case 1:
                            BExercise.setBackgroundResource(R.drawable.ic_run);
                            grid.setVisibility(View.GONE);
                            break;
                        case 2:
                            BExercise.setBackgroundResource(R.drawable.ic_ball);
                            grid.setVisibility(View.GONE);
                            break;
                        case 3:
                            BExercise.setBackgroundResource(R.drawable.ic_cycling);
                            grid.setVisibility(View.GONE);
                            break;
                        case 4:
                            BExercise.setBackgroundResource(R.drawable.ic_swim);
                            grid.setVisibility(View.GONE);
                            break;
                    }
                }
            });
        }
    }
}
