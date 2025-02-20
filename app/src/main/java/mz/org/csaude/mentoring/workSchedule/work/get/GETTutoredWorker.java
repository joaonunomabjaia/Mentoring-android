package mz.org.csaude.mentoring.workSchedule.work.get;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.tutored.Tutored;

public class GETTutoredWorker extends BaseWorker<Tutored> {


    public GETTutoredWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        getApplication().getTutoredRestService().restGetTutored(this, offset, limit);
    }



    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doAfterSearch(String flag, List<Tutored> recs) throws Exception {
        if (isPOSTRequest()) {
            changeStatusToFinished();
            doOnFinish();
        } else super.doAfterSearch(flag, recs);
    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Tutored> recs) {

    }
}
