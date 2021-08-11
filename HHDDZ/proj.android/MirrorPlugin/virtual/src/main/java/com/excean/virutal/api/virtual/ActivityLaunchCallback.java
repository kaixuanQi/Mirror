package com.excean.virutal.api.virtual;

import com.excean.virtual.api.binder.BinderName;

@BinderName("ActivityLaunchCallback")
public interface ActivityLaunchCallback {
    @BinderName("onLaunch")
    void onLaunch(int result);
}
