package database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "userData.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create the user information table
        String createUserTableQuery = "CREATE TABLE user_information (user_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT NOT NULL, firstname TEXT NOT NULL, lastname TEXT NOT NULL, email TEXT NOT NULL, " +
                "date_of_birth TEXT NOT NULL, age INTEGER);";
        db.execSQL(createUserTableQuery);

        // Create the average time table
        String createAverageTimeTableQuery = "CREATE TABLE average_time (user_id INTEGER PRIMARY KEY, " +
                "level_1 INTEGER, level_2 INTEGER, level_3 INTEGER, level_4 INTEGER, level_5 INTEGER, " +
                "level_6 INTEGER, level_7 INTEGER, level_8 INTEGER, level_9 INTEGER, level_10 INTEGER, " +
                "collective_average REAL);";
        db.execSQL(createAverageTimeTableQuery);

        // Create the fastest time table
        String createFastestTimeTableQuery = "CREATE TABLE fastest_time (user_id INTEGER PRIMARY KEY, " +
                "level_1 INTEGER, level_2 INTEGER, level_3 INTEGER, level_4 INTEGER, level_5 INTEGER, " +
                "level_6 INTEGER, level_7 INTEGER, level_8 INTEGER, level_9 INTEGER, level_10 INTEGER, " +
                "collective_average REAL);";
        db.execSQL(createFastestTimeTableQuery);

        // Create the score table
        String createScoreTableQuery = "CREATE TABLE score (user_id INTEGER PRIMARY KEY, " +
                "highest_score INTEGER, average_score REAL, highest_level_reached INTEGER);";
        db.execSQL(createScoreTableQuery);

        // Create the authentication table
        String createAuthenticationTableQuery = "CREATE TABLE authentication (user_id INTEGER PRIMARY KEY, " +
                "username TEXT NOT NULL, email TEXT NOT NULL, password TEXT NOT NULL);";
        db.execSQL(createAuthenticationTableQuery);

        // Insert initial data into the user_information table
        String insertDataQuery = "INSERT INTO user_information (username, firstname, lastname, email, date_of_birth, age) " +
                "VALUES ('Ellabella', 'Ella', 'Solomon', 'emmanuella.solomon@yahoo.co.uk', '1999-10-25', 23);";
        db.execSQL(insertDataQuery);

        // Insert password into the authentication table
        String insertPasswordQuery = "INSERT INTO authentication (user_id, username, email, password) " +
                "VALUES ((SELECT user_id FROM user_information WHERE username = 'Ellabella'), " +
                "'Ellabella', 'emmanuella.solomon@yahoo.co.uk', 'password');";
        db.execSQL(insertPasswordQuery);

        // Create the attempts table
        String createAttemptsTableQuery = "CREATE TABLE attempts (attempt_id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "user_id INTEGER, level_1 REAL, level_2 REAL, level_3 REAL, level_4 REAL, level_5 REAL, " +
                "level_6 REAL, level_7 REAL, level_8 REAL, level_9 REAL, level_10 REAL, " +
                "FOREIGN KEY (user_id) REFERENCES user_information(user_id));";
        db.execSQL(createAttemptsTableQuery);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Perform necessary database schema changes when the version is upgraded
        if (oldVersion < 2) {
            // Example: Add a new column to an existing table
            String alterTableQuery = "ALTER TABLE user_information ADD COLUMN address TEXT;";
            db.execSQL(alterTableQuery);
        }
    }

    public void insertPassword(String username, String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", username);
        values.put("email", email);
        values.put("password", password);

        long result = db.insert("authentication", null, values);
        if (result == -1) {
            // Failed to insert password
        } else {
            // Password inserted successfully
        }
        db.close();
    }

}



