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
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOError;
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
    private Button BWeather, BEmotion, BStar, BTag;
    private LinearLayout LBottom;
    private Dimension learn, problem;
    private ArrayList<Dimension> dimensionList;
    private ArrayList<String> dimensionInput;
    private Calendar calendar;

    @Override
    protected void onCreate(Bundle onSavedInstance) {
        super.onCreate(onSavedInstance);
        setContentView(R.layout.activity_new_entry);

        Log.d(TAG, "Entered onCreate");

        // initialize various components
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
        BStar = (Button) findViewById(R.id.new_entry_star);
        BTag = (Button) findViewById(R.id.new_entry_tag);
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new_entry, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.new_entry_finish_button) {
            // save entry to local dir
            saveDiary();

            return true;
        }
        return super.onOptionsItemSelected(item);
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
}
