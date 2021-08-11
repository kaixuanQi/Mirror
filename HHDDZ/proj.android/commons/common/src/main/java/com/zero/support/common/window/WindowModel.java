package com.zero.support.common.window;

import android.app.Activity;
import android.content.Context;

import androidx.databinding.BaseObservable;

import java.util.LinkedHashSet;
import java.util.Set;

public abstract class WindowModel extends BaseObservable {
    private BaseWindow window;
    private int locationX;
    private int locationY;

    private final Set<Class<? extends Activity>> disableActivity = new LinkedHashSet<>();

    private final Set<Class<? extends Activity>> enableActivity = new LinkedHashSet<>();

    public void disableActivity(Class<? extends Activity> cls) {
        disableActivity.add(cls);
    }

    public void enableActivity(Class<? extends Activity> cls) {
        enableActivity.add(cls);
    }

    public boolean canShow(Class<? extends Activity> cls) {
        if (enableActivity.size() != 0) {
            return enableActivity.contains(cls);
        }
        if (disableActivity.size() != 0) {
            return !disableActivity.contains(cls);
        }
        return isSupportActivity();
    }

    public int getLocationX() {
        return locationX;
    }

    public int getLocationY() {
        return locationY;
    }

    final void detachWindow() {
        window = null;
    }

    final void attachWindow(BaseWindow window) {
        this.window = window;
    }

    public BaseWindow requireWindow() {
        return window;
    }

    public void dismiss() {
        FloatWindowManager.getDefault().dismiss(this);
    }

    public void show() {
        FloatWindowManager.getDefault().show(this);
    }

    public boolean isDismiss() {
        return true;
    }

    public boolean isSupportApp() {
        return true;
    }

    public boolean isSupportActivity() {
        return true;
    }

    public abstract BaseWindow onCreateWindow(Context context, boolean app);

    public String getWindowName() {
        return getClass().getSimpleName();
    }

    void dispatchWindowLocationChanged(int x, int y) {
        boolean changed = false;
        if (locationX != x) {
            changed = true;
            locationX = x;
        }
        if (locationY != y) {
            changed = true;
            locationY = y;
        }
        if (changed) {
            onWindowLocationChanged(x, y);
        }
    }

    /**
     * 当悬浮窗位置发生变化时，记录改位置
     */
    protected void onWindowLocationChanged(int x, int y) {

    }

    public void notifyWindowLocationChanged(int x, int y) {
        boolean changed = false;
        if (this.locationX != x) {
            changed = true;
            this.locationX = x;
        }
        if (this.locationY != y) {
            changed = true;
            this.locationY = y;
        }
        if (changed) {
            if (window != null) {
                window.dispatchWindowLocationChanged(x, y);
            }
        }
    }
}
