package com.yyong.virutal.api.virtual;

import com.yyong.virtual.api.binder.BinderName;

@BinderName("VirtualOperator")
public interface VirtualOperator {
    @BinderName("startPlugin")
    void startPlugin(long attribute, String[] paths, ActivityLaunchCallback callback);

    @BinderName("finishSplashActivity")
    void finishSplashActivity();

}
