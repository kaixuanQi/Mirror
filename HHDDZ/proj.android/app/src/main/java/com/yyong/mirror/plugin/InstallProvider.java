package com.yyong.mirror.plugin;


import android.app.Application;
import android.net.Uri;

import com.yyong.mirror.producer.ProducerManager;
import com.yyong.mirror.version.DownloadManger;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.FileProvider;

import java.io.File;

public class InstallProvider extends FileProvider {
    private static String authority;

    static {
        Application app = AppGlobal.getApplication();
        authority = app.getPackageName() + ":file.provider";
        FileProvider.SimplePathStrategy strategy = new FileProvider.SimplePathStrategy(authority);
        strategy.addRoot("producer", ProducerManager.getDefault().getRoot());
        strategy.addRoot("upgrade", DownloadManger.getDefault().getRoot());
        FileProvider.registerPathStrategy(authority, strategy);
    }

    public static Uri getUriForFile(File file) {
        return getUriForFile(AppGlobal.getApplication(), authority, file);
    }
}
