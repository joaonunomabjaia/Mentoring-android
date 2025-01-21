package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.evaluationLocation.EvaluationLocation;
import mz.org.csaude.mentoring.workSchedule.rest.EvaluationLocationRestService;

public class GETEvaluationLocationWorker extends BaseWorker<EvaluationLocation> {

    private EvaluationLocationRestService evaluationLocationRestService;

    public GETEvaluationLocationWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.evaluationLocationRestService = new EvaluationLocationRestService((Application) getApplicationContext());
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.evaluationLocationRestService.restGetEvaluationLocations(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<EvaluationLocation> recs) throws SQLException {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnStart() {
        // Add any initialization logic if required
    }

    @Override
    protected void doOnFinish() {
        // Add any finalization logic if required
    }

    @Override
    protected void doSave(List<EvaluationLocation> recs) {
        // Implement logic to save fetched records if needed
    }
}
