package org.x.cassini;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.text.InputType;
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
    private String input, id;
    private EditText textFiled;
    private TextView header;
    private static int ACTIVE = 1, INACTIVE = 0;
    private static int TEXT = 0, NUMBER = 1;

//    public Dimension(Context context) {
//        super(context);
//    }

    public Dimension(Context context) {
        super(context);
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

    public String getHeader() {
        return header.getText().toString();
    }

    public void setInput(String savedInput) {
        textFiled.setText(savedInput);
    }

    public void setHeader(String newHeader) {
        header.setText(newHeader);
    }

    public void setDimensionId(String id) {
        this.id = id;
    }

    public String getDimensionId() {
        return id;
    }

    public void setColor(int color) {
        if (color == ACTIVE) {
            header.setTextColor(ContextCompat.getColor(getContext(), R.color.black));
        } else if (color == INACTIVE) {
            header.setTextColor(ContextCompat.getColor(getContext(), R.color.gray));
        }
    }

    public void setType(int type) {
        if (type == TEXT) {
            textFiled.setInputType(InputType.TYPE_CLASS_TEXT);
        } else if (type == NUMBER) {
            textFiled.setInputType(InputType.TYPE_CLASS_NUMBER);
        }
    }
}
