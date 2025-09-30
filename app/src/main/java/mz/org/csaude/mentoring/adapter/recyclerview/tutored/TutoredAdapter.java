package mz.org.csaude.mentoring.adapter.recyclerview.tutored;

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
import mz.org.csaude.mentoring.view.mentorship.CreateMentorshipActivity;
import mz.org.csaude.mentoring.viewmodel.ronda.RondaVM;

public class TutoredAdapter extends AbstractRecycleViewAdapter<Tutored> {
    public TutoredAdapter(RecyclerView recyclerView, List<Tutored> records, BaseActivity activity) {
        super(recyclerView, records, activity);
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        TutoredListItemBinding tutoredListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.tutored_list_item, parent, false);
        return new TutoredViewHolder(tutoredListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Tutored tutored = super.records.get(position);
        ((TutoredViewHolder) holder).tutoredListItemBinding.setTutored(tutored);
        ((TutoredViewHolder) holder).tutoredListItemBinding.btnRemoveSelected.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RondaVM) activity.getRelatedViewModel()).removeFromSelected(tutored);
            }
        });
        ((TutoredViewHolder) holder).tutoredListItemBinding.btnEdit.setVisibility(View.GONE);
    }

    public class TutoredViewHolder extends RecyclerView.ViewHolder {

        private TutoredListItemBinding tutoredListItemBinding;

        public TutoredViewHolder(@NonNull TutoredListItemBinding tutoredListItemBinding) {
            super(tutoredListItemBinding.getRoot());
            this.tutoredListItemBinding = tutoredListItemBinding;

            tutoredListItemBinding.getRoot().setOnClickListener(v -> {
                if (activity != null) {
                    ((CreateMentorshipActivity) activity).onLongItemClick(v, getAdapterPosition());
                }
                notifyItemChanged(getAdapterPosition());
                selectedPosition = getAdapterPosition();
                notifyItemChanged(selectedPosition);
            });
        }
    }
}
