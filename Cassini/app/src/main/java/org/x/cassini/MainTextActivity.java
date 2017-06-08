package org.x.cassini;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

/**
 * Created by Guo Mingxuan on 2017/6/8 0008.
 */

public class MainTextActivity extends AppCompatActivity {

    private String TAG = "MainText";
    private String receivedText;

    @Override
    protected void onCreate(Bundle savedInstance) {
        super.onCreate(savedInstance);
        Log.d(TAG, "Entered main text activity");
        receivedText = getIntent().getExtras().getString("mainText");
        Log.d(TAG, receivedText);
    }
}
