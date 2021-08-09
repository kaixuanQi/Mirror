package com.excean.mirror.plugin;

import android.app.Application;
import android.util.Log;

import com.excean.virutal.api.virtual.IVirtualApi;
import com.excean.virutal.api.virtual.Launcher;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.Singleton;

import java.io.File;

public class VirtualService implements IVirtualApi {
    private static Singleton<IVirtualApi> singleton = new Singleton<IVirtualApi>() {
        @Override
        protected IVirtualApi create() {
            return new VirtualService(AppGlobal.getApplication());
        }
    };
    private File root;

    public static IVirtualApi getDefault() {
        return singleton.get();
    }

    public VirtualService(Application application) {

    }


    @Override
    public String getVirtualEnginePath(String packageName) {
        return null;
    }

    @Override
    public boolean prepareSdk(int version, String name, File dir) {
        try {
            Launcher launcher = SdkManager.getDefault().getLauncher(dir, name);

            return launcher.getCurrentPath() != null;
        } catch (Exception e) {
            e.printStackTrace();

        }
        return false;
    }
}
