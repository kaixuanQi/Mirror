package com.yyong.virutal.api.virtual;

import com.yyong.virtual.api.binder.BinderName;

@BinderName("ActivityLaunchCallback")
public interface ActivityLaunchCallback {
    @BinderName("onLaunch")
    void onLaunch(int result);
}
