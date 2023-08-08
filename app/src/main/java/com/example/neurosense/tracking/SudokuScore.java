package com.example.neurosense.tracking;

public class SudokuScore {
    private int score;

    public SudokuScore() {
        score = 0;
    }

    public void increaseScore() {
        score++;
    }

    public int getScore() {
        return score;
    }
}

