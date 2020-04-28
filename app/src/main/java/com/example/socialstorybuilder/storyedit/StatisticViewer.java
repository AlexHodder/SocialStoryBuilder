package com.example.socialstorybuilder.storyedit;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.TableViewAdapter;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.util.ArrayList;

public class StatisticViewer extends AppCompatActivity {

    private TableLayout tableLayout;
    private ArrayList<ArrayList<String>> data;
    private TableViewAdapter adapter;
    private ArrayList<String> headings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.statistic_viewer);
        final Spinner childDropDown = findViewById(R.id.childSpinner);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] childProjection = {ChildUserEntry.COLUMN_NAME};
        Cursor childCursor = db.query(ChildUserEntry.TABLE_NAME, childProjection, null, null, null, null, null);
        ArrayList<String> childList = new ArrayList<>();
        childList.add("All");
        while(childCursor.moveToNext()) {
            String childName = childCursor.getString(childCursor.getColumnIndex(ChildUserEntry.COLUMN_NAME));
            childList.add(childName);
        }

        childCursor.close();
        db.close();

        ArrayAdapter<String> childAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, childList);

        childDropDown.setAdapter(childAdapter);
        Button search = findViewById(R.id.queryButton);
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                query(childDropDown.getSelectedItem().toString());
            }
        });

        tableLayout = findViewById(R.id.main_table);
        tableLayout.setVisibility(View.INVISIBLE);

        RecyclerView table = findViewById(R.id.tableRecyclerView);
        data = new ArrayList<>();


        headings = new ArrayList<>();
        headings.add(getString(R.string.story));
        headings.add(getString(R.string.child_name));
        headings.add(getString(R.string.total_reads));
        headings.add(getString(R.string.happy));
        headings.add(getString(R.string.sad));
        headings.add(getString(R.string.angry));
        headings.add(getString(R.string.confused));
        headings.add(getString(R.string.no_feedback));

        data.add(headings);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        table.setLayoutManager(linearLayoutManager);
        adapter = new TableViewAdapter(data);
        table.setAdapter(adapter);

    }

    private void query(String childSelection) {
        data.clear();
        data.add(headings);

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        // Retrieve childID from name (if individual name given)
        String childID;
        if (childSelection.equals("All")) {
            childID = null;
        } else {
            Cursor childIDCursor;
            String[] projection = {ChildUserEntry._ID};
            childIDCursor = db.query(ChildUserEntry.TABLE_NAME, projection, ChildUserEntry.COLUMN_NAME+" = ?", new String[]{childSelection}, null, null, null);
            childIDCursor.moveToFirst();
            childID = childIDCursor.getString(childIDCursor.getColumnIndex(ChildUserEntry._ID));
            childIDCursor.close();
        }

        Cursor feedbackCursor;
        if (childID != null) {
            // Setup correct selection from feedback entry table
            feedbackCursor = db.rawQuery("SELECT " + FeedbackEntry.COLUMN_STORY_ID +
                            ", count(" + FeedbackEntry._ID + ") as total_reads" +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS happy_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS sad_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS angry_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS confused_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS no_feedback_reads " +
                            "FROM " + FeedbackEntry.TABLE_NAME + " WHERE " + FeedbackEntry.COLUMN_USER_ID + " = ?" + " GROUP BY " + FeedbackEntry.COLUMN_STORY_ID,
                    new String[]{"0", "1", "2", "3", "-1", childID});
        } else {
            feedbackCursor = db.rawQuery("SELECT " + FeedbackEntry.COLUMN_STORY_ID +
                            ", count(" + FeedbackEntry._ID + ") as total_reads" +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS happy_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS sad_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS angry_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS confused_reads " +
                            ", count(CASE WHEN " + FeedbackEntry.COLUMN_FEEDBACK + " = ? THEN 1 END) AS no_feedback_reads " +
                            "FROM " + FeedbackEntry.TABLE_NAME + " GROUP BY " + FeedbackEntry.COLUMN_STORY_ID,
                    new String[]{"0", "1", "2", "3", "-1"});
        }
        while (feedbackCursor.moveToNext()) {
            ArrayList<String> row = new ArrayList<>();
            row.add(ActivityHelper.getTitleFromID(getApplicationContext(),feedbackCursor.getString(feedbackCursor.getColumnIndex(FeedbackEntry.COLUMN_STORY_ID))));
            row.add(childSelection);
            row.add(feedbackCursor.getString(feedbackCursor.getColumnIndex("total_reads")));
            row.add(feedbackCursor.getString(feedbackCursor.getColumnIndex("happy_reads")));
            row.add(feedbackCursor.getString(feedbackCursor.getColumnIndex("sad_reads")));
            row.add(feedbackCursor.getString(feedbackCursor.getColumnIndex("angry_reads")));
            row.add(feedbackCursor.getString(feedbackCursor.getColumnIndex("confused_reads")));
            row.add(feedbackCursor.getString(feedbackCursor.getColumnIndex("no_feedback_reads")));
            data.add(row);
        }
        adapter.notifyDataSetChanged();

        feedbackCursor.close();
        db.close();

    }

}
