package com.example.socialstorybuilder.childactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.MapRecyclerAdapter;
import com.example.socialstorybuilder.R;

import java.util.HashMap;
import java.util.TreeMap;

public class ChildLoginActivity extends AppCompatActivity {

    // Defining string adapter to handle ListView data
    private MapRecyclerAdapter adapter;

    private DecoratedRecyclerView childLayout;
    private String selectedUserID;
    private String selectedUser;
    private int selectedItem;

    private TreeMap<String, String> childMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_login);
        childMap = ActivityHelper.getChildUserMap(getApplicationContext());
        childLayout = findViewById(R.id.child_accounts);

        adapter = new MapRecyclerAdapter(childMap);
        adapter.setClickListener(new MapRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1){
                    selectedUserID = adapter.getKey(position);
                    selectedUser = adapter.getValue(position);
                }
                selectedItem = position;
                System.out.println(selectedItem);
            }
        });
        childLayout.setAdapter(adapter);

    }


    public void toChildInitial(View view){
        if (adapter.isItemChecked(selectedItem)) {
            Intent intent = new Intent(this, ChildInitialActivity.class);
            intent.putExtra("user", selectedUser);
            intent.putExtra("user_id", selectedUserID);
            startActivity(intent);
        }
        else{
            Log.d("Selected Item","Selected item is: " + selectedItem);
        }
    }

    public void toChildCreate(View view){
        Intent intent = new Intent(this, ChildCreateActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        finish();
    }

}
