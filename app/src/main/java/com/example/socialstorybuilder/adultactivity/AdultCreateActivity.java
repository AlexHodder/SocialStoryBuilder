package com.example.socialstorybuilder.adultactivity;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

public class AdultCreateActivity extends AppCompatActivity {

    private EditText nameInput;
    private EditText passwordInput;
    private EditText passwordConfirmationInput;
    private AlertDialog.Builder errorDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_create);

        errorDialog = new AlertDialog.Builder(AdultCreateActivity.this);
        errorDialog.setTitle("Error");
        errorDialog.setPositiveButton(R.string.popup_close, null);

        nameInput = findViewById(R.id.username_input);
        passwordInput = findViewById(R.id.password_input);
        passwordConfirmationInput = findViewById(R.id.password_input_confirm);

    }

    public void createAccount(View view){
        String username = nameInput.getText().toString();
        if(TextUtils.isEmpty(username)) {
            nameInput.setError("Name can't be empty");
            return;
        }

        String password = passwordInput.getText().toString();
        String confirmPassword = passwordConfirmationInput.getText().toString();

        if(TextUtils.isEmpty(password)) {
            passwordInput.setError("Password can't be empty");
            return;
        }
        if(TextUtils.isEmpty(confirmPassword)) {
            passwordConfirmationInput.setError("Password can't be empty");
            return;
        }

        DatabaseHelper dbHelper = new DatabaseHelper(getApplicationContext());
        SQLiteDatabase db = dbHelper.getWritableDatabase();

        if (password.equals(confirmPassword)){
            ContentValues values = new ContentValues();
            values.put(DatabaseNameHelper.AdultUserEntry.COLUMN_NAME, username);
            values.put(DatabaseNameHelper.AdultUserEntry.COLUMN_PASSWORD, password);
            long rowID = db.insert(DatabaseNameHelper.AdultUserEntry.TABLE_NAME, null, values);
            System.out.println(rowID);

            if (rowID == -1){
                errorDialog.setMessage(R.string.adult_name_error);
                errorDialog.show();
            }

            switchToAdultInitial(view);

        }
        else{
            errorDialog.setMessage(R.string.password_error);
            errorDialog.show();
        }
    }

    public void switchToAdultInitial(View view) {
        Intent intent = new Intent(this, AdultInitialActivity.class);
        intent.putExtra("user", nameInput.getText().toString());
        startActivity(intent);
    }

    public void switchToAdultLogin(View view) {
        Intent intent = new Intent(this, AdultLoginActivity.class);
        startActivity(intent);
    }

}
