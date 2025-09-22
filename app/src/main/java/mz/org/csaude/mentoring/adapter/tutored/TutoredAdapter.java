package mz.org.csaude.mentoring.adapter.tutored;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.generic.AbstractRecycleViewAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.databinding.TutoredListItemBinding;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.view.tutored.fragment.TutoredFragment;
import mz.org.csaude.mentoring.viewmodel.tutored.TutoredVM;

public class TutoredAdapter extends AbstractRecycleViewAdapter<Tutored> {

    private boolean ignoreCloseSwipe = false;

    public interface OnTutoredActionListener {
        void onEdit(Tutored tutored);
    }

    private final OnTutoredActionListener actionListener;
    private int swipedPosition = -1;

    public TutoredAdapter(RecyclerView recyclerView, List<Tutored> records, BaseActivity activity, OnTutoredActionListener actionListener) {
        super(recyclerView, records, activity);
        this.actionListener = actionListener;
    }


    public void setIgnoreCloseSwipe(boolean ignore) {
        this.ignoreCloseSwipe = ignore;
    }

    public boolean isIgnoreCloseSwipe() {
        return ignoreCloseSwipe;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TutoredListItemBinding binding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.tutored_list_item, parent, false);
        return new TutoredViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tutored tutored = records.get(position);
        TutoredViewHolder vh = (TutoredViewHolder) holder;
        vh.binding.setTutored(tutored);
        vh.binding.setViewModel(((TutoredFragment)actionListener).getRelatedViewModel());

        vh.binding.btnEdit.setOnClickListener(v -> {
            setIgnoreCloseSwipe(true); // impede que o scroll feche
            actionListener.onEdit(tutored);
        });

        vh.binding.btnEdit.setOnTouchListener((v, event) -> {
            // intercepta o toque para que não feche o item ao clicar
            v.getParent().requestDisallowInterceptTouchEvent(true);
            return false;
        });
    }



    public void removeItem(int position) {
        records.remove(position);
        notifyItemRemoved(position);
    }

    public void restoreItem(Tutored item, int position) {
        records.add(position, item);
        notifyItemInserted(position);
    }

    public View getForegroundView(RecyclerView.ViewHolder viewHolder) {
        return ((TutoredViewHolder) viewHolder).binding.viewForeground;
    }

    public static class TutoredViewHolder extends RecyclerView.ViewHolder {
        public TutoredListItemBinding binding;

        public TutoredViewHolder(TutoredListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }

    public void setSwipedPosition(int position) {
        if (swipedPosition != -1 && swipedPosition != position) {
            notifyItemChanged(swipedPosition); // fecha o anterior
        }
        swipedPosition = position;
    }

    public void closeSwipedItem() {
        if (swipedPosition != -1) {
            notifyItemChanged(swipedPosition);
            swipedPosition = -1;
        }
    }

    public int getSwipedPosition() {
        return swipedPosition;
    }
}

