package com.yyong.middleware;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.Singleton;

public class ChannelMeta {
    static Singleton<ChannelMeta> singleton = new Singleton<ChannelMeta>() {
        @Override
        protected ChannelMeta create() {
            Context context = AppGlobal.getApplication();
            PackageInfo info = null;
            try {
                info = context.getPackageManager().getPackageInfo(context.getPackageName(), PackageManager.GET_META_DATA);
            } catch (PackageManager.NameNotFoundException e) {
                throw new RuntimeException(e);
            }
            return new ChannelMeta(info);
        }
    };
    public String subChannelId;
    public String mainChannelId;
    public String packageName;
    public long versionCode;
    public String versionName;


    public ChannelMeta(PackageInfo info) {
        this.packageName = info.packageName;
        this.versionCode = info.versionCode;
        this.versionName = info.versionName;
        this.mainChannelId = String.valueOf(info.applicationInfo.metaData.get("MainChId"));
        this.subChannelId = String.valueOf(info.applicationInfo.metaData.get("SubChId"));
    }

    public static ChannelMeta getDefault() {
        return singleton.get();
    }

    public long getMainChannelId() {
        return Long.parseLong(mainChannelId);
    }

    public long getSubChannelId() {
        return Long.parseLong(subChannelId);
    }
}
