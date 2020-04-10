package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

import java.util.ArrayList;

public class ConfigureUsers extends AppCompatActivity {

    private String storyID;

    private ArrayList<IdData> childList;
    private ListRecyclerAdapter childListAdapter;
    private DecoratedRecyclerView childListView;

    private IdData selectedChild;
    private int selectedChildPosition = RecyclerView.NO_POSITION;

    private AlertDialog.Builder childPickDialog;
    private ListRecyclerAdapter childAddAdapter;

    private AlertDialog.Builder cancelConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_users);
        Intent intent = getIntent();
        storyID = intent.getStringExtra("story_id");

        TextView storyTitle = findViewById(R.id.textViewStoryTitle);
        String title = ActivityHelper.getTitleFromID(getApplicationContext(), storyID);
        storyTitle.setText(title);

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
    }

    public void newChild(View view){
        childPickDialog = new AlertDialog.Builder(ConfigureUsers.this);
        LayoutInflater childInflater = getLayoutInflater();
        View childPickView = childInflater.inflate(R.layout.custom_recycle_dialog, null);
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

    public void flushDynamicUserChanges(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ArrayList<IdData> currentUserMapDB = ActivityHelper.getUserList(getApplication(), storyID);
        for (IdData idData : currentUserMapDB) {
            if (!childList.contains(idData)) {
                String selectionChild = DatabaseNameHelper.UserStoryEntry.COLUMN_STORY_ID + " = ? AND " + DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID + " = ?";
                String[] args = {storyID, idData.getId()};
                db.delete(DatabaseNameHelper.UserStoryEntry.TABLE_NAME, selectionChild, args);
            }
        }
        for (IdData idData : childList) {
            if (!currentUserMapDB.contains(idData)) {
                ContentValues values = new ContentValues();
                values.put(DatabaseNameHelper.UserStoryEntry.COLUMN_STORY_ID, storyID);
                values.put(DatabaseNameHelper.UserStoryEntry.COLUMN_USER_ID, idData.getId());
                db.insert(DatabaseNameHelper.UserStoryEntry.TABLE_NAME, null, values);
            }
        }
        db.close();
    }

    public void confirm(View view){
        flushDynamicUserChanges();
        finish();
    }

    public void cancel(View view){
        cancelConfirmDialog = new AlertDialog.Builder(ConfigureUsers.this);
        cancelConfirmDialog.setTitle(R.string.cancel);
        cancelConfirmDialog.setMessage(getString(R.string.cancel_confirm));
        cancelConfirmDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        cancelConfirmDialog.setNegativeButton(R.string.back_button, null);
        cancelConfirmDialog.show();
    }

}
