package org.x.cassini;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.media.Image;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
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
    private int dimensionId;

    TimelinePreviewListAdapter(Context mContext, ArrayList<ArrayList<String>> result, int dimensionId) {
        this.mContext = mContext;
        monthList = result.get(0);
        dayList = result.get(1);
        info = result.get(2);
        this.dimensionId = dimensionId;
        Log.d("TP", "TimelinePreviewListAdapter: " + monthList + dayList + info + " with id " + dimensionId);
    }

    class ViewHolder {
        TextView day, month, main;
        ImageView image;
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
        Log.d("TP", "getView: getting views with id " + dimensionId);
        if (dimensionId >= -1) {
            Log.d("TP", "getView: load text layout");
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
            String monthText = new DateFormatSymbols().getMonths()[Integer.valueOf(monthList.get(position)) - 1];
            holder.main.setText(mainText);
            holder.day.setText(dayText);
            holder.month.setText(monthText);
        } else {
            Log.d("TP", "getView: load image layout");
            if (convertView == null) {
                holder = new ViewHolder();
                view = inflater.inflate(R.layout.timeline_preview_image_card, null);
                // setup textviews
                holder.image = (ImageView) view.findViewById(R.id.tpi_card_image);
                holder.day = (TextView) view.findViewById(R.id.tpi_card_date);
                holder.month = (TextView) view.findViewById(R.id.tpi_card_month);
                view.setTag(holder);
            } else {
                Log.e("TPI", "getView: convert view not null");
                view = convertView;
                holder = (ViewHolder) view.getTag();
                holder.image.setImageDrawable(null);
                holder.day.setText("");
                holder.month.setText("");
            }
            int iconInfo = Integer.valueOf(info.get(position));
            String dayText = "" + dayList.get(position);
            String monthText = new DateFormatSymbols().getMonths()[Integer.valueOf(monthList.get(position)) - 1];
            Drawable temp = findDrawable(iconInfo);
            holder.image.setImageDrawable(temp);
            holder.day.setText(dayText);
            holder.month.setText(monthText);
        }
        return view;
    }

    private Drawable findDrawable(int iconInfo) {
        Drawable temp = null;
        if (dimensionId == -4) {
            switch (iconInfo) {
                case 0: temp = mContext.getResources().getDrawable(R.drawable.ic_sunny, mContext.getTheme());
                    break;
                case 1: temp = mContext.getResources().getDrawable(R.drawable.ic_cloudy, mContext.getTheme());
                    break;
                case 2: temp = mContext.getResources().getDrawable(R.drawable.ic_rainy, mContext.getTheme());
                    break;
                case 3: temp = mContext.getResources().getDrawable(R.drawable.ic_heavy_rain, mContext.getTheme());
                    break;
                case 4: temp = mContext.getResources().getDrawable(R.drawable.ic_thunderstorm, mContext.getTheme());
                    break;
                case 5: temp = mContext.getResources().getDrawable(R.drawable.ic_snow, mContext.getTheme());
                    break;
            }
        } else if (dimensionId == -3) {
            switch (iconInfo) {
                case 0: temp = mContext.getResources().getDrawable(R.drawable.ic_happy, mContext.getTheme());
                    break;
                case 1: temp = mContext.getResources().getDrawable(R.drawable.ic_sad, mContext.getTheme());
                    break;
                case 2: temp = mContext.getResources().getDrawable(R.drawable.ic_neutral, mContext.getTheme());
                    break;
                case 3: temp = mContext.getResources().getDrawable(R.drawable.ic_angry, mContext.getTheme());
                    break;
                case 4: temp = mContext.getResources().getDrawable(R.drawable.ic_embarrassed, mContext.getTheme());
                    break;
                case 5: temp = mContext.getResources().getDrawable(R.drawable.ic_kiss, mContext.getTheme());
                    break;
            }
        } else if (dimensionId == -2) {
            switch (iconInfo) {
                case 0: temp = mContext.getResources().getDrawable(R.drawable.ic_walk, mContext.getTheme());
                    break;
                case 1: temp = mContext.getResources().getDrawable(R.drawable.ic_run, mContext.getTheme());
                    break;
                case 2: temp = mContext.getResources().getDrawable(R.drawable.ic_ball, mContext.getTheme());
                    break;
                case 3: temp = mContext.getResources().getDrawable(R.drawable.ic_cycling, mContext.getTheme());
                    break;
                case 4: temp = mContext.getResources().getDrawable(R.drawable.ic_swim, mContext.getTheme());
                    break;
            }
        }
        return temp;
    }
}
