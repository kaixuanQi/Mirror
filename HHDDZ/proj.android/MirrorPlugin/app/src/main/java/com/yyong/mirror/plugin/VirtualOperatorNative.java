package com.yyong.mirror.plugin;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.text.TextUtils;
import android.util.Log;

import com.yyong.virutal.api.virtual.ActivityLaunchCallback;
import com.yyong.virutal.api.virtual.Mirror;
import com.yyong.virutal.api.virtual.PluginManagerWrapper;
import com.yyong.virutal.api.virtual.VirtualOperator;

import java.util.Arrays;
import java.util.List;

@SuppressWarnings("all")
public class VirtualOperatorNative implements VirtualOperator {
    public static VirtualOperatorNative INSTANCE;
    private final Mirror mirror;
    private Activity activity;
    private int from;


    public static void initialize(Context context) {
        INSTANCE = new VirtualOperatorNative(context);
    }

    public void attachActivity(Activity activity) {
        this.activity = activity;
        this.from =  activity.getIntent().getIntExtra("from", 0);
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
    public void startPlugin(long attribute, String[] paths, ActivityLaunchCallback callback) {
        PackageInfo packageInfo = PluginManagerWrapper.getInstance().getPackageInfo(mirror.getMirrorPackageName(), PackageManager.GET_META_DATA);
        if (mirror.getRemoteMirrorPackageInfo() == null) {
            //应用被卸载
            Log.e("mirror", "startPlugin: " + mirror.getMirrorPackageName());
            callback.onLaunch(mirror.getMirrorPackageName(),from,-100);
            return;
        }
        boolean firstInstall = packageInfo == null;
        if (packageInfo == null || !TextUtils.equals(packageInfo.applicationInfo.publicSourceDir, mirror.getMirrorPackageInfo().applicationInfo.publicSourceDir)) {
            int ret = PluginManagerWrapper.getInstance().installPackage(mirror.userId, mirror.mirrorPackageName, PluginManagerWrapper.INSTALL_ARG_IS_PKGNAME | PluginManagerWrapper.INSTALL_IGNORE_PERMISSION_REQ | PluginManagerWrapper.INSTALL_REPLACE_EXISTING);
            packageInfo = PluginManagerWrapper.getInstance().getPackageInfo(mirror.getMirrorPackageName(), PackageManager.GET_META_DATA);
            if (paths != null) {
                Log.e("mirror", "install plugin: " + mirror.getMirrorPackageName() + " with " + Arrays.toString(paths));
                PluginManagerWrapper.getInstance().setExternalStorageRedirectPaths(mirror.userId, mirror.mirrorPackageName, paths);
            }
        }
        if (firstInstall) {
            PluginManagerWrapper.getInstance().fakeDeviceInfo(mirror.userId, PluginManagerWrapper.FAKE_MODE_FORCE_ALL);
        }
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.setPackage(mirror.getMirrorPackageName());
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        if (attribute != 0) {
            Log.e("mirror", "startPlugin: " + mirror.getMirrorPackageName() + " with " + attribute);
            PluginManagerWrapper.getInstance().updatePackageCapFlag(mirror.userId, mirror.mirrorPackageName, attribute, false);
        }
        Log.e("mirror", "startPlugin: begain start");
        ActivityManager.RunningAppProcessInfo processInfo = isRunning(mirror.mirrorPackageName);
        int ret = PluginManagerWrapper.getInstance().startActivity(mirror.userId, intent);
        if (ret >= 0 && processInfo==null) {
            Log.e("mirror", "startPlugin: wating start");
            UIWrapperProvider.obtainLock(mirror.mirrorPackageName).block(60 * 1000);
            Log.e("mirror", "startPlugin: success");
        }else {
            Log.e("mirror","wechat is running "+ret+"  "+processInfo.processName+"  "+processInfo.pid+" "+Arrays.toString(processInfo.pkgList)+" "+processInfo.uid);
        }
        if (callback != null) {
            callback.onLaunch(mirror.getMirrorPackageName(),from,ret);
        }
    }

    private ActivityManager.RunningAppProcessInfo isRunning(String processName) {
        List<ActivityManager.RunningAppProcessInfo> list = PluginManagerWrapper.getInstance().getRunningAppProcesses(mirror.userId);
        if (list == null) {
            return null;
        }
        for (ActivityManager.RunningAppProcessInfo processInfo : list) {
            if (TextUtils.equals(processInfo.processName, processName)) {
                return processInfo;
            }
        }
        return null;
    }


    @Override
    public void finishSplashActivity() {
        final Activity activity = this.activity;
        if (activity != null) {
            activity.finish();
        }
    }
}
