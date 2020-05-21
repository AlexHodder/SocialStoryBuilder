package com.example.socialstorybuilder.childactivity;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;

import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;

import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.ActivityHelper;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;
import com.example.socialstorybuilder.R;

import java.io.IOException;

/**
 * Activity for creating Child accounts.
 *
 * @since 1.0
 */
public class ChildCreateActivity extends AppCompatActivity {

    private Uri childAvatarImage;
    private ImageView avatar;



    private EditText nameInput;
    private String childID;
    private AlertDialog.Builder hintDialog;

    /**
     * Method called on activity creation, initialises from layout resource.
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_create);
        avatar = findViewById(R.id.imageView);
        nameInput = findViewById(R.id.child_name_input);
    }

    /**
     * Method to start a new activity for users to select images.
     *
     * @param view
     */
    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, ActivityHelper.GALLERY_REQUEST_CODE);
    }

    /**
     * Method called when Activity result ends.
     *
     * @param requestCode Code passed to previous activity
     * @param resultCode Code returned from previous activity
     * @param data Intent returned from previous activity
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            if (requestCode != ActivityHelper.GALLERY_REQUEST_CODE) {
                return;
            }
            childAvatarImage = data.getData();
            try {
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), childAvatarImage);
                avatar.setImageBitmap(bitmap);
            } catch (IOException e) {
                Log.i("TAG", "Some exception " + e);
            }
        }
    }

    /**
     * Method called on create child button.
     * Tests if name is valid, and if so adds to database. Else displays hint.
     * @param view
     */
    public void createChild(View view) {
        String name = getNameInput();

        if(TextUtils.isEmpty(name)) {
            nameInput.setError("Name can't be empty");
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        String childAvatarImageUri;
        if (childAvatarImage != null) childAvatarImageUri = childAvatarImage.toString();
        else childAvatarImageUri = getString(R.string.image_not_chosen);

        ContentValues values = new ContentValues();
        values.put(ChildUserEntry.COLUMN_NAME, name);
        values.put(ChildUserEntry.COLUMN_AVATAR, childAvatarImageUri);

        long rowID = db.insert(ChildUserEntry.TABLE_NAME, null, values);


        if (rowID == -1){
            hintDialog = new AlertDialog.Builder(ChildCreateActivity.this);
            hintDialog.setMessage(R.string.child_name_error);
            hintDialog.setPositiveButton(R.string.popup_close, null);
            hintDialog.show();
        }
        else {
            childID = String.valueOf(rowID);
            switchToChildInitial(view);
        }
    }

    /**
     * Activity switcher to ChildInitial passing child name and database id.
     * @param view
     */
    public void switchToChildInitial(View view) {
        Intent intent = new Intent(this, ChildInitialActivity.class);
        intent.putExtra("user", getNameInput());
        intent.putExtra("user_id", childID);
        startActivity(intent);
    }

    /**
     * Ends current activity, returning to top of stack.
     * @param view
     */
    public void switchToChildLogin(View view) {
        finish();
    }

    /**
     * Getter for text in EditText nameInput
     * @return
     */
    public String getNameInput() {
        return nameInput.getText().toString();
    }
}
