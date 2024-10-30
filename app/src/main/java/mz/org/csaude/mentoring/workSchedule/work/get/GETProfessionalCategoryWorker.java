package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.professionalCategory.ProfessionalCategory;
import mz.org.csaude.mentoring.workSchedule.rest.ProfessionalCategoryRestService;

public class GETProfessionalCategoryWorker extends BaseWorker<ProfessionalCategory> {

    private ProfessionalCategoryRestService professionalCategoryRestService;
    public GETProfessionalCategoryWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.professionalCategoryRestService = new ProfessionalCategoryRestService((Application) this.context.getApplicationContext());
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.professionalCategoryRestService.restGetProfessionalCategory(offset, limit, this);
    }
    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doAfterSearch(String flag, List<ProfessionalCategory> recs) throws SQLException {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doSave(List<ProfessionalCategory> recs) {

    }
}
