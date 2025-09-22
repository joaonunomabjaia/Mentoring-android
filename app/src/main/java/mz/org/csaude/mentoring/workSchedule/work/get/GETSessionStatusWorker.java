package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.session.SessionStatus;
import mz.org.csaude.mentoring.workSchedule.rest.SessionStatusRestService;

public class GETSessionStatusWorker extends BaseWorker<SessionStatus> {
    private SessionStatusRestService sessionStatusRestService;
    public GETSessionStatusWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sessionStatusRestService = new SessionStatusRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        this.sessionStatusRestService.restGetSessionStatuses(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<SessionStatus> recs) throws Exception {
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
    protected void doSave(List<SessionStatus> recs) {

    }
}
