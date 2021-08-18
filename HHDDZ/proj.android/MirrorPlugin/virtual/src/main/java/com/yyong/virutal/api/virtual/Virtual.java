package com.yyong.virutal.api.virtual;

import android.app.ActivityThread;
import android.app.Application;
import android.content.ContentProviderClient;
import android.os.Build;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;

import com.yyong.virtual.api.binder.Binder;

public class Virtual {
    public static final String AUTHORITY = ":mirror.service";
    private static Application app;
    private static IBinder service;
    private static String processName;
    private static ContentProviderClient client;

    public static void connect() {
        if (client!=null){
            return;
        }
        Mirror mirror = Mirror.getMirror(app, app.getPackageName());
        client = app.getContentResolver().acquireContentProviderClient(mirror.servicePackageName + AUTHORITY);
        try {
            service = client.call("@Service", null, null).getBinder("binder");
        } catch (RemoteException e) {
            throw new RuntimeException(e);
        }
    }

    public static IVirtualApi withApi() {
        return Binder.asInterface(service, IVirtualApi.class);
    }

    public static Application getApp() {
        return app;
    }

    public static void initialize(Application app) {
        Virtual.app = app;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Virtual.processName = Application.getProcessName();
        } else {
            Virtual.processName = ActivityThread.currentProcessName();
        }
    }

    public static String getProcessName() {
        return processName;
    }

    public static boolean isMain() {
        return TextUtils.equals(processName, app.getApplicationInfo().processName);
    }
}
