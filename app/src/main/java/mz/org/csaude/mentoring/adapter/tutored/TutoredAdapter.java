package mz.org.csaude.mentoring.adapter.tutored;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.generic.AbstractRecycleViewAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.databinding.TutoredListItemBinding;
import mz.org.csaude.mentoring.model.tutored.Tutored;
import mz.org.csaude.mentoring.viewmodel.tutored.TutoredVM;

public class TutoredAdapter extends AbstractRecycleViewAdapter<Tutored> {


    public TutoredAdapter(RecyclerView recyclerView, List<Tutored> records, BaseActivity activity) {
        super(recyclerView, records, activity);
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
        ((TutoredViewHolder) holder).tutoredListItemBinding.setTutored(tutored);
    }

    // ViewHolder class
    public static class TutoredViewHolder extends RecyclerView.ViewHolder {

        private TutoredListItemBinding tutoredListItemBinding;

        public TutoredViewHolder(@NonNull TutoredListItemBinding tutoredListItemBinding) {
            super(tutoredListItemBinding.getRoot());
            this.tutoredListItemBinding = tutoredListItemBinding;
        }
    }
}
