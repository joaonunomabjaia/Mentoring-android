package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.evaluationType.EvaluationType;
import mz.org.csaude.mentoring.workSchedule.rest.EvaluationTypeRestService;

public class GETEvaluationTypeWorker extends BaseWorker<EvaluationType> {
    private EvaluationTypeRestService evaluationTypeRestService;
    public GETEvaluationTypeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.evaluationTypeRestService = new EvaluationTypeRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        this.evaluationTypeRestService.restGetEvaluationTypes(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<EvaluationType> recs) throws Exception {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<EvaluationType> recs) {

    }
}
