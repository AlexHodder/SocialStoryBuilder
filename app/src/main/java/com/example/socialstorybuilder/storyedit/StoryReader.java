package com.example.socialstorybuilder.storyedit;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.speech.tts.Voice;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Locale;

public class StoryReader extends AppCompatActivity {

    private int pageNo;
    private String storyId;
    private boolean statistics;
    private String userId;

    private ImageButton textToSpeechButton;
    private TextToSpeech textToSpeech;

    private MediaPlayer mediaPlayer;

    private ImageButton playSoundImage;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.story_reader);
        TextView textView = findViewById(R.id.display_text);
        LinearLayout imageLayout = findViewById(R.id.image_display);
        TextView pageNoView = findViewById(R.id.page_no_text);
        Button prevPgButton = findViewById(R.id.prev_pg);
        Button nextPgButton = findViewById(R.id.next_pg);
        playSoundImage = findViewById(R.id.imageView3);

        ConstraintLayout background = findViewById(R.id.backgroundLayout);

        String pageId;
        final String text;
        int pageTotal;

        Intent intent = getIntent();
        pageNo = intent.getIntExtra("page_no", 1);
        storyId = intent.getStringExtra("story_id");
        statistics = false;
        if (intent.hasExtra("user_id")) {
            statistics = true;
            userId = intent.getStringExtra("user_id");
        }

        // LOAD ALL VARIABLES FROM DATABASE

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        //Story background color selection
        Cursor colorQuery = db.rawQuery("SELECT "+ StoryEntry.BACKGROUND_COLOR + " FROM " + StoryEntry.TABLE_NAME + " WHERE " + StoryEntry._ID + " = ?", new String[]{storyId});
        colorQuery.moveToFirst();
        String color = colorQuery.getString(colorQuery.getColumnIndex(StoryEntry.BACKGROUND_COLOR));
        background.setBackgroundColor(Color.parseColor(color));
        colorQuery.close();

        //Page_ID selection
        String[] pageProjection = {PageEntry.COLUMN_TEXT, PageEntry._ID, PageEntry.COLUMN_SOUND};
        String pageSelection = PageEntry.COLUMN_STORY_ID + " = ? AND " + PageEntry.COLUMN_PAGE_NO + " = ?" ;
        String[] pageSelectionArgs = { storyId, String.valueOf(pageNo) };

        Cursor pageCursor = db.query(PageEntry.TABLE_NAME, pageProjection, pageSelection, pageSelectionArgs, null, null, PageEntry._ID);
        pageCursor.moveToFirst();
        pageId = pageCursor.getString(pageCursor.getColumnIndex(PageEntry._ID));
        String sound = null;
        if (pageCursor.isNull(pageCursor.getColumnIndex(PageEntry.COLUMN_SOUND))) playSoundImage.setVisibility(View.INVISIBLE);
        else sound = pageCursor.getString(pageCursor.getColumnIndex(PageEntry.COLUMN_SOUND));
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
            String uriString = imageCursor.getString(imageCursor.getColumnIndex(ImageEntry.COLUMN_URI));
            Uri uri = Uri.parse(uriString);
            ImageView imageView = new ImageView(getApplicationContext());
            try{
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri);
                imageView.setImageBitmap(bitmap);
            }
            catch (IOException e){
                imageView.setImageResource(R.drawable.filenotfound);
            }
            imageLayout.addView(imageView);
        }
        imageCursor.close();
        db.close();

        // Sound Logic
        if (sound != null){

            mediaPlayer = MediaPlayer.create(this, Uri.parse(sound));
            if (mediaPlayer != null){
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    @Override
                    public void onCompletion(MediaPlayer mp) {
                        playSoundImage.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
                    }
                });
                playSoundImage.setVisibility(View.VISIBLE);
                playSoundImage.setClickable(true);
                playSoundImage.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        playSound(v);
                    }
                });
            }
            else Toast.makeText(getApplicationContext(), "Sound file doesn't exist.", Toast.LENGTH_LONG).show();

        }
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
        final String voiceName = "en-gb-x-rjs#female_1-local";
        textToSpeechButton = findViewById(R.id.textToSpeechButton);
        textToSpeech = new TextToSpeech(this, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status == TextToSpeech.SUCCESS){
                    int result = textToSpeech.setLanguage(Locale.ENGLISH);
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED){
                        Log.e("TTS", "Language not supported.");
                    }
                    else textToSpeechButton.setEnabled(true);
                    textToSpeech.setVoice(new Voice(voiceName, Locale.UK, Voice.QUALITY_NORMAL, Voice.LATENCY_NORMAL, false, null));
                }
                else Log.e("TTS", "Init. failed");
            }
        }, "com.google.android.tts");

        textToSpeechButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                textToSpeech.speak(text, TextToSpeech.QUEUE_FLUSH, null);
            }
        });
    }

    public void playSound(View view){
        if (mediaPlayer.isPlaying()) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playSoundImage.setImageDrawable(getDrawable(android.R.drawable.ic_media_play));
            }
        }
        else {
            mediaPlayer.start();
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                playSoundImage.setImageDrawable(getDrawable(android.R.drawable.ic_media_pause));
            }
        }
    }

    public void nextPage(View view){
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("page_no", pageNo + 1);
        intent.putExtra("story_id", storyId);
        if (statistics) intent.putExtra("user_id", userId);
        startActivity(intent);
        finish();
    }

    public void prevPage(View view){
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("page_no", pageNo - 1);
        intent.putExtra("story_id", storyId);
        if (statistics) intent.putExtra("user_id", userId);
        startActivity(intent);
        finish();
    }

    public void finishStory(final View v){
        final AlertDialog.Builder feedbackDialog = new AlertDialog.Builder(StoryReader.this);

        final Integer[] position = {-1};
        feedbackDialog.setTitle(R.string.feedback);
        final String[] feedbackList = getResources().getStringArray(R.array.feedback_array);

        final ArrayAdapter<String> adapter = new ArrayAdapter<>(StoryReader.this, android.R.layout.select_dialog_singlechoice);
        adapter.addAll(feedbackList);

        feedbackDialog.setSingleChoiceItems(adapter, -1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                position[0] = which;
                System.out.println(which + ", " + adapter.getItem(which));
            }
        });
        feedbackDialog.setPositiveButton(R.string.confirm, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                System.out.println("STATISTICS: " + statistics);
                if (statistics){
                    recordStatistics(position[0]);
                }
                dialog.dismiss();
                finish();
            }


        });
        feedbackDialog.show();

    }

    public void exitStory(View view){
        finish();
    }

    public void recordStatistics(Integer feedback){
        System.out.println("WRITE TO DB: " + feedback);


        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(FeedbackEntry.COLUMN_USER_ID, userId);
        values.put(FeedbackEntry.COLUMN_STORY_ID, storyId);
        values.put(FeedbackEntry.COLUMN_FEEDBACK, feedback);

        db.insert(FeedbackEntry.TABLE_NAME, null, values);
        db.close();
    }

    @Override
    protected void onDestroy() {
        if (textToSpeech != null){
            textToSpeech.stop();
            textToSpeech.shutdown();
        }
        super.onDestroy();
    }
}
