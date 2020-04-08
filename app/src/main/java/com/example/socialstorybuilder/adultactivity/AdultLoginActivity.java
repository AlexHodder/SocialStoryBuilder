package com.example.socialstorybuilder.adultactivity;

import android.content.Intent;

import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

public class AdultLoginActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText passwordInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_login);
        nameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
    }

    public void signIn(View view){
        String username = nameInput.getText().toString();
        String password = passwordInput.getText().toString();

        if(TextUtils.isEmpty(username)) {
            nameInput.setError("Name can't be empty");
            return;
        }

        if(TextUtils.isEmpty(password)) {
            passwordInput.setError("Password can't be empty");
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String selection = AdultUserEntry.COLUMN_NAME + " = ?" + " AND " + AdultUserEntry.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = new String[]{username, password};
        long cursorCount = DatabaseUtils.queryNumEntries(db, AdultUserEntry.TABLE_NAME, selection, selectionArgs);
        db.close();
        if (cursorCount > 0){
            switchToAdultInitial(view);
        }
        else System.out.println("incorrect login details");
    }

    public void switchToAdultInitial(View view) {
        Intent intent = new Intent(this, AdultInitialActivity.class);
        intent.putExtra("user", nameInput.getText().toString());
        startActivity(intent);
    }

    public void switchToAdultCreate(View view) {
        Intent intent = new Intent(this, AdultCreateActivity.class);
        startActivity(intent);
    }

    public void back(View view){
        finish();
    }

}
