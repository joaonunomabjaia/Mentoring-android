package mz.org.csaude.mentoring.adapter.tutored;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.generic.AbstractRecycleViewAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.databinding.TutoredListItemBinding;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.view.mentorship.CreateMentorshipActivity;
import mz.org.csaude.mentoring.view.tutored.TutoredActivity;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaVM;

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
        Tutored t = super.records.get(position);
        TutoredViewHolder vh = (TutoredViewHolder) holder;

        vh.binding.setTutored(t);

        // Overflow (3 dots) popup menu
        vh.binding.btnMore.setOnClickListener(v -> {
            PopupMenu menu = new PopupMenu(v.getContext(), v);
            menu.getMenuInflater().inflate(R.menu.menu_tutored_item, menu.getMenu());

            // Show/Hide "Remove" depending on your list type rule
            boolean showRemove = "SELECTION_LIST".equals(t.getListType());
            menu.getMenu().findItem(R.id.action_remove).setVisible(showRemove);

            menu.setOnMenuItemClickListener(item -> handleMenuClick(item, v, t, vh.getBindingAdapterPosition()));
            menu.show();
        });

        // We no longer use the legacy inline buttons:
        // - btnRemoveSelected
        // - btnEdit
        // Ensure they are gone in the layout or ignored.
    }

    private boolean handleMenuClick(MenuItem item, View anchor, Tutored t, int pos) {
        int id = item.getItemId();

        if (id == R.id.action_call) {
            String phone = (t.getEmployee() != null) ? t.getEmployee().getPhoneNumber() : null;
            if (phone == null || phone.isEmpty()) {
                Toast.makeText(anchor.getContext(), R.string.no_phone_available, Toast.LENGTH_SHORT).show();
                return true;
            }
            Intent dial = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + phone));
            anchor.getContext().startActivity(dial);
            return true;

        } else if (id == R.id.action_edit) {
            // Reuse your existing behavior used on row click
            if (activity instanceof CreateMentorshipActivity) {
                ((CreateMentorshipActivity) activity).onLongItemClick(anchor, pos);
            } else if (activity instanceof TutoredActivity) {
                actionListener.onEdit(t);
            }
            return true;

        } else if (id == R.id.action_remove) {
            if (activity.getRelatedViewModel() instanceof RondaVM) {
                ((RondaVM) activity.getRelatedViewModel()).removeFromSelected(t);
            }
            return true;
        }
        return false;
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

