package org.x.cassini;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * Created by Guo Mingxuan on 2017/6/11 0011.
 */

public class Dimension extends LinearLayout {
    private LayoutInflater inflater;
    private String TAG = "Dimension";
    private String input;
    private EditText textFiled;
    private TextView header;

    public Dimension(Context context, AttributeSet attrs) {
        super(context, attrs);
        inflater = (LayoutInflater)getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.dimension, this);
        textFiled = (EditText) findViewById(R.id.dimension_edittext);
        header = (TextView) findViewById(R.id.dimension_header);
        Log.d(TAG, "Dimension: inflated");

//        textFiled.setOnEditorActionListener(new EditText.OnEditorActionListener() {
//
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH ||
//                        actionId == EditorInfo.IME_ACTION_DONE ||
//                        event.getAction() == KeyEvent.ACTION_DOWN &&
//                                event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
//                    if (!event.isShiftPressed()) {
//                        return true;
//                    }
//                }
//                return false;
//            }
//        });
    }

    public String getInput() {
        input = String.valueOf(textFiled.getText());
        return input;
    }

    public void setHeader(String newHeader) {
        header.setText(newHeader);
    }
}
