package mz.org.csaude.mentoring.workSchedule.work.get;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.partner.Partner;

public class GETPartnerWorker extends BaseWorker<Partner> {


    public GETPartnerWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    protected void doOnStart() {

    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        getApplication().getPartnerRestService().restGetPartners(this);
        super.doOnlineSearch(offset, limit);
    }

    @Override
    protected void doAfterSearch(String flag, List<Partner> recs) throws Exception {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Partner> recs) {

    }
}
