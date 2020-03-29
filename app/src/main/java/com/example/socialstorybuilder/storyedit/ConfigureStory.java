package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.adultactivity.AdultInitialActivity;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.util.ArrayList;

public class ConfigureStory extends AppCompatActivity {

    private ArrayList<String> pageNumberList;
    private ArrayAdapter<String> pageAdapter;
    private ListView pageView;

    private ArrayList<Integer> childIDList;
    private ArrayList<String> childList;
    private ArrayAdapter<String> childListAdapter;
    private ListView childListView;

    private String title;
    private String author;
    private String date;


    private String storyID;

    private int selectedChild;

    private int selectedPage;
    private Integer selectedPageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_story);
        Intent intent = getIntent();

        if (intent.hasExtra("story_id")){
            storyID = intent.getStringExtra("story_id");
            System.out.println(storyID);

            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] story_projection = {StoryEntry.COLUMN_AUTHOR, StoryEntry.COLUMN_DATE, StoryEntry.COLUMN_TITLE};
            String story_selection = StoryEntry._ID + " = ?" + " AND " + StoryEntry.COLUMN_AUTHOR + " = ?";
            String[] story_selection_args = {storyID, intent.getStringExtra("user")};

            Cursor storyCursor = db.query(StoryEntry.TABLE_NAME, story_projection, story_selection, story_selection_args, null, null, null);
            storyCursor.moveToNext();
            title = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.COLUMN_TITLE));
            author = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.COLUMN_AUTHOR));
            date = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.COLUMN_DATE));

            System.out.println(childList);
        }
        else{
            author = intent.getStringExtra("user");
            //TODO write a date getter function
//            date = getCurrentDate();
            date = "INSERT DATE";
            title = "New Book";
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(StoryEntry.COLUMN_AUTHOR, author);
            values.put(StoryEntry.COLUMN_DATE, date);
            values.put(StoryEntry.COLUMN_TITLE, title);
            System.out.println("NO CURRENT STORY");
            long rowID = db.insert(StoryEntry.TABLE_NAME, null, values);
            System.out.println("Inserted, at row: " + rowID);
            if (rowID>0) storyID = Long.toString(rowID);
        }



        pageNumberList = getPageList();
        pageView = findViewById(R.id.page_list);
        pageAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, pageNumberList);
        pageView.setAdapter(pageAdapter);
        pageView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedPage = position;
            }
        });

        childList = getUserList();
        childListView = findViewById(R.id.user_list);
        childListAdapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, childList);
        childListView.setAdapter(childListAdapter);
        childListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedChild = position;
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        pageNumberList = getPageList();
        childList = getUserList();
        childListAdapter.notifyDataSetChanged();
        pageAdapter.notifyDataSetChanged();
    }

    public ArrayList<String> getPageList(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        String count = "SELECT " + PageEntry.COLUMN_PAGE_NO + " FROM " + PageEntry.TABLE_NAME + " WHERE " + PageEntry.COLUMN_STORY_ID + " = ?";
        Cursor countCursor = dbRead.rawQuery(count, new String[]{storyID});
        countCursor.moveToFirst();
        ArrayList<String> pageList = new ArrayList();
        while(countCursor.moveToNext()) {
            Integer page = countCursor.getInt(countCursor.getColumnIndex(DatabaseNameHelper.StoryEntry.COLUMN_TITLE));
            String pageS = page.toString();
            pageList.add(pageS);
        }
        countCursor.close();
        return pageList;
    }

    public ArrayList<String> getUserList(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] user_projection = {UserStoryEntry.COLUMN_USER_ID};
        String user_selection = UserStoryEntry.COLUMN_STORY_ID + " = ?";
        String[] user_selection_args = {storyID};

        Cursor userCursor = db.query(UserStoryEntry.TABLE_NAME, user_projection, user_selection, user_selection_args, null, null, null);

        ArrayList<String> userList = new ArrayList<>();
        while(userCursor.moveToNext()) {
            Integer childID = userCursor.getInt(userCursor.getColumnIndex(UserStoryEntry.COLUMN_USER_ID));
            userList.add(ActivityHelper.getChildNameFromID(getApplicationContext(), childID));
        }
        userCursor.close();
        return userList;
    }

    public void newPage(View view){
        selectedPageNo = pageNumberList.size() + 1;
        switchToPageEditor(view);
    }

    public void editPage(View view){
        selectedPageNo = Integer.valueOf(pageNumberList.get(selectedPage));
        switchToPageEditor(view);
    }

    public void switchToPageEditor(View view){
        Intent intent = new Intent(this, PageEditor.class);
        intent.putExtra("page_no", pageNumberList.size() + 1);
        intent.putExtra("story_id", storyID);
        startActivity(intent);
    }

    public void switchToAdultInitial(View view){
        Intent intent = new Intent(this, AdultInitialActivity.class);
        startActivity(intent);
    }

}
