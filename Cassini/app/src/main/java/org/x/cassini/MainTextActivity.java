package org.x.cassini;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;

/**
 * Created by Guo Mingxuan on 2017/6/8 0008.
 */

public class MainTextActivity extends AppCompatActivity {

    private String TAG = "MainText";
    private String receivedText;
    private Toolbar toolbar;
    private EditText mainBox;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.d(TAG, "Entered main text activity");
        receivedText = getIntent().getExtras().getString("mainText");
        Log.d(TAG, receivedText);
        setContentView(R.layout.activity_main_text);

        initComponents();

    }

    private void initComponents() {
        toolbar = (Toolbar) findViewById(R.id.main_text_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Edit mode");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        mainBox = (EditText) findViewById(R.id.main_text_box);
        if (!receivedText.equals("")) {
            mainBox.setText(receivedText);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main_text, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.main_text_confirm_button) {
            // send result back to parent activity
            String mainText = String.valueOf(mainBox.getText());
            Log.d(TAG, "onOptionsItemSelected: " + mainText);
            Intent intent = new Intent();
            intent.putExtra("mainText", mainText);
            setResult(RESULT_OK, intent);

            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
