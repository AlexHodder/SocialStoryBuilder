package com.example.socialstorybuilder;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;


import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import static android.database.DatabaseUtils.queryNumEntries;

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

    public static TreeMap<String, String> getChildUserMap(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.ChildUserEntry.COLUMN_NAME, DatabaseNameHelper.ChildUserEntry._ID};

        Cursor nameCursor = db.query(DatabaseNameHelper.ChildUserEntry.TABLE_NAME, projection, null, null, null, null, DatabaseNameHelper.ChildUserEntry.COLUMN_NAME + " ASC" );
        TreeMap<String, String> childMap = new TreeMap<>();
        while(nameCursor.moveToNext()) {
            String childName = nameCursor.getString(nameCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry.COLUMN_NAME));
            System.out.println(childName);
            String childID = nameCursor.getString(nameCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry._ID));
            childMap.put(childID, childName);
        }
        nameCursor.close();
        db.close();
        return childMap;
    }

    public static ArrayList<IdData> getChildUserArray(Context context) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.ChildUserEntry.COLUMN_NAME, DatabaseNameHelper.ChildUserEntry._ID};

        Cursor nameCursor = db.query(DatabaseNameHelper.ChildUserEntry.TABLE_NAME, projection, null, null, null, null, DatabaseNameHelper.ChildUserEntry.COLUMN_NAME + " ASC" );
        ArrayList<IdData> childList = new ArrayList<>();
        while(nameCursor.moveToNext()) {
            String childName = nameCursor.getString(nameCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry.COLUMN_NAME));
            String childID = nameCursor.getString(nameCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry._ID));
            childList.add(new IdData(childID, childName));
        }
        nameCursor.close();
        db.close();
        return childList;
    }

    public static TreeMap<String, String> getPageMap(Context context, String storyID){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseNameHelper.PageEntry._ID, DatabaseNameHelper.PageEntry.COLUMN_PAGE_NO};
        String selection = DatabaseNameHelper.PageEntry.COLUMN_STORY_ID + "= ?";
        String[] selectionArgs = {storyID};

        assert storyID!= null;
        Cursor countCursor = dbRead.query(DatabaseNameHelper.PageEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        TreeMap<String, String> pageMap = new TreeMap<>();
        while(countCursor.moveToNext()) {
            int page = countCursor.getInt(countCursor.getColumnIndex(DatabaseNameHelper.PageEntry.COLUMN_PAGE_NO));
            String pageS = Integer.toString(page);
            String pageID = countCursor.getString(countCursor.getColumnIndex(DatabaseNameHelper.PageEntry._ID));
            pageMap.put(pageID, pageS);
        }
        countCursor.close();
        return pageMap;
    }

    public static ArrayList<IdData> getPageList(Context context, String storyID){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        String[] projection = {DatabaseNameHelper.PageEntry._ID, DatabaseNameHelper.PageEntry.COLUMN_PAGE_NO};
        String selection = DatabaseNameHelper.PageEntry.COLUMN_STORY_ID + "= ?";
        String[] selectionArgs = {storyID};

        assert storyID!= null;
        Cursor countCursor = dbRead.query(DatabaseNameHelper.PageEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        ArrayList<IdData> pageList = new ArrayList<>();
        while(countCursor.moveToNext()) {
            int page = countCursor.getInt(countCursor.getColumnIndex(DatabaseNameHelper.PageEntry.COLUMN_PAGE_NO));
            String pageS = Integer.toString(page);
            String pageID = countCursor.getString(countCursor.getColumnIndex(DatabaseNameHelper.PageEntry._ID));
            pageList.add(new IdData(pageID, pageS));
        }
        countCursor.close();
        return pageList;
    }

    public static TreeMap<String, String> getUserMap(Context context, String storyID){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] user_projection = {DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID};
        String user_selection = DatabaseNameHelper.UserStoryEntry.COLUMN_STORY_ID + " = ?";
        String[] user_selection_args = {storyID};

        Cursor userCursor = db.query(DatabaseNameHelper.UserStoryEntry.TABLE_NAME, user_projection, user_selection, user_selection_args, null, null, null);

        TreeMap<String, String> userList = new TreeMap<>();
        while(userCursor.moveToNext()) {
            Integer childID = userCursor.getInt(userCursor.getColumnIndex(DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID));
            String childName = getChildNameFromID(context, childID);
            userList.put(childID.toString(), childName);
        }
        userCursor.close();
        return userList;
    }

    public static ArrayList<IdData> getUserList(Context context, String storyID){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] user_projection = {DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID};
        String user_selection = DatabaseNameHelper.UserStoryEntry.COLUMN_STORY_ID + " = ?";
        String[] user_selection_args = {storyID};

        Cursor userCursor = db.query(DatabaseNameHelper.UserStoryEntry.TABLE_NAME, user_projection, user_selection, user_selection_args, null, null, null);

        ArrayList<IdData> userList = new ArrayList<>();
        while(userCursor.moveToNext()) {
            Integer childID = userCursor.getInt(userCursor.getColumnIndex(DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID));
            String childName = getChildNameFromID(context, childID);
            userList.add(new IdData(childID.toString(), childName));
        }
        userCursor.close();
        return userList;
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

    public static TreeMap<String, String> getStoryMap(Context context, String user){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry._ID, DatabaseNameHelper.StoryEntry.COLUMN_TITLE};
        String selection = DatabaseNameHelper.StoryEntry.COLUMN_AUTHOR + " = ?";
        String[] selectionArgs = {user};

        Cursor cursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, DatabaseNameHelper.StoryEntry.COLUMN_TITLE + " ASC");

        TreeMap<String, String> map = new TreeMap<>();
        while(cursor.moveToNext()) {
            String storyTitle = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
            String storyID = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry._ID));
            map.put(storyID, storyTitle);
        }

        cursor.close();
        db.close();
        return map;
    }

    public static ArrayList<IdData> getAdultStoryList(Context context, String user){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry._ID, DatabaseNameHelper.StoryEntry.COLUMN_TITLE};
        String selection = DatabaseNameHelper.StoryEntry.COLUMN_AUTHOR + " = ?";
        String[] selectionArgs = {user};

        Cursor cursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, DatabaseNameHelper.StoryEntry.COLUMN_TITLE + " ASC");

        ArrayList<IdData> list = new ArrayList<>();
        while(cursor.moveToNext()) {
            String storyTitle = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
            String storyID = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry._ID));
            list.add(new IdData(storyID, storyTitle));
        }
        cursor.close();
        db.close();
        return list;
    }


    public static TreeMap<String, String> getChildStoryMap(Context context, String user_id) {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.UserStoryEntry.COLUMN_STORY_ID};
        String selection = DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID + " = ?";
        String[] selectionArgs = {user_id};
        Cursor storyCursor = db.query(DatabaseNameHelper.UserStoryEntry.TABLE_NAME, projection, selection, selectionArgs,null ,null, null);

        ArrayList<String> list = new ArrayList<>();
        while (storyCursor.moveToNext()){
            list.add(storyCursor.getString(storyCursor.getColumnIndex(DatabaseNameHelper.UserStoryEntry.COLUMN_STORY_ID)));
        }
        storyCursor.close();

        StringBuilder inQuery = new StringBuilder();

        inQuery.append("(");
        boolean first = true;
        for (String item : list) {
            if (first) {
                first = false;
                inQuery.append("'").append(item).append("'");
            } else {
                inQuery.append(", '").append(item).append("'");
            }
        }
        inQuery.append(")");

        String[] projectionStory = {DatabaseNameHelper.StoryEntry.COLUMN_TITLE, DatabaseNameHelper.StoryEntry._ID};
        String selectionString = DatabaseNameHelper.StoryEntry._ID + " IN " + inQuery.toString();

        Cursor cursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projectionStory, selectionString, null, null, null, DatabaseNameHelper.StoryEntry.COLUMN_TITLE + " ASC");

        TreeMap<String, String> storyMap = new TreeMap<>();

        while(cursor.moveToNext()) {
            String storyTitle = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
            String storyId = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry._ID));
            storyMap.put(storyId, storyTitle);
        }
        cursor.close();
        db.close();
        return storyMap;
    }

    public static String getAvatarURI(Context context, String user){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.ChildUserEntry.COLUMN_AVATAR};
        String selection = DatabaseNameHelper.ChildUserEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { user };

        Cursor avatarFileCursor = db.query(DatabaseNameHelper.ChildUserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        avatarFileCursor.moveToNext();
        String avatarFile = avatarFileCursor.getString(avatarFileCursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry.COLUMN_AVATAR));
        avatarFileCursor.close();
        return avatarFile;
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

    public static boolean isStoryReadable(Context context, String story_id){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseNameHelper.PageEntry.COLUMN_STORY_ID + " = ?";
        String[] selectionArgs = {story_id};
        long numEntries = DatabaseUtils.queryNumEntries(db, DatabaseNameHelper.PageEntry.TABLE_NAME, selection, selectionArgs);
        return (numEntries > 0);
    }

}
