package com.example.neurosense;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neurosense.buttons.NumberButtonClickListener;
import com.example.neurosense.tracking.LevelThreshold;
import com.example.neurosense.tracking.SudokuLevelManager;
import com.example.neurosense.tracking.SudokuScore;
import com.example.neurosense.tracking.TimeTracking;
import com.example.neurosense.ui.sudoku.GridSpacing;
import com.example.neurosense.ui.sudoku.SudokuGridAdapter;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import database.DatabaseHelper;

public class SudokuActivity extends AppCompatActivity implements View.OnClickListener {
    private SudokuScore sudokuScore;
    private TextInputEditText scoreCounter;
    private final int[][] grid = new int[9][9];
    private final int[][] gridValues = new int[9][9];
    private final int[][] originalGrid = new int[9][9];
    private int selectedNumber = 0;
    private TimeTracking timer;
    private SudokuLevelManager levelManager;
    private SudokuGridAdapter gridAdapter;
    private boolean userClickedCell = false;
    private final int maxLoops = 200;
    private final int LOOP_DELAY = 100; // Milliseconds
    private int loopCount;
    private String sessionID;
    private DatabaseHelper databaseHelper;
    private long currentLevelThreshold;
    private Handler handler = new Handler();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        // Find the score counter TextInputEditText
        scoreCounter = findViewById(R.id.score_counter);
        levelManager = new SudokuLevelManager();
        sudokuScore = new SudokuScore(); // Initialize SudokuScore
        loopCount = 0;

        sessionID = generateSessionID();

        // Initialize RecyclerView and adapter
        RecyclerView sudokuGridRecyclerView = findViewById(R.id.sudoku_grid_recycler_view);
        gridAdapter = new SudokuGridAdapter(this, gridValues);
        // Set the layout manager for the RecyclerView
        sudokuGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 9));
        // Set the adapter for the RecyclerView
        sudokuGridRecyclerView.setAdapter(gridAdapter);

        // Set onClick listeners for number buttons
        int[] buttonIds = {R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5, //put in seperate function
                R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9};

        NumberButtonClickListener buttonClickListener = new NumberButtonClickListener(this);

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(buttonClickListener);
        }
        timer = new TimeTracking(); // Create an instance of TimeTracking
        gameLoopA();
    }

    private String generateSessionID() {
        StringBuilder sessionID = new StringBuilder();
        Random random = new Random();

        // Define the set of characters from which to generate the session ID
        String characters = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";

        // Generate a 5-character session ID by randomly selecting characters from the set
        for (int i = 0; i < 5; i++) {
            int randomIndex = random.nextInt(characters.length());
            char randomChar = characters.charAt(randomIndex);
            sessionID.append(randomChar);
        }

        return sessionID.toString();
    }


    private class SudokuTask extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... voids) {
            clearGrid();
            generateCompletedSudoku(); // Generate a complete Sudoku
            copyGridValues(); // Copy the completed grid to gridValues
            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
        }

        private void generateCompletedSudoku() {
            solveSudoku();
        }

        private void copyGridValues() {
            for (int i = 0; i < 9; i++) {
                System.arraycopy(grid[i], 0, gridValues[i], 0, 9);
                System.arraycopy(grid[i], 0, originalGrid[i], 0, 9); // Copy to originalGrid
            }
        }
    }

    public void clearGrid() {
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                grid[row][col] = 0;
                gridValues[row][col] = 0;
            }
        }
    }

    public void solveSudoku() {
        solveSudoku(0, 0);
    }

    public boolean solveSudoku(int row, int col) {
        if (row == 9) {
            row = 0;
            if (++col == 9) {
                return true; // Grid has been filled
            }
        }
        if (grid[row][col] != 0) {
            return solveSudoku(row + 1, col);
        }
        List<Integer> numbers = new ArrayList<>(List.of(1, 2, 3, 4, 5, 6, 7, 8, 9));
        Collections.shuffle(numbers);

        for (int num : numbers) {
            if (isValid(row, col, num)) {
                grid[row][col] = num;
                if (solveSudoku(row + 1, col)) {
                    return true;
                }
                grid[row][col] = 0; // Undo the assignment
            }
        } return false; // No valid number found, backtrack
    }

    public boolean isValid(int row, int col, int num) {
        // Check row and column
        for (int i = 0; i < 9; i++) {
            if (grid[row][i] == num || grid[i][col] == num) {
                return false;
            }
        }
        // Check 3x3 box
        int boxRow = row - row % 3;
        int boxCol = col - col % 3;
        for (int i = boxRow; i < boxRow + 3; i++) {
            for (int j = boxCol; j < boxCol + 3; j++) {
                if (grid[i][j] == num) {
                    return false;
                }
            }
        } return true;
    }

    private void generateSudokuGrid() {
        new SudokuTask() {
            @Override
            protected void onPostExecute(Void aVoid) {
                super.onPostExecute(aVoid);
                tempLevelManager(loopCount);
                int currentLevel = levelManager.getCurrentLevel();
                levelManager.removeNumbers(gridValues, currentLevel);
                //currentLevelThreshold = LevelThreshold.getThresholdMillisForLevel(currentLevel);

                // Notify the adapter that data has changed after removing numbers
                gridAdapter.notifyDataSetChanged();

                // Start the timer after generating a new Sudoku
                timer.start();
            }
        }.execute();
    }

    public void selectNumber(int number) {
        if (number != 0) {
            if (number != selectedNumber) {
                selectedNumber = number; // Update selectedNumber
            }
        } else {
            selectedNumber = 0; // Reset selectedNumber
        }
    }

    @Override
    public void onClick(View view) {
        int viewId = view.getId();

        if (viewId == R.id.button_1 ||
                viewId == R.id.button_2 ||
                viewId == R.id.button_3 ||
                viewId == R.id.button_4 ||
                viewId == R.id.button_5 ||
                viewId == R.id.button_6 ||
                viewId == R.id.button_7 ||
                viewId == R.id.button_8 ||
                viewId == R.id.button_9) {

            int number = Integer.parseInt(((Button) view).getText().toString());
            selectNumber(number);
        }
    }
    public void clickEditView(int row, int col) {
        timer.stop();
        long timestamp = timer.getElapsedTime();
        Log.d("TSData", "Loop: " + loopCount + "Timestamp: " + timestamp);
        //long newRowId = databaseHelper.insertTimestamp(loopCount, timestamp);

        if (selectedNumber != 0) {
            // Update the gridValues array
            gridValues[row][col] = selectedNumber;

            // Notify the adapter that the data has changed for the specific position
            gridAdapter.notifyItemChanged(row * 9 + col);

            checkAnswer();
        }resetUserClickedCell();
    }
    private void resetUserClickedCell() {
        userClickedCell = false;
        gameLoopB();
    }

    private boolean checkAnswer() {
        // Access the data directly from the gridAdapter
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // Get the value at the specific row and column from the gridAdapter
                int value = gridAdapter.getItemValue(row, col);
                int originalNumber = originalGrid[row][col];

                if (value != 0 && value != originalNumber) {
                    return false; // User's input does not match the original number
                }
            }
        }
        sudokuScore.increaseScore();
        updateScoreText();
        return true; // User's input matches the original numbers
    }

    private void tempLevelManager(int loopCount) {
        levelManager.tempLevelManager(loopCount); // Call the tempLevelManager in SudokuLevelManager
    }

    public void gameLoopA() { // Grid setup
        if (loopCount <= 200) {
            tempLevelManager(loopCount);
            loopCount++;
            generateSudokuGrid();
        }
    }

    public void gameLoopB(){    //grid take down
        clearGrid();
        if (loopCount <= 200) {
            gameLoopA();
        } else {
            endGame();
        }
    }

    private void showToast(String message) { Toast.makeText(this, message, Toast.LENGTH_SHORT).show();}

    private void updateScoreText() {
        scoreCounter.setText(String.valueOf(sudokuScore.getScore()));
    }

    public int getLoopCount(){
        return loopCount;
    }

    public void endGame() {
        timer.stop();
        Intent intent = new Intent(this, GameHubActivity.class);
        startActivity(intent);
        finish();                 // Finish SudokuActivity
    }


    private void writeTimestampToFile(String sessionID, long timestamp) {
        // Get the directory path where you want to save the files
        File directory = new File(getFilesDir(), "database/trainingData");

        // Check if the directory exists, if not, create it
        if (!directory.exists()) {
            directory.mkdirs();
        }

        // Create a file with the session ID as the filename
        File file = new File(directory, sessionID + ".txt");

        // Write the timestamp to the file
        try {
            FileWriter writer = new FileWriter(file, true); // true to append, false to overwrite
            writer.write(timestamp + "\n");
            writer.close();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle any errors that may occur during writing to the file
        }
    }

    private void onThresholdExceeded() {
        // This method is called when the user's elapsed time exceeds the threshold
        showToast("Threshold Exceeded! You took too much time!");
    }

    // Runnable to track time and check the threshold for the current level
    private Runnable thresholdRunnable = new Runnable() {
        @Override
        public void run() {
            long elapsedTime = timer.getElapsedTime();

            // Get the current level threshold
            long currentLevelThreshold = LevelThreshold.getThresholdMillisForLevel(levelManager.getCurrentLevel());

            if (elapsedTime > currentLevelThreshold) {
                onThresholdExceeded();
            }
        }
    };
}