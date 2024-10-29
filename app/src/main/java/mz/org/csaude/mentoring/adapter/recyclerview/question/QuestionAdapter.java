package mz.org.csaude.mentoring.adapter.recyclerview.question;

import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.adapter.recyclerview.generic.AbstractRecycleViewAdapter;
import mz.org.csaude.mentoring.base.activity.BaseActivity;
import mz.org.csaude.mentoring.databinding.QuestionListItemBinding;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;
import mz.org.csaude.mentoring.viewmodel.mentorship.MentorshipVM;

public class QuestionAdapter extends AbstractRecycleViewAdapter<FormSectionQuestion> {

    public QuestionAdapter(RecyclerView recyclerView, List<FormSectionQuestion> records, BaseActivity activity) {
        super(recyclerView, records, activity);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        QuestionListItemBinding questionListItemBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.question_list_item, parent, false);
        return new QuestionAdapter.FormQuestionViewHolder(questionListItemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((FormQuestionViewHolder) holder).questionListItemBinding.setFormSectionQuestion(super.records.get(position));
        ((FormQuestionViewHolder) holder).questionListItemBinding.setViewModel((MentorshipVM) getActivity().getRelatedViewModel());
    }
    @Override
    public int getItemCount() {
        return records.size();
    }

    public class FormQuestionViewHolder extends RecyclerView.ViewHolder {

        private QuestionListItemBinding questionListItemBinding;

        public FormQuestionViewHolder(@NonNull QuestionListItemBinding questionListItemBinding) {
            super(questionListItemBinding.getRoot());
            this.questionListItemBinding = questionListItemBinding;
        }
    }
}
