package com.example.socialstorybuilder.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Build;
import android.provider.ContactsContract;

import com.example.socialstorybuilder.database.DatabaseNameHelper.*;
import androidx.annotation.Nullable;


/**
 * Helper class to access the database.
 *
 * @since 1.0
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "storynarrative.db";
    private static final int DATABASE_VERSION = 1;


    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Method called when no database with constant field DATABASE_NAME exists.
     * @param db The database
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_STORY_TABLE = "CREATE TABLE IF NOT EXISTS " +
                StoryEntry.TABLE_NAME + " (" +
                StoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                StoryEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                StoryEntry.COLUMN_AUTHOR_ID + " INTEGER NOT NULL, " +
                StoryEntry.COLUMN_DATE + " TEXT NOT NULL, " +
                StoryEntry.BACKGROUND_COLOR + " TEXT NOT NULL DEFAULT '#ffffff', " +
                " FOREIGN KEY ("+StoryEntry.COLUMN_AUTHOR_ID+") REFERENCES "+AdultUserEntry.TABLE_NAME+"("+AdultUserEntry._ID+") ON DELETE CASCADE)";

        final String SQL_CREATE_PAGES_TABLE = "CREATE TABLE IF NOT EXISTS " +
                PageEntry.TABLE_NAME + " (" +
                PageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                PageEntry.COLUMN_STORY_ID + " INTEGER NOT NULL, " +
                PageEntry.COLUMN_PAGE_NO + " INTEGER NOT NULL, " +
                PageEntry.COLUMN_TEXT + " TEXT, " +
                PageEntry.COLUMN_SOUND + " TEXT, " +
                " FOREIGN KEY ("+PageEntry.COLUMN_STORY_ID+") REFERENCES "+StoryEntry.TABLE_NAME+"("+ StoryEntry._ID +
                ") ON DELETE CASCADE)";

        final String SQL_CREATE_IMAGES_TABLE = "CREATE TABLE IF NOT EXISTS " +
                ImageEntry.TABLE_NAME + " (" +
                ImageEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ImageEntry.COLUMN_URI + " TEXT NOT NULL, " +
                ImageEntry.COLUMN_PAGE_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY ("+ImageEntry.COLUMN_PAGE_ID+") REFERENCES "+PageEntry.TABLE_NAME+"("+PageEntry._ID+
                ") ON DELETE CASCADE)";

        final String SQL_CREATE_CHILD_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                ChildUserEntry.TABLE_NAME + " (" +
                ChildUserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                ChildUserEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                ChildUserEntry.COLUMN_AVATAR + " TEXT NOT NULL " +
                ")";

        final String SQL_CREATE_ADULT_USERS_TABLE = "CREATE TABLE IF NOT EXISTS " +
                AdultUserEntry.TABLE_NAME + " (" +
                AdultUserEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                AdultUserEntry.COLUMN_NAME + " TEXT UNIQUE NOT NULL, " +
                AdultUserEntry.COLUMN_PASSWORD + " TEXT NOT NULL " +
                ")";

        final String SQL_CREATE_USER_STORY_TABLE = "CREATE TABLE IF NOT EXISTS " +
                UserStoryEntry.TABLE_NAME + " (" +
                UserStoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                UserStoryEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                UserStoryEntry.COLUMN_STORY_ID + " INTEGER NOT NULL, " +
                " FOREIGN KEY ("+UserStoryEntry.COLUMN_STORY_ID+") REFERENCES "+StoryEntry.TABLE_NAME+"("
                +StoryEntry._ID+") ON DELETE CASCADE ,"+
                " FOREIGN KEY ("+UserStoryEntry.COLUMN_USER_ID+") REFERENCES "+ChildUserEntry.TABLE_NAME+"("
                +ChildUserEntry._ID+") ON DELETE CASCADE)";

        final String SQL_CREATE_STATISTICS_STORY_TABLE = "CREATE TABLE IF NOT EXISTS " +
                FeedbackEntry.TABLE_NAME + " (" +
                FeedbackEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                FeedbackEntry.COLUMN_USER_ID + " INTEGER NOT NULL, " +
                FeedbackEntry.COLUMN_STORY_ID + " INTEGER NOT NULL, " +
                FeedbackEntry.COLUMN_FEEDBACK + " INTEGER, " +
                " FOREIGN KEY ("+FeedbackEntry.COLUMN_STORY_ID+") REFERENCES "+StoryEntry.TABLE_NAME+"("
                +StoryEntry._ID+") ON DELETE CASCADE ,"+
                " FOREIGN KEY ("+FeedbackEntry.COLUMN_USER_ID+") REFERENCES "+ChildUserEntry.TABLE_NAME+"("
                +ChildUserEntry._ID+") ON DELETE CASCADE)";


        db.execSQL(SQL_CREATE_STORY_TABLE);
        db.execSQL(SQL_CREATE_PAGES_TABLE);
        db.execSQL(SQL_CREATE_IMAGES_TABLE);
        db.execSQL(SQL_CREATE_CHILD_USERS_TABLE);
        db.execSQL(SQL_CREATE_ADULT_USERS_TABLE);
        db.execSQL(SQL_CREATE_USER_STORY_TABLE);
        db.execSQL(SQL_CREATE_STATISTICS_STORY_TABLE);
    }


    /**
     * Method called when database version is updated.
     * This method contains any updates to the database that occur after official release.
     *
     * @param db The database
     * @param oldVersion The old database version
     * @param newVersion The new database version
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

    /**
     * Method called every time the database is accessed, method ensures foreign keys are active.
     *
     * @param db The database
     */
    @Override
    public void onConfigure(SQLiteDatabase db) {
        super.onConfigure(db);
        db.setForeignKeyConstraintsEnabled(true);
    }
}
