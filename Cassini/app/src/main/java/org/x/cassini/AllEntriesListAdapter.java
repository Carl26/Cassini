package org.x.cassini;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.drawable.Drawable;
import android.support.annotation.IntRange;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
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
 * Created by Guo Mingxuan on 2017/6/23 0023.
 */

class AllEntriesListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Storie> stories;
    private String TAG = "AEListAdapter";
    private boolean isMultiple = false;

    public AllEntriesListAdapter(Context context, ArrayList<Storie> stories) {
        mContext = context;
        this.stories = stories;
    }

    class ViewHolder {
        TextView main, location, date, month;
        LinearLayout tags;
        CheckBox checkBox;
    }

    @Override
    public int getCount() {
        return stories.size();
    }

    @Override
    public Storie getItem(int position) {
        return stories.get(position);
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
        ViewHolder holder;
        Storie storie;
        ArrayList<String> tagList;
        ArrayList<Boolean> selectedItems = ((AllEntriesActivity) mContext).getSelectedItems();
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.all_entries_rows, null);
            // setup textviews
            holder.main = (TextView) view.findViewById(R.id.all_entries_list_main_text);
//            holder.location = (TextView) view.findViewById(R.id.all_entries_list_location);
            holder.date = (TextView) view.findViewById(R.id.all_entries_list_time_date);
            holder.month = (TextView) view.findViewById(R.id.all_entries_list_time_month);
            holder.tags = (LinearLayout) view.findViewById(R.id.all_entries_list_tags);
            holder.checkBox = (CheckBox) view.findViewById(R.id.all_entries_checkbox);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.main.setText("");
//            holder.location.setText("");
            holder.date.setText("");
            holder.month.setText("");
            holder.tags.removeAllViews();
//            holder.checkBox.setVisibility(View.GONE);
        }
        storie = stories.get(position);
        tagList = storie.getmTagList();
        holder.main.setText(storie.getmMainText());
//        Log.e(TAG, "getView: main text is " + storie.getmMainText());
//        holder.location.setText(storie.getmLocation());
        holder.date.setText(storie.getmDay());
        holder.month.setText(storie.getmMonth().substring(0, 3));
        // add tags programmatically to the right of location
        if (tagList != null) {
            for (String tag : tagList) {
                View v = inflater.inflate(R.layout.all_entries_row_tag,null);
                TextView tagView = (TextView) v.findViewById(R.id.tag_item);
                tagView.setText("#" + tag);
//                tagView.setTextColor(view.getResources().getColor(R.color.black));
//                tagView.setPadding(7, 7, 7, 7);
//                tagView.setTextSize(10);
//                tagView.setBackgroundResource(R.drawable.background_tag);
                ((ViewGroup)tagView.getParent()).removeView(tagView);
                holder.tags.addView(tagView);
            }
        }
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
