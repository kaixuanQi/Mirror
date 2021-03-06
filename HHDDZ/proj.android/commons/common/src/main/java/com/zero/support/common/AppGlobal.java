package com.zero.support.common;

import android.annotation.SuppressLint;
import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.widget.Toast;

import androidx.lifecycle.Observer;

import com.zero.support.common.observable.SingleLiveEvent;
import com.zero.support.common.util.Preferences;
import com.zero.support.common.util.SharedPreferenceObservable;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.NetObservable;
import com.zero.support.work.ObjectManager;

import java.io.File;

public class AppGlobal {
    private static final ObjectManager<String, SharedPreferenceObservable<?>> sharedPreferences = new ObjectManager<>();
    private static Application app;
    private static File preferenceDir;
    private final static SingleLiveEvent<String> message = new SingleLiveEvent<>();
    private static Toast toast;

    static {
        message.observeForever(new Observer<String>() {
            @Override
            public void onChanged(String s) {
                if (toast!=null){
                    toast.cancel();
                }
                if (s!=null){
                    toast= Toast.makeText(app, s, Toast.LENGTH_SHORT);
                    toast.show();
                }
            }
        });
    }

    private static final ObjectManager<String, Preferences> preferences = new ObjectManager<>(new ObjectManager.Creator<String, Preferences>() {
        @Override
        public Preferences creator(String key) {
            if (key.startsWith("/")) {
                return new Preferences(new File(key));
            }
            return new Preferences(new File(preferenceDir, key));
        }
    });
    private static final ObjectManager<String, Preferences> encryptPreferences = new ObjectManager<>(new ObjectManager.Creator<String, Preferences>() {
        @Override
        public Preferences creator(String key) {
            if (key.startsWith("/")) {
                return new Preferences(new File(key), true, true);
            }
            return new Preferences(new File(preferenceDir, key), true, true);
        }
    });
    @SuppressLint("StaticFieldLeak")
    private static NetObservable netObservable;

    public static void initialize(Application app) {
        AppGlobal.app = app;
        app.registerActivityLifecycleCallbacks(ActivityManager.callbacks);
        AppGlobal.preferenceDir = new File(app.getFilesDir(), "preferences");
    }


    public static Application getApplication() {
        return app;
    }


    public static SharedPreferences sharedPreferences(String group) {
        return app.getSharedPreferences(group, Context.MODE_PRIVATE);
    }

    public static Preferences preferences(String group) {
        return preferences.opt(group);
    }

    public static Preferences encryptPreferences(String group) {
        return encryptPreferences.opt(group);
    }


    @SuppressWarnings("ALL")
    public static <T> SharedPreferenceObservable<T> sharedPreferences(String group, String key, T value) {
        String realKey = group + key;
        synchronized (sharedPreferences) {
            @SuppressWarnings("ALL")
            SharedPreferenceObservable<T> observable = (SharedPreferenceObservable<T>) sharedPreferences.get(realKey);
            if (observable == null) {
                Class cls;
                if (value == null) {
                    cls = String.class;
                } else {
                    cls = value.getClass();
                }
                observable = new SharedPreferenceObservable<>(group, key, cls, value);
                sharedPreferences.put(realKey, observable);
            }
            return observable;
        }
    }

    public static synchronized NetObservable netStatus() {
        if (netObservable==null){
            AppGlobal.netObservable = new NetObservable(app);
        }
        return netObservable;
    }

    public static void sendMessage(String msg) {
        if (AppExecutor.isMainThread()) {
            message.setValue(msg);
        } else {
            message.postValue(msg);
        }
    }
    public static void cancelMessage() {
       sendMessage(null);
    }

}
