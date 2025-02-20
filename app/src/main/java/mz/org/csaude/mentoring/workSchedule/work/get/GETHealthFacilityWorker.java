package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.location.HealthFacility;
import mz.org.csaude.mentoring.workSchedule.rest.HealthFacilityRestService;

public class GETHealthFacilityWorker extends BaseWorker<HealthFacility> {

    private HealthFacilityRestService healthFacilityRestService;

    public GETHealthFacilityWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.healthFacilityRestService = new HealthFacilityRestService((Application) this.context.getApplicationContext());
    }

    @Override
    protected void doOnStart() {
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        this.healthFacilityRestService.restGetHealthFacility(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<HealthFacility> recs) throws Exception {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnFinish() {

    }
    @Override
    protected void doSave(List<HealthFacility> recs) {

    }
}
