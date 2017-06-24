package org.x.cassini;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/6/22 0022.
 */

class NewEntryTagAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> mTags;
    private String TAG = "tagAdapter";

    public NewEntryTagAdapter(Context context, ArrayList<String> tags) {
        mContext = context;
        mTags = tags;
    }

    @Override
    public int getCount() {
        return mTags.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View grid;
        TextView tagItem;
        if (convertView == null) {
            grid = inflater.inflate(R.layout.new_entry_tags, null);
            tagItem = (TextView) grid.findViewById(R.id.new_entry_tag_adapter_field);
            tagItem.setText(mTags.get(position));
        } else {
            grid = convertView;
            Log.e(TAG, "getView: convertView is not null");
        }
        return grid;
    }
}
