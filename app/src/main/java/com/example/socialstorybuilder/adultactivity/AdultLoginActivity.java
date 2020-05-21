package com.example.socialstorybuilder.adultactivity;

import android.content.Intent;

import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper.*;

/**
 * Activity for Adult users to login to their account.
 *
 * @since 1.0
 */

public class AdultLoginActivity extends AppCompatActivity {


    /**
     * Name input field
     * Password input field
     * Popup for incorrect login
     */
    private EditText nameInput;
    private EditText passwordInput;
    private AlertDialog.Builder incorrectLogin;

    /**
     * Method called on activity creation, initialises properties.
     *
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.adult_login);
        nameInput = findViewById(R.id.username);
        passwordInput = findViewById(R.id.password);
        incorrectLogin = new AlertDialog.Builder(AdultLoginActivity.this);
        incorrectLogin.setTitle(R.string.login_failed);
        incorrectLogin.setMessage(R.string.incorrect_login);
        incorrectLogin.setPositiveButton(R.string.popup_close, null);
    }

    /**
     * Tests if sign in details are correct in database.
     * If correct switches to signed in adult activity.
     * Else displays error message.
     * @param view
     */
    public void signIn(View view){
        String username = getNameInput();
        String password = getPasswordInput();

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

        String[] projection = {AdultUserEntry._ID};
        String selection = AdultUserEntry.COLUMN_NAME + " = ?" + " AND " + AdultUserEntry.COLUMN_PASSWORD + " = ?";
        String[] selectionArgs = new String[]{username, password};
        long cursorCount = DatabaseUtils.queryNumEntries(db, AdultUserEntry.TABLE_NAME, selection, selectionArgs);
        Cursor cursor = db.query(AdultUserEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);

        if (cursorCount > 0){
            cursor.moveToFirst();
            String id = cursor.getString(cursor.getColumnIndex(AdultUserEntry._ID));
            cursor.close();
            db.close();
            switchToAdultInitial(id);
        }
        else {
            cursor.close();
            db.close();
            incorrectLogin.show();
        }
    }

    /**
     * Starts a new AdultInitial activity with the created user account.
     * @param rowID
     */
    public void switchToAdultInitial(String rowID) {
        Intent intent = new Intent(this, AdultInitialActivity.class);
        intent.putExtra("user_id", rowID);
        intent.putExtra("user", getNameInput());
        startActivity(intent);
    }

    /**
     * Starts a new CreateAdult activity.
     * @param view
     */
    public void switchToAdultCreate(View view) {
        Intent intent = new Intent(this, AdultCreateActivity.class);
        startActivity(intent);
    }

    /**
     * Ends the activity, switching to top of stack.
     * @param view
     */
    public void back(View view){
        finish();
    }

    /**
     * Get text from name input field.
     * @return name input.
     */
    public String getNameInput() {
        return nameInput.getText().toString();
    }

    /**
     * Get password from password input field.
     * @return password input.
     */
    public String getPasswordInput() {
        return passwordInput.getText().toString();
    }

}
