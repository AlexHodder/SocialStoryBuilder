package com.example.socialstorybuilder;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
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

        //TODO - set up preferences for individual users (find default resource for no avatar)
        SharedPreferences prefs = getSharedPreferences(user + "Prefs", Context.MODE_PRIVATE);
        String avatarFile = prefs.getString("avatar", "android.resource://SocialStoryBuilder/drawable/default_avatar.png");
        System.out.println(avatarFile);
        if (avatarFile != getString(R.string.image_not_chosen)) {
            System.out.println("WOW reached the first if" + ", name: " + user);
            Uri path = Uri.parse(avatarFile);
            avatar.setImageURI(path);
        }
        else{
            System.out.println("WOW reached the else" + ", name: " + user);
            avatar.setImageResource(R.drawable.default_avatar);
        }



    }
}
