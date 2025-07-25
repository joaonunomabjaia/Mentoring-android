package mz.org.csaude.mentoring.util;

import android.graphics.Canvas;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import mz.org.csaude.mentoring.adapter.tutored.TutoredAdapter;

public class RecyclerItemTouchHelper extends ItemTouchHelper.SimpleCallback {

    private final RecyclerItemTouchHelperListener listener;

    public interface RecyclerItemTouchHelperListener {
        void onSwiped(RecyclerView.ViewHolder viewHolder, int direction, int position);
    }

    public RecyclerItemTouchHelper(int dragDirs, int swipeDirs, RecyclerItemTouchHelperListener listener) {
        super(dragDirs, swipeDirs);
        this.listener = listener;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        return false;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        if (listener != null) {
            listener.onSwiped(viewHolder, direction, viewHolder.getAdapterPosition());
        }
    }

    @Override
    public void onChildDraw(@NonNull Canvas c,
                            @NonNull RecyclerView recyclerView,
                            @NonNull RecyclerView.ViewHolder viewHolder,
                            float dX, float dY,
                            int actionState,
                            boolean isCurrentlyActive) {

        // Largura máxima que pode ser deslizada (ex: 300px ou equivalente ao tamanho do botão)
        final float maxSwipe = 300f;

        // Limita o swipe apenas até a largura dos botões
        if (dX < -maxSwipe) dX = -maxSwipe;
        if (dX > maxSwipe) dX = maxSwipe;

        if (viewHolder instanceof TutoredAdapter.TutoredViewHolder) {
            View foregroundView = ((TutoredAdapter.TutoredViewHolder) viewHolder).binding.viewForeground;
            getDefaultUIUtil().onDraw(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        if (viewHolder != null && viewHolder instanceof TutoredAdapter.TutoredViewHolder) {
            View foregroundView = ((TutoredAdapter.TutoredViewHolder) viewHolder).binding.viewForeground;
            getDefaultUIUtil().onSelected(foregroundView);
        }
    }

    @Override
    public void clearView(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder) {
        if (viewHolder instanceof TutoredAdapter.TutoredViewHolder) {
            View foregroundView = ((TutoredAdapter.TutoredViewHolder) viewHolder).binding.viewForeground;
            getDefaultUIUtil().clearView(foregroundView);
        }
    }

    @Override
    public void onChildDrawOver(@NonNull Canvas c,
                                @NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder,
                                float dX, float dY,
                                int actionState,
                                boolean isCurrentlyActive) {
        if (viewHolder instanceof TutoredAdapter.TutoredViewHolder) {
            View foregroundView = ((TutoredAdapter.TutoredViewHolder) viewHolder).binding.viewForeground;
            getDefaultUIUtil().onDrawOver(c, recyclerView, foregroundView, dX, dY, actionState, isCurrentlyActive);
        }
    }
}
