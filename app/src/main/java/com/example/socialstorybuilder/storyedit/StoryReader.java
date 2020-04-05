package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_story_reader);
        TextView textView = findViewById(R.id.display_text);
        LinearLayout imageLayout = findViewById(R.id.image_display);
        TextView pageNoView = findViewById(R.id.page_no_digits);
        Button prevPgButton = findViewById(R.id.prev_pg);
        Button nextPgButton = findViewById(R.id.next_pg);

        String pageId;
        String text;
        int pageTotal;

        Intent intent = getIntent();
        pageNo = intent.getIntExtra("page_no", 1);
        storyId = intent.getStringExtra("story_id");

        // LOAD ALL VARIABLES FROM DATABASE

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Page_ID selection
        String[] pageProjection = {PageEntry.COLUMN_TEXT, PageEntry._ID};
        String pageSelection = PageEntry.COLUMN_STORY_ID + " = ?";
        String[] pageSelectionArgs = { storyId };

        Cursor pageCursor = db.query(PageEntry.TABLE_NAME, pageProjection, pageSelection, pageSelectionArgs, null, null, PageEntry._ID);
        pageCursor.moveToFirst();
        pageCursor.moveToPosition(pageNo);
        pageId = pageCursor.getString(pageCursor.getColumnIndex(PageEntry._ID));
        text = pageCursor.getString(pageCursor.getColumnIndex(PageEntry.COLUMN_TEXT));
        pageTotal = pageCursor.getCount();
        pageCursor.close();

        textView.setText(text);
        String pageText = pageNo + "/" + pageTotal;
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
            imageLayout.addView(imageLayout);
        }
        imageCursor.close();

        // BUTTON LOGIC
        if (pageNo == 1){
            prevPgButton.setVisibility(View.INVISIBLE);
            prevPgButton.setEnabled(false);
        }
        if (pageNo == pageTotal){
            nextPgButton.setVisibility(View.INVISIBLE);
            nextPgButton.setEnabled(false);
        }

    }

    public void nextPage(View view){
        pageNo = pageNo++;

    }

    public void loadNewPage(View view){
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("page_no", pageNo);
        intent.putExtra("story_id", storyId);
        startActivity(intent);
    }
}
