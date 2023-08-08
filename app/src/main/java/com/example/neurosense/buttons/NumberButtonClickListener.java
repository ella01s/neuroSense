package com.example.neurosense.buttons;

import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.example.neurosense.R;
import com.example.neurosense.SudokuActivity;

public class NumberButtonClickListener implements View.OnClickListener {

    private SudokuActivity sudokuActivity;

    public NumberButtonClickListener(SudokuActivity activity) {
        sudokuActivity = activity;
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
            sudokuActivity.selectNumber(number);
        }
    }
}
