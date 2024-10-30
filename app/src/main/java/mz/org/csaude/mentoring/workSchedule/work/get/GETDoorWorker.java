package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.mentorship.Door;
import mz.org.csaude.mentoring.workSchedule.rest.DoorRestService;

public class GETDoorWorker extends BaseWorker<Door> {
    private DoorRestService doorRestService;
    public GETDoorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.doorRestService = new DoorRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.doorRestService.restGetDoors(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<Door> recs) throws SQLException {
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
    protected void doSave(List<Door> recs) {

    }
}
