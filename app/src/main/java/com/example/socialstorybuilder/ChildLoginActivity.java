package com.example.socialstorybuilder;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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

    // Defining string adapter to handle ListView data
    ArrayAdapter<String> adapter;
    ListView childLayout;
    int selectedItem;
    List<String> childList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_login);
        SharedPreferences childPreferences = getSharedPreferences("child_users",MODE_PRIVATE);
        Set<String> fetch = childPreferences.getStringSet("child", null);
        childList = new ArrayList<>();
        if (fetch != null) childList = new ArrayList<>(fetch);

        // Test list
        childList.add("Jeff");
        childList.add("John");
        childList.add("Ruth");

        adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, childList);

        childLayout = findViewById(R.id.child_accounts);
        childLayout.setAdapter(adapter);
        childLayout.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                selectedItem = position;
            }
        });
    }


    public void toChildInitial(View view){
        if (childLayout.isItemChecked(selectedItem)) {
            Intent intent = new Intent(this, ChildInitialActivity.class);
            intent.putExtra("user", childList.get(selectedItem));
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
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }

}
