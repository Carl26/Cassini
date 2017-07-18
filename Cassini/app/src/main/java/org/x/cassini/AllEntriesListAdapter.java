package org.x.cassini;

import android.content.Context;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
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
    private SparseBooleanArray mSelectedItems;

    public AllEntriesListAdapter(Context context, ArrayList<Storie> stories) {
        mContext = context;
        this.stories = stories;
        mSelectedItems = new SparseBooleanArray();
    }

    class ViewHolder {
        TextView main, location, date;
        LinearLayout tags;
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

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view;
        ViewHolder holder;
        Storie storie;
        ArrayList<String> tagList;
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.all_entries_rows, null);
            // setup textviews
            holder.main = (TextView) view.findViewById(R.id.all_entries_list_main_text);
            holder.location = (TextView) view.findViewById(R.id.all_entries_list_location);
            holder.date = (TextView) view.findViewById(R.id.all_entries_list_time);
            holder.tags = (LinearLayout) view.findViewById(R.id.all_entries_list_tags);
            view.setTag(holder);
        } else {
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.main.setText("");
            holder.location.setText("");
            holder.date.setText("");
            holder.tags.removeAllViews();
        }
        storie = stories.get(position);
        tagList = storie.getmTagList();
        holder.main.setText(storie.getmMainText());
//        Log.e(TAG, "getView: main text is " + storie.getmMainText());
        holder.location.setText(storie.getmLocation());
        holder.date.setText(storie.getmDateTime());
        // add tags programmatically to the right of location
        if (tagList != null) {
            for (String tag : tagList) {
                TextView tagView = new TextView(mContext);
                tagView.setTextColor(view.getResources().getColor(R.color.black));
                tagView.setText(tag);
                tagView.setPadding(5, 5, 5, 5);
                holder.tags.addView(tagView);
            }
        }
        return view;
    }

    public void remove(Storie item) {
        stories.remove(item);
    }

    public void toggleSelection(int position) {
        if (!mSelectedItems.get(position)) { // not selected before
            mSelectedItems.put(position, true);
        } else {
            mSelectedItems.delete(position);
        }
        notifyDataSetChanged();
    }

    public int getSelectedCount() {
        return mSelectedItems.size();
    }

    public SparseBooleanArray getmSelectedItems() {
        return mSelectedItems;
    }

    public void clearSelection() {
        mSelectedItems = new SparseBooleanArray();
        notifyDataSetChanged();
    }
}
