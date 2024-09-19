package mz.org.csaude.mentoring.util;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class SpacingItemDecoration extends RecyclerView.ItemDecoration {

    private final int verticalSpaceHeight;

    public SpacingItemDecoration(int verticalSpaceHeight) {
        this.verticalSpaceHeight = verticalSpaceHeight;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);

        // Add top margin only for the items after the first item to avoid double space
        if (parent.getChildAdapterPosition(view) != 0) {
            outRect.top = verticalSpaceHeight;
        }
    }
}
