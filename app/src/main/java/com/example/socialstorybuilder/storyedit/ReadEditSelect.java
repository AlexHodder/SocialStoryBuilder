package com.example.socialstorybuilder.storyedit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.ListIterator;

public class ReadEditSelect extends AppCompatActivity {

    private ArrayAdapter<String> adapter;
    private ListView storyView;
    private int selectedItem;
    private String user;
    private ArrayList<String> storyIDList;
    private ArrayList<String> storyList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_edit_select);

        Intent intent = getIntent();
        user = intent.getStringExtra("user");

        storyIDList = ActivityHelper.getAdultStoryId(getApplicationContext(), user);
        System.out.println("Story ID List size: " + storyIDList.size());

        System.out.println("Story ID List: " + storyIDList.get(0));

        storyList = new ArrayList<>();
        ListIterator<String> iterator = storyIDList.listIterator();
        while (iterator.hasNext()) {
            String next = iterator.next();
            storyList.add(ActivityHelper.getStoryNameFromID(getApplicationContext(), next));
        }

        System.out.println("Story List: " + storyList.get(0));
        System.out.println("storyList contains: " + storyList.size());

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, storyList);

        storyView = findViewById(R.id.story_select);

        storyView.setAdapter(adapter);
        storyView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
            }
        });
    }

    public void switchToRead(View view){

    }

    public void switchToEdit(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user", user);
        intent.putExtra("story_id", storyIDList.get(selectedItem));
        startActivity(intent);
    }

    public void switchToCreate(View view){
        Intent intent = new Intent(this, ConfigureStory.class);
        intent.putExtra("user", user);
        startActivity(intent);
    }



}
