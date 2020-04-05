package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.pdf.PdfDocument;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.socialstorybuilder.ActivityHelper.*;

public class PageEditor extends AppCompatActivity {

    private EditText text;
    private LinearLayout imageLayout;
    private String storyID;
    private String pageID;
    private ArrayList<String> addImageUriList;
    private ArrayList<String> removeImageUriList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_editor);
        text = findViewById(R.id.page_text);
        imageLayout = findViewById(R.id.image_layout);
        try {
            populateLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Intent intent = getIntent();
        storyID = intent.getStringExtra("story_id");
        Long pageIDLong = intent.getLongExtra("page_id", -1);
        pageID = String.valueOf(pageIDLong);
        addImageUriList = new ArrayList<>();
        removeImageUriList = new ArrayList<>();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if (requestCode == GALLERY_REQUEST_CODE) {
                Uri image = data.getData();
                try {
                    assert image != null;
                    Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), image);
                    ImageView imageView = new ImageView(getApplicationContext());
                    imageView.setClickable(true);
                    imageView.setImageBitmap(bitmap);
                    imageView.setTag(image.toString());
                    imageLayout.addView(imageView);
                    addImageUriList.add(image.toString());
                } catch (IOException e) {
                    Log.i("TAG", "Some exception " + e);
                }
            }
    }

    public void confirm(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues pageValues = new ContentValues();
        pageValues.put(PageEntry.COLUMN_STORY_ID, storyID);
        pageValues.put(PageEntry.COLUMN_TEXT, text.getText().toString());

        long rowID = db.update(PageEntry.TABLE_NAME, pageValues, "_id = ?", new String[]{String.valueOf(pageID)});

        for (String uri : addImageUriList){
            ContentValues imageValues = new ContentValues();
            imageValues.put(ImageEntry.COLUMN_PAGE_ID, pageID);
            imageValues.put(ImageEntry.COLUMN_URI, uri);

            long imageRowID = db.insert(ImageEntry.TABLE_NAME, null, imageValues);
            //TODO write something if an image isn't correctly put into database
            if (imageRowID < 0) {
                System.out.println("IMAGE NOT STORED PROPERLY, URI: " + uri);
                return;
            }
        }

        for (String uri : removeImageUriList){
            String selection = ImageEntry.COLUMN_URI + " = ? WHERE " + ImageEntry._ID + " = ";
            String[] selectionArgs = { uri };

            long imageRowID = db.delete(ImageEntry.TABLE_NAME, selection, selectionArgs);
            //TODO write something if an image isn't correctly put into database
            if (imageRowID < 0) {
                System.out.println("IMAGE NOT REMOVED PROPERLY, URI: " + uri);
                return;
            }
        }

        if (rowID > 0) finish();
    }

    public void removeImage(View view){
    }


    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ActivityHelper.GALLERY_REQUEST_CODE);
    }

    private void populateLayout() throws IOException {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {pageID};

        Cursor textCursor = db.rawQuery("SELECT "+ PageEntry.COLUMN_TEXT + " FROM " + PageEntry.TABLE_NAME + " WHERE " + PageEntry._ID + " = ?", selectionArgs);
        textCursor.moveToFirst();
        text.setText(textCursor.getString(textCursor.getColumnIndex(PageEntry.COLUMN_TEXT)));

        String[] projection = {ImageEntry.COLUMN_URI};
        String selection = ImageEntry.COLUMN_PAGE_ID + "= ?";

        Cursor imageCursor = db.query(ImageEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        while (imageCursor.moveToNext()){
            String uriString = imageCursor.getString(imageCursor.getColumnIndex(ImageEntry.COLUMN_URI));
            Uri uri = Uri.parse(uriString);
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
            ImageView imageView = new ImageView(getApplicationContext());
            imageView.setClickable(true);
            imageView.setImageBitmap(bitmap);
            imageView.setTag(uriString);
            imageLayout.addView(imageView);
        }
        imageCursor.close();

    }
}
