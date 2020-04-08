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
import android.widget.EditText;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;

import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.util.ArrayList;

public class ConfigureStory extends AppCompatActivity {

    private ArrayList<IdData> pageNumberList;
    private ListRecyclerAdapter pageHashAdapter;
    private DecoratedRecyclerView pageView;

    private ArrayList<IdData> childList;
    private ListRecyclerAdapter childListAdapter;
    private DecoratedRecyclerView childListView;

    private String title;
    private String author;
    private String date;

    private EditText storyTitle;

    private String storyID;

    private IdData selectedChild;
    private IdData selectedPage;

    private int selectedChildPosition = -1;
    private int selectedPagePosition = -1;

    private AlertDialog.Builder reorderDialog;
    private AlertDialog.Builder childPickDialog;
    private ListRecyclerAdapter childAddAdapter;

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

        pageNumberList = ActivityHelper.getPageList(getApplicationContext(), storyID);
        pageView = findViewById(R.id.page_list);
        pageHashAdapter = new ListRecyclerAdapter(pageNumberList);
        pageView.setAdapter(pageHashAdapter);
        pageHashAdapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1) {
                    selectedPage = pageHashAdapter.getItem(position);
                }
                selectedPagePosition = position;
            }
        });

        childList = ActivityHelper.getUserList(getApplication(), storyID);
        childListView = findViewById(R.id.user_list);
        childListAdapter = new ListRecyclerAdapter(childList);
        childListView.setAdapter(childListAdapter);
        childListAdapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1){
                    selectedChild = childListAdapter.getItem(position);
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
        DecoratedRecyclerView pageView = convertView.findViewById(R.id.recycleView1);
        ListRecyclerAdapter adapter = new ListRecyclerAdapter(childList);

        pageView.setAdapter(adapter);

        reorderDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                flushDynamicChanges();
            }
        });
        reorderDialog.setNegativeButton(R.string.cancel, null);

        // Setup child adder dialog


    }

    @Override
    protected void onResume() {
        super.onResume();
//        pageNumberMap.clear();
//        pageNumberMap = ActivityHelper.getPageMap(getApplicationContext(), storyID);
//        childList.clear();
//        childList = ActivityHelper.getUserList(getApplication(), storyID);
//        childListAdapter.notifyDataSetChanged();
//        pageHashAdapter.notifyDataSetChanged();
    }


    public void newPage(View view){
        flushDynamicChanges();
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(PageEntry.COLUMN_TEXT, "");
        values.put(PageEntry.COLUMN_STORY_ID, storyID);
        int pageNo = pageNumberList.size() + 1;
        values.put(PageEntry.COLUMN_PAGE_NO, pageNumberList.size() + 1);
        long pageIDLong = db.insert(PageEntry.TABLE_NAME,null,values);
        String pageID = String.valueOf(pageIDLong);
        Intent intent = new Intent(this, PageEditor.class);
        intent.putExtra("story_id", storyID);
        intent.putExtra("page_id", pageID);
        intent.putExtra("page_no", pageNo);
        startActivity(intent);
        int insertIndex = pageNumberList.size();
        IdData data = new IdData(pageID, String.valueOf(pageNo));
        pageNumberList.add(insertIndex, data);
        pageHashAdapter.itemAdded(insertIndex, data);
    }

    public void editPage(View view){
        flushDynamicChanges();
        Intent intent = new Intent(this, PageEditor.class);
        intent.putExtra("story_id", storyID);
        intent.putExtra("page_id", selectedPage.getId());
        int pageNo = Integer.valueOf(selectedPage.getData());
        intent.putExtra("page_no", pageNo);
        startActivity(intent);
    }

    public void removePage(View view){
        if (pageHashAdapter.itemSelected()){

            String positionS = selectedPage.getId();
            Integer position = Integer.valueOf(positionS);
            for (int i = 0; i < pageNumberList.size(); i++){
                IdData data = pageNumberList.get(i);
                Integer pageNumber = Integer.valueOf(data.getData());
                if (pageNumber > position){
                    data.setData(String.valueOf(pageNumber - 1));
                    pageNumberList.set(i, data);
                }
            }
            pageNumberList.remove(selectedPage);
            pageHashAdapter.itemRemoved(selectedPagePosition);
            pageHashAdapter.notifyItemRangeChanged(selectedPagePosition, pageNumberList.size() - selectedPagePosition);
            pageHashAdapter.deselect();
            selectedPagePosition = RecyclerView.NO_POSITION;
        }
    }

    public void newChild(View view){
        childPickDialog = new AlertDialog.Builder(ConfigureStory.this);
        LayoutInflater childInflater = getLayoutInflater();
        View childPickView = childInflater.inflate(R.layout.custom_recycle, null);
        childPickDialog.setView(childPickView);
        childPickDialog.setTitle(R.string.child_select);
        DecoratedRecyclerView listView = childPickView.findViewById(R.id.recycleView1);
        ArrayList<IdData> childUnselectedUsers = ActivityHelper.getChildUserArray(getApplicationContext());
        for (IdData data : childList){
            childUnselectedUsers.remove(data);
        }
        childAddAdapter = new ListRecyclerAdapter(childUnselectedUsers, R.color.adultColor);
        listView.setAdapter(childAddAdapter);

        final IdData[] newData = new IdData[1];
        final Integer[] selectedPos = {RecyclerView.NO_POSITION};
        childAddAdapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > RecyclerView.NO_POSITION){
                    newData[0] = childAddAdapter.getItem(position);
                }
                selectedPos[0] = position;
            }
        });

        childPickDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedPos[0] != RecyclerView.NO_POSITION) {
                    int insertIndex = childList.size();
                    childList.add(insertIndex, newData[0]);
                    childListAdapter.itemAdded(insertIndex, newData[0]);
                }

            }
        });
        childPickDialog.setNegativeButton(R.string.cancel, null);
        childPickDialog.show();
    }
    public void removeChild(View view){
        if (childListAdapter.itemSelected()){
            childList.remove(selectedChild);
            childListAdapter.itemRemoved(selectedChildPosition);
            childListAdapter.deselect();
            selectedChildPosition = RecyclerView.NO_POSITION;
        }
    }

    public void flushDynamicChanges(){

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();


        for (IdData data : pageNumberList){
            String id = data.getId();
            ContentValues values = new ContentValues();
            String[] args = {id};
            String pageNumberString = data.getData();
            assert pageNumberString != null;
            Integer pageNumber = Integer.valueOf(pageNumberString);
            values.put(PageEntry.COLUMN_PAGE_NO, pageNumber);
            db.update(PageEntry.TABLE_NAME, values, "_id = ?", args);
        }

        StringBuilder inQuery = new StringBuilder();

        inQuery.append("(");
        boolean first = true;
        for (IdData data : pageNumberList) {
            String item = data.getId();
            if (first) {
                first = false;
                inQuery.append("'").append(item).append("'");
            } else {
                inQuery.append(", '").append(item).append("'");
            }
        }
        inQuery.append(")");

        String selection = PageEntry._ID + " NOT IN " + inQuery.toString() + " AND " + PageEntry.COLUMN_STORY_ID + " = ?";
        String[] selectionArgs = {storyID};
        db.delete(PageEntry.TABLE_NAME, selection, selectionArgs);



        ArrayList<IdData> currentUserMapDB = ActivityHelper.getUserList(getApplication(), storyID);
        for (IdData idData : currentUserMapDB) {
            if (!childList.contains(idData)) {
                String selectionChild = UserStoryEntry.COLUMN_STORY_ID + " = ? AND " + UserStoryEntry.COLUMN_USER_ID + " = ?";
                String[] args = {storyID, idData.getId()};
                db.delete(UserStoryEntry.TABLE_NAME, selectionChild, args);
            }
        }
        for (IdData idData : childList) {
            if (!currentUserMapDB.contains(idData)) {
                ContentValues values = new ContentValues();
                values.put(UserStoryEntry.COLUMN_STORY_ID, storyID);
                values.put(UserStoryEntry.COLUMN_USER_ID, idData.getId());
                long rowID = db.insert(UserStoryEntry.TABLE_NAME, null, values);
                if (rowID < 0) {
                    System.out.println("Insert failed");
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
