package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
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
import android.widget.TextView;

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
    private ImageView selectedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_page_editor);

        Intent intent = getIntent();
        storyID = intent.getStringExtra("story_id");
        pageID = intent.getStringExtra("page_id");
        TextView title = findViewById(R.id.text_select_story);
        int pageNo = intent.getIntExtra("page_no", 0);
        title.setText(getResources().getString(R.string.page_no, pageNo));
        text = findViewById(R.id.page_text);
        imageLayout = findViewById(R.id.image_layout);



        addImageUriList = new ArrayList<>();
        removeImageUriList = new ArrayList<>();

        try {
            populateLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }
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
                    imageView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            if (selectedImage != null) selectedImage.setColorFilter(null);
                            selectedImage = (ImageView) v;
                            selectedImage.setColorFilter(Color.argb(50, 169, 169, 169));
                        }
                    });
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
                db.close();
                return;
            }
        }

        for (String uri : removeImageUriList){
            String selection = ImageEntry.COLUMN_URI + " = ? AND " + ImageEntry.COLUMN_PAGE_ID + " = ?";
            String[] selectionArgs = { uri, pageID };

            long imageRowID = db.delete(ImageEntry.TABLE_NAME, selection, selectionArgs);
            //TODO write something if an image isn't correctly put into database
            if (imageRowID < 0) {
                System.out.println("IMAGE NOT REMOVED PROPERLY, URI: " + uri);
                db.close();
                return;
            }
        }
        db.close();
        if (rowID > 0) finish();
    }

    public void removeImage(View view){
        if (selectedImage != null){
            String uri = (String) selectedImage.getTag();
            if (addImageUriList.contains(uri)){
                addImageUriList.remove(uri);
            }
            else removeImageUriList.add(uri);
            imageLayout.removeView(selectedImage);
            selectedImage = null;
        }
    }


    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, ActivityHelper.GALLERY_REQUEST_CODE);
    }

    private void populateLayout() throws IOException {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {pageID};

        Cursor textCursor = db.rawQuery("SELECT "+ PageEntry.COLUMN_TEXT + " FROM " + PageEntry.TABLE_NAME + " WHERE " + PageEntry._ID + " = ?", selectionArgs);
        System.out.println(textCursor.getCount());
        textCursor.moveToFirst();
        text.setText(textCursor.getString(textCursor.getColumnIndex(PageEntry.COLUMN_TEXT)));
        textCursor.close();

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
            imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (selectedImage != null) selectedImage.setColorFilter(null);
                    selectedImage = (ImageView) v;
                    selectedImage.setColorFilter(Color.argb(50, 169, 169, 169));
                }
            });
            imageLayout.addView(imageView);
        }
        imageCursor.close();

    }
}
