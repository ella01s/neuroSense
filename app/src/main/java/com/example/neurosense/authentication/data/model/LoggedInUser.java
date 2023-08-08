package com.example.neurosense.authentication.data.model;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import database.DatabaseHelper;

/**
 * Data class that captures user information for logged in users retrieved from LoginRepository
 */

public class LoggedInUser {
    private String userId;
    private String displayName;
    private String email;

    public LoggedInUser(String userId, String displayName, String email) {
        this.userId = userId;
        this.displayName = displayName;
        this.email = email;
    }

    public String getUserId() {
        return userId;
    }

    public String getDisplayName() {
        return displayName;
    }

    public String getEmail() {
        return email;
    }

    public static LoggedInUser getUserFromDatabase(DatabaseHelper databaseHelper, String username, String password) {
        SQLiteDatabase db = databaseHelper.getReadableDatabase();

        String[] projection = {"user_id", "username", "email"};
        String selection = "username = ? AND password = ?";
        String[] selectionArgs = {username, password};

        Cursor cursor = db.query("authentication", projection, selection, selectionArgs, null, null, null);

        LoggedInUser user = null;

        if (cursor != null && cursor.moveToFirst()) {
            String userId = cursor.getString(cursor.getColumnIndexOrThrow("user_id"));
            String displayName = cursor.getString(cursor.getColumnIndexOrThrow("username"));
            String email = cursor.getString(cursor.getColumnIndexOrThrow("email"));

            user = new LoggedInUser(userId, displayName, email);
        }

        if (cursor != null) {
            cursor.close();
        }

        db.close();

        return user;
    }
}