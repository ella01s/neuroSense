package com.example.neurosense.authentication.data;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.neurosense.authentication.data.model.LoggedInUser;
//import com.example.neurosense.database.DatabaseHelper;
import database.DatabaseHelper;


import java.io.IOException;

/**
 * Class that handles authentication w/ login credentials and retrieves user information.
 */
public class LoginDataSource {

    private DatabaseHelper databaseHelper;

    public LoginDataSource(DatabaseHelper databaseHelper) {
        this.databaseHelper = databaseHelper;
    }

    public Result<LoggedInUser> login(String username, String password) {
        try {
            // Handle loggedInUser authentication
            LoggedInUser loggedInUser = getUserFromDatabase(username, password);
            if (loggedInUser != null) {
                // User found in the database
                return new Result.Success<>(loggedInUser);
            } else {
                // User not found or authentication failed
                return new Result.Error(new IOException("Invalid username or password"));
            }
        } catch (Exception e) {
            return new Result.Error(new IOException("Error logging in", e));
        }
    }

    public void logout() {
        // TODO: Revoke authentication
    }

    private LoggedInUser getUserFromDatabase(String username, String password) {
        // Get a readable instance of the database
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        // Define the columns you want to retrieve
        String[] columns = {"user_id", "username", "email"};

        // Define the selection criteria
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};

        // Execute the query and get the cursor
        Cursor cursor = db.query("authentication", columns, selection, selectionArgs, null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            // User found in the database
            int userIdIndex = cursor.getColumnIndex("user_id");
            int displayNameIndex = cursor.getColumnIndex("username");
            int emailIndex = cursor.getColumnIndex("email");

            // Check if the columns exist in the cursor
            if (userIdIndex != -1 && displayNameIndex != -1 && emailIndex != -1) {
                String userId = cursor.getString(userIdIndex);
                String displayName = cursor.getString(displayNameIndex);
                String email = cursor.getString(emailIndex);

                // Create a LoggedInUser object with the retrieved information
                return new LoggedInUser(userId, displayName, email);
            }
        }

        // User not found or authentication failed
        return null;
    }
}