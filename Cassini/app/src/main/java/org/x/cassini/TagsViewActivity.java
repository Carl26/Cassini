package org.x.cassini;

import android.content.Context;
import android.nfc.Tag;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;

import java.lang.reflect.Array;
import java.util.ArrayList;

public class TagsViewActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private Context mContext;
    private ArrayList<TagBlock> mTagBlocks;
    private

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tagsview);

    }


}
