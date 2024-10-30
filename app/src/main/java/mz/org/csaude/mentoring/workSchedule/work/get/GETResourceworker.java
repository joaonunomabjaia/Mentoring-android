package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.resourceea.Resource;
import mz.org.csaude.mentoring.workSchedule.rest.ResourceRestService;

public class GETResourceworker extends BaseWorker<Resource> {

    ResourceRestService resourceRestService;
    public GETResourceworker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.resourceRestService = new ResourceRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.resourceRestService.restGetResources(this);
    }

/*
    protected void doAfterSearch(String flag, List<Program> recs) throws SQLException {
        changeStatusToFinished();
        doOnFinish();
    }
    */

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Resource> recs) {

    }
}
