package com.example.socialstorybuilder.database;


import android.provider.BaseColumns;

/**
 * Helper Class to store database table and column names
 *
 * @since 1.0
 */

public class DatabaseNameHelper {

    private DatabaseNameHelper(){}

    /**
     * StoryEntry Table
     */
    public static final class StoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "stories";
        public static final String COLUMN_TITLE = "title";
        public static final String COLUMN_AUTHOR_ID = "author_id";
        public static final String COLUMN_DATE = "date";
        public static final String BACKGROUND_COLOR = "bg_colour";
    }

    /**
     * ChildUserEntry Table
     */
    public static final class ChildUserEntry implements BaseColumns {
        public static final String TABLE_NAME = "child_users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_AVATAR = "avatar_uri";
    }

    /**
     * AdultUserEntry Table
     */
    public static final class AdultUserEntry implements BaseColumns {
        public static final String TABLE_NAME = "adult_users";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_PASSWORD = "password";
    }

    /**
     * PageEntry Table
     */
    public static final class PageEntry implements BaseColumns {
        public static final String TABLE_NAME = "pages";
        public static final String COLUMN_STORY_ID = "story_ID";
        public static final String COLUMN_PAGE_NO = "page_no";
        public static final String COLUMN_TEXT = "text";
        public static final String COLUMN_SOUND = "sound";
    }

    /**
     * ImageEntry Table
     */
    public static final class ImageEntry implements BaseColumns {
        public static final String TABLE_NAME = "images";
        public static final String COLUMN_URI = "uri";
        public static final String COLUMN_PAGE_ID = "page_ID";
    }

    /**
     * UserStoryEntry Table
     */
    public static final class UserStoryEntry implements BaseColumns {
        public static final String TABLE_NAME = "user_stories";
        public static final String COLUMN_USER_ID = "user_ID";
        public static final String COLUMN_STORY_ID = "story_ID";
    }

    /**
     * FeedbackEntry Table
     */
    public static final class FeedbackEntry implements BaseColumns {
        public static final String TABLE_NAME = "statistics";
        public static final String COLUMN_USER_ID = "user_ID";
        public static final String COLUMN_STORY_ID = "story_ID";
        public static final String COLUMN_FEEDBACK = "feedback";
    }



}
