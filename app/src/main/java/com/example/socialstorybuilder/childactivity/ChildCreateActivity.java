package com.example.socialstorybuilder.childactivity;

import android.app.ActionBar;
import android.app.Activity;
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
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;
import com.example.socialstorybuilder.R;

import java.io.IOException;

public class ChildCreateActivity extends AppCompatActivity {

    public static final int GALLERY_REQUEST_CODE = 123;
    private Uri childAvatarImage;

    private ImageView avatar;
    private EditText nameInput;

    private PopupWindow errorWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_create);
        avatar = findViewById(R.id.imageView);
        nameInput = findViewById(R.id.child_name_input);
    }


    public void selectImage(View view) {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            switch (requestCode) {
                case GALLERY_REQUEST_CODE:
                    childAvatarImage = data.getData();
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), childAvatarImage);
                        avatar.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        Log.i("TAG", "Some exception " + e);
                    }
                    break;
            }
    }

    public void createChild(View view) {
        System.out.println("CREATE CHILD CALLED");
        String name = nameInput.getText().toString();

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

        System.out.println(rowID);

        if (rowID == -1){
            System.out.println("Name DUPL.");

            errorWindow = new PopupWindow(this);

            LinearLayout errorLayout = new LinearLayout(this);
            errorLayout.setOrientation(LinearLayout.VERTICAL);

            TextView textPopup = new TextView(this);
            textPopup.setText(R.string.child_name_error);
            textPopup.setTextColor(Color.WHITE);

            Button closePopup = new Button(this);
            closePopup.setText(R.string.popup_close);

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT, ActionBar.LayoutParams.WRAP_CONTENT);
            params.gravity = Gravity.CENTER;

            errorLayout.addView(textPopup, params);
            errorLayout.addView(closePopup, params);

            errorWindow.setContentView(errorLayout);

            errorWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
            closePopup.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    errorWindow.dismiss();
                }
            });
        }
        else switchToChildInitial(view);
    }

    public void switchToChildInitial(View view) {
        Intent intent = new Intent(this, ChildInitialActivity.class);
        intent.putExtra("user", nameInput.getText().toString());
        startActivity(intent);
    }

    public void switchToChildLogin(View view) {
        Intent intent = new Intent(this, ChildLoginActivity.class);
        startActivity(intent);
    }
}
