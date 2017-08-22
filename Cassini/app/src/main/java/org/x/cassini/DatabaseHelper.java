package org.x.cassini;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Iterator;

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

    // for export table
    private static String TABLE_EXPORT_NAME = "export_table";
    private static String COL_INFO = "INFO";
    private static String COL_DIMENSION_ID = "DIMENSION_ID";

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

        String CREATE_TABLE_EXPORT = "CREATE TABLE IF NOT EXISTS " + TABLE_EXPORT_NAME
                + "(" + COL_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + COL_DIMENSION_ID + " INTEGER, "
                + COL_INFO + " TEXT)";

        db.execSQL(CREATE_TABLE_ENTRY);
        db.execSQL(CREATE_TABLE_TAG);
        db.execSQL(CREATE_TABLE_EXPORT);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (newVersion > oldVersion) {
            // add new dimension as column into entry table
            int count = newVersion - oldVersion;
            for (int i = 1; i <= count; i++) {
                int columnId = oldVersion + i;
                String NEW_COLUMN_NAME = "D" + columnId;
                Log.d("DB", "onUpgrade: new column id is " + NEW_COLUMN_NAME);
                db.execSQL("ALTER TABLE " + TABLE_ENTRY_NAME + " ADD COLUMN " + NEW_COLUMN_NAME + " TEXT");
            }
        }
        Log.e("DB", "onUpgrade: upgrade complete");
    }

    public int saveData(int dimensionId, String info) {
        SQLiteDatabase db = this.getWritableDatabase();
        // check whether the info is already set in db
        String findDuplicate = "Select * from " + TABLE_EXPORT_NAME + " where " + COL_INFO + " = ? and " + COL_DIMENSION_ID + " = ? ";
        String idString = "" + dimensionId;
        Cursor cursor = db.rawQuery(findDuplicate, new String[] {info, idString});
        if (cursor.getCount() > 0) {
            cursor.close();
            // found duplicate
            return 0;
        } else {
            cursor.close();
            ContentValues contentValues = new ContentValues();
            contentValues.put(COL_DIMENSION_ID, dimensionId);
            contentValues.put(COL_INFO, info);
            long res = db.insert(TABLE_EXPORT_NAME, null, contentValues);
            if (res != -1) {
                // success
                return 1;
            } else {
                // failed
                return -1;
            }
        }
    }

    // upgrade db right after calling this
//    public void updateDimensions(String newDimension, int oldVersion, int newVersion) {
//        dimensionHolder = newDimension;
//        Log.d("DB", "updateDimensions: old version is " + oldVersion + " new version is " + newVersion + " and new dimension is " + dimensionHolder);
//    }

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

    public boolean updateData(String id, String date, String location, int weather, int emotion, int exercise, int star,
                              ArrayList<String> tagList, ArrayList<String> oldTagList,
                              String mainText, ArrayList<ArrayList<String>> dimensionData) {
        Log.d("DB", "update data: loaded info id " + id + " date " + date + " location " + location +
                " weather " + weather + " emotion " + emotion + " exercise " + exercise + " star " + star +
                " tags " + tagList + " old tagList " + oldTagList + " maintext " + mainText + " dimension indicators " + dimensionData);
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isSuccessful, isTagEmpty;
        Gson gson = new Gson();
        // insert data into entry table
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_ID, id);
        contentValues.put(COL_DATE, date);
        contentValues.put(COL_LOCATION, location);
        contentValues.put(COL_WEATHER, weather);
        contentValues.put(COL_EMOTION, emotion);
        contentValues.put(COL_EXERCISE, exercise);
        contentValues.put(COL_STAR, star);
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
        Log.d("DB", "updateData: dimension pointers " + pointers);
        int numberOfRows = db.update(TABLE_ENTRY_NAME, contentValues, COL_ID + " = ?", new String[] { id });
        isSuccessful = (numberOfRows>0);

        // update tag table if needed
        ArrayList<String> deletedTags = new ArrayList<>();
        ArrayList<String> newTags = new ArrayList<>();
        long tagRes;
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        for (int i = 0; i < oldTagList.size(); i++) {
            String item = oldTagList.get(i);
            boolean toBeDeleted = true;
            if (!tagList.contains(item)) {
                deletedTags.add(item);
                toBeDeleted = false;
                Log.d("DB", "updateData: deleted tags added " + item);
            }
            if (toBeDeleted) {
                boolean isDeleted = tagList.remove(item);
                Log.d("DB", "updateData: is item " + item + " removed " + isDeleted);
            }
        }
        for (String remainingItem : tagList) {
            newTags.add(remainingItem);
        }
        for (String what : newTags) {
            Log.d("DB", "updateData: new tag contains " + what);
        }
        // remove deleted tags
        if (!deletedTags.isEmpty()) {
            for (String deletedItem : deletedTags) {
                String query = "Select * from " + TABLE_TAG_NAME + " where " + COL_TAG + " = '" + deletedItem + "'";
                ContentValues contentValuesForTags = new ContentValues();
                Cursor cursor = db.rawQuery(query, null);
                cursor.moveToNext();
                String tagId = cursor.getString(0);
                Log.d("DB", "updateData: tag id " + tagId);
                String tagName = cursor.getString(1);
                Log.d("DB", "updateData: tag name " + tagName);
                String tagEntryId = cursor.getString(2);
                Log.d("DB", "updateData: tag entry id " + tagEntryId);
                ArrayList<String> entryIdArray = gson.fromJson(tagEntryId, type);
                entryIdArray.remove(id);
                String newEntryId = gson.toJson(entryIdArray);
                Log.d("DB", "insertData: new entry id " + newEntryId);

                contentValuesForTags.put(COL_ID, tagId);
                contentValuesForTags.put(COL_TAG, tagName);
                contentValuesForTags.put(COL_ENTRY_ID, newEntryId);
                int tagNumberOfRows = db.update(TABLE_TAG_NAME, contentValuesForTags, COL_ID + " = ?", new String[] { tagId });
                tagRes = tagNumberOfRows;
                cursor.close();
                isSuccessful = isSuccessful && (tagRes != -1);
            }
        }
        // add in new tags
        if (!newTags.isEmpty()) {
            for (String newTagItem : newTags) {
                String query = "Select * from " + TABLE_TAG_NAME + " where " + COL_TAG + " = '" + newTagItem + "'";
                Cursor cursor = db.rawQuery(query, null);
                ContentValues contentValuesForTags = new ContentValues();
                if (cursor.getCount() <= 0) {
                    ArrayList<String> tempIdArray = new ArrayList<>();
                    tempIdArray.add(id);
                    String entryIdJson = gson.toJson(tempIdArray);
                    contentValuesForTags.put(COL_TAG, newTagItem);
                    contentValuesForTags.put(COL_ENTRY_ID, entryIdJson);
                    Log.d("DB", "updateData: added a new tag " + newTagItem);
                    tagRes = db.insert(TABLE_TAG_NAME, null, contentValuesForTags);
                } else {
                    Log.d("DB", "updateData: tag found");
                    cursor.moveToNext();
                    String tagId = cursor.getString(0);
                    Log.d("DB", "updateData: tag id " + tagId);
//                    // adding new tag into existing filed
                    String tagName = cursor.getString(1);
                    Log.d("DB", "updateData: tag name " + tagName);
                    // adding new entry into existing field
                    String tagEntryId = cursor.getString(2);
                    Log.d("DB", "insertData: tag entry id " + tagEntryId);
                    ArrayList<String> entryIdArray = gson.fromJson(tagEntryId, type);
                    entryIdArray.add(id);
                    String newEntryId = gson.toJson(entryIdArray);
                    Log.d("DB", "updateData: new entry id " + newEntryId);

                    contentValuesForTags.put(COL_ID, tagId);
                    contentValuesForTags.put(COL_TAG, tagName);
                    contentValuesForTags.put(COL_ENTRY_ID, newEntryId);
                    tagRes = db.update(TABLE_TAG_NAME, contentValuesForTags, COL_ID + " = ?", new String[] { tagId });
                }
                cursor.close();
                isSuccessful = isSuccessful && (tagRes != -1);
            }
        }
        return isSuccessful;
    }

    public boolean deleteStories(String dimensionId, String info) {
        Log.d("DB", "deleteStories: received id and info are " + dimensionId + " " + info);
        SQLiteDatabase db = this.getWritableDatabase();
        boolean isDeleted;
        String deleteQuery = COL_DIMENSION_ID + " = ? and " + COL_INFO + " = ?";
        int rows = db.delete(TABLE_EXPORT_NAME, deleteQuery, new String[] {dimensionId, info});
        isDeleted = (rows > 0);
        return isDeleted;
    }

    public boolean deleteData(String date, ArrayList<String> tagList) {
        SQLiteDatabase db = this.getWritableDatabase();
        Type type = new TypeToken<ArrayList<String>>(){}.getType();
        Gson gson = new Gson();
        boolean isDeleted;
        // delete in entry table
        Cursor mCursor = db.rawQuery("Select " + COL_ID + " from " + TABLE_ENTRY_NAME + " where " + COL_DATE + " = '" + date + "'", null);
        mCursor.moveToNext();
        String id = mCursor.getString(0);
        Log.d("DB", "deleteData: deleting id " + id);
        mCursor.close();
        isDeleted = db.delete(TABLE_ENTRY_NAME, COL_DATE + " = ?", new String[] { date }) > 0;
        // delete in tag table
        for (String tag : tagList) {
            String query = "Select * from " + TABLE_TAG_NAME + " where " + COL_TAG + " = '" + tag + "'";
            ContentValues contentValuesForTags = new ContentValues();
            Cursor cursor = db.rawQuery(query, null);
            cursor.moveToNext();
            String tagId = cursor.getString(0);
            Log.d("DB", "deleteData: tag id " + tagId);
            String tagName = cursor.getString(1);
            Log.d("DB", "deleteData: tag name " + tagName);
            String tagEntryId = cursor.getString(2);
            Log.d("DB", "deleteData: tag entry id " + tagEntryId);
            ArrayList<String> entryIdArray = gson.fromJson(tagEntryId, type);
            entryIdArray.remove(id);
            String newEntryId = gson.toJson(entryIdArray);
            Log.d("DB", "deleteData: new entry id " + newEntryId);

            contentValuesForTags.put(COL_ID, tagId);
            contentValuesForTags.put(COL_TAG, tagName);
            contentValuesForTags.put(COL_ENTRY_ID, newEntryId);
            int tagNumberOfRows = db.update(TABLE_TAG_NAME, contentValuesForTags, COL_ID + " = ?", new String[] { tagId });
            cursor.close();
            isDeleted = isDeleted && (tagNumberOfRows != -1);
        }
        return isDeleted;
    }

    public Cursor getAllEntryData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_ENTRY_NAME, null);
        return res;
    }

    public Cursor getAllStoriesData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_EXPORT_NAME, null);
        return res;
    }

    public Cursor getAllTagData() {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_TAG_NAME, null);
        return res;
    }


    public Cursor getEntry(String query) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery(query, null);
        return res;
    }

    public Cursor getTagList(char alphabet){
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select TAG from " + TABLE_TAG_NAME + " where TAG like '" + alphabet + "%'", null);
        return res;
    }

    public Cursor getTagEntries(String tagName) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor res = db.rawQuery("select * from " + TABLE_ENTRY_NAME + " where TAG like '%" + tagName + "%'", null);
        return res;
    }

    public ArrayList<ArrayList<String>> getTimeline(String startDate, String endDate, int dimensionId) {
        SQLiteDatabase db = this.getWritableDatabase();
        String columnNeeded;
        if (dimensionId == -4) {
            columnNeeded = COL_WEATHER;
        } else if (dimensionId == -3) {
            columnNeeded = COL_EMOTION;
        } else if (dimensionId == -2) {
            columnNeeded = COL_EXERCISE;
        } else if (dimensionId == -1) {
            columnNeeded = COL_TAG;
        } else {
            columnNeeded = "D" + dimensionId;
        }
        Cursor res = db.rawQuery("select " + COL_ID + "," + COL_DATE + "," + columnNeeded + " from " + TABLE_ENTRY_NAME, null);
        // find starting entry
        long start = Long.valueOf(startDate);
        long end = Long.valueOf(endDate);
        int minId = -1, maxId;
        ArrayList<String> resultInfo = new ArrayList<>();
        ArrayList<String> resultDate = new ArrayList<>();
        ArrayList<String> resultMonth = new ArrayList<>();
        ArrayList<ArrayList<String>> resultBulk = new ArrayList<>();
        Gson gson = new Gson();
        String emptyJson = gson.toJson(new ArrayList<String>());
        while (res.moveToNext()) {
            String date = res.getString(1);
            String day = date.substring(0, 2);
            String month = date.substring(3, 5);
            String year = date.substring(6, 10);
            String currentString = year + month + day;
            long current = Long.valueOf(currentString);
            if (dimensionId >= -1) {
                if (current >= start && current <= end) {
                    // greater than lower range
                    // check if its the first
                    if (minId == -1) {
                        minId = Integer.valueOf(res.getString(0));
                        String info = res.getString(2);
                        if (info != null && !info.equals("") && !info.equalsIgnoreCase(emptyJson)) {
                            resultInfo.add(info);
                            resultDate.add(day);
                            resultMonth.add(month);
                            Log.d("DB", "getTimeline: found the first " + info + " with id " + minId + " at " + month + " " + day);
                        } else {
                            Log.d("DB", "getTimeline: null not first");
                        }
                    } else {
                        maxId = Integer.valueOf(res.getString(0));
                        String info = res.getString(2);
                        if (info != null && !info.equals("") && !info.equalsIgnoreCase(emptyJson)) {
                            resultInfo.add(info);
                            resultDate.add(day);
                            resultMonth.add(month);
                            Log.d("DB", "getTimeline: current included " + info + " id is " + maxId + " at " + month + " " + day);
                        } else {
                            Log.d("DB", "getTimeline: found null entry");
                        }
                    }
                }
            } else {
                if (current >= start && current <= end) {
                    // greater than lower range
                    // check if its the first
                    if (minId == -1) {
                        minId = Integer.valueOf(res.getString(0));
                        String info = res.getString(2);
                        if (Integer.valueOf(info) != -1) {
                            resultInfo.add(info);
                            resultDate.add(day);
                            resultMonth.add(month);
                            Log.d("DB", "getTimeline: found the first " + info + " with id " + minId + " at " + month + " " + day);
                        } else {
                            Log.d("DB", "getTimeline: dimension id " + dimensionId + " at " + month + " " + day + " is not set");
                        }
                    } else {
                        maxId = Integer.valueOf(res.getString(0));
                        String info = res.getString(2);
                        if (Integer.valueOf(info) != -1) {
                            resultInfo.add(info);
                            resultDate.add(day);
                            resultMonth.add(month);
                            Log.d("DB", "getTimeline: current included " + info + " id is " + maxId + " at " + month + " " + day);
                        } else {
                            Log.d("DB", "getTimeline: dimension id " + dimensionId + " at " + month + " " + day + " is not set");
                        }
                    }
                }
            }
        }
        resultBulk.add(resultMonth);
        resultBulk.add(resultDate);
        resultBulk.add(resultInfo);
        return resultBulk;
    }
}
