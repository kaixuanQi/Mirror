package com.yyong.mirror.plugin;

import android.app.Application;
import android.content.Context;
import android.util.Log;

import com.yyong.mirror.plugin.holder.BuildConfig;
import com.yyong.virutal.api.virtual.Launcher;
import com.yyong.virutal.api.virtual.PluginManagerWrapper;
import com.yyong.virutal.api.virtual.SdkManager;
import com.yyong.virutal.api.virtual.Virtual;

import java.io.File;
import java.io.IOException;

public class App extends Application {

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        PluginManagerWrapper.setProguard(BuildConfig.PROGUARD);
        Virtual.initialize(this);
        File sdkDir = new File(getCacheDir().getParentFile(), "sdk");
        SdkManager manager = SdkManager.initialize(this);
        Launcher launcher = manager.getLauncher(sdkDir, "i8wcq8p73ijb");
        if (Virtual.isMain()) {
            VirtualOperatorNative.initialize(this);
            if (launcher.isNeedInstall()) {
                manager.install(launcher);
            }
        }
        installToParent(launcher.getCurrentPath());
        PluginManagerWrapper.getInstance().attachBaseContext(this, base);

    }


    public void installToParent(String dir) {
        try {
            String dex = new File(dir, "i8wcq8p73ijb.jar").getCanonicalPath();
            String lib = new File(new File(dir, "lib"), getAbiName()).getCanonicalPath();
//            DexClassLoader loader = new DexClassLoader(dex, dir,lib, getClassLoader().getParent());
//
//            Log.e("mirror0", "installToParent: " + dex + " " + lib + getClassLoader());
//            Field field = ClassLoader.class.getDeclaredField("parent");
//            field.setAccessible(true);
//            field.set(App.class.getClassLoader(), loader);
            HackUtil.addJarToClassLoaderList(dex, dir, new File(dir, "lib").getCanonicalPath(), getClassLoader(), false, true);
//            Class.forName("com.excelliance.kxqp.platform.ApplicationProxy");
        } catch (Throwable e) {
            e.printStackTrace();
            Log.e("mirror0", "installToParent: " + getClassLoader());
            throw new RuntimeException(e);
        }
    }

    String getAbiName() throws IOException {
        return new File(getApplicationInfo().nativeLibraryDir).getCanonicalFile().getName();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        PluginManagerWrapper.getInstance().onCreate(this);
    }
}
