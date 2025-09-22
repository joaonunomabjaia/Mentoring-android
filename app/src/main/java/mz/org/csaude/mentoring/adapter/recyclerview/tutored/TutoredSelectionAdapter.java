package mz.org.csaude.mentoring.adapter.recyclerview.tutored;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.generic.AbstractRecycleViewAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.databinding.SelectTutoredListItemBinding;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaVM;

public class TutoredSelectionAdapter extends AbstractRecycleViewAdapter<Tutored> {

    private final RondaVM viewModel;
    private final List<Tutored> allRecords; // full list for filtering

    public TutoredSelectionAdapter(RecyclerView recyclerView, List<Tutored> records, BaseActivity activity, RondaVM viewModel) {
        super(recyclerView, new ArrayList<>(records), activity);
        this.allRecords = new ArrayList<>(records);
        this.viewModel = viewModel;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SelectTutoredListItemBinding binding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.getContext()),
                R.layout.select_tutored_list_item,
                parent,
                false
        );
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tutored tutored = records.get(position);
        ViewHolder viewHolder = (ViewHolder) holder;

        viewHolder.binding.setTutored(tutored);
        viewHolder.binding.setSelected(tutored.isSelected());

        viewHolder.binding.checkIcon.setOnClickListener(v -> {
            if (viewModel.getSelectedMentees().contains(tutored)) {
                viewModel.removeFromSelected(tutored);
                tutored.setItemSelected(false);
            } else {
                viewModel.addToSelected(tutored);
                tutored.setItemSelected(true);
            }
            notifyItemChanged(position);
        });


        viewHolder.binding.getRoot().setOnClickListener(v -> {
            if (viewModel.getSelectedMentees().contains(tutored)) {
                viewModel.removeFromSelected(tutored);
                tutored.setItemSelected(false);
            } else {
                viewModel.addToSelected(tutored);
                tutored.setItemSelected(true);
            }
            notifyItemChanged(position);
        });
    }

    public void filter(String query) {
        records.clear();
        if (query == null || query.trim().isEmpty()) {
            records.addAll(allRecords);
        } else {
            String lowerQuery = query.toLowerCase();
            for (Tutored tutored : allRecords) {
                String name = tutored.getEmployee().getFullName() != null ? tutored.getEmployee().getFullName().toLowerCase() : "";
                String role = tutored.getEmployee().getProfessionalCategory() != null ? tutored.getEmployee().getProfessionalCategory().getDescription().toLowerCase() : "";
                long phone = tutored.getEmployee().getNuit() > 0 ? tutored.getEmployee().getNuit() : 0;

                if (name.contains(lowerQuery) || role.contains(lowerQuery) || Long.toString(phone).contains(query)) {
                    records.add(tutored);
                }
            }
        }
        notifyDataSetChanged();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final SelectTutoredListItemBinding binding;

        public ViewHolder(@NonNull SelectTutoredListItemBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
