package com.example.socialstorybuilder.adultactivity;

import android.app.AlertDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.socialstorybuilder.R;
import com.example.socialstorybuilder.database.DatabaseHelper;
import com.example.socialstorybuilder.database.DatabaseNameHelper;

/**
 * Activity for creating a new Adult
 * This activity displays the resources to enter username and password
 * and then add to the database.
 *
 * @since 1.0
 */

public class AdultCreateActivity extends AppCompatActivity {

    /**
     * Username input text field
     * Password input text field
     * Password confirmation input text field
     * Popup for errors in password fields
     */
    private EditText nameInput;
    private EditText passwordInput;
    private EditText passwordConfirmationInput;
    private AlertDialog.Builder errorDialog;

    /**
     * Method called on activity creation and initialising the properties
     *
     * @param savedInstanceState Bundle
     */
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

    /**
     * Create Account and switch to logged in account if new account details valid,
     * else display error messages.
     *
     * @param view Current view
     */
    public void createAccount(View view){
        String username = getNameInput();
        if(TextUtils.isEmpty(username)) {
            nameInput.setError("Name can't be empty");
            return;
        }

        String password = getPasswordInput();
        String confirmPassword = getPasswordConfirmationInput();

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

            if (rowID == -1){
                errorDialog.setMessage(R.string.adult_name_error);
                errorDialog.show();
            }
            else{
                switchToAdultInitial(String.valueOf(rowID));
            }

        }
        else{
            errorDialog.setMessage(R.string.password_error);
            errorDialog.show();
        }
    }

    /**
     * Starts a new AdultInitial activity with the created user account.
     *
     * @param rowID the ID in the database of the created user.
     */
    public void switchToAdultInitial(String rowID) {
        Intent intent = new Intent(this, AdultInitialActivity.class);
        intent.putExtra("user_id", rowID);
        intent.putExtra("user", getNameInput());
        startActivity(intent);
        finish();
    }

    /**
     * Ends the activity at the top of the stack
     *
     * @param view Current view
     */
    public void switchToAdultLogin(View view) {
        finish();
    }

    /**
     * Method to return the text entered in the username input field.
     *
     * @return username string
     */
    public String getNameInput() {
        return nameInput.getText().toString();
    }

    /**
     * Method to return the text entered in the password input field.
     *
     * @return password string
     */
    public String getPasswordInput() {
        return passwordInput.getText().toString();
    }

    /**
     * Method to return the text entered in the confirmation password input field.
     *
     * @return confirmed password string
     */
    public String getPasswordConfirmationInput() {
        return passwordConfirmationInput.getText().toString();
    }
}
