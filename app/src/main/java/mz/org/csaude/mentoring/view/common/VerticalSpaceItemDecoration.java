// mz/org/csaude/mentoring/view/common/VerticalSpaceItemDecoration.java
package mz.org.csaude.mentoring.view.common;

import android.graphics.Rect;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class VerticalSpaceItemDecoration extends RecyclerView.ItemDecoration {
    private final int spacePx;
    private final boolean addBottomForLast; // if you want a bottom gap on the last item

    public VerticalSpaceItemDecoration(int spacePx) {
        this(spacePx, false);
    }

    public VerticalSpaceItemDecoration(int spacePx, boolean addBottomForLast) {
        this.spacePx = spacePx;
        this.addBottomForLast = addBottomForLast;
    }

    @Override
    public void getItemOffsets(Rect out, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        int last = state.getItemCount() - 1;

        // No top space for the first item; space only between items
        if (pos > 0) out.top = spacePx;

        // Optional: bottom space only on the last item
        if (addBottomForLast && pos == last) out.bottom = spacePx;
    }
}
