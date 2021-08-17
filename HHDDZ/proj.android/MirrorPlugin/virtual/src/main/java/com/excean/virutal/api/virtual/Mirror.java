package com.excean.virutal.api.virtual;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;

import java.util.LinkedHashMap;
import java.util.Map;

public class Mirror {

    public int userId;
    public String name;
    public String mirrorPackageName = "";
    public String servicePackageName;
    public String mirrorName;
    public long attribute;
    public boolean obb;
    public static final String KEY_SERVICE = "mirror.service";
    public static final String KEY_USER = "mirror.user";
    public static final String KEY_PACKAGE = "mirror.package";

    public static final String KEY_ATTRIBUTE = "mirror.attribute";
    public static final String KEY_OBB = "mirror.obb";
    public static final String KEY_LABEL = "mirror.label";
    private static final Map<String, Mirror> mirrors = new LinkedHashMap<>();
    private PackageInfo packageInfo;
    /**
     * 被分身应用
     */
    private PackageInfo mirrorPackageInfo;
    private PackageManager pm;

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
            this.pm = pm;
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
            this.obb = Boolean.parseBoolean(String.valueOf(bundle.get(KEY_OBB)));
            this.mirrorName = String.valueOf(bundle.get(KEY_LABEL));
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            mirrorPackageInfo = pm.getPackageInfo(mirrorPackageName, PackageManager.GET_META_DATA);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            Log.d("mirror", "Mirror error: " + e.getMessage());
        }
    }

    /**
     * @param pm
     */
    public Mirror(PackageManager pm, PackageInfo info) {
        try {
            this.pm = pm;
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

    public PackageInfo getRemoteMirrorPackageInfo() {
        try {
            mirrorPackageInfo = pm.getPackageInfo(mirrorPackageName, PackageManager.GET_META_DATA);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mirrorPackageInfo;
    }
}
