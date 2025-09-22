package mz.org.csaude.mentoring.adapter.recyclerview.sync;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.WorkInfo;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.generic.AbstractRecycleViewAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.databinding.ItemSyncStatusBinding;
import mz.org.csaude.mentoring.model.sync.SyncStatus;

public class SyncStatusAdapter extends AbstractRecycleViewAdapter<SyncStatus> {

    public SyncStatusAdapter(RecyclerView recyclerView, List<SyncStatus> records, BaseActivity activity) {
        super(recyclerView, records, activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSyncStatusBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.item_sync_status,
                parent,
                false
        );
        return new SyncStatusViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        SyncStatus status = records.get(position);
        SyncStatusViewHolder viewHolder = (SyncStatusViewHolder) holder;
        viewHolder.binding.setStatus(status);
        viewHolder.binding.iconStatus.setImageDrawable(
                ContextCompat.getDrawable(activity, status.getIconResource())
        );
    }

    public void updateStatus(String tag, WorkInfo.State newState) {
        for (int i = 0; i < records.size(); i++) {
            SyncStatus status = records.get(i);
            if (status.getTag().equals(tag)) {
                status.setState(newState);
                notifyItemChanged(i);
                break;
            }
        }
    }

    public static class SyncStatusViewHolder extends RecyclerView.ViewHolder {
        private final ItemSyncStatusBinding binding;

        public SyncStatusViewHolder(ItemSyncStatusBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
