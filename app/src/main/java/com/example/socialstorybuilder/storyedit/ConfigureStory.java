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

import java.util.ArrayList;

public class ConfigureStory extends AppCompatActivity {

    private String title;
    private String author;
    private String date;

    private String backgroundColour;
    private Button backgroundColourButton;

    private EditText storyTitle;

    private String storyID;

    private AlertDialog.Builder colorPickerDialog;
    private AlertDialog.Builder cancelConfirmDialog;



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
            author = intent.getStringExtra("user");

            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getReadableDatabase();

            String[] story_projection = {StoryEntry.COLUMN_DATE, StoryEntry.COLUMN_TITLE, StoryEntry.BACKGROUND_COLOR};
            String story_selection = StoryEntry._ID + " = ?" + " AND " + StoryEntry.COLUMN_AUTHOR + " = ?";
            String[] story_selection_args = {storyID, author};

            Cursor storyCursor = db.query(StoryEntry.TABLE_NAME, story_projection, story_selection, story_selection_args, null, null, null);
            storyCursor.moveToNext();
            title = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.COLUMN_TITLE));

            date = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.COLUMN_DATE));
            String color = storyCursor.getString(storyCursor.getColumnIndex(StoryEntry.BACKGROUND_COLOR));
            setBackgroundColour(color);
            storyCursor.close();
        }
        else{
            author = intent.getStringExtra("user");
            //TODO write a date getter function
//            date = getCurrentDate();
            date = "INSERT DATE";
            title = "New Book";
            setBackgroundColour("#FFFFFF");
            DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
            SQLiteDatabase db = dbHelper.getWritableDatabase();
            ContentValues values = new ContentValues();
            values.put(StoryEntry.COLUMN_AUTHOR, author);
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

    public void switchToConfigureUsers(View view){
        Intent intent = new Intent(this, ConfigureUsers.class);
        intent.putExtra("story_id", storyID);
        startActivity(intent);
    }

    public void switchToConfigurePages(View view){
        Intent intent = new Intent(this, ConfigurePages.class);
        intent.putExtra("story_id", storyID);
        startActivity(intent);
    }

    public void flushDynamicChanges(){
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(StoryEntry.COLUMN_TITLE, storyTitle.getText().toString());
        values.put(StoryEntry.COLUMN_DATE, "01-01-2020");
        values.put(StoryEntry.BACKGROUND_COLOR, backgroundColour);
        String[] args = {storyID};
        db.update(StoryEntry.TABLE_NAME, values, "_id = ?", args);
        db.close();
    }

    public void preview(View view){
        flushDynamicChanges();
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("story_id", storyID);
        startActivity(intent);
    }

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

    public void confirm(View view){
        flushDynamicChanges();
        finish();
    }

    public String getBackgroundDBColour() {
        return backgroundColour;
    }

    public void setBackgroundColour(String backgroundColour) {
        this.backgroundColour = backgroundColour;
        this.backgroundColourButton.setBackgroundColor(Color.parseColor(backgroundColour));
    }
}
