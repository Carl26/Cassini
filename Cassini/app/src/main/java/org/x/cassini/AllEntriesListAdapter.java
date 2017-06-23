package org.x.cassini;

import android.content.Context;
import android.util.Log;
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

    public AllEntriesListAdapter(Context context, ArrayList<Storie> stories) {
        mContext = context;
        this.stories = stories;
    }

    @Override
    public int getCount() {
        return stories.size();
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
        View view;
        TextView main, location, date;
        ArrayList<String> tagList;
        Storie storie;
        if (convertView == null) {
            storie = stories.get(position);
            tagList = storie.getmTagList();
            view = inflater.inflate(R.layout.all_entries_rows, null);
            // setup textviews
            main = (TextView) view.findViewById(R.id.all_entries_list_main_text);
            main.setText(storie.getmMainText());
            location = (TextView) view.findViewById(R.id.all_entries_list_location);
            location.setText(storie.getmLocation());
            date = (TextView) view.findViewById(R.id.all_entries_list_time);
            date.setText(storie.getmDateTime());
            // add tags programmatically to the right of location
            LinearLayout tags = (LinearLayout) view.findViewById(R.id.all_entries_list_tags);
            for (String tag : tagList) {
                TextView tagView = new TextView(mContext);
                tagView.setTextColor(view.getResources().getColor(R.color.black));
                tagView.setText(tag);
                tagView.setPadding(5, 5, 5, 5);
                tags.addView(tagView);
            }
        } else {
            view = convertView;
            Log.e(TAG, "getView: convertView is not null");
        }
        return view;
    }
}
