package com.example.socialstorybuilder.database;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.provider.BaseColumns;

import java.util.ArrayList;

public class DatabaseNameHelper {

    private DatabaseNameHelper(){}

    public static final class StoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "stories";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR = "author";
        public static final String COLUMN_DATE = "date";
    }

    public static final class ChildUserEntry implements BaseColumns {
        public static final String TABLE_NAME = "child_users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AVATAR = "avatar_uri";
    }

    public static final class AdultUserEntry implements BaseColumns {
        public static final String TABLE_NAME = "adult_users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PASSWORD = "password";
    }

    public static final class PageEntry implements BaseColumns {
        public static final String TABLE_NAME = "pages";
        public static final String COLUMN_STORY_ID = "story_ID";
        public static final String COLUMN_PAGE_NO = "page_no";
        public static final String COLUMN_TEXT = "text";
    }

    public static final class AnswerPageEntry implements BaseColumns {
        public static final String TABLE_NAME = "answer_pages";
        public static final String COLUMN_PAGE_ID = "page_ID";
        public static final String COLUMN_QUESTION_PAGE_ID = "question_page_ID";
        public static final String COLUMN_ANSWER = "answer";
    }

    public static final class ImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "images";
        public static final String COLUMN_URI = "uri";
        public static final String COLUMN_PAGE_ID = "page_ID";
    }

    public static final class UserStoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_stories";
        public static final String COLUMN_USER_ID = "user_ID";
        public static final String COLUMN_STORY_ID = "story_ID";
    }




}
