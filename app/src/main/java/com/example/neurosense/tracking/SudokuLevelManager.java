package com.example.neurosense.tracking;

import com.example.neurosense.SudokuActivity;

import java.util.Random;

public class SudokuLevelManager {
    private int currentLevel;
    private int correctEntries;


    public SudokuLevelManager() {
        currentLevel = 1;
        //SudokuActivity activity = new SudokuActivity();
    }

    public void tempLevelManager(int loopCount) {
        int maxLoopsPerLevel = 10;
        currentLevel = Math.min(10, (loopCount - 1) / maxLoopsPerLevel + 1);
    }


    public void incrementCorrectEntries() {
        correctEntries++;
        if (correctEntries % 10 == 0) {
            increaseLevel();
        }
    }

    private void increaseLevel() {
        currentLevel++;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }



    public void removeNumbers(int[][] gridValues, int level) {
        int count = level;
        Random random = new Random();

        while (count > 0) {
            int row = random.nextInt(9);
            int col = random.nextInt(9);

            if (gridValues[row][col] != 0) {
                gridValues[row][col] = 0;
                count--;
            }
        }
    }
}

