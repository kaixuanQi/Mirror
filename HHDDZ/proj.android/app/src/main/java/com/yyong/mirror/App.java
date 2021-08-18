package com.yyong.mirror;

import android.content.Context;


import androidx.multidex.MultiDexApplication;

import com.yyong.mirror.version.VersionMonitor;
import com.zero.support.common.AppGlobal;

public class App extends MultiDexApplication {
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        AppGlobal.initialize(this);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        AppHolder.userGuide.observe(agree -> {
            if (agree){
                BIHelper.initialize(App.this);
                VersionMonitor.inject(SplashActivity.class,StubActivity.class);
            }
        });

    }
}
