package com.excean.mirror.plugin;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;

import com.excean.virutal.api.virtual.Mirror;
import com.excean.virutal.api.virtual.PluginManagerWrapper;
import com.excean.virutal.api.virtual.VirtualOperator;

@SuppressWarnings("all")
public class VirtualOperatorNative implements VirtualOperator {
    public static VirtualOperatorNative INSTANCE;
    private final Mirror mirror;
    private Activity activity;


    public static void initialize(Context context) {
        INSTANCE = new VirtualOperatorNative(context);
    }

    public void attachActivity(Activity activity) {
        this.activity = activity;
    }

    public void detach() {
        this.activity = null;
    }

    public VirtualOperatorNative(Context context) {
        mirror = new Mirror(context.getPackageManager(), context.getPackageName());
    }

    public Mirror getMirror() {
        return mirror;
    }

    @Override
    public void startPlugin() {
//        Virtual.connect();
        PackageInfo packageInfo = PluginManagerWrapper.getInstance().getPackageInfo(mirror.getMirrorPackageName(), PackageManager.GET_META_DATA);
        if (mirror.getMirrorPackageInfo() == null) {
            //应用被卸载
            return;
        }
        if (packageInfo == null || !TextUtils.equals(packageInfo.applicationInfo.publicSourceDir, mirror.getMirrorPackageInfo().applicationInfo.publicSourceDir)) {
            int ret = PluginManagerWrapper.getInstance().installPackage(mirror.userId, mirror.mirrorPackageName, PluginManagerWrapper.INSTALL_ARG_IS_PKGNAME | PluginManagerWrapper.INSTALL_IGNORE_PERMISSION_REQ | PluginManagerWrapper.INSTALL_REPLACE_EXISTING);
            packageInfo = PluginManagerWrapper.getInstance().getPackageInfo(mirror.getMirrorPackageName(), PackageManager.GET_META_DATA);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(mirror.getMirrorPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        PluginManagerWrapper.getInstance().preStartProcess(mirror.userId,mirror.getMirrorPackageName(),0,intent);
        int ret = PluginManagerWrapper.getInstance().startActivity(mirror.userId, intent);
    }

    @Override
    public void startPluginWithFlags(int flags) {
    }

    @Override
    public void finishSplashActivity() {
        final Activity activity = this.activity;
        if (activity != null) {
            activity.finish();
        }
    }
}
