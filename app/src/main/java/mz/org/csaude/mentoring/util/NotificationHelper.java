package mz.org.csaude.mentoring.util;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.util.Log;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import mz.org.csaude.mentoring.R;
import mz.org.csaude.mentoring.model.session.Session;
import mz.org.csaude.mentoring.view.login.LoginActivity;
import mz.org.csaude.mentoring.view.session.SessionListActivity;

public class NotificationHelper {

    private static final String CHANNEL_ID = "session_channel";
    private static final String CHANNEL_NAME = "Sessões de Mentoria";
    private static final String CHANNEL_DESCRIPTION = "Notificações sobre sessões próximas.";

    public static void createNotificationChannel(Context context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager manager = context.getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    public static void notifyNextSession(Context context, Session session) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w("NotificationHelper", "Permission POST_NOTIFICATIONS not granted");
            return;
        }

        Intent postLoginIntent = new Intent(context, SessionListActivity.class);
        postLoginIntent.putExtra("source", "notification");
        postLoginIntent.putExtra("showUpcomingOnly", true);
        postLoginIntent.putExtra("ronda", session.getRonda());

        Intent loginIntent = new Intent(context, LoginActivity.class);
        loginIntent.putExtra("redirectAfterLogin", postLoginIntent);
        loginIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                0,
                loginIntent,
                PendingIntent.FLAG_IMMUTABLE
        );


        String menteeName = session.getTutored() != null ? session.getTutored().getEmployee().getFullName() : "o mentorando";
        String dateStr = formatDate(session.getNextSessionDate());

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Sessão de Mentoria em Breve")
                .setContentText("Sua próxima sessão com " + menteeName + " será em " + dateStr)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(session.getId(), builder.build());
    }

    public static void notifyMultipleUpcomingSessions(Context context, List<Session> sessions) {
        if (sessions == null || sessions.isEmpty()) return;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU &&
                ActivityCompat.checkSelfPermission(context, android.Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {
            Log.w("NotificationHelper", "Permission POST_NOTIFICATIONS not granted");
            return;
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle()
                .setBigContentTitle("Sessões de Mentoria nos próximos dias:");

        for (Session session : sessions) {
            String menteeName = session.getTutored() != null ? session.getTutored().getEmployee().getFullName() : "Mentorando";
            String dateStr = formatDate(session.getNextSessionDate());
            inboxStyle.addLine(menteeName + ": " + dateStr);
        }

        inboxStyle.addLine("Abra o app para mais detalhes.");

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notification)
                .setContentTitle("Você tem " + sessions.size() + " sessões de mentoria em breve")
                .setStyle(inboxStyle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(null);

        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);
        notificationManager.notify(9999, builder.build()); // 9999 as a fixed ID for grouped notification
    }

    private static String formatDate(Date date) {
        if (date == null) return "breve";
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        return sdf.format(date);
    }
}
