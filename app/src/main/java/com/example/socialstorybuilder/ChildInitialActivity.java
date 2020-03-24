package com.example.socialstorybuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.example.socialstorybuilder.DatabaseNameHelper.*;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class ChildInitialActivity extends AppCompatActivity {

    private String user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_initial);
        Intent intent = getIntent();
        TextView welcomeMessage = findViewById(R.id.welcome_msg);
        user = intent.getStringExtra("user");
        welcomeMessage.setText("Hi, " + user);

        ImageView avatar = findViewById(R.id.avatar);

        /* SharedPreferences method
        SharedPreferences prefs = getSharedPreferences(user + "Prefs", Context.MODE_PRIVATE);
        String avatarFile = prefs.getString("avatar", "android.resource://SocialStoryBuilder/drawable/default_avatar.png");
        System.out.println(avatarFile);

         */

        // Database method

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {ChildUserEntry.COLUMN_AVATAR};
        String selection = ChildUserEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { user };

        Cursor avatarFileCursor = db.query(ChildUserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
        avatarFileCursor.moveToNext();
        String avatarFile = avatarFileCursor.getString(avatarFileCursor.getColumnIndex(ChildUserEntry.COLUMN_AVATAR));
        avatarFileCursor.close();

        System.out.println(avatarFile);

        if (!avatarFile.equals(getString(R.string.image_not_chosen))) {

            System.out.println(avatarFile + " != " + getString(R.string.image_not_chosen));
            Uri path = Uri.parse(avatarFile);
            avatar.setImageURI(path);
        }
        else{
            avatar.setImageResource(R.drawable.default_avatar);
        }



    }
}
