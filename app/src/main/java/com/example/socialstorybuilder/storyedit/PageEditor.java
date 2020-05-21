package com.example.socialstorybuilder.storyedit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.io.IOException;
import java.util.ArrayList;

import static com.example.socialstorybuilder.ActivityHelper.*;

/**
 * Activity used to edit individual pages.
 */
public class PageEditor extends AppCompatActivity {

    private EditText text;
    private LinearLayout imageLayout;
    private String storyID;
    private String pageID;
    private ArrayList<String> addImageUriList;
    private ArrayList<String> removeImageUriList;
    private ImageView selectedImage;
    private AlertDialog.Builder cancelConfirmDialog;
    private MediaPlayer mp;
    private Uri soundFile;
    private ImageButton playSoundImage;
    private AlertDialog.Builder sampleSentenceDialog;

    /**
     * Method called on activity creation.
     * Initialises properties from intent.
     * Sets up the page layout.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.page_editor);

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

        soundFile = null;
        try {
            populateLayout();
        } catch (IOException e) {
            e.printStackTrace();
        }

        playSoundImage = findViewById(R.id.imageView2);
        playSoundImage.setClickable(true);
        playSoundImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    if (soundFile != null) playSound(v);
                    else{
                        Toast toast = Toast.makeText(getApplicationContext(), "No file selected.", Toast.LENGTH_SHORT);
                        toast.show();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });

    }

    /**
     * Method called when returning from a different activity.
     * Used to update image or audio files.
     * @param requestCode code passed to previous activity on its creation
     * @param resultCode code returned from previous activity on destruction
     * @param data data returned from previous activity
     */
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
                    Log.i("File Import", "File not imported: " + e);
                }
            }
        else if (requestCode == AUDIO_REQUEST_CODE){
            soundFile = data.getData();
            }
    }

    /**
     * Activity switcher that ends the current activity.
     * Method also flushes all changes, to the database.
     * @param view Current view
     */
    public void confirm(View view) {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues pageValues = new ContentValues();
        pageValues.put(PageEntry.COLUMN_STORY_ID, storyID);
        pageValues.put(PageEntry.COLUMN_TEXT, text.getText().toString());

        if (soundFile != null) pageValues.put(PageEntry.COLUMN_SOUND, soundFile.toString());
        else pageValues.putNull(PageEntry.COLUMN_SOUND);

        long rowID = db.update(PageEntry.TABLE_NAME, pageValues, "_id = ?", new String[]{String.valueOf(pageID)});

        for (String uri : addImageUriList){
            ContentValues imageValues = new ContentValues();
            imageValues.put(ImageEntry.COLUMN_PAGE_ID, pageID);
            imageValues.put(ImageEntry.COLUMN_URI, uri);

            long imageRowID = db.insert(ImageEntry.TABLE_NAME, null, imageValues);
            if (imageRowID < 0) {
                db.close();
                return;
            }
        }

        for (String uri : removeImageUriList){
            String selection = ImageEntry.COLUMN_URI + " = ? AND " + ImageEntry.COLUMN_PAGE_ID + " = ?";
            String[] selectionArgs = { uri, pageID };

            long imageRowID = db.delete(ImageEntry.TABLE_NAME, selection, selectionArgs);
            if (imageRowID < 0) {
                db.close();
                return;
            }
        }
        db.close();
        dbHelper.close();
        if (rowID > 0) finish();
    }

    /**
     * Method to remove selected image from the page.
     * @param view Current view
     */
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

    /**
     * Activity switcher to select an image from the gallery.
     * @param view Current view
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("image/*");
        startActivityForResult(intent, ActivityHelper.GALLERY_REQUEST_CODE);
    }

    /**
     * Activity switcher to select a sound file from files.
     * @param view Current view
     */
    public void selectSound(View view) {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.setType("audio/*");
        startActivityForResult(intent, ActivityHelper.AUDIO_REQUEST_CODE);
    }

    /**
     * Method to remove the current sound file.
     * @param view Current view
     */
    public void removeSound(View view) {
        soundFile = null;
    }

    /**
     * Method to play the currently loaded file.
     * If no file loaded displays message to the user.
     * @param view Current view
     * @throws IOException if file not found
     */
    public void playSound(View view) throws IOException {
        if (mp == null){
            try {
                mp = MediaPlayer.create(this, soundFile);
                mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            playSoundImage.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                        }
                    }
                });
                mp.start();
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    playSoundImage.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                }
            } catch (NullPointerException e){
                Toast.makeText(getApplicationContext(), "Sound file doesn't exist. Try Removing.", Toast.LENGTH_LONG).show();
            }

        }
        else {
            if (mp.isPlaying()) {
                mp.pause();
                mp.seekTo(0);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    playSoundImage.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                }
            }
            else{
                mp.stop();
                mp.reset();
                mp.setDataSource(this, soundFile);
                mp.prepare();// might take long! (for buffering, etc)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    playSoundImage.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
                }
                mp.start();
            }
        }
    }

    /**
     * Method to populate the layout with information from the database.
     * @throws IOException if files not found
     */
    private void populateLayout() throws IOException {
        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] selectionArgs = {pageID};

        // Query selects text and sound from the PageEntry table with the page with matching id.
        Cursor textCursor = db.rawQuery("SELECT "+ PageEntry.COLUMN_TEXT + ", " + PageEntry.COLUMN_SOUND + " FROM " + PageEntry.TABLE_NAME + " WHERE " + PageEntry._ID + " = ?", selectionArgs);
        textCursor.moveToFirst();
        text.setText(textCursor.getString(textCursor.getColumnIndex(PageEntry.COLUMN_TEXT)));
        if (!textCursor.isNull(textCursor.getColumnIndex(PageEntry.COLUMN_SOUND))){
            String soundFileString = textCursor.getString(textCursor.getColumnIndex(PageEntry.COLUMN_SOUND));
            soundFile = Uri.parse(soundFileString);
        } else soundFile = null;
        textCursor.close();

        String[] projection = {ImageEntry.COLUMN_URI};
        String selection = ImageEntry.COLUMN_PAGE_ID + "= ?";

        // Query selects image URI's (in string format) from ImageEntry table with page matching id.
        Cursor imageCursor = db.query(ImageEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        while (imageCursor.moveToNext()){
            String uriString = imageCursor.getString(imageCursor.getColumnIndex(ImageEntry.COLUMN_URI));
            Uri uri = Uri.parse(uriString);
            Bitmap bitmap;
            ImageView imageView = new ImageView(getApplicationContext());
            try{
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setClickable(true);
                imageView.setImageBitmap(bitmap);
                imageView.setTag(uriString);
            }
            catch (IOException e){
                imageView.setImageResource(R.drawable.filenotfound);
                imageView.setTag(uriString);
            }

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
        db.close();
    }

    /**
     * Activity switcher, cancelling changes.
     * Creates a pop-up asking if user is sure they want to cancel, and ends current activity if confirmed.
     * @param view Current view
     */
    public void cancel(final View view){
        cancelConfirmDialog = new AlertDialog.Builder(PageEditor.this);
        cancelConfirmDialog.setTitle(R.string.cancel);
        cancelConfirmDialog.setMessage(getString(R.string.cancel_confirm));
        cancelConfirmDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        });
        cancelConfirmDialog.setNegativeButton(R.string.cancel, null);
        cancelConfirmDialog.show();
    }

    /**
     * Method creating a pop-up, with a list of sample sentences.
     * @param view Current view
     */
    public void sampleSentence(View view){
        sampleSentenceDialog = new AlertDialog.Builder(PageEditor.this);
        sampleSentenceDialog.setTitle(R.string.sample_sentence_title);
        final String[] sampleSentenceList = getResources().getStringArray(R.array.sample_sentences);
        sampleSentenceDialog.setItems(sampleSentenceList, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                text.setText(sampleSentenceList[which]);
            }
        });
        sampleSentenceDialog.setNegativeButton(R.string.cancel, null);
        sampleSentenceDialog.show();
    }

    /**
     * Method to release the sound file resources on activity exit.
     */
    @Override
    protected void onDestroy() {
        if (mp != null){
            mp.release();
            mp = null;
        }

        super.onDestroy();
    }
}
