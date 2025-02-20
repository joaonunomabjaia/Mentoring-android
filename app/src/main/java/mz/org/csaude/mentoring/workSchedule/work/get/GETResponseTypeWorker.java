package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.responseType.ResponseType;
import mz.org.csaude.mentoring.workSchedule.rest.ResponseTypeRestService;

public class GETResponseTypeWorker extends BaseWorker<ResponseType> {
    private ResponseTypeRestService responseTypeRestService;
    public GETResponseTypeWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.responseTypeRestService = new ResponseTypeRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        this.responseTypeRestService.restGetResponseTypes(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<ResponseType> recs) throws Exception {
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
    protected void doSave(List<ResponseType> recs) {

    }
}
