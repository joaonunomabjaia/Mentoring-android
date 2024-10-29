package mz.org.csaude.mentoring.workSchedule.work;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.formSectionQuestion.FormSectionQuestion;

public class FormSectionQuestionWorker extends BaseWorker<FormSectionQuestion> {
    private String requestType;

    public FormSectionQuestionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        requestType = getInputData().getString("requestType");
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
            getApplication().getFormQuestionRestService().restGetFormSectionQuestion(this, limit, offset);
    }

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<FormSectionQuestion> recs) {

    }
}
