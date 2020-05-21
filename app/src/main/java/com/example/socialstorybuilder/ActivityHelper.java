package com.example.socialstorybuilder;

import android.content.Context;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;


import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

import java.util.ArrayList;

/**
 * Helper method to reduce repeated code, and store constants.
 *
 * @since 1.0
 */
public class ActivityHelper extends AppCompatActivity {

    public static final int GALLERY_REQUEST_CODE = 123;
    public static final int AUDIO_REQUEST_CODE = 246;

    public ActivityHelper(){
    }

    /**
     *
     * @param context current context of the program
     * @return ArrayList of the colour codes paired with colour.
     */
    public static ArrayList<IdData> colorWheel(Context context){
        ArrayList<IdData> colorList = new ArrayList<>();
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.white)), "White"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.red)), "Red"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.blue)), "Blue"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.green)), "Green"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.yellow)), "Yellow" ));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.purple)), "Purple"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.orange)), "Orange"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.yellow_orange)), "Yellow-Orange"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.yellow_green)), "Yellow-Green"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.red_orange)), "Red-Orange"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.red_purple)), "Red-Purple"));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.blue_purple)), "Blue-Purple" ));
        colorList.add(new IdData('#' + Integer.toHexString(context.getResources().getColor(R.color.blue_green)), "Blue-Green"));

        return colorList;
    }

    /**
     *
     * @param context current context of the program
     * @return ArrayList of the child users in the database.
     */
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

    /**
     *
     * @param context current context of the program
     * @return ArrayList of pairs of current child users, with corresponding ID
     */
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

    /**
     *
     * @param context current context of the program
     * @param storyID to be selected from database
     * @return ArrayList of pairs of current pages, with corresponding ID
     */
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

    /**
     *
     * @param context current context of the program
     * @param storyID to be selected from database
     * @return ArrayList of pairs of current users, with corresponding ID
     */
    public static ArrayList<IdData> getUserList(Context context, String storyID){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] user_projection = {DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID};
        String user_selection = DatabaseNameHelper.UserStoryEntry.COLUMN_STORY_ID + " = ?";
        String[] user_selection_args = {storyID};

        Cursor userCursor = db.query(DatabaseNameHelper.UserStoryEntry.TABLE_NAME, user_projection, user_selection, user_selection_args, null, null, null);

        ArrayList<IdData> userList = new ArrayList<>();
        while(userCursor.moveToNext()) {
            String childID = userCursor.getString(userCursor.getColumnIndex(DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID));
            String childName = getChildNameFromID(context, childID);
            userList.add(new IdData(childID, childName));
        }
        userCursor.close();
        return userList;
    }

    /**
     *
     * @param context current context of the program
     * @param user_id of adult
     * @return ArrayList of pairs of current story names, with corresponding ID that meet selection
     */
    public static ArrayList<IdData> getAdultStoryList(Context context, String user_id){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry._ID, DatabaseNameHelper.StoryEntry.COLUMN_TITLE};
        String selection = DatabaseNameHelper.StoryEntry.COLUMN_AUTHOR_ID + " = ?";
        String[] selectionArgs = {user_id};

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

    /**
     *
     * @param context current context of the program
     * @param user_id of child
     * @return ArrayList of pairs of current stories, with corresponding ID that meet selection
     */
    public static ArrayList<IdData> getChildStoryList(Context context, String user_id) {
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

        ArrayList<IdData> storyMap = new ArrayList<>();

        while(cursor.moveToNext()) {
            String storyTitle = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
            String storyId = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.StoryEntry._ID));
            storyMap.add(new IdData(storyId, storyTitle));
        }
        cursor.close();
        db.close();
        return storyMap;
    }

    /**
     *
     * @param context current context of the program
     * @param user to select avatar from
     * @return avatar URI as a string
     */
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

    /**
     *
     * @param context current context of the program
     * @param id of story to be selected
     * @return title of story
     */
    public static String getTitleFromID(Context context, String id){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.StoryEntry.COLUMN_TITLE};
        String selection = DatabaseNameHelper.StoryEntry._ID + " = ?";
        String[] selectionArgs = { id };

        Cursor titleCursor = db.query(DatabaseNameHelper.StoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        titleCursor.moveToNext();
        String title = titleCursor.getString(titleCursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
        titleCursor.close();
        return title;
    }

    /**
     *
     * @param context current context of the program
     * @param id of child to be selected
     * @return child name
     */
    public static String getChildNameFromID(Context context, String id){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {DatabaseNameHelper.ChildUserEntry.COLUMN_NAME};
        String selection = DatabaseNameHelper.ChildUserEntry._ID + " = ?";
        String[] selectionArgs = {id};

        Cursor cursor = db.query(DatabaseNameHelper.ChildUserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        cursor.moveToFirst();
        String name = cursor.getString(cursor.getColumnIndex(DatabaseNameHelper.ChildUserEntry.COLUMN_NAME));
        cursor.close();
        db.close();
        return name;
    }

    /**
     *
     * @param context current context of the program
     * @param id of adult to be selected
     * @return adult name
     */
    public static String getAdultNameFromID(Context context, Integer id){
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

    /**
     *
     * @param context current context of the program
     * @param story_id of story to be selected
     * @return true if story is not empty, else false
     */
    public static boolean isStoryReadable(Context context, String story_id){
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        String selection = DatabaseNameHelper.PageEntry.COLUMN_STORY_ID + " = ?";
        String[] selectionArgs = {story_id};
        long numEntries = DatabaseUtils.queryNumEntries(db, DatabaseNameHelper.PageEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        return (numEntries > 0);
    }




}
