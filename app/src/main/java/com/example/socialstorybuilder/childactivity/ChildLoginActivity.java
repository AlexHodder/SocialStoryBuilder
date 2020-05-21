package com.example.socialstorybuilder.childactivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.DecoratedRecyclerView;
import com.example.socialstorybuilder.IdData;
import com.example.socialstorybuilder.ListRecyclerAdapter;
import com.example.socialstorybuilder.R;

import java.util.ArrayList;

/**
 * Activity to allow Child login, shows a list of child accounts.
 *
 * @since 1.0
 */
public class ChildLoginActivity extends AppCompatActivity {

    // Defining string adapter to handle ListView data
    private ListRecyclerAdapter adapter;

    private DecoratedRecyclerView childLayout;
    private String selectedUserID;
    private String selectedUser;
    private int selectedItem;

    private ArrayList<IdData> childMap;

    /**
     * Method called on activity creation, displaying Child list.
     * @param savedInstanceState Bundle
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_login);
        childMap = ActivityHelper.getChildUserArray(getApplicationContext());
        childLayout = findViewById(R.id.child_accounts);

        adapter = new ListRecyclerAdapter(childMap);
        adapter.setClickListener(new ListRecyclerAdapter.ItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                if (position > -1){
                    selectedUserID = adapter.getItem(position).getId();
                    selectedUser = adapter.getItem(position).getData();
                }
                selectedItem = position;
            }
        });
        childLayout.setAdapter(adapter);

    }

    /**
     * Activity switcher to initial child login passing child's name and id if child selected.
     * @param view Current view
     */
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

    /**
     * Activity switcher to create a new child.
     * @param view Current view
     */
    public void toChildCreate(View view){
        Intent intent = new Intent(this, ChildCreateActivity.class);
        startActivity(intent);
    }

    /**
     * Ends activity, switching to top of stack.
     * @param view Current view
     */
    public void back(View view){
        finish();
    }

}
