package com.example.socialstorybuilder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;


import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

import java.util.ArrayList;
import java.util.HashMap;

public class ActivityHelper extends AppCompatActivity {

    public static final int GALLERY_REQUEST_CODE = 123;

    public ActivityHelper(){

    }


    public static ArrayList<String> getChildUsers(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.ChildUserEntry.COLUMN_NAME};

        Cursor nameCursor = db.query(DatabaseNameHelper.ChildUserEntry.TABLE_NAME, projection, null, null, null, null, null);
        ArrayList<String> childList = new ArrayList<>();
        while(nameCursor.moveToNext()) {
            String childName = nameCursor.getString(nameCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry.COLUMN_NAME));
            childList.add(childName);
        }
        nameCursor.close();
        db.close();
        return childList;
    }

    public static HashMap<String, String> getChildUserMap(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.ChildUserEntry.COLUMN_NAME, DatabaseNameHelper.ChildUserEntry._ID};

        Cursor nameCursor = db.query(DatabaseNameHelper.ChildUserEntry.TABLE_NAME, projection, null, null, null, null, null);
        HashMap<String, String> childMap = new HashMap<>();
        while(nameCursor.moveToNext()) {
            String childName = nameCursor.getString(nameCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry.COLUMN_NAME));
            String childID = nameCursor.getString(nameCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry._ID));
            childMap.put(childID, childName);
        }
        nameCursor.close();
        db.close();
        return childMap;
    }


    public static ArrayList<String> getAdultStoryId(Context context, String user) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry._ID};
        String selection = DatabaseNameHelper.StoryEntry.COLUMN_AUTHOR + " = ?";
        String[] selectionArgs = {user};

        Cursor cursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, DatabaseNameHelper.StoryEntry.COLUMN_TITLE + " ASC");

        ArrayList<String> storyList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String storyTitle = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry._ID));
            storyList.add(storyTitle);
        }
        cursor.close();
        db.close();
        return storyList;
    }

    public static HashMap<String, String> getStoryMap(Context context, String user){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry._ID, DatabaseNameHelper.StoryEntry.COLUMN_TITLE};
        String selection = DatabaseNameHelper.StoryEntry.COLUMN_AUTHOR + " = ?";
        String[] selectionArgs = {user};

        Cursor cursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, DatabaseNameHelper.StoryEntry.COLUMN_TITLE + " ASC");

        HashMap<String, String> map = new HashMap<>();
        while(cursor.moveToNext()) {
            String storyTitle = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
            String storyID = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry._ID));
            map.put(storyID, storyTitle);
        }

        cursor.close();
        db.close();
        return map;
    }

    public static ArrayList<String> getChildStories(Context context, String user_id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry.COLUMN_TITLE};
        String selection = DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {user_id};

        Cursor cursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, DatabaseNameHelper.StoryEntry.COLUMN_TITLE + " ASC");
        ArrayList<String> storyList = new ArrayList<>();
        while(cursor.moveToNext()) {
            String storyTitle = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
            storyList.add(storyTitle);
        }
        cursor.close();
        db.close();
        return storyList;
    }

    public static String getStoryNameFromID(Context context, String id){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry.COLUMN_TITLE};
        String selection = DatabaseNameHelper.StoryEntry._ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        String title = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
        cursor.close();
        db.close();
        return title;
    }

    public static String getChildNameFromID(Context context, Integer id){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.ChildUserEntry.COLUMN_NAME};
        String selection = DatabaseNameHelper.ChildUserEntry._ID + " = ?";
        String[] selectionArgs = {id.toString()};

        Cursor cursor = db.query(DatabaseNameHelper.ChildUserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry.COLUMN_NAME));
        cursor.close();
        db.close();
        return name;
    }

}
