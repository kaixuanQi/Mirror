package com.yyong.virutal.api.virtual;

import com.yyong.virtual.api.binder.BinderName;

import java.io.File;

@BinderName("VirtualApi")
public interface IVirtualApi {
   String getVirtualEnginePath(String packageName);

   @BinderName("prepareSdk")
   boolean prepareSdk(int version,String name,File dir);
}
