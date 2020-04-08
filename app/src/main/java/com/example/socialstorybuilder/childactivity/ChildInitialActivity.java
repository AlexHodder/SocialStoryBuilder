package com.example.socialstorybuilder.childactivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.HashMapAdapter;
import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.MapRecyclerAdapter;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.storyedit.StoryReader;

import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

public class ChildInitialActivity extends AppCompatActivity {

    private String userID;
    private String selectedStory;
    private MapRecyclerAdapter adapter;
    private AlertDialog.Builder emptyBookWarning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_initial);
        Intent intent = getIntent();
        TextView welcomeMessage = findViewById(R.id.welcome_msg);
        String user = intent.getStringExtra("user");
        userID = intent.getStringExtra("user_id");

        emptyBookWarning = new AlertDialog.Builder(ChildInitialActivity.this);
        emptyBookWarning.setMessage(R.string.book_empty);
        emptyBookWarning.setPositiveButton(R.string.popup_close, null);

        welcomeMessage.setText("Hi, " + user);

        ImageView avatar = findViewById(R.id.avatar);
        TreeMap<String, String> map = ActivityHelper.getChildStoryMap(getApplicationContext(), userID);
        DecoratedRecyclerView list = findViewById(R.id.child_story_list);
        adapter = new MapRecyclerAdapter(map);
        list.setAdapter(adapter);
        adapter.setClickListener(new MapRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1){
                    selectedStory = adapter.getKey(position);
                    System.out.println(selectedStory);
                }
            }
        });
        String avatarFile = ActivityHelper.getAvatarURI(getApplicationContext(), user);

        if (!avatarFile.equals(getString(R.string.image_not_chosen))) {

            System.out.println(avatarFile + " != " + getString(R.string.image_not_chosen));
            Uri path = Uri.parse(avatarFile);
            avatar.setImageURI(path);
        }
        else{
            avatar.setImageResource(R.drawable.default_avatar);
        }
    }

    public void readStory(View view){
        if (adapter.itemSelected()){
            if (ActivityHelper.isStoryReadable(getApplicationContext(), selectedStory)){
                Intent intent = new Intent(this, StoryReader.class);
                intent.putExtra("story_id", selectedStory);
                intent.putExtra("statistics", true);
                startActivity(intent);
            }
            else {
                emptyBookWarning.show();
            }
        }
    }

    public void logout(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
