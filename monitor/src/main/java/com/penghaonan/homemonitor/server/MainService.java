package com.penghaonan.homemonitor.server;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.penghaonan.homemonitor.connectionservice.R;

import static android.app.PendingIntent.FLAG_UPDATE_CURRENT;

public class MainService extends Service {
    private Notification mNotification;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (mNotification == null) {
            showNotif();
        }
        return START_STICKY;
    }

    private void showNotif() {
        Notification.Builder builder = new Notification.Builder(this);
        builder.setTicker(getString(R.string.service_start));
        builder.setContentTitle(getString(R.string.app_name));
        builder.setContentText(getString(R.string.service_notif_content_msg));
        Intent intent = new Intent(this, MainActivity.class);
        PendingIntent pi = PendingIntent.getActivity(this, 1, intent, FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pi);
        builder.setOngoing(true);
        mNotification = builder.build();
        startForeground(1, mNotification);
    }
}
