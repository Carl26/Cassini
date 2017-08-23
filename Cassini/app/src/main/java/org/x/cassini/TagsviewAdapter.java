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
import java.util.Arrays;

/**
 * Created by shuangyang on 11/7/17.
 */

class TagsViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private Context mContext;
    private ArrayList<String> mTagList;
    private int[] mAlphabetIndex;
    private String TAG = "TagsViewAdapter";

    static class VHAlphabet extends RecyclerView.ViewHolder {
        TextView tagAlphabet;
        View divider;

        VHAlphabet(View view) {
            super(view);
            tagAlphabet = (TextView) view.findViewById(R.id.tag_alphabet);
            divider = view.findViewById(R.id.tag_divider);
        }

    }

    static class VHTag extends RecyclerView.ViewHolder {
        TextView tagName;
        ImageView goTo;

        VHTag(View view) {
            super(view);
            tagName = (TextView) view.findViewById(R.id.tag_name);
            goTo = (ImageView) view.findViewById(R.id.tag_goTo);
        }
    }

    public TagsViewAdapter(ArrayList<String> tagList, Context context) {
        mContext = context;
        mTagList = tagList;
    }

    public void setmAlphabetIndex(int[] alphabetIndex) {
        mAlphabetIndex = alphabetIndex;
    }

    public static boolean contains(int[] arr, int i) {
        for(int s: arr){
            if(s == i)
                return true;
        }
        return false;
    }

    @Override
    public int getItemViewType(int position) {
        if(contains(mAlphabetIndex,position)) {
            return 0;
        }
        else {
            return 1;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.tag_block_title, parent, false);
            VHAlphabet alphabetHolder = new TagsViewAdapter.VHAlphabet(view);
            return alphabetHolder;
        }
        else{
            View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.tagsview_tag, parent, false);
            VHTag tagHolder = new TagsViewAdapter.VHTag(v);
            return tagHolder;
        }
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder.getItemViewType() == 0) {
            VHAlphabet alphabetHolder = (VHAlphabet) holder;
            alphabetHolder.tagAlphabet.setText(mTagList.get(position));
            if (contains(mAlphabetIndex,position + 1)) {
                alphabetHolder.tagAlphabet.setVisibility(View.GONE);
                alphabetHolder.divider.setVisibility(View.GONE);
            }
            else if(position == mTagList.size() - 1) {
                alphabetHolder.tagAlphabet.setVisibility(View.GONE);
                alphabetHolder.divider.setVisibility(View.GONE);
            }
        }
        else {
            VHTag tagHolder = (VHTag) holder;
            tagHolder.tagName.setText("#" + mTagList.get(position));
        }
    }


    @Override
    public int getItemCount() {
        return mTagList.size();
    }

}
