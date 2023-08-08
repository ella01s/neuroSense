package com.example.neurosense.ui.sudoku;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;

public class GridSpacing extends RecyclerView.ItemDecoration {
    private int spanCount;
    private int spacing;
    private Context context;

    public GridSpacing(int spanCount, int spacing, Context context) {
        this.spanCount = spanCount;
        this.spacing = spacing;
        this.context = context;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int position = parent.getChildAdapterPosition(view);
        int column = position % spanCount;
        int row = position / spanCount;

        // Calculate the item width (excluding spacing)
        int itemWidth = (parent.getWidth() - (spanCount - 1) * spacing) / spanCount;

        // Calculate the item height (excluding spacing)
        int itemHeight = (parent.getHeight() - (spanCount - 1) * spacing) / spanCount;

        // Calculate the starting and ending positions for the item
        int start = column * (itemWidth + spacing);
        int end = start + itemWidth;
        int top = row * (itemHeight + spacing);
        int bottom = top + itemHeight;

        // Set the calculated offsets for the item
        outRect.set(start, top, parent.getWidth() - end, parent.getHeight() - bottom);
    }
}
