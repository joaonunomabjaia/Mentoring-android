package mz.org.csaude.mentoring.workSchedule.work.post;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.session.SessionRecommendedResource;

public class POSTSessionRecommendedResourceWorker extends BaseWorker<SessionRecommendedResource> {
    private String requestType;

    public POSTSessionRecommendedResourceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        requestType = getInputData().getString("requestType");
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        getApplication().getSessionRecommendedResourceRestService().postSessionRecommendedResource(this);
    }

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doAfterSearch(String flag, List<SessionRecommendedResource> recs) throws Exception {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<SessionRecommendedResource> recs) {

    }
}
