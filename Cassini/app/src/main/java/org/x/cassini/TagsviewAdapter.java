package org.x.cassini;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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

    public TagsViewAdapter(ArrayList<TagBlock> tagBlocks, Context context) {
        mContext = context;
        mTagBlocks = tagBlocks;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType){
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_block, parent, false);
        ViewHolder holder = new ViewHolder(view);
        Log.d(TAG,"On Create ViewHolder");
        return holder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        TagBlock tagBlock = mTagBlocks.get(position);
        holder.tagAlphabet.setText(String.valueOf(tagBlock.getmAlphabet()));
        Log.d(TAG,"Bind Tags with Alphabet" + tagBlock.getmAlphabet());
        LinearLayoutManager layoutManager = new LinearLayoutManager(mContext);
        holder.tagList.setLayoutManager(layoutManager);
        final TagBlockAdapter mTagListAdapter = new TagBlockAdapter(tagBlock.getmTagNames());
        Log.d(TAG,"Created new TagBlockAdapter with " + tagBlock.getmTagNames());
        holder.tagList.setAdapter(mTagListAdapter);
        Log.d(TAG,"Set Adapter for tags with Alphabet " + tagBlock.getmAlphabet() + " and is_empty = " + tagBlock.isEmpty());
        if(tagBlock.isEmpty()) {
            holder.tagAlphabet.setVisibility(View.GONE);
            holder.divider.setVisibility(View.GONE);
            holder.tagList.setVisibility(View.GONE);
        }
        ItemClickSupport.addTo(holder.tagList)
                .setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
                    @Override
                    public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                        Intent intent = new Intent(mContext,TagEntriesActivity.class);
                        intent.putExtra("Tag",mTagListAdapter.getTag(position));
                        mContext.startActivity(intent);
                    }
                });
    }

    @Override
    public int getItemCount() {
        return mTagBlocks.size();
    }

    public void setmContext(Context context) {
        mContext = context;
    }
}
