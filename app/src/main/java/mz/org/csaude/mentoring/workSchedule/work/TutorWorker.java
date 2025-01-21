package mz.org.csaude.mentoring.workSchedule.work;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.tutor.Tutor;
import mz.org.csaude.mentoring.model.user.User;
import mz.org.csaude.mentoring.workSchedule.rest.TutorRestService;

public class TutorWorker extends BaseWorker<Tutor> {

    private TutorRestService tutorRestService;

    public TutorWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void doOnStart() {
        this.tutorRestService = new TutorRestService((Application) getApplicationContext());
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.tutorRestService.restPatchTutor(getApplication().getCurrMentor(),this);
    }

    @Override
    protected void doAfterSearch(String flag, List<Tutor> recs) throws SQLException {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Tutor> recs) {

    }
}
