package com.example.neurosense.ui.sudoku;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.neurosense.R;
import com.example.neurosense.SudokuActivity;

public class SudokuGridAdapter extends RecyclerView.Adapter<SudokuGridAdapter.GridViewHolder> {
    private final int[][] gridValues;
    private final Context context;

    public SudokuGridAdapter(Context context, int[][] gridValues) {
        this.context = context;
        this.gridValues = gridValues;


    }

    @NonNull
    @Override
    public GridViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.sudoku_grid_cell, parent, false);
        return new GridViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull GridViewHolder holder, int position) {
        int row = position / 9;
        int col = position % 9;
        int value = gridValues[row][col];

        // Get reference to TextView
        TextView cellTextView = holder.cellTextView;

        if (value != 0) {
            cellTextView.setText(String.valueOf(value));
            cellTextView.setEnabled(false);
        } else {
            cellTextView.setText("");
            cellTextView.setEnabled(true);
        }

        // Set OnClickListener for the cellTextView
        cellTextView.setOnClickListener(v -> {
            ((SudokuActivity) context).clickEditView(row, col);
        });
    }

    @Override
    public int getItemCount() {
        return 81; // 9 rows x 9 columns
    }

    static class GridViewHolder extends RecyclerView.ViewHolder {
        TextView cellTextView;

        public GridViewHolder(@NonNull View itemView) {
            super(itemView);
            cellTextView = itemView.findViewById(R.id.gridCellTextView);
        }
    }
    public int getItemValue(int row, int col) {
        return gridValues[row][col];
    }
}
