package com.zero.support.common.window;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.zero.support.common.ActivityManager;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.Singleton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;

@SuppressWarnings("ALL")
public class FloatWindowManager {
    private static final Singleton<FloatWindowManager> singleton = new Singleton<FloatWindowManager>() {
        @Override
        protected FloatWindowManager create() {
            return new FloatWindowManager();
        }
    };
    private WindowManager manager;
    private Application app;
    private Map<WindowModel, BaseWindow> appWindows = new HashMap<>();
    private Map<WindowModel, WeakHashMap<Activity, BaseWindow>> activityWindows = new HashMap<>();
    private List<WindowModel> models = new ArrayList<>();
    private boolean appMode = false;

    public FloatWindowManager() {
        app = AppGlobal.getApplication();
        manager = (WindowManager) app.getSystemService(Context.WINDOW_SERVICE);
        app.registerActivityLifecycleCallbacks(new FloatActivityCallback());
    }

    public static FloatWindowManager getDefault() {
        return singleton.get();
    }

    private BaseWindow getAppWindow(WindowModel model) {
        BaseWindow window = appWindows.get(model);
        if (window == null) {
            window = model.onCreateWindow(app, true);
            appWindows.put(model, window);
        }
        return window;
    }

    private BaseWindow getActivityWindow(WindowModel model, Activity activity) {
        if (!model.isSupportActivity()) {
            return null;
        }
        WeakHashMap<Activity, BaseWindow> map = activityWindows.get(model);
        if (map == null) {
            map = new WeakHashMap<>();
            activityWindows.put(model, map);
        }
        BaseWindow window = map.get(activity);
        if (window == null) {
            window = model.onCreateWindow(activity, false);
            if (window != null) {
                map.put(activity, window);
            }
        }
        return window;
    }

    @SuppressWarnings("ALL")
    private void show(WindowModel model, Activity activity) {
        if (appMode) {
            return;
        }
        if (activity == null) {
            return;
        }
        BaseWindow window = getActivityWindow(model, activity);
        if (window == null) {
            return;
        }
        model.attachWindow(window);
        window.show(model);
    }

    private void showAppWinowOnly(WindowModel model) {
        if (model.isSupportApp()) {
            dismissActivityWindows(model, null);
            BaseWindow window = getAppWindow(model);
            if (window != null) {
                window.show(model);
            }
        }
    }

    private void dismissWindowOnly(WindowModel model, Activity activity) {
        if (appMode) {
            dismissAppWindows(model);
        } else {
            dismissActivityWindows(model, activity);
        }
    }

    public void dismiss(WindowModel model) {
        dismissAppWindows(model);
        dismissActivityWindows(model, null);
        models.remove(model);
    }

    @SuppressWarnings("ALL")
    private void dismissAppWindows(WindowModel model) {
        BaseWindow window = appWindows.remove(model);
        if (window != null) {
            window.dismiss();
        }
    }

    private void dismissActivityWindows(WindowModel model, Activity activity) {
        WeakHashMap<Activity, BaseWindow> map = activityWindows.get(model);
        if (map == null) {
            return;
        }
        if (activity != null) {
            BaseWindow window = map.get(activity);
            if (window != null) {
                window.dismiss();
            }
            return;
        }
        for (BaseWindow w : map.values()) {
            w.dismiss();
        }
        map.clear();
    }

    @SuppressWarnings("ALL")
    public void show(WindowModel model) {
        if (models.contains(model)) {
            return;
        }
        boolean useApp;
        models.add(model);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            useApp = Settings.canDrawOverlays(app);
        } else {
            useApp = true;
        }
        appMode = useApp;
        if (useApp) {
            showAppWinowOnly(model);
        } else {
            dismissAppWindows(model);
            Activity activity = ActivityManager.getFirstActivity();
            if (activity == null) {
                return;
            }
            if (model.canShow(activity.getClass())) {
                show(model, activity);
            }
        }

    }

    @SuppressWarnings("ALL")
    private class FloatActivityCallback implements Application.ActivityLifecycleCallbacks {

        @Override
        public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {
            for (WindowModel model : models) {
                getActivityWindow(model, activity);
            }

        }

        @Override
        public void onActivityStarted(@NonNull Activity activity) {
            for (WindowModel model : models) {
                show(model, activity);
            }
        }

        @Override
        public void onActivityResumed(@NonNull Activity activity) {

        }

        @Override
        public void onActivityPaused(@NonNull Activity activity) {

        }

        @Override
        public void onActivityStopped(@NonNull Activity activity) {
            for (WindowModel model : models) {
                BaseWindow window = getActivityWindow(model, activity);
                if (window == null) {
                    continue;
                }
                dismissActivityWindows(model, activity);
            }


        }

        @Override
        public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

        }

        @Override
        public void onActivityDestroyed(@NonNull Activity activity) {

        }
    }
}
