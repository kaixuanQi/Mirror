package com.yyong.virutal.api.virtual;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.util.Log;

import java.io.File;

/**
 * 基于安装时间的目录管理器，只维护两个目录
 */
public class Launcher {
    private Preferences preferences;
    private boolean needInstall;
    private boolean firstInstall;
    private String name;
    private long realInstallTime;
    private File root;


    public Launcher(File root, String name, long token) {
        this(new File(root, name + ".json"), root, name, token);
    }

    public Launcher(File launcher, File root, String name, long token) {
        this.name = name;
        this.root = root;
        preferences = new Preferences(launcher, false);
        long installTime = preferences.getLong("installTime", 0);
        long firstInstallTime = preferences.getLong("firstInstallTime", 0);
        realInstallTime = token;
        needInstall = realInstallTime != installTime;
        firstInstall = firstInstallTime == 0;
    }

    public boolean isInstall() {
        return getCurrentPath() != null;
    }

    public File getAvailableNext() {
        if (getNextPath() != null) {
            return null;
        }
        String currentPath = getCurrentPath();
        if (currentPath == null) {
            return new File(root, name + "-1");
        }
        File current = new File(currentPath);
        if (TextUtils.equals(name + "-1", current.getName())) {
            return new File(root, name + "-2");
        } else {
            return new File(root, name + "-1");
        }
    }

    public String getName() {
        return name;
    }

    public String getNextPath() {
        return preferences.getString("next", null);
    }

    public void setNext(String next) {
        preferences.putString("next", next);
    }


    public String getCurrentPath() {
        return preferences.getString("current", null);
    }


    public String getPreviousPath() {
        return preferences.getString("previous", null);
    }

    public boolean isNeedInstall() {
        return needInstall;
    }

    @SuppressLint("ApplySharedPref")
    public void switchToNext(String next) {
        SharedPreferences.Editor editor = preferences.edit();
        if (firstInstall) {
            editor.putLong("firstInstallTime", realInstallTime);
        }
        if (needInstall) {
            editor.putLong("installTime", realInstallTime);
        }
        editor.putString("previous", getCurrentPath())
                .putString("current", next)
                .putString("next", null).commit();
    }


    public void removePrevious() {
        String previous = getPreviousPath();
        if (previous != null) {
            File scratch = new File(previous);
            if (FileUtils.deleteQuietly(scratch)) {
                if (scratch.exists()) {
                    Log.e("launcher", "removePrevious: fail");
                }
            }
        }
        preferences.putString("previous", null);
    }

    public boolean isFirstInstall() {
        return firstInstall;
    }


}
