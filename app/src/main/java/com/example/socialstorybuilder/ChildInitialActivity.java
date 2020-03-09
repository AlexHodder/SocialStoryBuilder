package com.example.socialstorybuilder;

import android.content.Intent;
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
//        int avatarResource = getSharedPreferences(user, MODE_PRIVATE).getInt("avatar", R.drawable.)
//        avatar.setImageResource(avatarResource);
    }
}
