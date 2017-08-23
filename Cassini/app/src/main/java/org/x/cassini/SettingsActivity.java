package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.percent.PercentRelativeLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import static android.R.attr.bitmap;

public class SettingsActivity extends AppCompatActivity {

    private Context mContext;
    private String TAG = "SettingsActivity";
    private Toolbar toolbar;
    private LinearLayout editTemplate, setFont, setPassword, setWallpaper;
    private static final int SELECT_PICTURE = 1;
    private String  selectedImagePath;

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

        setFont = (LinearLayout) findViewById(R.id.settings_font);
        setFont.setOnClickListener(new View.OnClickListener(){
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
                        "This feature will be available in the next release. The best things are yet to come!",
                        Toast.LENGTH_LONG
                );
                myToast.show();
            }
        });


    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Log.d(TAG, "onActivityResult: is data null " + data);
                Uri selectedImageUri = data.getData();
                Log.d(TAG, "onActivityResult: is uri null " + selectedImageUri);
                selectedImagePath = getPath(selectedImageUri);
//                selectedImagePath = selectedImageUri.getPath();
                Log.d(TAG, "onActivityResult: " + selectedImagePath);
                try {
                    Log.d(TAG, "onActivityResult: " + selectedImagePath);
                    FileInputStream fileis = new FileInputStream(selectedImagePath);
                    BufferedInputStream bufferedstream = new BufferedInputStream(fileis);
                    byte[] bMapArray = new byte[bufferedstream.available()];
                    bufferedstream.read(bMapArray);
                    Bitmap bMap = BitmapFactory.decodeByteArray(bMapArray, 0, bMapArray.length);
                    //Here you can set this /Bitmap image to the button background image

                    if (fileis != null) {
                        fileis.close();
                    }
                    if (bufferedstream != null) {
                        bufferedstream.close();
                    }

                    saveImageToInternalStorage(bMap);

                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public Bitmap getThumbnail(String filename) {
        Bitmap thumbnail = null;
        try {
            File filePath = mContext.getFileStreamPath(filename);
            FileInputStream fi = new FileInputStream(filePath);
            thumbnail = BitmapFactory.decodeStream(fi);
        } catch (Exception ex) {
            Log.e("getThumbnail() on intSt", ex.getMessage());
        }
        return thumbnail;
    }


    public String getPath(Uri uri) {
        String[] projection = { MediaStore.Images.Media.DATA };
        Cursor cursor = managedQuery(uri, projection, null, null, null);
        int column_index = cursor
                .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }

    public boolean saveImageToInternalStorage(Bitmap image) {
        try {
            FileOutputStream fos = mContext.openFileOutput("Wallpaper.png", Context.MODE_PRIVATE);
            image.compress(Bitmap.CompressFormat.PNG, 100, fos);
            fos.close();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

}
