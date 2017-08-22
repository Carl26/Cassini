package org.x.cassini;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/8/18 0018.
 */

class StoriesListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> infoList, titleList;
    private String TAG = "StoriesListAdapter";
    private boolean isMultiple = false;

    public StoriesListAdapter(Context context, ArrayList<String> infoList, ArrayList<String> titleList) {
        mContext = context;
        this.infoList = infoList;
        this.titleList = titleList;
    }

    class ViewHolder {
        TextView title, period;
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return infoList.size();
    }

    @Override
    public String getItem(int position) {
        return infoList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setIsMultiple(boolean isMultiple) {
        this.isMultiple = isMultiple;
        notifyDataSetChanged();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        StoriesListAdapter.ViewHolder holder;
        String info;
        ArrayList<Boolean> selectedItems = ((StoriesActivity) mContext).getSelectedItems();
        if (convertView == null) {
            holder = new StoriesListAdapter.ViewHolder();
            view = inflater.inflate(R.layout.stories_row, null);
            // setup textviews
            holder.title = (TextView) view.findViewById(R.id.stories_row_title);
            holder.period = (TextView) view.findViewById(R.id.stories_row_time_period);
            holder.checkBox = (CheckBox) view.findViewById(R.id.stories_row_checkbox);
        } else {
            view = convertView;
            holder = (StoriesListAdapter.ViewHolder) view.getTag();
            holder.title.setText("");
            holder.period.setText("");
//            holder.checkBox.setVisibility(View.GONE);
        }
//        id = idList.get(position);
//        switch (id) {
//            case "-4": holder.title.setText("Weather");
//                break;
//            case "-3": holder.title.setText("Emotion");
//                break;
//            case "-2": holder.title.setText("Exercise");
//                break;
//            case "-1": holder.title.setText("Tag");
//                break;
//            default: holder.title.setText(titleList.get(Integer.valueOf(id)));
//                break;
//        }
        holder.title.setText(titleList.get(position));
        Log.d(TAG, "getView: title is " + holder.title.getText().toString());
        info = infoList.get(position);
        String infoString = "From " + info.substring(0, 4) + "/" + info.substring(4, 6) + "/" + info.substring(6, 8)
                + " to " + info.substring(8, 12) + "/" + info.substring(12, 14) + "/" + info.substring(14);
        Log.d(TAG, "getView: info string formed is " + infoString);
        holder.period.setText(infoString);
        if (isMultiple) {
            holder.checkBox.setVisibility(View.VISIBLE);
        } else {
            holder.checkBox.setVisibility(View.GONE);
        }
        if (selectedItems != null) {
            holder.checkBox.setChecked(selectedItems.get(position));
        }
        return view;
    }
}
