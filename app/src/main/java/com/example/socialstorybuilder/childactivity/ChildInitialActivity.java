package com.example.socialstorybuilder.childactivity;

import android.app.AlertDialog;
import android.content.Intent;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.storyedit.StoryReader;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.io.IOException;
import java.util.ArrayList;

/**
 * Activity on child login, displaying stories available and avatar.
 *
 * @since 1.0
 */

public class ChildInitialActivity extends AppCompatActivity {

    private String userID;
    private String selectedStory;
    private ListRecyclerAdapter adapter;
    private AlertDialog.Builder emptyBookWarning;

    /**
     * Method called on activity creation, initialising properties.
     * Sets up alert for empty book readings.
     * Sets up list of books available to child.
     * Loads saved avatar in.
     * @param savedInstanceState Bundle
     */
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
        ArrayList<IdData> map = ActivityHelper.getChildStoryList(getApplicationContext(), userID);
        DecoratedRecyclerView list = findViewById(R.id.child_story_list);
        adapter = new ListRecyclerAdapter(map);
        list.setAdapter(adapter);
        adapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position != RecyclerView.NO_POSITION){
                    selectedStory = adapter.getItem(position).getId();
                }
            }
        });
        String avatarFile = ActivityHelper.getAvatarURI(getApplicationContext(), user);

        if (!avatarFile.equals(getString(R.string.image_not_chosen))) {
            Uri path = Uri.parse(avatarFile);
            try{
                Bitmap bitmap;
                bitmap = MediaStore.Images.Media.getBitmap(this.getContentResolver(), path);
                avatar.setImageBitmap(bitmap);
            }
            catch (IOException e){
                Toast.makeText(this, "Avatar file could not be found. Displaying default avatar.", Toast.LENGTH_SHORT).show();
                avatar.setImageResource(R.drawable.default_avatar);
            }
        }
        else{
            avatar.setImageResource(R.drawable.default_avatar);
        }
    }

    /**
     * Activity switcher to read story, passing story_id and user_id.
     * @param view Current view
     */
    public void readStory(View view){
        if (adapter.itemSelected()){
            if (ActivityHelper.isStoryReadable(getApplicationContext(), selectedStory)){
                Intent intent = new Intent(this, StoryReader.class);
                intent.putExtra("story_id", selectedStory);
                intent.putExtra("user_id", userID);
                startActivity(intent);
            }
            else {
                emptyBookWarning.show();
            }
        }
    }

    /**
     * Activity switcher to log out user, and switch to the home initial activity.
     * @param view Current view
     */
    public void logout(View view){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        finish();
    }

}
