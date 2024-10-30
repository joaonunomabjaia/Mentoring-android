package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.location.District;
import mz.org.csaude.mentoring.workSchedule.rest.DistrictRestService;

public class GETDistrictWorker extends BaseWorker<District> {

    private DistrictRestService districtRestService;

    public GETDistrictWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.districtRestService = new DistrictRestService((Application) this.context.getApplicationContext());
    }

    @Override
    protected void doAfterSearch(String flag, List<District> recs) throws SQLException {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.districtRestService.restGetDistricts(offset, limit, this);
    }

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<District> recs) {

    }
}
