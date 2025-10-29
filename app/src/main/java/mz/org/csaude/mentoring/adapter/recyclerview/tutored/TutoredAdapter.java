package mz.org.csaude.mentoring.adapter.recyclerview.tutored;

import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
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
import mz.org.csaude.mentoring.viewmodel.ronda.RondaVM;

public class TutoredAdapter extends AbstractRecycleViewAdapter<Tutored> {

    private final mz.org.csaude.mentoring.adapter.tutored.TutoredAdapter.OnTutoredActionListener actionListener;

    public TutoredAdapter(RecyclerView recyclerView, List<Tutored> records, BaseActivity activity, mz.org.csaude.mentoring.adapter.tutored.TutoredAdapter.OnTutoredActionListener actionListener) {
        super(recyclerView, records, activity);
        this.actionListener = actionListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TutoredListItemBinding b = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.tutored_list_item,
                parent,
                false
        );
        return new TutoredViewHolder(b);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tutored t = super.records.get(position);
        TutoredViewHolder vh = (TutoredViewHolder) holder;

        vh.b.setTutored(t);

        // Overflow (3 dots) popup menu
        vh.b.btnMore.setOnClickListener(v -> {
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
                actionListener.onEdit(t);
                /*((CreateMentorshipActivity) activity).onLongItemClick(anchor, pos);*/
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

    public class TutoredViewHolder extends RecyclerView.ViewHolder {
        private final TutoredListItemBinding b;

        public TutoredViewHolder(@NonNull TutoredListItemBinding binding) {
            super(binding.getRoot());
            this.b = binding;

            // Keep your existing root click behavior
            b.getRoot().setOnClickListener(v -> {
                if (activity instanceof CreateMentorshipActivity) {
                    ((CreateMentorshipActivity) activity).onLongItemClick(v, getBindingAdapterPosition());
                }
                notifyItemChanged(getBindingAdapterPosition());
                selectedPosition = getBindingAdapterPosition();
                notifyItemChanged(selectedPosition);
            });
        }
    }
}
