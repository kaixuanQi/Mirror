package com.excean.virutal.api.virtual;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class SdkManager {
    private final String processName;
    private PackageInfo packageInfo;
    private final Map<String, Launcher> launchers = new HashMap<>();
    private final Application app;
    private boolean main;

    public String installProcessName;

    private static SdkManager INSTANCE;

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public static SdkManager getDefault() {
        return INSTANCE;
    }

    public static SdkManager initialize(Application app) {
        INSTANCE = new SdkManager(app);
        return INSTANCE;
    }

    public Application getApplication() {
        return app;
    }

    public SdkManager(Application app) {
        this.app = app;
        this.processName = Virtual.getProcessName();
        try {
            this.packageInfo = this.app.getPackageManager().getPackageInfo(this.app.getPackageName(), PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        this.main = TextUtils.equals(processName, packageInfo.applicationInfo.processName);
    }


    public boolean isMainProcess() {
        return main;
    }

    public String getProcessName() {
        return processName;
    }

    public Launcher getLauncher(File root, String name) {
        synchronized (launchers) {
            Launcher launcher = launchers.get(name);
            if (launcher == null) {
                Log.e("xfg", "getLauncher: " + getInstallTime());
                launcher = new Launcher(root, name, getInstallTime());
                launchers.put(name, launcher);
            }
            return launcher;
        }
    }

    public void install(Launcher launcher) {
        File next = launcher.getAvailableNext();
        next.mkdirs();
        File target = new File(next, launcher.getName() + ".jar");
        try {
            FileUtils.extractAsset(app, "sdk/" + launcher.getName() + ".jar", target);
            FileUtils.extractFile(target, "lib", target.getParentFile());
            launcher.switchToNext(next.getCanonicalPath());
            launcher.removePrevious();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public long getInstallTime() {
        return new File(getPackageInfo().applicationInfo.sourceDir).lastModified();
    }
}
