package com.excean.virutal.api.virtual;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import java.util.LinkedHashMap;
import java.util.Map;

public class Mirror {
    public int userId;
    public String name;
    public String mirrorPackageName = "";
    public String servicePackageName;
    public long attribute;
    public static final String KEY_SERVICE = "mirror.service";
    public static final String KEY_USER = "mirror.user";
    public static final String KEY_PACKAGE = "mirror.package";

    public static final String KEY_ATTRIBUTE = "mirror.attribute";

    private static final Map<String, Mirror> mirrors = new LinkedHashMap<>();
    private PackageInfo packageInfo;
    /**
     * 被分身应用
     */
    private PackageInfo mirrorPackageInfo;

    public static Mirror getMirror(Context context, String packageName) {
        synchronized (mirrors) {
            Mirror mirror = mirrors.get(packageName);
            if (mirror == null) {
                mirror = new Mirror(context.getPackageManager(), packageName);
                if (mirror.getMirrorPackageInfo() != null) {
                    mirrors.put(packageName, mirror);
                }
            }
            return mirror;
        }
    }


    /**
     * @param pm
     * @param packageName 分身包名
     */
    public Mirror(PackageManager pm, String packageName) {
        try {
            packageInfo = pm.getPackageInfo(packageName, PackageManager.GET_META_DATA);
            Bundle bundle = packageInfo.applicationInfo.metaData;
            if (bundle == null) {
                throw new RuntimeException();
            }
            this.name = String.valueOf(packageInfo.applicationInfo.loadLabel(pm));
            this.servicePackageName = String.valueOf(bundle.get(KEY_SERVICE));
            this.userId = Integer.parseInt(String.valueOf(bundle.get(KEY_USER)));
            this.mirrorPackageName = String.valueOf(bundle.get(KEY_PACKAGE));
            this.attribute = Long.parseLong(String.valueOf(bundle.get(KEY_ATTRIBUTE)));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mirrorPackageInfo = pm.getPackageInfo(mirrorPackageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * @param pm
     */
    public Mirror(PackageManager pm, PackageInfo info) {
        try {
            packageInfo = info;
            this.name = String.valueOf(packageInfo.applicationInfo.loadLabel(pm));
            Bundle bundle = packageInfo.applicationInfo.metaData;
            if (bundle == null) {
                throw new RuntimeException();
            }
            this.servicePackageName = String.valueOf(bundle.get(KEY_SERVICE));
            this.userId = Integer.parseInt(String.valueOf(bundle.get(KEY_USER)));
            this.mirrorPackageName = String.valueOf(bundle.get(KEY_PACKAGE));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mirrorPackageInfo = pm.getPackageInfo(mirrorPackageName, PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public String getMirrorPackageName() {
        return mirrorPackageName;
    }

    public String getServicePackageName() {
        return servicePackageName;
    }

    public PackageInfo getMirrorPackageInfo() {
        return mirrorPackageInfo;
    }
}
