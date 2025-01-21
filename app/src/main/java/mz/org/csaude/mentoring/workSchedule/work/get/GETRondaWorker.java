package mz.org.csaude.mentoring.workSchedule.work.get;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.ronda.Ronda;
import mz.org.csaude.mentoring.util.Http;
import mz.org.csaude.mentoring.util.Utilities;

public class GETRondaWorker extends BaseWorker<Ronda> {
    private String requestType;

    public GETRondaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        requestType = getInputData().getString("requestType");
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        getApplication().getRondaRestService().restGetRondas(this);
    }

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doAfterSearch(String flag, List<Ronda> recs) throws SQLException {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Ronda> recs) {

    }
}
