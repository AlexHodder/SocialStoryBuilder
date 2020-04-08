package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

import java.util.ArrayList;


public class ReadEditSelect extends AppCompatActivity {

    private ListRecyclerAdapter listAdapter;
    private ArrayList<IdData> storyList;
    private DecoratedRecyclerView storyView;

    private int selectedItem;
    private IdData selectedStory;
    private String user;
    private AlertDialog.Builder deleteConfirmDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_edit_select);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        storyList = ActivityHelper.getAdultStoryList(getApplicationContext(), user);
        listAdapter = new ListRecyclerAdapter(storyList);

        storyView = findViewById(R.id.story_select);
        storyView.setAdapter(listAdapter);

        listAdapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != RecyclerView.NO_POSITION) selectedStory = listAdapter.getItem(position);
                selectedItem = position;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        storyList = new ArrayList<>(ActivityHelper.getAdultStoryList(getApplicationContext(), user));
        listAdapter.refresh(storyList);
    }

    public void switchToRead(View view){
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("story_id", selectedStory.getId());
        intent.putExtra("statistics", false);
        startActivity(intent);
    }

    public void switchToEdit(View view){
        if (selectedItem != RecyclerView.NO_POSITION){
            Intent intent = new Intent(this, ConfigureStory.class);
            intent.putExtra("user", user);
            intent.putExtra("story_id", selectedStory.getId());
            startActivity(intent);
        }
    }

    public void deletePage(final View view){
        if (selectedItem != RecyclerView.NO_POSITION) {
            deleteConfirmDialog = new AlertDialog.Builder(ReadEditSelect.this);
            deleteConfirmDialog.setMessage(getString(R.string.delete_confirm, selectedStory.getData()));
            deleteConfirmDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    confirmDelete(view);
                }
            });
            deleteConfirmDialog.setNegativeButton(R.string.cancel, null);
            deleteConfirmDialog.show();
        }

    }

    public void confirmDelete(View view){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] selectionArgs = {selectedStory.getId()};
        db.delete(DatabaseNameHelper.StoryEntry.TABLE_NAME, "_id = ?", selectionArgs);
        storyList.remove(selectedItem);
        listAdapter.itemRemoved(selectedItem);
    }

    public void switchToCreate(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void back(View view){
        finish();
    }



}
