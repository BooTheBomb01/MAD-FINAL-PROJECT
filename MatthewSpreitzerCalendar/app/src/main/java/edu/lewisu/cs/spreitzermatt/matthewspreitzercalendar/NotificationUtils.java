package edu.lewisu.cs.spreitzermatt.matthewspreitzercalendar;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import java.util.Date;

public class NotificationUtils {
    private static final int TODO_REMINDER_NOTIFICATION_ID = 2466;
    private static final String REMINDER_NOTIFICATION_CHANNEL = "reminder_notification_channel";
    private static final int TODO_REMINDER_PENDING_INTENT = 1234;
    private static final int ACTION_IGNORE_PENDING_INTENT_ID = 13;
    public static void remindUser(Context context){
        Intent startActivity = new Intent(context, MainActivity.class);
        PendingIntent startActivityPendingIntent = PendingIntent.getActivity(
                context,
                TODO_REMINDER_PENDING_INTENT,
                startActivity,
                PendingIntent.FLAG_UPDATE_CURRENT
        );

        Intent ignoreReminderIntent = new Intent(context, NotificationAlertReceiver.class);
        ignoreReminderIntent.setAction(NotificationAlertReceiver.ACTION_DISMISS_NOTIFICATION);
        PendingIntent ignoreReminderPendingIntent = PendingIntent.getBroadcast(
                context,
                ACTION_IGNORE_PENDING_INTENT_ID,
                ignoreReminderIntent,
                0);

        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel(
                    REMINDER_NOTIFICATION_CHANNEL,
                    context.getString((R.string.channel_name)),
                    NotificationManager.IMPORTANCE_HIGH
            );
            notificationManager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context, REMINDER_NOTIFICATION_CHANNEL)
                .setColor(ContextCompat.getColor(context, R.color.blue_200))
                .setSmallIcon(R.drawable.ic_assignment_black_24dp)
                .setContentTitle(context.getString((R.string.reminder_text)))
                .setContentText(context.getString((R.string.reminder_text)))
                .setStyle(new NotificationCompat.BigTextStyle().bigText(context.getString(R.string.reminder_text)))
                .setDefaults(Notification.DEFAULT_VIBRATE)
                .setContentIntent(startActivityPendingIntent)
                .addAction(R.drawable.ic_cancel_black_24dp, "not now", ignoreReminderPendingIntent)
                .setAutoCancel(true);
        if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O){
            notificationBuilder.setPriority(NotificationCompat.PRIORITY_HIGH);
        }

        notificationManager.notify(TODO_REMINDER_NOTIFICATION_ID, notificationBuilder.build());
    }

    public static void clearAllNotifications(Context context){
        NotificationManager notificationManager = (NotificationManager)context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.cancel(TODO_REMINDER_NOTIFICATION_ID);
    }

}


//Make a loop that runs every minute and checks the current logged in users firebase