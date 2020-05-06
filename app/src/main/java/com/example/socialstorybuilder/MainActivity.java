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


public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.initial_layout);
    }

    public void toChildLogin(View view){
        Intent intent = new Intent(this, ChildLoginActivity.class);
        startActivity(intent);
    }

    public void toAdultLogin(View view){
        Intent intent = new Intent(this, AdultLoginActivity.class);
        startActivity(intent);
    }
    public void toTutorial(View view){
        Intent intent = new Intent(this, Tutorial.class);
        startActivity(intent);
    }

}
