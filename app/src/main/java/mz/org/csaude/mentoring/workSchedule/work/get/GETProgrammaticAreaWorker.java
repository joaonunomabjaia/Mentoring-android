package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.programmaticArea.ProgrammaticArea;
import mz.org.csaude.mentoring.workSchedule.rest.ProgrammaticAreaRestService;

public class GETProgrammaticAreaWorker extends BaseWorker<ProgrammaticArea> {
    private ProgrammaticAreaRestService programmaticAreaRestService;
    public GETProgrammaticAreaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.programmaticAreaRestService = new ProgrammaticAreaRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.programmaticAreaRestService.restGetProgrammaticAreas(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<ProgrammaticArea> recs) throws SQLException {
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
    protected void doSave(List<ProgrammaticArea> recs) {

    }
}
