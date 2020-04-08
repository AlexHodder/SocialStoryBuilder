package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.HashMapAdapter;
import com.example.socialstorybuilder.MapRecyclerAdapter;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;

import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

public class ConfigureStory extends AppCompatActivity {

    private TreeMap<String, String> pageNumberMap;
    private MapRecyclerAdapter pageHashAdapter;
    private DecoratedRecyclerView pageView;

    private TreeMap<String, String> childMap;
    private MapRecyclerAdapter childListAdapter;
    private DecoratedRecyclerView childListView;

    private String title;
    private String author;
    private String date;

    private EditText storyTitle;

    private String storyID;
    private String selectedChildID;
    private String selectedPageID;
    private int selectedChildPosition = -1;
    private int selectedPagePosition = -1;

    private AlertDialog.Builder reorderDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_configure_story);
        Intent intent = getIntent();
        storyTitle = findViewById(R.id.story_name_input);

        //Retrieve values and setup database
        if (intent.hasExtra("story_id")){
            storyID = intent.getStringExtra("story_id");
            System.out.println("Story ID: " + storyID);

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
            storyCursor.close();
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
            db.close();
        }

        pageNumberMap = getPageMap();
        pageView = findViewById(R.id.page_list);
        pageHashAdapter = new MapRecyclerAdapter(pageNumberMap);
        pageView.setAdapter(pageHashAdapter);
        pageHashAdapter.setClickListener(new MapRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1) {
                    selectedPageID = pageHashAdapter.getKey(position);
                    System.out.println(selectedPageID);
                }
                selectedPagePosition = position;
            }
        });

        childMap = getUserMap();
        childListView = findViewById(R.id.user_list);
        childListAdapter = new MapRecyclerAdapter(childMap);
        childListView.setAdapter(childListAdapter);
        childListAdapter.setClickListener(new MapRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1){
                    selectedChildID = childListAdapter.getKey(position);
                }
                selectedChildPosition = position;
            }
        });

        storyTitle.setText(title);

        // Setup reorder dialog
        reorderDialog = new AlertDialog.Builder(ConfigureStory.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom_recycle, null);
        reorderDialog.setView(convertView);
        DecoratedRecyclerView pageView = findViewById(R.id.recycleView1);
        MapRecyclerAdapter adapter = new MapRecyclerAdapter(childMap);

        pageView.setAdapter(adapter);

        reorderDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flushDynamicChanges();
            }
        });
        reorderDialog.setNegativeButton(R.string.cancel, null);

    }

    @Override
    protected void onResume() {
        super.onResume();
        pageNumberMap.clear();
        pageNumberMap = getPageMap();
        pageHashAdapter.refresh(pageNumberMap);
        childMap.clear();
        childMap = getUserMap();
        childListAdapter.refresh(childMap);
    }

    public TreeMap<String, String> getPageMap(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase dbRead = dbHelper.getReadableDatabase();
        String[] projection = {PageEntry._ID, PageEntry.COLUMN_PAGE_NO};
        String selection = PageEntry.COLUMN_STORY_ID + "= ?";
        String[] selectionArgs = {storyID};

        assert storyID!= null;
        Cursor countCursor = dbRead.query(PageEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        TreeMap<String, String> pageMap = new TreeMap<>();
        while(countCursor.moveToNext()) {
            int page = countCursor.getInt(countCursor.getColumnIndex(PageEntry.COLUMN_PAGE_NO));
            String pageS = Integer.toString(page);
            String pageID = countCursor.getString(countCursor.getColumnIndex(PageEntry._ID));
            pageMap.put(pageID, pageS);
        }
        countCursor.close();
        return pageMap;
    }


    public TreeMap<String, String> getUserMap(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] user_projection = {UserStoryEntry.COLUMN_USER_ID};
        String user_selection = UserStoryEntry.COLUMN_STORY_ID + " = ?";
        String[] user_selection_args = {storyID};

        Cursor userCursor = db.query(UserStoryEntry.TABLE_NAME, user_projection, user_selection, user_selection_args, null, null, null);

        TreeMap<String, String> userList = new TreeMap<>();
        while(userCursor.moveToNext()) {
            Integer childID = userCursor.getInt(userCursor.getColumnIndex(UserStoryEntry.COLUMN_USER_ID));
            String childName = ActivityHelper.getChildNameFromID(getApplicationContext(), childID);
            userList.put(childID.toString(), childName);
        }
        userCursor.close();
        return userList;
    }

    public void newPage(View view){
        flushDynamicChanges();
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PageEntry.COLUMN_TEXT, "");
        values.put(PageEntry.COLUMN_STORY_ID, storyID);
        int pageNo = pageNumberMap.size() + 1;
        values.put(PageEntry.COLUMN_PAGE_NO, pageNumberMap.size() + 1);
        long pageID = db.insert(PageEntry.TABLE_NAME,null,values);
        Intent intent = new Intent(this, PageEditor.class);
        intent.putExtra("story_id", storyID);
        intent.putExtra("page_id", String.valueOf(pageID));
        intent.putExtra("page_no", pageNo);
        startActivity(intent);
    }

    public void editPage(View view){
        flushDynamicChanges();
        Intent intent = new Intent(this, PageEditor.class);
        intent.putExtra("story_id", storyID);
        intent.putExtra("page_id", selectedPageID);
        int pageNo = Integer.valueOf(pageNumberMap.get(selectedPageID));
        intent.putExtra("page_no", pageNo);
        startActivity(intent);
    }

    public void removePage(View view){
        if (pageHashAdapter.itemSelected()){

            String positionS = pageNumberMap.get(selectedPageID);
            Integer position = Integer.valueOf(positionS);
            for (String key : pageNumberMap.keySet()){
                Integer pageNumber = Integer.valueOf(pageNumberMap.get(key));
                if (pageNumber > position){
                    String value = String.valueOf(pageNumber - 1);
                    pageNumberMap.put(key, value);
                }
            }
            pageNumberMap.remove(selectedPageID);
            pageHashAdapter.itemRemoved(selectedPagePosition);
            pageHashAdapter.notifyItemRangeChanged(selectedPagePosition, pageNumberMap.size() - selectedPagePosition);
            pageHashAdapter.deselect();
            selectedPagePosition = RecyclerView.NO_POSITION;
        }
    }

    public void newChild(View view){
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(ConfigureStory.this);
        LayoutInflater inflater = getLayoutInflater();
        View convertView = inflater.inflate(R.layout.custom, null);
        alertDialog.setView(convertView);
        alertDialog.setTitle(R.string.child_select);
        ListView listView = convertView.findViewById(R.id.listView1);

        TreeMap<String, String> childUsers = ActivityHelper.getChildUserMap(getApplicationContext());
        for (String key : childMap.keySet()){
            if (childUsers.containsKey(key)) childUsers.remove(key);
        }
        final HashMapAdapter childAdapter = new HashMapAdapter(childUsers);
        final String[] key = new String[1];
        final String[] value = new String[1];
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Map.Entry<Object, Object> map = (Map.Entry<Object, Object>) parent.getItemAtPosition(position);
                key[0] = (String) map.getKey();
                value[0] = (String) map.getValue();
            }
        });

        listView.setAdapter(childAdapter);

        alertDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                childMap.put(key[0], value[0]);
                childListAdapter.itemAdded(key[0], value[0]);
            }
        });
        alertDialog.setNegativeButton(R.string.cancel, null);
        alertDialog.show();

    }
    public void removeChild(View view){
        if (childListAdapter.itemSelected()){
            if (childMap.containsKey(selectedChildID)) childMap.remove(selectedChildID);
            childListAdapter.itemRemoved(selectedChildPosition);
            childListAdapter.deselect();
            selectedChildPosition = RecyclerView.NO_POSITION;
        }
    }

    public void flushDynamicChanges(){

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (!pageNumberMap.isEmpty()) {
            for (String id : pageNumberMap.keySet()){
                ContentValues values = new ContentValues();
                String[] args = {id};
                String pageNumberString = pageNumberMap.get(id);
                assert pageNumberString != null;
                Integer pageNumber = Integer.valueOf(pageNumberString);
                values.put(PageEntry.COLUMN_PAGE_NO, pageNumber);
                db.update(PageEntry.TABLE_NAME, values, "_id = ?", args);
            }

            StringBuilder inQuery = new StringBuilder();

            inQuery.append("(");
            boolean first = true;
            for (String item : pageNumberMap.keySet()) {
                if (first) {
                    first = false;
                    inQuery.append("'").append(item).append("'");
                } else {
                    inQuery.append(", '").append(item).append("'");
                }
            }
            inQuery.append(")");


            String selection = PageEntry._ID + " NOT IN " + inQuery.toString();
            db.delete(PageEntry.TABLE_NAME, selection, null);
        }

        if (!childMap.isEmpty()) {
            TreeMap<String, String> currentUserMapDB = getUserMap();
            for (String key : currentUserMapDB.keySet()) {
                if (!childMap.containsKey(key)) {
                    String selection = UserStoryEntry.COLUMN_STORY_ID + " = ? AND " + UserStoryEntry.COLUMN_USER_ID + " = ?";
                    String[] args = {storyID, key};
                    db.delete(UserStoryEntry.TABLE_NAME, selection, args);
                }
            }
            for (String key : childMap.keySet()) {
                if (!currentUserMapDB.containsKey(key)) {
                    ContentValues values = new ContentValues();
                    values.put(UserStoryEntry.COLUMN_STORY_ID, storyID);
                    values.put(UserStoryEntry.COLUMN_USER_ID, key);
                    long rowID = db.insert(UserStoryEntry.TABLE_NAME, null, values);
                    if (rowID < 0) {
                        System.out.println("Insert failed");
                    }
                }
            }
        }

        db.close();
    }


    public void finish(View view){
        finish();
    }

    public void confirm(View view){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StoryEntry.COLUMN_TITLE, storyTitle.getText().toString());
        values.put(StoryEntry.COLUMN_DATE, "01-01-2020");
        values.put(StoryEntry.COLUMN_AUTHOR, author);
        String[] args = {storyID};
        db.update(StoryEntry.TABLE_NAME, values, "_id = ?", args);
        db.close();
        flushDynamicChanges();
        finish();
    }

}
