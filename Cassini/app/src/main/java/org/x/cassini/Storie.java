package org.x.cassini;

import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/6/23 0023.
 */

class Storie {
    private String mMainText, mLocation, mDateTime,mDay,mMonth;
    private ArrayList<String> mTagList;
    private int weather, emotion, exercise, star;
    private ArrayList<ArrayList<String>> dimensionData;

    public Storie(String dateTime, String location, int weather, int emotion, int exercise, int star,
                  ArrayList<String> tagList, String mainText, ArrayList<ArrayList<String>> dimensionData) {
        mMainText = mainText;
        mLocation = location;
        mDateTime = dateTime;
        mTagList = tagList;
        this.weather = weather;
        this.emotion = emotion;
        this.exercise = exercise;
        this.star = star;
        this.dimensionData = dimensionData;
    }

    public Storie(String dateTime, String location, ArrayList<String> tagList, String mainText) {
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

    public int getWeather() {
        return weather;
    }

    public void setWeather(int weather) {
        this.weather = weather;
    }

    public int getEmotion() {
        return emotion;
    }

    public void setEmotion(int emotion) {
        this.emotion = emotion;
    }

    public int getExercise() {
        return exercise;
    }

    public void setExercise(int exercise) {
        this.exercise = exercise;
    }

    public int getStar() {
        return star;
    }

    public void setStar(int star) {
        this.star = star;
    }

    public ArrayList<ArrayList<String>> getDimensionData() {
        return dimensionData;
    }

    public void setDimensionData(ArrayList<ArrayList<String>> dimensionData) {
        this.dimensionData = dimensionData;
    }

    public String getmDay(){
        return mDateTime.substring(0,2);
    }

    public String getmMonth() {
        String month = mDateTime.substring(3,5);
        String result = "";
        switch (month) {
            case "01":
                result = "Jan";
                break;
            case "02":
                result = "Feb";
                break;
            case "03":
                result = "Mar";
                break;
            case "04":
                result = "Apr";
                break;
            case "05":
                result = "May";
                break;
            case "06":
                result = "Jun";
                break;
            case "07":
                result = "Jul";
                break;
            case "08":
                result = "Aug";
                break;
            case "09":
                result = "Sep";
                break;
            case "10":
                result = "Oct";
                break;
            case "11":
                result = "Nov";
                break;
            case "12":
                result = "Dec";
                break;
        }
        return result;
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Storie && mDateTime.equals(((Storie) o).getmDateTime());
    }
}
