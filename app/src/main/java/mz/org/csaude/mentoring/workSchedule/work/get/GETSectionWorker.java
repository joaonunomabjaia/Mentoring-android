package mz.org.csaude.mentoring.workSchedule.work.get;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.form.Section;
import mz.org.csaude.mentoring.workSchedule.rest.SectionRestService;

public class GETSectionWorker extends BaseWorker<Section> {

    private SectionRestService sectionRestService;

    public GETSectionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
        this.sectionRestService = new SectionRestService((Application) getApplicationContext());
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws SQLException {
        this.sectionRestService.restGetSections(this);
    }
    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Section> recs) {

    }
}
