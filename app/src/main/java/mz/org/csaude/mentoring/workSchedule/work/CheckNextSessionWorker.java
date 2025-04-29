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

public class CheckNextSessionWorker extends BaseWorker<Session> {

    public CheckNextSessionWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @Override
    public void doOnlineSearch(long offset, long limit) throws Exception {
        // Fetch sessions where nextSessionDate is 2 days ahead
        List<Session> upcomingSessions = getApplication().getSessionService().getSessionsWithinNextDays(2);

        for (Session session : upcomingSessions) {
            sendNotification(session);
        }

    }

    private void sendNotification(Session session) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "session_channel")
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Sessão de Mentoria Aproximando-se")
                .setContentText("Sua próxima sessão com " + session.getTutored().getEmployee().getFullName() + " será em breve!")
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) { // Android 13+
            if (ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED) {
                notificationManager.notify(session.getId(), builder.build());
            } else {
                // Permission not granted, maybe log or silently skip
                Log.w("Notification", "POST_NOTIFICATIONS permission not granted.");
            }
        } else {
            // Android 12 or lower, permission not needed
            notificationManager.notify(session.getId(), builder.build());
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
