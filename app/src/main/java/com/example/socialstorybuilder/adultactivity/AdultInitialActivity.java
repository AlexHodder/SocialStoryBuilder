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

/**
 * Activity for signed in adults.
 * This activity displays the adult initial layout.
 *
 * @since 1.0
 */
public class AdultInitialActivity extends AppCompatActivity {

    /**
     * A string to store the users ID
     */
    private String userID;

    /**
     * Method called on activity creation and initialising the properties
     * Set listeners on required buttons.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_initial);

        Intent intent = getIntent();
        userID = intent.getStringExtra("user_id");
        String user = intent.getStringExtra("user");

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

    /**
     * Activity switcher to story page activity, with user ID passed.
     * @param view
     */
    public void switchToStoryPageActivity(View view){
        Intent intent = new Intent(this, AdultStoryPageActivity.class);
        intent.putExtra("user_id", getUserID());
        startActivity(intent);
    }

    /**
     * Activity switcher to tutorial.
     * @param view
     */
    public void switchToTutorial(View view){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

    /**
     * Activity switcher to create a new story, with user ID passed.
     * @param view
     */
    public void switchToCreate(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user_id", getUserID());
        startActivity(intent);
    }

    /**
     * Activity switcher to log out user, and switch to the home initial activity.
     * @param view
     */
    public void logout(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * Getter method for userID
     * @return userID string.
     */
    public String getUserID() {
        return userID;
    }
}
