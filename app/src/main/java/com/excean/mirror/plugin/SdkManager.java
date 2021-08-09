package com.excean.mirror.plugin;

import android.app.ActivityManager;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.excean.virutal.api.virtual.FileUtils;
import com.excean.virutal.api.virtual.Launcher;
import com.excean.virutal.api.virtual.Virtual;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.Singleton;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SdkManager {
    private final String processName;
    private PackageInfo packageInfo;
    private final Map<String, Launcher> launchers = new HashMap<>();
    private final Application app;
    private boolean main;
    private static Singleton<SdkManager> singleton = new Singleton<SdkManager>() {
        @Override
        protected SdkManager create() {
            return new SdkManager();
        }
    };
    public String installProcessName;

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public static SdkManager getDefault() {
        return singleton.get();
    }

    public Application getApplication() {
        return app;
    }

    public SdkManager() {
        this.app = AppGlobal.getApplication();
        this.processName = Virtual.getProcessName();
        try {
            this.packageInfo = app.getPackageManager().getPackageInfo(app.getPackageName(), PackageManager.GET_META_DATA);
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
                Log.e("xfg", "getLauncher: "+getInstallTime() );
                launcher = new Launcher(root, name, getInstallTime());
                launchers.put(name, launcher);
                if (launcher.isNeedInstall()) {
                    File next = launcher.getAvailableNext();
                    next.mkdirs();
                    File target = new File(next, name+".jar");
                    try {
                        FileUtils.extractAsset(app, "sdk/" + name + ".jar", target);
                        FileUtils.extractFile(target, "lib", target.getParentFile());
                        launcher.switchToNext(next.getCanonicalPath());
                        launcher.removePrevious();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                }
            }
            return launcher;
        }
    }


    public static String getProcessName(Context context, int pid) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> list = am.getRunningAppProcesses();
        if (list == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : list) {
            if (pid == processInfo.pid) {
                return processInfo.processName;
            }
        }
        return null;
    }

    public long getInstallTime() {
        return new File(getPackageInfo().applicationInfo.sourceDir).lastModified();
    }
}
