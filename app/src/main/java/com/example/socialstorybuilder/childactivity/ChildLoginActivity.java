package com.example.socialstorybuilder.childactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.MainActivity;
import com.example.socialstorybuilder.R;

import java.util.ArrayList;

public class ChildLoginActivity extends AppCompatActivity {

    // Defining string adapter to handle ListView data
    ArrayAdapter<String> adapter;

    ListView childLayout;
    int selectedItem;

    private ArrayList<String> childList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_login);

        childList = ActivityHelper.getChildUsers(getApplicationContext());
        System.out.println("Child list: " + childList);

        adapter = new ArrayAdapter<>(getApplicationContext(), android.R.layout.simple_list_item_1, childList);

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
