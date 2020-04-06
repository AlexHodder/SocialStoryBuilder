package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;

import android.database.Cursor;
import android.os.Bundle;
import android.os.storage.StorageManager;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.HashMapAdapter;
import com.example.socialstorybuilder.R;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;


public class ReadEditSelect extends AppCompatActivity {

    private HashMapAdapter hashAdapter;
    private HashMap<String, String> storyMap;

    private ListView storyView;
    private int selectedItem;
    private String selectedID;
    private String user;
    private ArrayList<String> storyIDList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_edit_select);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        storyMap = ActivityHelper.getStoryMap(getApplicationContext(), user);
        hashAdapter = new HashMapAdapter(storyMap);

        storyView = findViewById(R.id.story_select);

        storyView.setAdapter(hashAdapter);
        storyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
                System.out.println("Position: " + position);
                Map.Entry<Object, Object> map = (Map.Entry<Object, Object>) parent.getItemAtPosition(position);
                selectedID = (String) map.getKey();
                System.out.println("Selected ID: " + selectedID);


            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        storyMap.clear();
        storyMap = ActivityHelper.getStoryMap(getApplicationContext(), user);
        hashAdapter.refresh(storyMap);
    }

    public void switchToRead(View view){
        Intent intent = new Intent(this, StoryReader.class);
        intent.putExtra("story_id", selectedID);
        intent.putExtra("statistics", false);
        startActivity(intent);
    }

    public void switchToEdit(View view){
        if (selectedID!=null){
            Intent intent = new Intent(this, ConfigureStory.class);
            intent.putExtra("user", user);
            intent.putExtra("story_id", selectedID);
            startActivity(intent);
        }
    }

    public void switchToCreate(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }

    public void back(View view){
        finish();
    }



}
