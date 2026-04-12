package com.salvi.atividadetres;

import android.app.Activity;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.os.Build;

import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;

import static android.Manifest.permission.POST_NOTIFICATIONS;

import org.jspecify.annotations.Nullable;

public class NotificationExecutor {

    private static final String CHANNEL_ID = "default_channel";
    private static final String CHANNEL_NAME = "Notificações";

    public static void sendNotification(Context context, String title, String message) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(context, POST_NOTIFICATIONS)
                    != PackageManager.PERMISSION_GRANTED) {

                if (context instanceof Activity) {
                    ActivityCompat.requestPermissions(
                            (Activity) context,
                            new String[]{POST_NOTIFICATIONS},
                            1
                    );
                }
                return;
            }
        }

        createChannel(context);

        NotificationManager manager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        long[] timing = {500, 500};
        NotificationCompat.Builder builder =
                new NotificationCompat.Builder(context, CHANNEL_ID)
                        .setSmallIcon(R.drawable.ic_notification_icon)
                        .setContentTitle(title)
                        .setContentText(message)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setVibrate(timing)
                        .setAutoCancel(true);

        manager.notify((int) System.currentTimeMillis(), builder.build());
    }

    private static void createChannel(Context context) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );

            NotificationManager manager =
                    (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

            manager.createNotificationChannel(channel);
        }
    }
}