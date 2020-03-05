package com.example.socialstorybuilder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class ChildLoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_login);
        SharedPreferences childPreferences = getSharedPreferences("child_users",MODE_PRIVATE);
        Set<String> fetch = childPreferences.getStringSet("child", null);
        List<String> childList = new ArrayList<>();
        if (fetch != null) childList = new ArrayList<>(fetch);

//        childList.add("Jeff");
//        childList.add("John");
//        childList.add("Ruth");

        ListView childLayout = findViewById(R.id.child_accounts);

        for (int i = 1; i <= childList.size(); i++){
            TextView childName = new TextView(this);
            childName.setText(childList.get(i));
            childLayout.addView(childName);
        }
    }


    public void signIn(){

    }

    public void toChildInitial(View view){
        Intent intent = new Intent(this, ChildInitialActivity.class);
        startActivity(intent);
    }

    public void toChildCreate(View view){
        Intent intent = new Intent(this, ChildCreateActivity.class);
        startActivity(intent);
    }
}
