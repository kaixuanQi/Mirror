package com.yyong.mirror.plugin;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ConditionVariable;
import android.text.TextUtils;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class UIWrapperProvider extends ContentProvider {
    public static final String METHOD_SPLASH_START = "method.splash.start";
    public static final String METHOD_SPLASH_STOP = "method.splash.stop";
    public static final String METHOD_GET_MMAUTOSEND_MSG = "method.get.autosendmsg";
    public static final String METHOD_GET_MMAUTOSEND_FILTER = "method.get.autosendfilter";

    public static final String METHOD_UIEVENT = "method.uievent";

    public static final String UIEVENT_ARG_EVENT = "extra.uievent.arg.event";
    public static final String UIEVENT_ARG_1 = "extra.uievent.arg.1";
    public static final String UIEVENT_ARG_2 = "extra.uievent.arg.2";
    public static final String UIEVENT_ARG_3 = "extra.uievent.arg.3";
    public static final String UIEVENT_RESULT_CODE = "extra.uievent.result.code";
    public static final String UIEVENT_RESULT = "extra.uievent.result";

    public static final int MODE_ALLOWED = 0;
    public static final int MODE_IGNORED = 1;

    private static final boolean DEBUG = true;
    public static final String TAG = "UIWrapperProvider";
    private static final int SPLASH_START = 0;
    private static final int SPLASH_STOP = 1;

    private String lastComponentName = null;
    private String lastPkgName = null;
    private int lastUid = -1;
    private long lastHandleTime = 0;
    public static final Map<String, ConditionVariable> locks = new HashMap<>();

    public static ConditionVariable obtainLock(String pkg){
        synchronized (locks){
            ConditionVariable variable = locks.get(pkg);
            if (variable==null){
                variable = new ConditionVariable();
                locks.put(pkg,variable);
            }
            return variable;
        }
    }

    @Override
    public Bundle call(String method, String arg, Bundle extras) {
        Log.d(TAG, "call: method = " + method + ", " + arg + ", " + extras);
        if (method.equals(METHOD_SPLASH_START)) {
            log("start send msg..." + arg);
            //Message msg = handler.obtainMessage(SPLASH_START, arg);
            //handler.sendMessage(msg);
        } else if (method.equals(METHOD_SPLASH_STOP)) {
            log("stop send msg..." + arg + ", " + extras);
            synchronized (locks) {
                String appInfo = String.valueOf(arg);
                String packageName = null;
                if (appInfo.contains(":")) {
                    String[] appArr = appInfo.split(":");
                    if (appArr.length > 1) {
                        String uidStr = appArr[0];
                        if (TextUtils.isDigitsOnly(uidStr)) {
                            int uid = Integer.valueOf(uidStr);
                        }
                        packageName = appArr[1];
                    }
                }
                if (packageName != null) {
                    ConditionVariable variable =obtainLock(packageName);
                    variable.open();
                    Log.e("mirror", "call: launch"+" "+packageName );
                }

            }
        } else if (method.equals(METHOD_GET_MMAUTOSEND_MSG) && extras != null) {
            final String pluginName = extras.getString("arg.plugin");
            final String userName = extras.getString("arg.username");
            //Log.d(TAG, "method: " + method + ", plugin " + pluginName + ", username " + userName+", context = "+getContext());

            if (TextUtils.equals("MMAutoText", pluginName)) {
                final Bundle result = new Bundle();
                SharedPreferences sp = getContext().getSharedPreferences("feature_all", Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
                int flag = sp.getInt("flag", 0);
                String info = sp.getString("info", null);
                //Log.d(TAG, "method: " + method + ", flag " + flag+", info = "+info);
                if ((flag & 0x1) == 0x1 && info != null && info.length() > 0) {
                    result.putBoolean("result.needsend", true);
                    result.putString("result.message", info);
                }
                return result;
            }
        } else if (method.equals(METHOD_GET_MMAUTOSEND_FILTER) && extras != null) {
            final String pluginName = extras.getString("arg.plugin");
            //Log.d(TAG, "method: " + method + ", plugin " + pluginName+", context = "+getContext());
            if (TextUtils.equals("MMAutoText", pluginName)) {
                SharedPreferences sp = getContext().getSharedPreferences("feature_all", Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB ? Context.MODE_PRIVATE : Context.MODE_MULTI_PROCESS);
                String ft = sp.getString("flt", null);
                //Log.d(TAG, "method: " + method + ", plugin with MMAutoText: ft = "+ft);
                final Bundle result = new Bundle();
                if (ft != null && ft.length() > 0) {
                    //Log.d(TAG, "method: " + method + ", plugin with MMAutoText: filter = "+filter);
                    result.putString("result.filter", ft);
                }
                return result;
            }
        } else if (method.equals(METHOD_UIEVENT)) {
//            try {
//                return handleUiEvent(arg, extras);
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
        }
        return super.call(method, arg, extras);
    }


    @Override
    public boolean onCreate() {
        return true;
    }


    private void log(Object msg) {
        if (DEBUG) {
            Log.d(TAG, "MSG:" + msg.toString());
        }
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }


    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        return null;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        return null;
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        return 0;
    }


}
