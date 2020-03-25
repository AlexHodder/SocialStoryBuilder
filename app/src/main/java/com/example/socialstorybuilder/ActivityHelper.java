package com.example.socialstorybuilder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

import java.util.ArrayList;

public class ActivityHelper {

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

        return childList;
    }

}
