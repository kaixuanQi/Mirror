package com.excean.mirror.version;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;

import com.excean.mirror.MainActivity;
import com.excean.mirror.R;
import com.zero.support.work.Observer;
import com.zero.support.work.Snapshot;

public class DownloadService extends Service {
    public static final int ID = 1;
    public String CHANNEL;
    private NotificationManager manager;
    private Notification.Builder downloadNotification;
    private final Observer<Snapshot> observer = new Observer<Snapshot>() {
        @Override
        public void onChanged(Snapshot snapshot) {
            if (snapshot.isRunning()) {
                showProgressNotification(snapshot);
            } else if (snapshot.isFinished()) {
                if (snapshot.isOK()) {
                    manager.cancel(ID);
                }
                showProgressNotification(snapshot);
            }
        }
    };

    @Override
    public void onCreate() {
        super.onCreate();
        CHANNEL = getPackageName() + "-download-service-notification-channel";
        manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        UpgradeOperation.getDefault().snapshot().observe(observer);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        UpgradeOperation.getDefault().snapshot().remove(observer);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        showForegroundNotification(intent);
        if (downloadNotification == null) {
            downloadNotification = createDownloadNotification(intent);
        }
        return super.onStartCommand(intent, flags, startId);
    }

    private void showProgressNotification(Snapshot snapshot) {

        if (downloadNotification == null) {
            return;
        }

        if (snapshot.isRunning()){
            downloadNotification.setProgress(100, snapshot.progress().progress(), false);
            manager.notify(ID, downloadNotification.build());
        }else if (snapshot.isFinished()){
            if (snapshot.isOK()){
                downloadNotification.setContentText(getString(R.string.dialog_install_content));
                Intent intent = new Intent(this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
                downloadNotification.setContentIntent(PendingIntent.getActivity(this,-1,intent,PendingIntent.FLAG_CANCEL_CURRENT));
                downloadNotification.setProgress(0,0,false);
                manager.notify(ID, downloadNotification.build());
            }
        }

    }

    private Notification.Builder createDownloadNotification(Intent intent) {
        String title = intent.getStringExtra("notification_title");
        String subtext = intent.getStringExtra("notification_subtext");
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            builder = new Notification.Builder(this, CHANNEL);
        } else {
            builder = new Notification.Builder(this);
            builder = builder.setPriority(Notification.PRIORITY_MIN);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher).setOngoing(false);

        if (title == null) {
            title = "Downloading additional file";
        }

        builder = builder.setContentTitle(title);
        if (subtext == null) {
            subtext = "Transferring";
        }
        builder.setSubText(subtext);
        if (Build.VERSION.SDK_INT >= 21) {
            int color = intent.getIntExtra("notification_color", 0);
            if (color != 0) {
                builder.setColor(color).setVisibility(Notification.VISIBILITY_SECRET);
            }
        }
        if (Build.VERSION.SDK_INT >= 26) {
            this.createChannel(intent.getStringExtra("notification_channel_name"));
        }
        return builder;
    }

    private final synchronized void showForegroundNotification(Intent intent) {
        String title = intent.getStringExtra("notification_title");
        String subtext = intent.getStringExtra("notification_subtext");
        long timeout = intent.getLongExtra("notification_timeout", 600000L);
        PendingIntent pendingIntent = (PendingIntent) intent.getParcelableExtra("notification_on_click_intent");
        Notification.Builder builder;
        if (Build.VERSION.SDK_INT >= 26) {
            builder = new Notification.Builder(this, CHANNEL);
            builder = builder.setTimeoutAfter(timeout);
        } else {
            builder = new Notification.Builder(this);
            builder = builder.setPriority(Notification.PRIORITY_MIN);
        }


        if (pendingIntent != null) {
            builder.setContentIntent(pendingIntent);
        }

        builder.setSmallIcon(R.mipmap.ic_launcher).setOngoing(false);

        if (title == null) {
            title = "Downloading additional file";
        }

        builder = builder.setContentTitle(title);
        if (subtext == null) {
            subtext = "Transferring";
        }
        builder.setSubText(subtext);
        if (Build.VERSION.SDK_INT >= 21) {
            int color = intent.getIntExtra("notification_color", 0);
            if (color != 0) {
                builder.setColor(color).setVisibility(Notification.VISIBILITY_SECRET);
            }
        }
        Notification notification = builder.build();
        if (Build.VERSION.SDK_INT >= 26) {
            this.createChannel(intent.getStringExtra("notification_channel_name"));
        }

        this.startForeground(ID, notification);
    }

    private synchronized void stopService() {
        this.stopForeground(true);
        this.stopSelf();
    }

    @TargetApi(26)
    private synchronized void createChannel(@Nullable String name) {
        if (name == null) {
            name = "File downloads by Mirror";
        }
        NotificationChannel channel = new NotificationChannel(CHANNEL, name, NotificationManager.IMPORTANCE_LOW);
        manager.createNotificationChannel(channel);
    }
}
