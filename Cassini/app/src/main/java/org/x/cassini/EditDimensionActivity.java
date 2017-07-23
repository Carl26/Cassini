package org.x.cassini;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.Toast;

public class EditDimensionActivity extends AppCompatActivity {
    private String header;
    private int id, type;
    private String TAG = "EditDimension";
    private Toolbar toolbar;
    private Button saveButton, activateButton;
    private EditText titleText;
    private boolean isActivated = false, isExist = false;
    private RadioGroup typeBox;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_dimension);

        initViews();
        loadIntent();

    }

    private void initViews() {
        toolbar = (Toolbar) findViewById(R.id.edit_dimension_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        saveButton = (Button) findViewById(R.id.edit_dimension_save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // return new header and type back
                String newTitle = titleText.getText().toString();
                if (typeBox.getCheckedRadioButtonId() == -1) {
                    Log.e(TAG, "onClick: no type selected");
                    Toast.makeText(getApplicationContext(), "Please select input type!", Toast.LENGTH_SHORT).show();
                } else if (newTitle.equals("")) {
                    Log.e(TAG, "onClick: no title");
                    Toast.makeText(getApplicationContext(), "Please enter the title!", Toast.LENGTH_SHORT).show();
                } else {
                    Intent intent = new Intent();
                    Bundle bundle = new Bundle();
                    bundle.putBoolean("isExist", isExist);
                    if (isExist) {
                        bundle.putInt("id", id);
                        Log.d(TAG, "onClick: has id " + id);
                    }
                    bundle.putBoolean("isActivated", isActivated);
                    bundle.putString("newTitle", newTitle);
                    int typeId = -1;
                    if (typeBox.getCheckedRadioButtonId() == R.id.edit_dimension_dim_type_text) {
                        typeId = 0;
                    } else if (typeBox.getCheckedRadioButtonId() == R.id.edit_dimension_dim_type_number) {
                        typeId = 1;
                    }
                    bundle.putInt("type", typeId);
                    intent.putExtras(bundle);
                    setResult(RESULT_OK, intent);
                    Log.d(TAG, "onClick: type is " + typeId);
                    Log.d(TAG, "saveButton: sent result");
                    finish();
                }
            }
        });
        titleText = (EditText) findViewById(R.id.edit_dimension_dim_title_text);
        typeBox = (RadioGroup) findViewById(R.id.edit_dimension_dim_type_box_layout);
        activateButton = (Button) findViewById(R.id.edit_dimension_activate_button);
        activateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isActivated = !isActivated;
                if (isActivated) {
                    activateButton.setBackgroundTintList(ContextCompat.getColorStateList(EditDimensionActivity.this, android.R.color.holo_red_light));
                    activateButton.setText(R.string.deactivate);
                    Toast.makeText(getApplicationContext(), "Fragment activated!", Toast.LENGTH_SHORT).show();
                } else {
                    activateButton.setBackgroundTintList(ContextCompat.getColorStateList(EditDimensionActivity.this, android.R.color.darker_gray));
                    activateButton.setText(R.string.activate);
                    Toast.makeText(getApplicationContext(), "Fragment deactivated!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public void loadIntent() {
        Intent receivedIntent = getIntent();
        Bundle bundle = receivedIntent.getExtras();
        if (bundle.getBoolean("isExist")) {
            // from dimension
            header = bundle.getString("header");
            id = bundle.getInt("id");
            type = bundle.getInt("type");
            isActivated = bundle.getBoolean("isActivated");
            Log.d(TAG, "onCreate: header " + header + " id " + id + " type " + type + " activated " + isActivated);
            titleText.setText(header);
            if (type == 0) {
                typeBox.check(R.id.edit_dimension_dim_type_text);
                Log.d(TAG, "loadIntent: type is text");
            } else if (type == 1) {
                typeBox.check(R.id.edit_dimension_dim_type_number);
                Log.d(TAG, "loadIntent: type is number");
            } else {
                Log.e(TAG, "loadIntent: no such type");
            }
            if (isActivated) {
                activateButton.setBackgroundTintList(ContextCompat.getColorStateList(EditDimensionActivity.this, android.R.color.holo_red_light));
                activateButton.setText(R.string.deactivate);
            }
            isExist = true;
        } else {
            // empty
            Log.d(TAG, "onCreate: empty intent");
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        onBackPressed();
        return super.onOptionsItemSelected(item);
    }
}
