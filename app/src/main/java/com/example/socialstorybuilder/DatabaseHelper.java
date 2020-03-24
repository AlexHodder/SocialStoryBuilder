package com.example.socialstorybuilder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.socialstorybuilder.DatabaseNameHelper.*;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "storybuilder.db";
    public static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_STORY_TABLE = "CREATE TABLE " +
                StoryEntry.TABLE_NAME + " (" +
                StoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StoryEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                StoryEntry.COLUMN_AUTHOR + " TEXT NOT NULL, " +
                StoryEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                StoryEntry.COLUMN_USER_ID + " INTEGER, " +
                " FOREIGN KEY ("+StoryEntry.COLUMN_USER_ID+") REFERENCES "+ChildUserEntry.TABLE_NAME+
                ")";

        final String SQL_CREATE_PAGES_TABLE = "CREATE TABLE " +
                PageEntry.TABLE_NAME + " (" +
                PageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PageEntry.COLUMN_STORY_ID + " INTEGER NOT NULL, " +
                PageEntry.COLUMN_PAGE_NO + " INTEGER NOT NULL, " +
                PageEntry.COLUMN_TEXT + " TEXT, " +
                " FOREIGN KEY ("+PageEntry.COLUMN_STORY_ID+") REFERENCES "+StoryEntry.TABLE_NAME+
                ")";


        final String SQL_CREATE_ANSWER_PAGES_TABLE = "CREATE TABLE " +
                AnswerPageEntry.TABLE_NAME + " (" +
                AnswerPageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AnswerPageEntry.COLUMN_PAGE_ID + " INTEGER NOT NULL, " +
                AnswerPageEntry.COLUMN_QUESTION_PAGE_ID + " INTEGER NOT NULL, " +
                AnswerPageEntry.COLUMN_ANSWER + " TEXT NOT NULL, " +
                " FOREIGN KEY ("+AnswerPageEntry.COLUMN_PAGE_ID+") REFERENCES "+PageEntry.TABLE_NAME+" ,"+
                " FOREIGN KEY ("+AnswerPageEntry.COLUMN_QUESTION_PAGE_ID+") REFERENCES "+PageEntry.TABLE_NAME+
                ")";

        final String SQL_CREATE_IMAGES_TABLE = "CREATE TABLE " +
                ImageEntry.TABLE_NAME + " (" +
                ImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ImageEntry.COLUMN_URI + " TEXT NOT NULL, " +
                ImageEntry.COLUMN_PAGE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY ("+ImageEntry.COLUMN_PAGE_ID+") REFERENCES "+PageEntry.TABLE_NAME+
                ")";

        final String SQL_CREATE_CHILD_USERS_TABLE = "CREATE TABLE " +
                ChildUserEntry.TABLE_NAME + " (" +
                ChildUserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ChildUserEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                ChildUserEntry.COLUMN_AVATAR + " TEXT NOT NULL " +
                ")";

        final String SQL_CREATE_ADULT_USERS_TABLE = "CREATE TABLE " +
                AdultUserEntry.TABLE_NAME + " (" +
                AdultUserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AdultUserEntry.COLUMN_NAME + " TEXT NOT NULL, " +
                AdultUserEntry.COLUMN_PASSWORD + " TEXT NOT NULL " +
                ")";

        db.execSQL(SQL_CREATE_STORY_TABLE);
        db.execSQL(SQL_CREATE_PAGES_TABLE);
        db.execSQL(SQL_CREATE_ANSWER_PAGES_TABLE);
        db.execSQL(SQL_CREATE_IMAGES_TABLE);
        db.execSQL(SQL_CREATE_CHILD_USERS_TABLE);
        db.execSQL(SQL_CREATE_ADULT_USERS_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + StoryEntry.TABLE_NAME);
        onCreate(db);
    }


}
