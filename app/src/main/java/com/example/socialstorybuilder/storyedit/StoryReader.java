package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

public class StoryReader extends AppCompatActivity {

    private int pageNo;
    private String storyId;
    private boolean statistics;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_reader);
        TextView textView = findViewById(R.id.display_text);
        LinearLayout imageLayout = findViewById(R.id.image_display);
        TextView pageNoView = findViewById(R.id.page_no_text);
        Button prevPgButton = findViewById(R.id.prev_pg);
        Button nextPgButton = findViewById(R.id.next_pg);

        String pageId;
        String text;
        int pageTotal;

        Intent intent = getIntent();
        pageNo = intent.getIntExtra("page_no", 1);
        storyId = intent.getStringExtra("story_id");
        statistics = intent.getBooleanExtra("statistics", false);

        // LOAD ALL VARIABLES FROM DATABASE

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Page_ID selection
        String[] pageProjection = {PageEntry.COLUMN_TEXT, PageEntry._ID};
        String pageSelection = PageEntry.COLUMN_STORY_ID + " = ? AND " + PageEntry.COLUMN_PAGE_NO + " = ?" ;
        String[] pageSelectionArgs = { storyId, String.valueOf(pageNo) };

        Cursor pageCursor = db.query(PageEntry.TABLE_NAME, pageProjection, pageSelection, pageSelectionArgs, null, null, PageEntry._ID);
        pageCursor.moveToFirst();
        pageId = pageCursor.getString(pageCursor.getColumnIndex(PageEntry._ID));
        text = pageCursor.getString(pageCursor.getColumnIndex(PageEntry.COLUMN_TEXT));
        pageTotal = (int) DatabaseUtils.queryNumEntries(db, PageEntry.TABLE_NAME, PageEntry.COLUMN_STORY_ID + " = ?", new String[]{storyId});
        pageCursor.close();

        textView.setText(text);
        String pageText = String.format("%s/%d",getResources().getString(R.string.page_no, pageNo), pageTotal);
        pageNoView.setText(pageText);

        // Image Selection
        String[] imageProjection = {ImageEntry.COLUMN_URI};
        String imageSelection = ImageEntry.COLUMN_PAGE_ID + " = ?";
        String[] imageSelectionArgs = { pageId };

        Cursor imageCursor = db.query(ImageEntry.TABLE_NAME, imageProjection, imageSelection, imageSelectionArgs, null, null, null);
        while(imageCursor.moveToNext()) {
            String uri = imageCursor.getString(imageCursor.getColumnIndex(ImageEntry.COLUMN_URI));
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setImageURI(Uri.parse(uri));
            imageLayout.addView(imageView);
        }
        imageCursor.close();
        db.close();
        // BUTTON LOGIC
        if (pageNo == 1){
            prevPgButton.setVisibility(View.INVISIBLE);
            prevPgButton.setEnabled(false);
        }
        if (pageNo == pageTotal){
            nextPgButton.setText(R.string.finish_story);
            nextPgButton.setOnClickListener(new View.OnClickListener(){

                @Override
                public void onClick(View v) {
                    finishStory(v);
                }
            });
        }

    }

    public void nextPage(View view){
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("page_no", pageNo + 1);
        intent.putExtra("story_id", storyId);
        intent.putExtra("statistics", statistics);
        startActivity(intent);
        finish();
    }

    public void prevPage(View view){
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("page_no", pageNo - 1);
        intent.putExtra("story_id", storyId);
        intent.putExtra("statistics", statistics);
        startActivity(intent);
        finish();
    }

    public void finishStory(View v){
        if (statistics){
            recordStatistics();
        }
        finish();
    }

    public void recordStatistics(){
        //WRITE STATISTICS RECORDING DATABASE TABLE
    }
}
