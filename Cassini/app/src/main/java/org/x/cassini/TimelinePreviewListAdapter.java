package org.x.cassini;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.text.DateFormatSymbols;
import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/7/23 0023.
 */

class TimelinePreviewListAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<String> info, monthList, dayList;

    TimelinePreviewListAdapter(Context mContext, ArrayList<ArrayList<String>> result) {
        this.mContext = mContext;
        monthList = result.get(0);
        dayList = result.get(1);
        info = result.get(2);
        Log.d("TP", "TimelinePreviewListAdapter: " + monthList + dayList + info);
    }

    class ViewHolder {
       TextView day, month, main;
    }

    @Override
    public int getCount() {
        return dayList.size();
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
        ViewHolder holder;
        Log.d("TP", "getView: getting views");
        if (convertView == null) {
            holder = new ViewHolder();
            view = inflater.inflate(R.layout.timeline_preview_card, null);
            // setup textviews
            holder.main = (TextView) view.findViewById(R.id.tp_card_main);
            holder.day = (TextView) view.findViewById(R.id.tp_card_date);
            holder.month = (TextView) view.findViewById(R.id.tp_card_month);
            view.setTag(holder);
        } else {
            Log.e("TP", "getView: convert view not null");
            view = convertView;
            holder = (ViewHolder) view.getTag();
            holder.main.setText("");
            holder.day.setText("");
            holder.month.setText("");
        }
        String mainText = "" + info.get(position);
        String dayText = "" + dayList.get(position);
        String monthText = new DateFormatSymbols().getMonths()[Integer.valueOf(monthList.get(position))-1];
        holder.main.setText(mainText);
        holder.day.setText(dayText);
        holder.month.setText(monthText);
        return view;
    }
}
