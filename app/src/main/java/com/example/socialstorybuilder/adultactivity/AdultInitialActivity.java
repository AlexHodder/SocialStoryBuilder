package com.example.socialstorybuilder.adultactivity;

import android.app.ActionBar;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.storyedit.ConfigureStory;
import com.example.socialstorybuilder.storyedit.ReadEditSelect;

public class AdultInitialActivity extends AppCompatActivity {

    private String user;
    private PopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_initial);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        TextView welcomeMessage = findViewById(R.id.welcome_adult);
        String welcome = getString(R.string.welcome) + ", " + user;
        welcomeMessage.setText(welcome);

        popupWindow = new PopupWindow(this);
        LinearLayout layout = new LinearLayout(this);
        layout.setOrientation(LinearLayout.VERTICAL);

        TextView textPopup = new TextView(this);
        textPopup.setTextColor(Color.WHITE);
        textPopup.setText(R.string.story_create_help);

        Button closePopup = new Button(this);
        closePopup.setText(R.string.popup_close);

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER;
        layout.setAlpha(1);
//        layout.setBackgroundColor(getResources().getColor(R.color.helpColor));
        layout.addView(textPopup, params);
        layout.addView(closePopup, params);

        popupWindow.setContentView(layout);

        closePopup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
            }
        });

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
        popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
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
