package com.example.socialstorybuilder;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.socialstorybuilder.adultactivity.AdultLoginActivity;
import com.example.socialstorybuilder.adultactivity.Tutorial;
import com.example.socialstorybuilder.childactivity.ChildLoginActivity;

import androidx.appcompat.app.AppCompatActivity;

import java.text.DateFormat;
import java.util.Date;
import java.util.Locale;

/**
 * Activity loaded on start up
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Method called on creation.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_layout);
    }

    /**
     * Activity switcher to child login page
     * @param view
     */
    public void toChildLogin(View view){
        Intent intent = new Intent(this, ChildLoginActivity.class);
        startActivity(intent);
    }

    /**
     * Activity switcher to adult login page
     * @param view
     */
    public void toAdultLogin(View view){
        Intent intent = new Intent(this, AdultLoginActivity.class);
        startActivity(intent);
    }

    /**
     * Activity switcher to tutorial page
     * @param view
     */
    public void toTutorial(View view){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

}
