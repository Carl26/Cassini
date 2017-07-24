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

    class ViewHolder {
        TextView tag;
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
        ViewHolder holder;
        if (convertView == null) {
            grid = inflater.inflate(R.layout.new_entry_tags, null);
            holder = new ViewHolder();
            holder.tag = (TextView) grid.findViewById(R.id.new_entry_tag_adapter_field);
            grid.setTag(holder);
        } else {
            grid = convertView;
            holder = (ViewHolder) grid.getTag();
            holder.tag.setText("");
        }
        String tagWithHash = "#" + mTags.get(position);
        holder.tag.setText(tagWithHash);
        return grid;
    }
}
