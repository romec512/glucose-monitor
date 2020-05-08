package com.example.glucose_monitor;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import androidx.core.app.NotificationCompat;

import static android.content.Context.NOTIFICATION_SERVICE;

public class NotificationsBroadcastReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        Intent saveValueIntent = new Intent(context, InputGlucoseActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, saveValueIntent,
                PendingIntent.FLAG_UPDATE_CURRENT);
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.mipmap.ic_launcher)
                        .setContentTitle("Пора сделать замер!")
                        .setContentText("Проведите тест и сохраните значение")
                        .setContentIntent(pendingIntent)
                        .setAutoCancel(true);

        Notification notification = builder.build();

        NotificationManager notificationManager =
                (NotificationManager)context.getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }
}
