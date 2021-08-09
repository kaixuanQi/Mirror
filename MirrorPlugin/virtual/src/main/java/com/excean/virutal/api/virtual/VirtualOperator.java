package com.excean.virutal.api.virtual;

import com.excean.virtual.api.binder.BinderName;

@BinderName("VirtualOperator")
public interface VirtualOperator {
    @BinderName("startPlugin")
    void startPlugin();

    @BinderName("startPluginWithFlags")
    void startPluginWithFlags(int flags);

    @BinderName("finishSplashActivity")
    void finishSplashActivity();

}
