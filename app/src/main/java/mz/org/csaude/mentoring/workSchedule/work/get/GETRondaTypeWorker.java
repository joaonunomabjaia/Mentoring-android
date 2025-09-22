package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.rondatype.RondaType;
import mz.org.csaude.mentoring.workSchedule.rest.RondaTypeRestService;

public class GETRondaTypeWorker extends BaseWorker<RondaType> {
    private RondaTypeRestService rondaTypeRestService;
    public GETRondaTypeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.rondaTypeRestService = new RondaTypeRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        this.rondaTypeRestService.restGetRondaTypes(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<RondaType> recs) throws Exception {
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
    protected void doSave(List<RondaType> recs) {

    }
}
