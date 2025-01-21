package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.location.Province;
import mz.org.csaude.mentoring.workSchedule.rest.ProvinceRestService;

public class GETProvinceWorker extends BaseWorker<Province> {

    ProvinceRestService provinceRestService;
    public GETProvinceWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);

        provinceRestService = new ProvinceRestService((Application) this.context.getApplicationContext());

    }

    @Override
    protected void doAfterSearch(String flag, List<Province> recs) throws SQLException {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.provinceRestService.restGetProvince(this);
    }

    @Override
    protected void doOnStart() {


    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Province> recs) {

    }
}
