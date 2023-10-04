package com.example.neurosense;

import androidx.annotation.NonNull;
import androidx.annotation.OptIn;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.Image;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.camera.core.CameraSelector;
import androidx.camera.core.ExperimentalGetImage;
import androidx.camera.core.ImageCapture;

import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.ImageProxy;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neurosense.buttons.NumberButtonClickListener;
import com.example.neurosense.hardware.CameraHelper;
import com.example.neurosense.tracking.LevelThreshold;
import com.example.neurosense.tracking.SudokuLevelManager;
import com.example.neurosense.tracking.SudokuScore;
import com.example.neurosense.tracking.TimeTracking;
import com.example.neurosense.ui.sudoku.SudokuGridAdapter;
import com.google.android.material.textfield.TextInputEditText;
import com.google.common.util.concurrent.ListenableFuture;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

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

    private int loopCount;
    private DatabaseHelper databaseHelper;
    private long currentLevelThreshold;
    private final Handler handler = new Handler();
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 100;
    private static final int CAMERA_PERMISSION_CODE = 1001;
    private static final int STORAGE_PERMISSION_CODE = 1002;
    private ImageCapture imageCapture;
    private String capturedImageFileName;
    private Context context;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sudoku);
        requestCameraPermission();
        requestStoragePermission();
        // Find the score counter TextInputEditText
        scoreCounter = findViewById(R.id.score_counter);
        levelManager = new SudokuLevelManager();
        sudokuScore = new SudokuScore(); // Initialize SudokuScore
        loopCount = 0;
        String sessionID = generateSessionID();

        // Initialize RecyclerView and adapter
        RecyclerView sudokuGridRecyclerView = findViewById(R.id.sudoku_grid_recycler_view);
        gridAdapter = new SudokuGridAdapter(this, gridValues);
        // Set the layout manager for the RecyclerView
        sudokuGridRecyclerView.setLayoutManager(new GridLayoutManager(this, 9));
        // Set the adapter for the RecyclerView
        sudokuGridRecyclerView.setAdapter(gridAdapter);

        // Set onClick listeners for number buttons
        int[] buttonIds = {R.id.button_1, R.id.button_2, R.id.button_3, R.id.button_4, R.id.button_5, //put in separate function
                R.id.button_6, R.id.button_7, R.id.button_8, R.id.button_9};

        NumberButtonClickListener buttonClickListener = new NumberButtonClickListener(this);

        for (int buttonId : buttonIds) {
            Button button = findViewById(buttonId);
            button.setOnClickListener(buttonClickListener);
        }

        // Initialize CameraHelper on the main thread
        runOnUiThread(() -> {
            CameraHelper cameraHelper = new CameraHelper(this);
            imageCapture = new ImageCapture.Builder().build();
            startCameraPermissionCheck();
        });
        // Find the show image button and set its click listener
        Button showImageButton = findViewById(R.id.button_show_image); // Replace with your button's ID
        showImageButton.setOnClickListener(this);

    }



    private void startCameraPermissionCheck() {
        if (!isCameraPermissionGranted()) {
            requestCameraPermission();
        } else {
            // Initialize camera and start the game loop
            initializeCameraAndStartGameLoop();
        }
    }

    private void initializeCameraAndStartGameLoop() {
        // Initialize CameraProvider
        ListenableFuture<ProcessCameraProvider> cameraProviderFuture = ProcessCameraProvider.getInstance(this);

        cameraProviderFuture.addListener(() -> {
            try {
                ProcessCameraProvider cameraProvider = cameraProviderFuture.get();

                // Initialize camera components on the main thread
                runOnUiThread(() -> {
                    bindCameraUseCases(cameraProvider);
                    timer = new TimeTracking();
                    gameLoopA();
                });
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
                // Handle camera initialization error
            }
        }, ContextCompat.getMainExecutor(this));
    }


    private void bindCameraUseCases(ProcessCameraProvider cameraProvider) {
        // Configure camera use cases and bind them to the preview or other UI components
        // You can configure ImageCapture use case here
        ImageCapture.Builder builder = new ImageCapture.Builder();
        imageCapture = builder.build();

        // Create a CameraSelector based on your requirements (e.g., front or back camera)
        CameraSelector cameraSelector = new CameraSelector.Builder()
                .requireLensFacing(CameraSelector.LENS_FACING_BACK) // Change as needed
                .build();

        try {
            cameraProvider.unbindAll(); // Unbind any previously bound use cases
            cameraProvider.bindToLifecycle(this, cameraSelector, imageCapture);
        } catch (Exception e) {
            e.printStackTrace();
            // Handle camera binding error
        }
    }


    private void requestCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.CAMERA}, CAMERA_PERMISSION_CODE);
        }
    }

    private void requestStoragePermission() {
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE}, STORAGE_PERMISSION_CODE);
        }
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

                // Notify the adapter that data has changed after removing numbers
                gridAdapter.notifyDataSetChanged();
                timer.start(); //user data tracking
                currentLevel = levelManager.getCurrentLevel();
                // Start the timer for the current level's threshold
                long currentLevelThreshold = LevelThreshold.getThresholdMillisForLevel(currentLevel);
                startTimer(currentLevelThreshold);
            }

        }.execute();
    }

    private void startTimer(long delayMillis) {
        handler.postDelayed(thresholdRunnable, delayMillis);
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
        // Cancel the threshold check runnable
        handler.removeCallbacks(thresholdRunnable);
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
        boolean userClickedCell = false;
        gameLoopB();
    }

    private void checkAnswer() {
        // Flag to track if all answers are correct
        boolean allCorrect = true;

        // Access the data directly from the gridAdapter
        for (int row = 0; row < 9; row++) {
            for (int col = 0; col < 9; col++) {
                // Get the value at the specific row and column from the gridAdapter
                int value = gridAdapter.getItemValue(row, col);
                int originalNumber = originalGrid[row][col];

                if (value != 0 && value != originalNumber) {
                    // User's input does not match the original number
                    allCorrect = false;
                    break;  // Exit the loop when an incorrect answer is found
                }
            }
        }

        // Check if all answers are correct before updating the score
        if (allCorrect) {
            sudokuScore.increaseScore();
            updateScoreText();
        }
    }


    private void tempLevelManager(int loopCount) {
        levelManager.tempLevelManager(loopCount); // Call the tempLevelManager in SudokuLevelManager
    }

    public void gameLoopA() { // Grid setup
        if (loopCount <= 50) {
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

    private void updateScoreText() {
        scoreCounter.setText(String.valueOf(sudokuScore.getScore()));
    }

    public int getLoopCount(){
        return loopCount;
    }

    public void endGame() {
        timer.stop();
        handler.removeCallbacks(thresholdRunnable);
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
        handler.removeCallbacks(thresholdRunnable);
        Log.d("user feedback", "Threshold Exceeded! You took too much time!");

        // Capture an image using the camera
        Executor executor = Executors.newSingleThreadExecutor();
        imageCapture.takePicture(executor, new ImageCapture.OnImageCapturedCallback() {
            @OptIn(markerClass = ExperimentalGetImage.class)
            @Override
            public void onCaptureSuccess(@NonNull ImageProxy image) {
                Log.d("debug", "Image captured successfully!");

                // Call the captureAndSaveImage method with the captured ImageProxy on the main thread
                runOnUiThread(() -> {
                    CameraHelper cameraHelper = new CameraHelper(getApplicationContext());
                    cameraHelper.captureAndSaveImage(image);

                    // Close the ImageProxy
                    Log.d("debug", "Closing ImageProxy...");
                    image.close();
                    Log.d("debug", "ImageProxy closed.");
                });
            }

            @Override
            public void onError(@NonNull ImageCaptureException exception) {
                Log.e("debug", "Image capture error:", exception);
                exception.printStackTrace();
            }
        });
    }

    @OptIn(markerClass = ExperimentalGetImage.class)
    private Bitmap imageProxyToBitmap(ImageProxy imageProxy) {
        Image image = imageProxy.getImage();
        if (image == null) {
            return null;
        }

        ByteBuffer buffer = image.getPlanes()[0].getBuffer();
        byte[] bytes = new byte[buffer.remaining()];
        buffer.get(bytes);

        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inMutable = true;

        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length, options);

        image.close();
        imageProxy.close();

        return bitmap;
    }





    // Runnable to track time and check the threshold for the current level
    private final Runnable thresholdRunnable = this::onThresholdExceeded;

    //CAMERA, EXTERNAL STORAGE PERMISSIONS
    private boolean arePermissionsGranted() {
        return isCameraPermissionGranted() && isStoragePermissionGranted();
    }

    private boolean isCameraPermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED;
    }

    private boolean isStoragePermissionGranted() {
        return ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    private void requestPermissions() {
        List<String> permissionsToRequest = new ArrayList<>();

        if (!isCameraPermissionGranted()) {
            permissionsToRequest.add(android.Manifest.permission.CAMERA);
        }

        if (!isStoragePermissionGranted()) {
            permissionsToRequest.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }

        if (!permissionsToRequest.isEmpty()) {
            ActivityCompat.requestPermissions(this, permissionsToRequest.toArray(new String[0]), CAMERA_PERMISSION_CODE);
        }
    }



    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == CAMERA_PERMISSION_CODE) {
            // Check if camera permission was granted
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Camera permission granted, you can proceed with camera-related operations
                timer = new TimeTracking();
                gameLoopA();
            } else {
                // Camera permission denied
                endGame();
            }
        }
    }
}