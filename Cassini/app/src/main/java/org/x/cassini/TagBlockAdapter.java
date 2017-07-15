package org.x.cassini;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shuangyang on 9/7/17.
 */


class TagBlockAdapter extends RecyclerView.Adapter<TagBlockAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<String> tagNames;
    private String TAG = "TagsBlockAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tagName;
        ImageView goTo;

        public ViewHolder(View view) {
            super(view);
            tagName = (TextView) view.findViewById(R.id.tagNames);
            goTo = (ImageView) view.findViewById(R.id.tagBlock_goTo);
        }

    }

    public TagBlockAdapter(ArrayList<String> tagsList) {
        tagNames = tagsList;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tagsview_tag, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        String tagName = tagNames.get(position);
        holder.tagName.setText(tagName);
    }

    @Override
    public int getItemCount() {
        return tagNames.size();
    }

}
