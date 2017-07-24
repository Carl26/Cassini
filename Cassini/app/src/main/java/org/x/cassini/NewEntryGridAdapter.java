package org.x.cassini;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

/**
 * Created by Guo Mingxuan on 2017/6/20 0020.
 */

class NewEntryGridAdapter extends BaseAdapter {
    private Context mContext;
    private int[] mIcons;
    private String TAG = "GridAdapter";

    public NewEntryGridAdapter(Context context, int[] icons) {
        mContext = context;
        mIcons = icons;
        Log.d(TAG, "NewEntryGridAdapter: created");
        for (int i : mIcons) {
            Log.d(TAG, "NewEntryGridAdapter: int array item is " + i);
        }
    }

    class ViewHolder {
        ImageView image;
    }

    @Override
    public int getCount() {
        return mIcons.length;
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
        int whichIcon;
        ViewHolder holder;
        if (convertView == null) {
            Log.d(TAG, "getView: convertView is null");
            grid = inflater.inflate(R.layout.new_entry_items, null);
            holder = new ViewHolder();
            holder.image = (ImageView) grid.findViewById(R.id.new_entry_item);
            grid.setTag(holder);
        } else {
            grid = convertView;
            holder = (ViewHolder) grid.getTag();
            holder.image.setImageDrawable(null);
        }

        whichIcon = mIcons[position];
        Log.d(TAG, "getView: whichIcon value = " + whichIcon);
        switch (whichIcon) {
            // sunny
            case 0: holder.image.setImageResource(R.drawable.ic_sunny);
                break;
            // cloudy
            case 1: holder.image.setImageResource(R.drawable.ic_cloudy);
                break;
            // rainy
            case 2: holder.image.setImageResource(R.drawable.ic_rainy);
                break;
            // heavy rain
            case 3: holder.image.setImageResource(R.drawable.ic_heavy_rain);
                break;
            // thunderstorm
            case 4: holder.image.setImageResource(R.drawable.ic_thunderstorm);
                break;
            // snow
            case 5: holder.image.setImageResource(R.drawable.ic_snow);
                break;
            // happy
            case 6: holder.image.setImageResource(R.drawable.ic_happy);
                break;
            // sad
            case 7: holder.image.setImageResource(R.drawable.ic_sad);
                break;
            // neutral
            case 8: holder.image.setImageResource(R.drawable.ic_neutral);
                break;
            // angry
            case 9: holder.image.setImageResource(R.drawable.ic_angry);
                break;
            // embarrassed
            case 10: holder.image.setImageResource(R.drawable.ic_embarrassed);
                break;
            // kiss
            case 11: holder.image.setImageResource(R.drawable.ic_kiss);
                break;
            // walk
            case 12: holder.image.setImageResource(R.drawable.ic_walk);
                break;
            // run
            case 13: holder.image.setImageResource(R.drawable.ic_run);
                break;
            // ball
            case 14: holder.image.setImageResource(R.drawable.ic_ball);
                break;
            // cycling
            case 15: holder.image.setImageResource(R.drawable.ic_cycling);
                break;
            // swim
            case 16: holder.image.setImageResource(R.drawable.ic_swim);
                break;
        }
        return grid;
    }
}
