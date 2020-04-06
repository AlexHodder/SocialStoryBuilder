package com.example.socialstorybuilder.adultactivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.storyedit.ConfigureStory;
import com.example.socialstorybuilder.storyedit.ReadEditSelect;

public class AdultInitialActivity extends AppCompatActivity {

    private String user;
    private AlertDialog.Builder hintDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_initial);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        TextView welcomeMessage = findViewById(R.id.welcome_adult);
        String welcome = getString(R.string.welcome) + ", " + user;
        welcomeMessage.setText(welcome);

        hintDialog = new AlertDialog.Builder(AdultInitialActivity.this);
        hintDialog.setMessage(R.string.story_create_help);
        hintDialog.setPositiveButton(R.string.popup_close, null);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
    }

    public void helpPopup(View view){
        hintDialog.show();
    }

    public void switchToHomeScreen(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

    public void switchToConfigureStoryNew(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void switchToReadEditStory(View view){
        Intent intent = new Intent(this, ReadEditSelect.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

}
