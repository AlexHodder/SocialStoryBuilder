package com.example.socialstorybuilder.adultactivity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.storyedit.ConfigureStory;

public class AdultInitialActivity extends AppCompatActivity {

    private String user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_initial);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        TextView welcome = findViewById(R.id.title);
        String welcomeT = getResources().getString(R.string.welcome, user);
        welcome.setText(welcomeT);

        Button linkToSocial = findViewById(R.id.viewSocialStoryTutorialButton);
        linkToSocial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri link = Uri.parse("https://www.autism.org.uk/about/strategies/social-stories-comic-strips.aspx");
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, link);
                startActivity(browserIntent);
            }
        });
    }

    public void switchToStoryPageActivity(View view){
        Intent intent = new Intent(this, AdultStoryPageActivity.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void switchToTutorial(View view){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

    public void switchToCreate(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void logout(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }
}
