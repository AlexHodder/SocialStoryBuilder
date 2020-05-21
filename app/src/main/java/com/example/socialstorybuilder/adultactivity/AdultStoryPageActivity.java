package com.example.socialstorybuilder.adultactivity;

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
import com.example.socialstorybuilder.storyedit.ConfigureStory;
import com.example.socialstorybuilder.storyedit.StatisticViewer;
import com.example.socialstorybuilder.storyedit.StoryReader;

import java.util.ArrayList;

/**
 * Activity for Adult account stories.
 *
 * @since 1.2.3
 */
public class AdultStoryPageActivity extends AppCompatActivity {

    private ListRecyclerAdapter listAdapter;
    private ArrayList<IdData> storyList;
    private DecoratedRecyclerView storyView;

    private int selectedItem = RecyclerView.NO_POSITION;
    private IdData selectedStory;
    private String userID;

    private AlertDialog.Builder deleteConfirmDialog;
    private AlertDialog.Builder hintDialog;

    /**
     * Method called on activity creation, initialising properties.
     * ClickListeners generated, and adapters set.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_story_page);

        Intent intent = getIntent();
        userID = intent.getStringExtra("user_id");

        storyList = ActivityHelper.getAdultStoryList(getApplicationContext(), userID);
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

        hintDialog = new AlertDialog.Builder(AdultStoryPageActivity.this);
        hintDialog.setMessage(R.string.story_create_help);
        hintDialog.setPositiveButton(R.string.popup_close, null);

    }

    /**
     * Method called when activity refreshed.
     * Refreshes list with up to date stories.
     */
    @Override
    protected void onResume() {
        super.onResume();
        storyList = new ArrayList<>(ActivityHelper.getAdultStoryList(getApplicationContext(), userID));
        listAdapter.refresh(storyList);
    }

    /**
     * Activity switcher to read selected story.
     * @param view Current view
     */
    public void switchToRead(View view){
        if (selectedItem != RecyclerView.NO_POSITION) {
            Intent intent = new Intent(this, StoryReader.class);
            intent.putExtra("story_id", selectedStory.getId());
            intent.putExtra("statistics", false);
            startActivity(intent);
        }
    }

    /**
     * Activity switcher to edit selected story.
     * @param view Current view
     */
    public void switchToEdit(View view){
        if (selectedItem != RecyclerView.NO_POSITION){
            Intent intent = new Intent(this, ConfigureStory.class);
            intent.putExtra("user_id", userID);
            intent.putExtra("story_id", selectedStory.getId());
            startActivity(intent);
        }
    }

    /**
     * Method to ensure user confirms page deletion.
     * Calls confirmDelete if user confirms.
     * @param view Current view
     */
    public void deletePage(final View view){
        if (selectedItem != RecyclerView.NO_POSITION) {
            deleteConfirmDialog = new AlertDialog.Builder(AdultStoryPageActivity.this);
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

    /**
     * Removes selected story from story database and updates list.
     *
     * @param view Current view
     */
    public void confirmDelete(View view){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        String[] selectionArgs = {selectedStory.getId()};
        db.delete(DatabaseNameHelper.StoryEntry.TABLE_NAME, "_id = ?", selectionArgs);
        storyList.remove(selectedItem);
        listAdapter.itemRemoved(selectedItem);
    }

    /**
     * Activity switcher to create a new story, passes userID.
     * @param view Current view
     */
    public void switchToCreate(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user_id", userID);
        startActivity(intent);
    }

    /**
     * Activity switcher to statistics viewer.
     * @param view Current view
     */
    public void switchToStatistics(View view){
        Intent intent = new Intent(this, StatisticViewer.class);
        startActivity(intent);
    }

    /**
     * Ends the activity, switching to top of stack.
     * @param view Current view
     */
    public void back(View view){
        finish();
    }

}
