package com.example.socialstorybuilder.adultactivity;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.R;

import java.util.ArrayList;

/**
 * Activity to display the tutorial images to the user.
 *
 * @since 1.2.3
 */

public class Tutorial extends AppCompatActivity {

    private ImageSwitcher root;
    private Button nextButton;
    private Button backButton;
    private int i;
    private TextView description;

    /**
     * Method called on activity creation, initialising image roots.
     * Adds description for each image and sets up listeners for next/back buttons.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tutorial_page);
        root = findViewById(R.id.sceneRootLayout);
        description = findViewById(R.id.descriptionBox);

        final ArrayList<Integer> tutorialList = new ArrayList<>();
        tutorialList.add(R.drawable.initial_screen_tutorial);
        tutorialList.add(R.drawable.child_create_tutorial);
        tutorialList.add(R.drawable.edit_page_tutorial);

        final ArrayList<String> descriptionList = new ArrayList<>();
        descriptionList.add("This is the initial load screen.");
        descriptionList.add("This is the child creation screen.");
        descriptionList.add("This is the story page editor.");


        i = 0;

        final int listSize = tutorialList.size()-1;

        nextButton = findViewById(R.id.nextButton);
        backButton = findViewById(R.id.backButton);

        root.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                return new ImageView(getApplicationContext());
            }
        });
        root.setImageResource(tutorialList.get(0));
        description.setText(descriptionList.get(i));

        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i++;
                updateView(i, listSize, tutorialList.get(i), descriptionList.get(i));
            }
        });
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                i--;
                updateView(i, listSize, tutorialList.get(i),  descriptionList.get(i));
            }
        });
    }

    /**
     * Method updates image and description, and buttons available.
     * @param i current image position
     * @param size number of images total
     * @param resource the next resource id
     * @param desc the next description update
     */
    private void updateView(int i, int size, int resource, String desc){
        if (i==0){
            backButton.setVisibility(View.INVISIBLE);
            backButton.setClickable(false);
            nextButton.setVisibility(View.VISIBLE);
            nextButton.setClickable(true);
        }
        else if(i == size){
            backButton.setClickable(true);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setClickable(false);
            nextButton.setVisibility(View.INVISIBLE);
        }
        else{
            backButton.setClickable(true);
            backButton.setVisibility(View.VISIBLE);
            nextButton.setClickable(true);
            nextButton.setVisibility(View.VISIBLE);
        }
        root.setImageResource(resource);
        description.setText(desc);
    }


    /**
     * Ends the activity, returning to next top in activity stack.
     * @param view Current view
     */
    public void finishTutorial(View view){
        finish();
    }

}
