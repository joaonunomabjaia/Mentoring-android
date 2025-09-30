package mz.org.csaude.mentoring.workSchedule.work;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.work.WorkerParameters;

import java.util.List;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.base.worker.BaseWorker;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.util.NotificationHelper;
import mz.org.csaude.mentoring.util.Utilities;

public class CheckNextSessionWorker extends BaseWorker<Session> {

    public static final int NEXT_SESSION_NOTIFICATION_INTERVAL = 2;

    public CheckNextSessionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        // Fetch sessions where nextSessionDate is 2 days ahead
        List<Session> upcomingSessions = getApplication().getSessionService().getSessionsWithinNextDays(NEXT_SESSION_NOTIFICATION_INTERVAL);


        if (Utilities.listHasElements(upcomingSessions) && upcomingSessions.size() == 1) {
            NotificationHelper.notifyNextSession(context, upcomingSessions.get(0));
        } else if (Utilities.listHasElements(upcomingSessions) && upcomingSessions.size() > 1) {
            NotificationHelper.notifyMultipleUpcomingSessions(context, upcomingSessions);
        }
    }

    @Override
    protected void doAfterSearch(String flag, List<Session> recs) throws Exception {

    }

    @Override
    protected void doOnStart() {

    }

    @Override
    protected void doOnFinish() {

    }

    @Override
    protected void doSave(List<Session> recs) {

    }
}
