package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.program.Program;
import mz.org.csaude.mentoring.workSchedule.rest.ProgramRestService;
public class GETProgramWorker extends BaseWorker<Program> {
    private ProgramRestService programRestService;
    public GETProgramWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.programRestService = new ProgramRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.programRestService.restGetPrograms(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<Program> recs) throws SQLException {
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
    protected void doSave(List<Program> recs) {

    }
}
