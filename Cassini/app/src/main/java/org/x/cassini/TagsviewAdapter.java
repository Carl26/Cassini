package org.x.cassini;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by shuangyang on 11/7/17.
 */

class TagsViewAdapter extends RecyclerView.Adapter<TagsViewAdapter.ViewHolder> {
    private Context mContext;
    private ArrayList<TagBlock> mTagBlocks;
    private String TAG = "TagsViewAdapter";

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView tagAlphabet;
        View divider;
        RecyclerView tagList;

        ViewHolder(View view) {
            super(view);
            tagAlphabet = (TextView) view.findViewById(R.id.tag_alphabet);
            divider = (View) view.findViewById(R.id.tag_divider);
            tagList = (RecyclerView) view.findViewById(R.id.tag_list);
        }

    }

    public TagsViewAdapter(ArrayList<TagBlock> tagBlocks) {
        mTagBlocks = tagBlocks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_block, parent, false);
        ViewHolder holder = new ViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TagBlock tagBlock = mTagBlocks.get(position);
        holder.tagAlphabet.setText(tagBlock.getmAlphabet());
        TagBlockAdapter adapter = new TagBlockAdapter(tagBlock.getmTagNames());
        holder.tagList.setAdapter(adapter);
    }

    @Override
    public int getItemCount() {
        return mTagBlocks.size();
    }

}
