package com.zero.support.common;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.os.IBinder;

import androidx.annotation.MainThread;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.RequestViewModel;
import com.zero.support.common.window.BaseWindow;
import com.zero.support.work.Observable;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ActivityManager {
    private static final List<Activity> activities = new ArrayList<>();
    private static final List<BaseWindow> windows = new ArrayList<>();
    private static final Map<IBinder, WindowInfo> windowInfoList = new HashMap<>();

    private final static Observable<Activity> mTopActivity = new Observable<>();
    static final Application.ActivityLifecycleCallbacks callbacks = new Application.ActivityLifecycleCallbacks() {
        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            if (activity instanceof AppCompatActivity) {
                activities.add(0, activity);
            }
        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            mTopActivity.setValue(activity);
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            if (mTopActivity.getValue() == activity) {
                mTopActivity.setValue(null);
            }
        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {
            if (activity instanceof AppCompatActivity) {
                activities.remove(activity);
            }
        }
    };

    public static void createActivityWindow(IBinder token, String name) {
        windowInfoList.put(token, new WindowInfo(WindowInfo.TYPE_ACTIVITY, name));
    }

    public static void createDialogWindow(IBinder token, String name) {
        windowInfoList.put(token, new WindowInfo(WindowInfo.TYPE_DIALOG, name));
    }

    public static void createAppWindow(IBinder token, String name) {
        windowInfoList.put(token, new WindowInfo(WindowInfo.TYPE_APP, name));
    }

    public static void onCreateWindow(BaseWindow window) {
        windows.add(window);
    }

    public static void onDismissWindow(BaseWindow window) {
        windows.remove(window);
    }

    public static void destroyWindow(IBinder token) {
        windowInfoList.remove(token);
    }

    @MainThread
    public static void finishAll() {
        for (Activity activity : activities) {
            if (!activity.isFinishing()) {
                activity.finish();
            }
        }
    }

    @MainThread
    public static Activity getFirstActivity() {
        for (Activity activity : activities) {
            if (activity.isFinishing()) {
                continue;
            }
            return activity;
        }
        return null;
    }

    @MainThread
    public static CommonActivity getFirstSupportActivity() {
        for (Activity activity : activities) {
            if (activity.isFinishing()) {
                continue;
            }
            if (activity instanceof CommonActivity) {
                return (CommonActivity) activity;
            }
        }
        return null;
    }

    public static RequestViewModel getFirstRequestViewModel() {
        CommonActivity activity = getFirstSupportActivity();
        if (activity == null) {
            return null;
        }
        return activity.attachSupportViewModel(RequestViewModel.class);
    }

    @MainThread
    public static BaseWindow getFirstWindow() {
        for (BaseWindow window : windows) {
            return window;
        }
        return null;
    }

    public static WindowInfo getTopWindowInfo() {
        Activity activity = getTopActivity();
        IBinder token = null;

        if (activity != null) {
            token = activity.getWindow().getDecorView().getWindowToken();
        } else {
            BaseWindow window = getFirstWindow();
            if (window != null) {
                token = window.getRoot().getWindowToken();
            }
        }
        return getWindowInfo(token);
    }

    @MainThread
    public static Activity getTopActivity() {
        return mTopActivity.getValue();
    }

    @MainThread
    public static Observable<Activity> getTopObservable() {
        return mTopActivity;
    }

    public static WindowInfo getWindowInfo(IBinder token) {
        if (token == null) {
            return null;
        }
        return windowInfoList.get(token);
    }

    public static class WindowInfo {
        public static final int TYPE_ACTIVITY = 0;
        public static final int TYPE_DIALOG = 1;
        public static final int TYPE_APP = 2;
        public int type;
        public String name;

        public WindowInfo(int type, String name) {
            this.type = type;
            this.name = name;
        }

        public String getTypeName() {
            if (type == TYPE_ACTIVITY) {
                return "??????";
            } else if (type == TYPE_DIALOG) {
                return "?????????";
            } else if (type == TYPE_APP) {
                return "?????????";
            }
            return String.valueOf(type);
        }

    }
}
