package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.programmaticArea.TutorProgrammaticArea;
import mz.org.csaude.mentoring.workSchedule.rest.TutorProgrammaticAreaRestService;

public class GETTutorProgrammaticAreaWorker extends BaseWorker<TutorProgrammaticArea> {
    private TutorProgrammaticAreaRestService tutorProgrammaticAreaRestService;
    public GETTutorProgrammaticAreaWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.tutorProgrammaticAreaRestService = new TutorProgrammaticAreaRestService((Application) getApplicationContext());
    }
    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.tutorProgrammaticAreaRestService.restGetTutorProgrammaticAreas(this);
    }

    @Override
    protected void doAfterSearch(String flag, List<TutorProgrammaticArea> recs) throws SQLException {
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
    protected void doSave(List<TutorProgrammaticArea> recs) {

    }
}
