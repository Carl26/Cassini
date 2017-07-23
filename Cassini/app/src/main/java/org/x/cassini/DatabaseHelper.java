package org.x.cassini;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;

/**
 * Created by Guo Mingxuan on 2017/7/11 0011.
 */

public class DatabaseHelper extends SQLiteOpenHelper {
    private static String DATABASE_NAME = "Storie.db";
    // for entry table
    private static String TABLE_ENTRY_NAME = "entry_table";
    private static String COL_ID = "ID";
    private static String COL_DATE = "DATE";
    private static String COL_LOCATION = "LOCATION";
    private static String COL_WEATHER = "WEATHER";
    private static String COL_EMOTION = "EMOTION";
    private static String COL_EXERCISE = "EXERCISE";
    private static String COL_STAR = "STAR";
    private static String COL_TAG = "TAG";
    private static String COL_MAIN_TEXT = "MAINTEXT";
    private static String COL_DIMENSION_POINTER = "DIMENSION_POINTER";
    private static String COL_DIMENSION_1 = "D1";

    // for tag table
    private static String TABLE_TAG_NAME = "tag_table";
    private static String COL_ENTRY_ID = "ENTRY_ID";

    private String dimensionHolder = "";

    // read version from config file or input version + 1 as new version for dimension update
    public DatabaseHelper(Context context, int version) {
        super(context, DATABASE_NAME, null, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_TABLE_ENTRY = "CREATE TABLE IF NOT EXISTS " + TABLE_ENTRY_NAME
                + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DATE + " TEXT, "
                + COL_LOCATION + " TEXT, "
                + COL_WEATHER + " INTEGER, "
                + COL_EMOTION + " INTEGER, "
                + COL_EXERCISE + " INTEGER, "
                + COL_STAR + " INTEGER, "
                + COL_TAG + " TEXT, "
                + COL_MAIN_TEXT + " TEXT, "
                + COL_DIMENSION_POINTER + " TEXT, "
                + COL_DIMENSION_1 + " TEXT)";

        String CREATE_TABLE_TAG = "CREATE TABLE IF NOT EXISTS " + TABLE_TAG_NAME
                + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_TAG + " TEXT, "
                + COL_ENTRY_ID + " TEXT)";

        db.execSQL(CREATE_TABLE_ENTRY);
        db.execSQL(CREATE_TABLE_TAG);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // add new dimension as column into entry table
            String NEW_COLUMN_NAME = dimensionHolder;
            db.execSQL("ALTER TABLE " + TABLE_ENTRY_NAME + "ADD COLUMN " + NEW_COLUMN_NAME + "TEXT");
        }
    }

    // upgrade db right after calling this
    public void updateDimensions(String newDimension, int oldVersion, int newVersion) {
        dimensionHolder = newDimension;
    }

    public boolean insertData(String date, String location, int weather, int emotion, int exercise, int star,
                        ArrayList<String> tagList, String mainText, ArrayList<ArrayList<String>> dimensionData) {
        Log.d("DB", "loadDiary: loaded info " + " date " + date + " location " + location +
                " weather " + weather + " emotion " + emotion + " exercise " + exercise + " star " + star +
                " tags " + tagList + " maintext " + mainText + " dimension indicators " + dimensionData);
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessful, isTagEmpty;
        Gson gson = new Gson();
        // insert data into entry table
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_LOCATION, location);
        contentValues.put(COL_WEATHER, weather);
        contentValues.put(COL_EMOTION, emotion);
        contentValues.put(COL_EXERCISE, exercise);
        contentValues.put(COL_STAR, star);
        if (tagList.isEmpty()) {
            isTagEmpty = true;
            Log.d("DB", "insertData: tag is empty");
        } else {
            isTagEmpty = false;
            Log.d("DB", "insertData: tag is not empty");
        }
        String tag = gson.toJson(tagList);
        contentValues.put(COL_TAG, tag);
        contentValues.put(COL_MAIN_TEXT, mainText);
        ArrayList<String> dimensionPointer = new ArrayList<>();
        for (ArrayList<String> pair : dimensionData) {
            String COLUMN_HEADER = pair.get(0);
            String data = pair.get(1);
            // forming list of pointers
            dimensionPointer.add(COLUMN_HEADER);
            contentValues.put(COLUMN_HEADER, data);
        }
        String pointers = gson.toJson(dimensionPointer);
        contentValues.put(COL_DIMENSION_POINTER, pointers);
        long id = db.insert(TABLE_ENTRY_NAME, null, contentValues);
        isSuccessful = (id!=-1);

        // only modify tag table if data is inserted
        if (isSuccessful && !isTagEmpty) {
            long tagRes;
            Type type = new TypeToken<ArrayList<String>>(){}.getType();
            // insert data into tag table
            String entryId = Long.toString(id);
            for (String tagItem : tagList) {
                String query = "Select * from " + TABLE_TAG_NAME + " where " + COL_TAG + " = '" + tagItem + "'";
                ContentValues contentValuesForTags = new ContentValues();
                Cursor cursor = db.rawQuery(query, null);
                if (cursor.getCount() <= 0) {
                    // no record found, add tag
//                    String tagJson = gson.toJson(tagItem);
                    ArrayList<String> tempIdArray = new ArrayList<>();
                    tempIdArray.add(entryId);
                    String entryIdJson = gson.toJson(tempIdArray);
                    contentValuesForTags.put(COL_TAG, tagItem);
                    contentValuesForTags.put(COL_ENTRY_ID, entryIdJson);
                    Log.d("DB", "insertData: added a new tag " + tagItem);
                    tagRes = db.insert(TABLE_TAG_NAME, null, contentValuesForTags);
                } else {
                    Log.d("DB", "insertData: tag found");
                    cursor.moveToNext();
                    String tagId = cursor.getString(0);
                    Log.d("DB", "insertData: tag id " + tagId);
//                    // adding new tag into existing filed
                    String tagName = cursor.getString(1);
                    Log.d("DB", "insertData: tag name " + tagName);
//                    ArrayList<String> tagNameArray = gson.fromJson(tagName, type);
//                    tagNameArray.add(tagItem);
//                    String newTagName = gson.toJson(tagNameArray);


                    // adding new entry into existing field
                    String tagEntryId = cursor.getString(2);
                    Log.d("DB", "insertData: tag entry id " + tagEntryId);
                    ArrayList<String> entryIdArray = gson.fromJson(tagEntryId, type);
                    entryIdArray.add(entryId);
                    String newEntryId = gson.toJson(entryIdArray);
                    Log.d("DB", "insertData: new entry id " + newEntryId);

                    contentValuesForTags.put(COL_ID, tagId);
                    contentValuesForTags.put(COL_TAG, tagName);
                    contentValuesForTags.put(COL_ENTRY_ID, newEntryId);
                    int numberOfRows = db.update(TABLE_TAG_NAME, contentValuesForTags, COL_ID + " = ?", new String[] { tagId });
                    tagRes = numberOfRows;
                }
                cursor.close();

                isSuccessful = isSuccessful && (tagRes != -1);
            }
        }
        return isSuccessful;
    }

    public Cursor getAllEntryData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_ENTRY_NAME, null);
        return res;
    }

    public Cursor getAllTagData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_TAG_NAME, null);
        return res;
    }

    public Cursor getTagList(char alphabet){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select TAG from " + TABLE_TAG_NAME + " where TAG like '" + alphabet + "%'", null);
        return res;
    }

    public Cursor getTagEntries(String tagName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_ENTRY_NAME + " where TAG Like '%" + tagName + "%'", null);
        return res;
    }

    public Cursor getEntry(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(query, null);
        return res;
    }
}
