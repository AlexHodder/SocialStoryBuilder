package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;

import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * Activity to edit current story features.
 *
 * @since 1.0
 */
public class ConfigureStory extends AppCompatActivity {

    private String title;
    private String authorID;
    private String date;

    private String backgroundColour;
    private Button backgroundColourButton;

    private EditText storyTitle;

    private String storyID;

    private AlertDialog.Builder colorPickerDialog;
    private AlertDialog.Builder cancelConfirmDialog;

    /**
     * Method called on activity creation.
     * Initialises properties using intent, and calls to the database to retrieve story information.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configure_story);
        Intent intent = getIntent();
        storyTitle = findViewById(R.id.story_name_input);
        backgroundColourButton = findViewById(R.id.colorButton);

        //Retrieve values and setup database
        if (intent.hasExtra("story_id")){
            storyID = intent.getStringExtra("story_id");
            authorID = intent.getStringExtra("user_id");

            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] story_projection = {StoryEntry.COLUMN_DATE, StoryEntry.COLUMN_TITLE, StoryEntry.BACKGROUND_COLOR};
            String story_selection = StoryEntry._ID + " = ?" + " AND " + StoryEntry.COLUMN_AUTHOR_ID + " = ?";
            String[] story_selection_args = {storyID, authorID};

            Cursor storyCursor = db.query(StoryEntry.TABLE_NAME, story_projection, story_selection, story_selection_args, null, null, null);
            storyCursor.moveToNext();
            title = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.COLUMN_TITLE));

            date = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.COLUMN_DATE));
            String color = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.BACKGROUND_COLOR));
            setBackgroundColour(color);
            storyCursor.close();
        }
        else{
            authorID = intent.getStringExtra("user_id");
            Date d = new Date();
            DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, Locale.UK);
            date = df.format(d);
            title = "New Book";
            setBackgroundColour("#FFFFFF");
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(StoryEntry.COLUMN_AUTHOR_ID, authorID);
            values.put(StoryEntry.COLUMN_DATE, date);
            values.put(StoryEntry.COLUMN_TITLE, title);
            values.put(StoryEntry.BACKGROUND_COLOR, getBackgroundDBColour());
            long rowID = db.insert(StoryEntry.TABLE_NAME, null, values);
            if (rowID>0) storyID = Long.toString(rowID);
            db.close();
            title = "";
        }

        backgroundColourButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                colorPicker();
            }
        });
        storyTitle.setText(title);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    /**
     * Method called via button press.
     * Creates a pop-up, with a list of colours that can be selected for the story.
     */
    public void colorPicker(){
        colorPickerDialog = new AlertDialog.Builder(ConfigureStory.this);
        LayoutInflater childInflater = getLayoutInflater();
        View colorPickView = childInflater.inflate(R.layout.custom_recycle_dialog, null);
        colorPickerDialog.setView(colorPickView);
        colorPickerDialog.setTitle(R.string.color_picker);
        DecoratedRecyclerView listView = colorPickView.findViewById(R.id.recycleView1);
        ArrayList<IdData> colorList = ActivityHelper.colorWheel(getApplicationContext());
        final ListRecyclerAdapter colorListAdapter = new ListRecyclerAdapter(colorList, R.color.adultColor);
        listView.setAdapter(colorListAdapter);

        final IdData[] newData = new IdData[1];
        final Integer[] selectedPos = {RecyclerView.NO_POSITION};

        colorListAdapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > RecyclerView.NO_POSITION){
                    newData[0] = colorListAdapter.getItem(position);
                }
                selectedPos[0] = position;
            }
        });
        colorPickerDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(selectedPos[0] != RecyclerView.NO_POSITION) {
                    setBackgroundColour(newData[0].getId());
                }

            }
        });
        colorPickerDialog.setNegativeButton(R.string.cancel, null);
        colorPickerDialog.show();
    }

    /**
     * Activity switcher to ConfigureUsers, passing current story ID.
     * @param view
     */
    public void switchToConfigureUsers(View view){
        Intent intent = new Intent(this, ConfigureUsers.class);
        intent.putExtra("story_id", storyID);
        startActivity(intent);
    }

    /**
     * Activity switcher to ConfigurePages, passing current story ID.
     * @param view
     */
    public void switchToConfigurePages(View view){
        Intent intent = new Intent(this, ConfigurePages.class);
        intent.putExtra("story_id", storyID);
        startActivity(intent);
    }

    /**
     * Method to update database, with current title and background colour.
     */
    public void flushDynamicChanges(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StoryEntry.COLUMN_TITLE, storyTitle.getText().toString());
        values.put(StoryEntry.BACKGROUND_COLOR, backgroundColour);
        String[] args = {storyID};
        db.update(StoryEntry.TABLE_NAME, values, "_id = ?", args);
        db.close();
    }

    /**
     * Activity switcher to StoryReader, passing the story ID.
     * @param view
     */
    public void preview(View view){
        flushDynamicChanges();
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("story_id", storyID);
        startActivity(intent);
    }

    /**
     * Activity switcher, cancelling changes.
     * Creates a pop-up asking if user is sure they want to cancel, and ends current activity if confirmed.
     * @param view
     */
    public void cancel(View view){
        cancelConfirmDialog = new AlertDialog.Builder(ConfigureStory.this);
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

    /**
     * Activity switcher, ends current activity and flushes changes to the database.
     * @param view
     */
    public void confirm(View view){
        flushDynamicChanges();
        finish();
    }

    /**
     *
     * @return current background colour
     */
    public String getBackgroundDBColour() {
        return backgroundColour;
    }

    /**
     * Sets background colour and changes the colour of the background button.
     * @param backgroundColour background colour to set
     */
    public void setBackgroundColour(String backgroundColour) {
        this.backgroundColour = backgroundColour;
        this.backgroundColourButton.setBackgroundColor(Color.parseColor(backgroundColour));
    }
}
