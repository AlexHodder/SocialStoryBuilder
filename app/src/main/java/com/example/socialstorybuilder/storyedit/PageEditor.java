package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.io.IOException;

import static com.example.socialstorybuilder.ActivityHelper.*;

public class PageEditor extends AppCompatActivity {

    private EditText text;
    private LinearLayout imageLayout;
    private String storyID;
    private int pageNo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_editor);
        text = findViewById(R.id.page_text);
        imageLayout = findViewById(R.id.image_layout);
        Intent intent = getIntent();
        storyID = intent.getStringExtra("story_id");
        pageNo = intent.getIntExtra("page_no", -1);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    Uri image = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                        ImageView imageView = new ImageView(getApplicationContext());
                        imageView.setImageBitmap(bitmap);
                        imageLayout.addView(imageView);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }

    public void confirm(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(PageEntry.COLUMN_STORY_ID, storyID);
        values.put(PageEntry.COLUMN_PAGE_NO, pageNo);
        values.put(PageEntry.COLUMN_TEXT, text.getText().toString());

        long rowID = db.insert(ChildUserEntry.TABLE_NAME, null, values);
        if (rowID > 0) switchToConfigureStory(view);
    }

    public void switchToConfigureStory(View view) {
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }


    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ActivityHelper.GALLERY_REQUEST_CODE);
    }
}
