package mz.org.csaude.mentoring.workSchedule.work.get;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.WorkerParameters;

import java.sql.SQLException;
import java.util.List;

import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.setting.Setting;
import mz.org.csaude.mentoring.workSchedule.rest.SettingRestService;

public class GETSettingWorker extends BaseWorker<Setting> {
    public GETSettingWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        SettingRestService settingRestService = new SettingRestService(getApplication());
        settingRestService.restGetSettings(this);
    }

    @Override
    protected void doOnStart() {

    }

    protected void doAfterSearch(String flag, List<Setting> recs) throws Exception {
        changeStatusToFinished();
        doOnFinish();
    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Setting> recs) {

    }
}
