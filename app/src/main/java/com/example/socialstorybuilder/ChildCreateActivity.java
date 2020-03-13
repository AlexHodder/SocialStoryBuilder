package com.example.socialstorybuilder;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Set;

public class ChildCreateActivity extends AppCompatActivity {

    public static final int GALLERY_REQUEST_CODE = 123;
    private Uri childAvatarImage;

    private ImageView avatar;
    private EditText nameInput;
    private ArrayList<String> childList;
    private PopupWindow errorWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.child_create);
        avatar = findViewById(R.id.imageView);
        nameInput = findViewById(R.id.child_name_input);
        childList = getChildUsers();
    }

    public ArrayList<String> getChildUsers(){
        ArrayList<String> childList;
        SharedPreferences childPreferences = getSharedPreferences("child_users",MODE_PRIVATE);
        Set<String> fetch = childPreferences.getStringSet("child", null);
        childList = new ArrayList<>();
        if (fetch != null) childList = new ArrayList<>(fetch);
        return childList;
    }

    public void selectImage(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == Activity.RESULT_OK)
            switch (requestCode){
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

    public void createChild(View view){
        System.out.println("CREATE CHILD CALLED");
        String name = nameInput.getText().toString();

        if (childList.contains(name) || name.trim().isEmpty()){
            System.out.println("Name EMPTY/DUPL.");
            LayoutInflater layoutInflater =((LayoutInflater) this.getSystemService(Context.LAYOUT_INFLATER_SERVICE));
            View errorView = layoutInflater.inflate(R.layout.popup_error, null);
            ConstraintLayout errorConstraint = findViewById(R.id.popup_error_layout);
            Button closePopup = findViewById(R.id.closePopUp);
            errorWindow = new PopupWindow(errorView, ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            errorWindow.showAtLocation(errorConstraint, Gravity.CENTER, 0,0);
            closePopup.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View v){
                    errorWindow.dismiss();
                }
            });

        }
        else{
            String childPreferencesFileName = name + "Prefs";
            SharedPreferences prefs = getSharedPreferences(childPreferencesFileName, Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = prefs.edit();
            editor.putString("avatar", childAvatarImage.toString());
            editor.putString("name", name);
            editor.apply();
        }

    }

}

