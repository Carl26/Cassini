package org.x.cassini;

import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/6/23 0023.
 */

class Storie {
    private String mMainText, mLocation, mDateTime;
    private ArrayList<String> mTagList;
    public Storie(String mainText, String location, String dateTime, ArrayList<String> tagList) {
        mMainText = mainText;
        mLocation = location;
        mDateTime = dateTime;
        mTagList = tagList;
    }

    public String getmMainText() {
        return mMainText;
    }

    public void setmMainText(String mMainText) {
        this.mMainText = mMainText;
    }

    public String getmLocation() {
        return mLocation;
    }

    public void setmLocation(String mLocation) {
        this.mLocation = mLocation;
    }

    public String getmDateTime() {
        return mDateTime;
    }

    public void setmDateTime(String mDateTime) {
        this.mDateTime = mDateTime;
    }

    public ArrayList<String> getmTagList() {
        return mTagList;
    }

    public void setmTagList(ArrayList<String> mTagList) {
        this.mTagList = mTagList;
    }
}
