package com.excean.virutal.api.virtual;


import android.accounts.Account;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageStats;
import android.content.pm.ResolveInfo;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.SparseArray;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class PluginManagerWrapper {
    private static final boolean DEBUG = true;
    private static final String TAG = PluginManagerWrapper.class.getSimpleName();


    private Context mContext;
    private static PluginManagerWrapper sInstance = null;
    private static Application sApplicationProxyInstance = null;

    public static PluginManagerWrapper getInstance() {
        if (sInstance == null) {
            sInstance = new PluginManagerWrapper();
        }
        return sInstance;
    }

    public void onCreate(Application app) {
        Application appProxy = getApplicationProxyInstance();
        if (appProxy != null) {
            appProxy.onCreate();
        }
    }


    public void attachBaseContext(Application app, Context base) {
        mContext = app;
        Application appProxy = getApplicationProxyInstance();
        if (appProxy != null) {
            //appProxy.attachBaseContext(base);
            try {
                invokeMethod(appProxy, "attachBaseContext", new Class[]{Context.class}, new Object[]{base});
            } catch (Throwable thr) {
                thr.printStackTrace();
            }
        }
    }

    public boolean isVmLoaded() {
        Class<?> clazz;
        try {
            clazz = Class.forName("com.excelliance.kxqp.platform.ApplicationProxy");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return false;
        }catch (Throwable thr) {
            thr.printStackTrace();
            return false;
        }
        return clazz != null;
    }

    private Application getApplicationProxyInstance() {
        if (sApplicationProxyInstance == null) {
            try {
                Class<?> clazz = Class.forName("com.excelliance.kxqp.platform.ApplicationProxy");
                Method getInstance = clazz.getDeclaredMethod("getInstance", new Class[]{});
                sApplicationProxyInstance = (Application) getInstance.invoke(null, new Object[]{});
            } catch (Exception e) {
                e.printStackTrace();
            } catch (Error error) {
                error.printStackTrace();
            }
        }
        return sApplicationProxyInstance;
    }

    private static Object invokeMethod(Object obj, String methodName, Class[] parameterTypes, Object[] args) throws InvocationTargetException {
        if (obj == null) {
            return null;
        }
        Class cls = obj.getClass();
        try {
            Method method = cls.getDeclaredMethod(methodName, parameterTypes);
            method.setAccessible(true);
            return method.invoke(obj, args);
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static final String ACTION_PACKAGE_ADDED = "com.excelliance.kxqp.platform.pm.PACKAGE_ADDED";
    public static final String ACTION_PACKAGE_REMOVED = "com.excelliance.kxqp.platform.pm.PACKAGE_REMOVED";
    public static final String ACTION_PACKAGE_UNAVAILABLE = "com.excelliance.kxqp.platform.pm.PACKAGE_UNAVAILABLE";
    public static final String EXTRA_USER_ID = "extra.user.id";
    // removePackage() return value
    public static final int DELETE_SUCCEEDED = 1;
    public static final int DELETE_FAILED_INTERNAL_ERROR = -1;
    public static final int DELETE_FAILED_INVALID_PKG = -2;
    // installPackage() flags
    public static final int INSTALL_REPLACE_EXISTING = 0x00000002;
    public static final int INSTALL_IGNORE_PERMISSION_REQ = 0x00010000;
    public static final int INSTALL_ARG_IS_PKGNAME = 0x80000000;
    public static final int INSTALL_PKG_IS_SHARED = 0x00080000;

    ///////////////////////////////////////////////////////////// API start /////////////////////////////////////////////////////////////
    // installPackage() return value
    public static final int INSTALL_SUCCEEDED = 1;
    public static final int INSTALL_FAILED_ALREADY_EXISTS = -1;
    public static final int INSTALL_FAILED_INVALID_APK = -2;
    public static final int INSTALL_FAILED_INTERNAL_ERROR = -110;
    public static final int INSTALL_FAILED_NO_REQUESTEDPERMISSION = -100001;
    public static final int INSTALL_FAILED_INVALID_USER = -100002;
    // startActivity return code
    public static final int START_PKG_NOT_AVAILABLE = -100;
    public static final int START_PERMISSION_DENIED = -4;
    public static final int START_CLASS_NOT_FOUND = -2;
    public static final int START_SUCCESS = 0;
    public static final int START_TASK_TO_FRONT = 2;
    // fakeDeviceInfo mode:
    public static final int FAKE_MODE_FORCE_ALL = 0x1;
    // getRunningAppMemoryInfos
    public static final String MEMINFO_KEY_PKG = "pkg";
    public static final String MEMINFO_KEY_PSS = "pss";
    public static final String MEMINFO_KEY_PROCESS = "process";
    // updatePackageCapFlag: flags
    public static final long CAP_USER_FLAG_START_BIT = 20;
    public static final long CAP_PRIVATE_EXTERNAL_STORAGE = 1L << (CAP_USER_FLAG_START_BIT + 0);
    public static final long CAP_OVERRIDE_METADATA = 1L << (CAP_USER_FLAG_START_BIT + 1);
    public static final long CAP_PROHIBIT_LAUNCHED_PKG = 1L << (CAP_USER_FLAG_START_BIT + 2);
    public static final long CAP_PROHIBIT_SETALARM_PKG = 1L << (CAP_USER_FLAG_START_BIT + 3);
    public static final long CAP_OVERRIDE_PKG_SIGNATURE = 1L << (CAP_USER_FLAG_START_BIT + 4);
    public static final long CAP_BLOCK_NOTIFICATION = 1L << (CAP_USER_FLAG_START_BIT + 5);
    public static final long CAP_BLOCK_LOCATION = 1L << (CAP_USER_FLAG_START_BIT + 6);
    public static final long CAP_BLOCK_INSTALL_APK = 1L << (CAP_USER_FLAG_START_BIT + 7);
    public static final long CAP_ISOLATE_SYSTEM_SETTINGS = 1L << (CAP_USER_FLAG_START_BIT + 8);
    public static final long CAP_DISABLED_BROWSER_CHOOSER = 1L << (CAP_USER_FLAG_START_BIT + 9);
    public static final long CAP_NOTIFICATION_LISTENER = 1L << (CAP_USER_FLAG_START_BIT + 10);
    public static final long CAP_FAKE_DEVICE_INFO = 1L << (CAP_USER_FLAG_START_BIT + 11);
    public static final long CAP_FORCE_LANDSCAPE = 1L << (CAP_USER_FLAG_START_BIT + 12);
    public static final long CAP_KEEPDATA_DIR = 1L << (CAP_USER_FLAG_START_BIT + 13);
    public static final long CAP_DISABEL_EXTERNAL_ANDROID_DIRS_NH = 1L << (CAP_USER_FLAG_START_BIT + 14);
    public static final long CAP_DISABEL_HOOK_LIBC_EXPORT_SYMBOL = 1L << (CAP_USER_FLAG_START_BIT + 15);
    public static final long CAP_DISABEL_HOOK_LIBDL_EXPORT_SYMBOL = 1L << (CAP_USER_FLAG_START_BIT + 16);
    public static final long CAP_REMOVE_64BIT_FROM_ABILIST = 1L << (CAP_USER_FLAG_START_BIT + 17);
    public static final long CAP_IORELOCATE_FIX_PATH = 1L << (CAP_USER_FLAG_START_BIT + 18);
    public static final long CAP_DISABLE_MODIFY_DLOPEN_SYMBOL = 1L << (CAP_USER_FLAG_START_BIT + 19);
    public static final long CAP_ENABLE_HOOK_UNITY_WITH_COMPATIBLE_SO = 1L << (CAP_USER_FLAG_START_BIT + 20);
    public static final long CAP_ENABLE_OPAQUE_SOINFO = 1L << (CAP_USER_FLAG_START_BIT + 21);

    // dataDirConfig, accountTypeConfig
    public static final String CONFIG_ALL = "*ALL*";
    public static final String CONFIG_CURRENT = "*CURRENT*";
    // registerUiEvent: event
    public static final long EVENT_START_ACTIVITY = 1L << 0;
    public static final long EVENT_RESOLVE_INTENT = 1L << 1;
    public static final long EVENT_DOWNLOAD_REQUEST = 1L << 2;

    private static final int DEFAULT_ERR_CODE = -1111;
    private static final int PROTOCAL_VERSION_DEFAULT = Integer.MAX_VALUE;
    private static final int PROTOCAL_VERSION_2 = 2;
    private static final int PROTOCAL_VERSION_3 = 3;
    private static final int CMD_GET_PROTOCAL_VERSION = 0;
    // private
    private static final int CMD_IS_SERVICE_READY = 1;
    private static final int CMD_ADD_SERVICECON = 2;
    private static final int CMD_REMOVE_SERVICECON = 3;
    private static final int CMD_GET_PKGINFO = 4;
    private static final int CMD_INSTALL_PKG = 5;
    private static final int CMD_REMOVE_PKG = 6;
    private static final int CMD_GET_INSTALLED_PKGS = 7;
    private static final int CMD_WAIT_FOR_SERVICE_READY = 8;
    private static final int CMD_FORWARD_BROADCAST = 9;
    private static final int CMD_START_ACTIVITY = 10;
    // version 2
    private static final int CMD_GET_ALL_VIRTUALUSER = 20;
    private static final int CMD_NEW_VIRTUALUSER = 21;
    private static final int CMD_DELETE_VIRTUALUSER = 22;
    private static final int CMD_GET_PKGINFO_EXT = 23;
    private static final int CMD_INSTALL_PKG_EXT = 24;
    private static final int CMD_REMOVE_PKG_EXT = 25;
    private static final int CMD_GET_INSTALLED_PKGS_EXT = 26;
    private static final int CMD_FORWARD_BROADCAST_EXT = 27;
    private static final int CMD_START_ACTIVITY_EXT = 28;
    private static final int CMD_GET_UNAVAILABLE_PKGS = 29;
    private static final int CMD_FAKE_DEVICE_INFO = 30;
    private static final int CMD_QUERY_INTENT_ACTIVITIES = 31;
    private static final int CMD_LOAD_LABEL = 32;
    private static final int CMD_LOAD_ICON = 33;
    // version 3
    private static final int CMD_GET_RUNNING_APP_PROCESSES = 40;
    private static final int CMD_GET_RUNNING_APP_MEMORYINFOS = 41;
    private static final int CMD_GET_RUNNING_SERVICES = 42;
    private static final int CMD_GET_RUNNING_TASKS = 43;
    private static final int CMD_FORCE_STOP_PACKAGE = 44;
    private static final int CMD_GET_APP_INFO = 45;
    private static final int CMD_QUERY_INTENT_SERVICES = 46;
    private static final int CMD_QUERY_INTENT_RECEIVERS = 47;
    private static final int CMD_SET_COMPONENT_ENABLED_SETTING = 48;
    private static final int CMD_GET_COMPONENT_ENABLED_SETTING = 49;
    private static final int CMD_LOAD_APP_LABEL = 50;
    private static final int CMD_LOAD_APP_ICON = 51;
    private static final int CMD_START_SERVICE = 52;
    private static final int CMD_UPDATE_PKG_CAP_FLAG = 53;
    private static final int CMD_PRESTART_PROCESS = 54;
    private static final int CMD_PREINSTALL_PKG = 55;
    private static final int CMD_ENABLE_PKG = 56;
    private static final int CMD_SET_OVERLAY_RESOURCE = 57;
    private static final int CMD_PRECACHE_PKG = 58;
    private static final int CMD_INSTALL_PKG_PLUGIN = 59;
    private static final int CMD_DELETE_PKG_PLUGIN = 60;
    private static final int CMD_ENABLE_PKG_PLUGIN = 61;
    private static final int CMD_DISABLE_PKG_PLUGIN = 62;
    //version 4
    private static final int CMD_SET_ADBLOCK_CONFIG_FILE = 63;
    private static final int CMD_SET_RUNNING_PKGS_LIMIT = 64;
    private static final int CMD_GET_BADGEINFO = 65;
    //version 5
    private static final int CMD_INSTALL_ENCRYPT_PKG = 66;
    private static final int CMD_SET_NET_PROXY_CONFIG_FILE = 67;
    private static final int CMD_SET_EXTERNALSTORAGE_REDIRECT_PATHS = 68;
    @Deprecated
    private static final int CMD_CLEAR_APPLICATION_USER_DATA = 69;
    private static final int CMD_SET_VIRTUAL_EXTERNALSTORAGE_NAME = 70;
    private static final int CMD_GET_PKG_SIZE_INFO = 71;
    private static final int CMD_SET_AMS_FOREGROUND = 72;
    private static final int CMD_INSTALL_SHARED_LIBRARY = 73;
    private static final int CMD_DELETE_SHARED_LIBRARY = 74;
    private static final int CMD_GET_SHARED_LIBRARIES = 75;
    private static final int CMD_SET_GMS_STATE = 76;
    private static final int CMD_GET_GMS_STATE = 77;
    private static final int CMD_SET_AUTOINSTALL_STATE = 78;
    private static final int CMD_GET_AUTOINSTALL_STATE = 79;
    private static final int CMD_SET_PACKAGE_DATADIR_CONFIG = 80;
    private static final int CMD_GET_PACKAGE_DATADIR_CONFIG = 81;
    private static final int CMD_SET_ACCOUNTTYPE_CONFIG = 82;
    private static final int CMD_GET_ACCOUNTTYPE_CONFIG = 83;
    private static final int CMD_GET_ACCOUNTS_BY_CONFIG = 84;
    private static final int CMD_REMOVE_ACCOUNT_WITH_CONFIG = 85;
    private static final int CMD_EXEC_COMMAND = 86;
    private static final int CMD_CLEAR_APPLICATION_USER_DATA_WITH_CONFIG = 87;
    private static final int CMD_DELETE_APPLICATION_CACHE_FILES_WITH_CONFIG = 88;
    private static final int CMD_REGISTER_UIEVENT = 89;
    private static final int CMD_GET_INSTALLED_PACKAGE_PLUGINS = 90;
    private static final int CMD_GET_HOST_PKGINFO = 91;

    private static final int CMD_SET_PACKAGE_LOCATION = 92;
    private static final int CMD_SET_GLOBAL_LOCATION = 93;
    private static final int CMD_GET_LOCATION = 94;
    private static final int CMD_SET_NET_PROXY_STATE = 95;
    private static final int CMD_GET_NET_PROXY_STATE = 96;
    private static final int CMD_GET_ASSISTANT_INFO = 97;
    private static final int CMD_SET_SYSTEM_PACKAGEINSTALL_STATE = 98;
    private static final int CMD_GET_SYSTEM_PACKAGEINSTALL_STATE = 99;
    private static final int CMD_SET_INCOMPATIBLE_PACKAGES = 100;
    private static final int CMD_GET_INCOMPATIBLE_PACKAGES = 101;
    private static final int CMD_GET_INSTALL_COMPLETE_CODE = 102;
    private static final int CMD_UPDATE_DEVICE_FAKE_INFO = 103;
    private static final int CMD_SET_NET_PROXY_OPTION = 104;
    private static final int CMD_GET_NET_PROXY_OPTION = 105;
    private static final int CMD_SET_TASK_DESCRIPTION_LABEL_SUFFIX = 106;
    private static final int CMD_GET_TASK_DESCRIPTION_LABEL_SUFFIX = 107;
    private static final int CMD_SET_OVERRIDE_META_DATA = 108;
    private static final int CMD_GET_OVERRIDE_META_DATA = 109;

    private Object call(int cmd, Object[] args) {
        Application appProxy = getApplicationProxyInstance();
        if (appProxy != null) {
            try {
                return invokeMethod(appProxy, "call", new Class[]{int.class, Object[].class}, new Object[]{cmd, args});
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }
        return null;
    }

    private Object callNoException(int cmd, Object[] args) {
        try {
            return call(cmd, args);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private int mCurProtocalVersion = PROTOCAL_VERSION_DEFAULT;
    private int getProtocalVersion() {
        if (mCurProtocalVersion == PROTOCAL_VERSION_DEFAULT) {
            Object result = callNoException(CMD_GET_PROTOCAL_VERSION, new Object[0]);
            if (result instanceof Integer) {
                mCurProtocalVersion = (Integer) result;
            }
        }
        return mCurProtocalVersion;
    }


    public List<ResolveInfo> queryIntentActivities(Context context, int vuid, Intent intent) {
        Object result = callNoException(CMD_QUERY_INTENT_ACTIVITIES, new Object[]{vuid, intent, intent.resolveType(context), /*PackageManager.MATCH_ALL*/0x00020000});
        if (result instanceof List) {
            return (List) result;
        }
        return new ArrayList<ResolveInfo>(0);
    }

    public boolean isServiceReady() {
        Object result = callNoException(CMD_IS_SERVICE_READY, null);
        if (result instanceof Boolean) {
            return ((Boolean) result).booleanValue();
        }
        return false;
    }

    public void addServiceConnection(ServiceConnection sc) {
        callNoException(CMD_ADD_SERVICECON, new Object[]{sc});
    }

    public void removeServiceConnection(ServiceConnection sc) {
        callNoException(CMD_REMOVE_SERVICECON, new Object[]{sc});
    }

    public void waitForServiceReady() {
        callNoException(CMD_WAIT_FOR_SERVICE_READY, null);
    }

    public PackageInfo getPackageInfo(String packageName, int flags) {
        return getPackageInfo(0, packageName, flags);
    }

    public PackageInfo getPackageInfo(int vuid, String packageName, int flags) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            Object result = call(CMD_GET_PKGINFO_EXT, new Object[]{vuid, packageName, flags});
            if (result instanceof PackageInfo) {
                return (PackageInfo) result;
            }
        } else {
            Object result = call(CMD_GET_PKGINFO, new Object[]{packageName, flags});
            if (result instanceof PackageInfo) {
                return (PackageInfo) result;
            }
        }
        return null;
    }

    public int installPackage(String filepath, int flags) {
        return installPackage(0, filepath, flags);
    }

    public int installPackage(int vuid, String filepath, int flags) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            Object result = call(CMD_INSTALL_PKG_EXT, new Object[]{vuid, filepath, flags});
            if (result instanceof Integer) {
                return (Integer) result;
            }
        } else {
            Object result = call(CMD_INSTALL_PKG, new Object[]{filepath, flags});
            if (result instanceof Integer) {
                return (Integer) result;
            }
        }
        return DEFAULT_ERR_CODE;
    }

    public void removePackage(String packageName, int flags) {
        removePackage(0, packageName, flags);
    }

    public void removePackage(int vuid, String packageName, int flags) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            call(CMD_REMOVE_PKG_EXT, new Object[]{vuid, packageName, flags});
        } else {
            call(CMD_REMOVE_PKG, new Object[]{packageName, flags});
        }
    }

    public List<PackageInfo> getInstalledPackages(int vuid, int flags) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            Object result = call(CMD_GET_INSTALLED_PKGS_EXT, new Object[]{vuid, flags});
            if (result instanceof List) {
                return (List) result;
            }
        } else {
            Object result = call(CMD_GET_INSTALLED_PKGS, new Object[]{flags});
            if (result instanceof List) {
                return (List) result;
            }
        }
        return null;
    }


    public void forwardBroadcast(Intent intent, List<String> pkgs) {
        forwardBroadcast(0, intent, pkgs);
    }

    public void forwardBroadcast(int vuid, Intent intent, List<String> pkgs) {
        Log.d(TAG, "forwardBroadcast: " + vuid + ", " + pkgs);

        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            call(CMD_FORWARD_BROADCAST_EXT, new Object[]{vuid, intent, pkgs});
        } else {
            call(CMD_FORWARD_BROADCAST, new Object[]{intent, pkgs});
        }
    }

    public int startActivity(Intent intent) {
        return startActivity(0, intent);
    }


    public int startActivity(int vuid, Intent intent) {
        Object result;
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            result = call(CMD_START_ACTIVITY_EXT, new Object[]{vuid, intent});
        } else {
            result = call(CMD_START_ACTIVITY, new Object[]{intent});
        }
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public List<Integer> getAllVirtualUsers() {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            Object result = call(CMD_GET_ALL_VIRTUALUSER, null);
            if (result instanceof List) {
                return (List) result;
            }
        }
        ArrayList<Integer> result = new ArrayList<Integer>();
        result.add(0);
        return result;
    }

    public int newVirtualUser() {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            Object result = call(CMD_NEW_VIRTUALUSER, null);
            if (result instanceof Integer) {
                return (Integer) result;
            }
        }
        return DEFAULT_ERR_CODE;
    }

    public void deleteVirtualUser(int vuid) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            call(CMD_DELETE_VIRTUALUSER, new Object[]{vuid});
        }
    }

    // Õâ¸ö½Ó¿Ú¿ÉÒÔÓÃÀ´»ñÈ¡°²×°¹ýµÄ°ü¶ÔÓ¦µÄapkÎÄ¼þ±»É¾³ýºó(apkÉý¼¶¹ý³ÌÒ²±»ÊÓ×÷apkÎÄ¼þÉ¾³ý)£¬ÓÖÃ»ÓÐuninstall»òÕßÖØÐÂinstall
    // µÄ°üÁÐ±í£¬Ö®Ç°µÄgetInstalledPackages½Ó¿ÚÖ»ÄÜ»ñÈ¡µ½×´Ì¬Õý³£µÄ°üÁÐ±í¡£
    // ¿ÉÒÔÓÃÕâ¸ö½Ó¿Ú²éÑ¯²»Õý³£µÄ°ü£¬È»ºóÖ´ÐÐuninstall »òÕß ÖØÐÂinstall
    public List<String> getUnavailblePackages(int vuid) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            Object result = call(CMD_GET_UNAVAILABLE_PKGS, new Object[]{vuid});
            if (result instanceof List) {
                return (List) result;
            }
        }
        return new ArrayList<String>(0);
    }

    public int fakeDeviceInfo(int vuid, int mode) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_2) {
            Object result = call(CMD_FAKE_DEVICE_INFO, new Object[]{vuid, mode});
            if (result instanceof Integer) {
                Log.d(TAG, "fakeDeviceInfo: " + result + ", " + vuid + ", " + mode);
                return (Integer) result;
            }
        }
        Log.d(TAG, "fakeDeviceInfo: " + DEFAULT_ERR_CODE + ", " + vuid + ", " + mode);
        return DEFAULT_ERR_CODE;
    }

    public List<ResolveInfo> queryIntentActivities(int vuid, Intent intent) {
        Object result = call(CMD_QUERY_INTENT_ACTIVITIES, new Object[]{vuid, intent, intent.resolveType(mContext), /*PackageManager.MATCH_ALL*/0x00020000});
        if (result instanceof List) {
            return (List) result;
        }
        return new ArrayList<ResolveInfo>(0);
    }

    public CharSequence loadLabel(int vuid, ResolveInfo info) {
        Object result = call(CMD_LOAD_LABEL, new Object[]{vuid, info});
        if (result instanceof CharSequence) {
            return (CharSequence) result;
        }
        return null;
    }

    public Drawable loadIcon(int vuid, ResolveInfo info) {
        Object result = call(CMD_LOAD_ICON, new Object[]{vuid, info});
        if (result instanceof Drawable) {
            return (Drawable) result;
        }
        return null;
    }

    public List<ActivityManager.RunningAppProcessInfo> getRunningAppProcesses(int vuid) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3 || true) {
            Object result = call(CMD_GET_RUNNING_APP_PROCESSES, new Object[]{vuid});
            if (result instanceof List) {
                return (List) result;
            }
        }
        return new ArrayList<ActivityManager.RunningAppProcessInfo>(0);
    }

    public SparseArray<HashMap> getRunningAppMemoryInfos(int vuid) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_GET_RUNNING_APP_MEMORYINFOS, new Object[]{vuid});
            if (result instanceof SparseArray) {
                return (SparseArray<HashMap>) result;
            }
        }
        return new SparseArray<HashMap>(0);
    }

    public List<ActivityManager.RunningServiceInfo> getRunningServices(int vuid, int maxNum) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_GET_RUNNING_SERVICES, new Object[]{vuid, maxNum});
            if (result instanceof List) {
                return (List) result;
            }
        }
        return new ArrayList<ActivityManager.RunningServiceInfo>(0);
    }

    public List<ActivityManager.RunningTaskInfo> getRunningTasks(int vuid, int maxNum) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_GET_RUNNING_TASKS, new Object[]{vuid, maxNum});
            if (result instanceof List) {
                return (List) result;
            }
        }
        return new ArrayList<ActivityManager.RunningTaskInfo>(0);
    }

    public void forceStopPackage(int vuid, String packageName) {
        Log.d(TAG, "forceStopPackage: " + vuid + ", " + packageName);
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            call(CMD_FORCE_STOP_PACKAGE, new Object[]{vuid, packageName});
        }
    }

    public ApplicationInfo getApplicationInfo(int vuid, String packageName, int flags) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_GET_APP_INFO, new Object[]{vuid, packageName, flags});
            if (result instanceof ApplicationInfo) {
                return (ApplicationInfo) result;
            }
        }
        return null;
    }

    public List<ResolveInfo> queryIntentServices(int vuid, Intent intent) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_QUERY_INTENT_SERVICES, new Object[]{vuid, intent, intent.resolveType(mContext), /*PackageManager.MATCH_ALL*/0x00020000});
            if (result instanceof List) {
                return (List) result;
            }
        }
        return new ArrayList<ResolveInfo>(0);
    }

    public List<ResolveInfo> queryIntentReceivers(int vuid, Intent intent) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_QUERY_INTENT_RECEIVERS, new Object[]{vuid, intent, intent.resolveType(mContext), /*PackageManager.MATCH_ALL*/0x00020000});
            if (result instanceof List) {
                return (List) result;
            }
        }
        return new ArrayList<ResolveInfo>(0);
    }

    public void setComponentEnabledSetting(int vuid, ComponentName componentName, int newState, int flags) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            call(CMD_SET_COMPONENT_ENABLED_SETTING, new Object[]{vuid, componentName, newState, flags});
        }
    }

    public int getComponentEnabledSetting(int vuid, ComponentName componentName, int newState, int flags) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_GET_COMPONENT_ENABLED_SETTING, new Object[]{vuid, componentName});
            if (result instanceof Integer) {
                return (Integer) result;
            }
        }
        return DEFAULT_ERR_CODE;
    }

    public CharSequence loadAppLabel(int vuid, ApplicationInfo info) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_LOAD_APP_LABEL, new Object[]{vuid, info});
            if (result instanceof CharSequence) {
                return (CharSequence) result;
            }
        }
        return null;
    }

    public Drawable loadAppIcon(int vuid, ApplicationInfo info) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_LOAD_APP_ICON, new Object[]{vuid, info});
            if (result instanceof Drawable) {
                return (Drawable) result;
            }
        }
        return null;
    }

    public ComponentName startService(int vuid, Intent intent) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_START_SERVICE, new Object[]{vuid, intent});
            if (result instanceof ComponentName) {
                return (ComponentName) result;
            }
        }
        return null;
    }

    // return true if update success.
    public boolean updatePackageCapFlag(int vuid, String pkg, long flag, boolean clearFlag) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            Object result = call(CMD_UPDATE_PKG_CAP_FLAG, new Object[]{vuid, pkg, flag, clearFlag});
            if (result instanceof Boolean) {
                return (Boolean) result;
            }
        }
        return false;
    }

    // type: 0 - activity, 1 - service
    public void preStartProcess(int vuid, String packageName, int type, Intent intent) {
        if (getProtocalVersion() >= PROTOCAL_VERSION_3) {
            call(CMD_PRESTART_PROCESS, new Object[]{vuid, packageName, type, intent});
        }
    }

    public int preInstallPackage(int vuid, String filepath, int flags) {
        Object result = call(CMD_PREINSTALL_PKG, new Object[]{vuid, filepath, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public int enablePackage(int vuid, String filepath, int flags) {
        Object result = call(CMD_ENABLE_PKG, new Object[]{vuid, filepath, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    /* set resouce to null to disable overlay */
    public boolean setOverlayResource(int uid, String pkg, String resource) {
        Object result = call(CMD_SET_OVERLAY_RESOURCE, new Object[]{uid, pkg, resource});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public int preCachePackage(int vuid, String filepath, int flags) {
        Object result = call(CMD_PRECACHE_PKG, new Object[]{vuid, filepath, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public int installPackagePlugin(int vuid, String packageName, String pluginPath, int flags) {
        Log.d(TAG, "installPackagePlugin: " + vuid + ", " + packageName + ", " + pluginPath + ", " + flags);
        Object result = call(CMD_INSTALL_PKG_PLUGIN, new Object[]{vuid, packageName, pluginPath, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public int deletePackagePlugin(int vuid, String packageName, String packagePluginName, int flags) {
        Object result = call(CMD_DELETE_PKG_PLUGIN, new Object[]{vuid, packageName, packagePluginName, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public boolean enablePackagePlugin(int vuid, String packageName, String packagePluginName) {
        Object result = call(CMD_ENABLE_PKG_PLUGIN, new Object[]{vuid, packageName, packagePluginName});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public boolean disablePackagePlugin(int vuid, String packageName, String packagePluginName) {
        Object result = call(CMD_DISABLE_PKG_PLUGIN, new Object[]{vuid, packageName, packagePluginName});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public boolean setAdBlockConfigFile(int uid, String pkg, String filePath) {
        Object result = call(CMD_SET_ADBLOCK_CONFIG_FILE, new Object[]{uid, pkg, filePath});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public boolean setNetProxyConfigFile(int uid, String pkg, String filePath) {
        Object result = call(CMD_SET_NET_PROXY_CONFIG_FILE, new Object[]{uid, pkg, filePath});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }
    public boolean setRunningPackagesLimit(int userLimit, int monkeyLimit) {
        Object result = call(CMD_SET_RUNNING_PKGS_LIMIT, new Object[]{userLimit, monkeyLimit});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }
    public boolean setAmsForeground(int value) {
        Object result = call(CMD_SET_AMS_FOREGROUND, new Object[]{value});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public byte[] getBadgeInfo() {
        return (byte[]) call(CMD_GET_BADGEINFO, new Object[0]);
    }

    public int installEncryptPackage(int uid, String filePath, String key, int flags) {
        Object result = call(CMD_INSTALL_ENCRYPT_PKG, new Object[]{uid, filePath, key, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public boolean setExternalStorageRedirectPaths(int uid, String pkg, String[] paths) {
        Object result = call(CMD_SET_EXTERNALSTORAGE_REDIRECT_PATHS, new Object[]{uid, pkg, paths});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }


    public boolean setVirtualExternalStorageName(String name) {
        Object result = call(CMD_SET_VIRTUAL_EXTERNALSTORAGE_NAME, new Object[]{name});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public PackageStats getPackageSizeInfo(int vuid, String pkg) {
        Object result = call(CMD_GET_PKG_SIZE_INFO, new Object[]{vuid, pkg});
        if (result instanceof PackageStats) {
            return (PackageStats) result;
        }
        return null;
    }

    public int installSharedLibrary(String libname, String libpath, int flags) {
        Object result = call(CMD_INSTALL_SHARED_LIBRARY, new Object[]{libname, libpath, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public int deleteSharedLibrary(String libname, int flags) {
        Object result = call(CMD_DELETE_SHARED_LIBRARY, new Object[]{libname, flags});
        if (result instanceof Integer) {
            return (Integer) result;
        }
        return DEFAULT_ERR_CODE;
    }

    public Map<String, String> getInstalledSharedLibraries(String[] libnames, int flags) {
        Object result = call(CMD_GET_SHARED_LIBRARIES, new Object[]{libnames, flags});
        if (result instanceof Map) {
            return (Map<String, String>) result;
        }
        return null;
    }

    // state: 0 - disable, 1 - enable
    public boolean setGmsState(int state) {
        Log.d(TAG, "setGmsState: " + state);
        Object result = call(CMD_SET_GMS_STATE, new Object[]{state});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public Integer getGmsState() {
        Object result = call(CMD_GET_GMS_STATE, new Object[0]);
        if (result instanceof Integer) {
            return (Integer)result;
        }
        return DEFAULT_ERR_CODE;
    }

    // state: 0 - disable, 1 - enable
    public boolean setAutoInstallState(int state) {
        Object result = call(CMD_SET_AUTOINSTALL_STATE, new Object[]{state});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public Integer getAutoInstallState() {
        Object result = call(CMD_GET_AUTOINSTALL_STATE, new Object[0]);
        if (result instanceof Integer) {
            return (Integer)result;
        }
        return DEFAULT_ERR_CODE;
    }

    public boolean setPackageDataDirConfig(int vuid, String pkg, String config) {
        Object result = call(CMD_SET_PACKAGE_DATADIR_CONFIG, new Object[]{vuid, pkg, config});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public String getPackageDataDirConfig(int vuid, String pkg) {
        Object result = call(CMD_GET_PACKAGE_DATADIR_CONFIG, new Object[]{vuid, pkg});
        if (result instanceof String) {
            return (String)result;
        }
        return null;
    }

    public final boolean setAccountTypeConfig(int vuid, String accountType, String accountTypeConfig) {
        Object result = call(CMD_SET_ACCOUNTTYPE_CONFIG, new Object[]{vuid, accountType, accountTypeConfig});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public final String getAccountTypeConfig(int vuid, String accountType) {
        Object result = call(CMD_GET_ACCOUNTTYPE_CONFIG, new Object[]{vuid, accountType});
        if (result instanceof String) {
            return (String)result;
        }
        return null;
    }

    public final HashMap<String, ArrayList<Account>> getAccountsByConfig(int vuid, String accountType, String accountTypeConfig) {
        Object result = call(CMD_GET_ACCOUNTS_BY_CONFIG, new Object[]{vuid, accountType, accountTypeConfig});
        if (result instanceof HashMap) {
            return (HashMap)result;
        }
        return null;
    }

    public final boolean removeAccountExplicitlyWithConfig(int vuid, Account account, String accountTypeConfig) {
        Object result = call(CMD_REMOVE_ACCOUNT_WITH_CONFIG, new Object[]{vuid, account, accountTypeConfig});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public Bundle execCommand(List<String> cmds, Map<String, String> envs, String directory, int flags, Bundle options) {
        Object result = call(CMD_EXEC_COMMAND, new Object[]{cmds, envs, directory, flags, options});
        if (result instanceof Bundle) {
            return (Bundle)result;
        }
        return null;
    }

    public void clearApplicationUserDataWithConfig(int vuid, String pkg, String dataDirConfig) {
        call(CMD_CLEAR_APPLICATION_USER_DATA_WITH_CONFIG, new Object[]{vuid, pkg, dataDirConfig});
    }

    @Deprecated
    public void clearApplicationUserData(int vuid, String pkg) {
        clearApplicationUserDataWithConfig(vuid, pkg, CONFIG_ALL);
    }

    public void deleteApplicationCacheFilesWithConfig(int vuid, String pkg, String dataDirConfig) {
        call(CMD_DELETE_APPLICATION_CACHE_FILES_WITH_CONFIG, new Object[]{vuid, pkg, dataDirConfig});
    }

    public boolean registerUiEvent(int vuid, String pkg, long event, boolean clearFlag) {
        Log.d(TAG, "registerUiEvent: " + vuid + ", " + pkg + ", " + event + ", " + clearFlag);
        Object result = call(CMD_REGISTER_UIEVENT, new Object[]{vuid, pkg, event, clearFlag});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public Map<String, Boolean> getInstalledPackagePlugins(int vuid, String packageName) {
        Object result = call(CMD_GET_INSTALLED_PACKAGE_PLUGINS, new Object[]{vuid, packageName});
        if (result instanceof Map) {
            return (Map<String, Boolean>) result;
        }
        return null;
    }

    public PackageInfo getHostPackageInfo(int flags) {
        Object result = call(CMD_GET_HOST_PKGINFO, new Object[]{flags});
        if (result instanceof PackageInfo) {
            return (PackageInfo) result;
        }
        return null;
    }

    public boolean setPackageLocation(int vuid, String pkg, boolean enable, double latitude, double longtitude) {
        Object result = call(CMD_SET_PACKAGE_LOCATION, new Object[]{vuid, pkg, enable, latitude, longtitude});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public boolean setGlobalLocation(boolean enable, double latitude, double longtitude) {
        Object result = call(CMD_SET_GLOBAL_LOCATION, new Object[]{enable, latitude, longtitude});
        return result instanceof Boolean && ((Boolean) result).booleanValue();
    }

    public double[] getLocation(int vuid, String pkg, boolean ignoreGlobal) {
        Object result = call(CMD_GET_LOCATION, new Object[]{vuid, pkg, ignoreGlobal});
        return result instanceof double[] ? (double[]) result : null;
    }

    public boolean setNetProxyDisabledState(int vuid, String pkg, int state, boolean temporary) {
        Object result = call(CMD_SET_NET_PROXY_STATE, new Object[]{vuid, pkg, state, temporary});
        return result instanceof Boolean && (boolean) result;
    }

    public boolean getNetProxyDisabledState(int vuid, String pkg) {
        Object result = call(CMD_GET_NET_PROXY_STATE, new Object[]{vuid, pkg});
        return result instanceof Boolean && (boolean) result;
    }

    public Bundle getAssistantInfo(int flags) {
        Object result = call(CMD_GET_ASSISTANT_INFO, new Object[]{flags});
        if (result instanceof Bundle) {
            return (Bundle)result;
        }
        return null;
    }

    // state: 0 - disable, 1 - enable
    public boolean setSystemPackageInstallerState(int state) {
        Object result = call(CMD_SET_SYSTEM_PACKAGEINSTALL_STATE, new Object[]{state});
        return result instanceof Boolean && (boolean) result;
    }

    public Integer getSystemPackageInstallerState() {
        Object result = call(CMD_GET_SYSTEM_PACKAGEINSTALL_STATE, new Object[0]);
        if (result instanceof Integer) {
            return (Integer)result;
        }
        return DEFAULT_ERR_CODE;
    }

    public boolean setIncompatiblePackagesForPackage(int uid, String pkg, String[] pkgs) {
        Object result = call(CMD_SET_INCOMPATIBLE_PACKAGES, new Object[]{uid, pkg, pkgs});
        return result instanceof Boolean && (boolean) result;
    }

    public String[] getIncompatiblePackagesForPackage(int vuid, String pkg) {
        Object result = call(CMD_GET_INCOMPATIBLE_PACKAGES, new Object[]{vuid, pkg});
        return result instanceof String[] ? (String[]) result : null;
    }

    public int getInstallCompleteWarningCode(int vuid, String pkg) {
        Object result = call(CMD_GET_INSTALL_COMPLETE_CODE, new Object[]{vuid, pkg});
        if (result instanceof Integer) {
            return (Integer)result;
        }
        return 0;
    }

    public int updateDeviceFakeInfo(int vuid, Map<String, String> info) {
        Object result = call(CMD_UPDATE_DEVICE_FAKE_INFO, new Object[]{vuid, info});
        if (result instanceof Integer) {
            return (Integer)result;
        }
        return 0;
    }

    public boolean setNetProxyConfigOption(int vuid, String pkg, int option) {
        Object result = call(CMD_SET_NET_PROXY_OPTION, new Object[]{vuid, pkg, option});
        return result instanceof Boolean && (boolean) result;
    }

    public int getNetProxyConfigOption(int vuid, String pkg) {
        Object result = call(CMD_GET_NET_PROXY_OPTION, new Object[]{vuid, pkg});
        if (result instanceof Integer) {
            return (Integer)result;
        }
        return 0;
    }

    public boolean setTaskDescLabelSuffix(int vuid, String pkg, String suffix) {
        Object result = call(CMD_SET_TASK_DESCRIPTION_LABEL_SUFFIX, new Object[]{vuid, pkg, suffix});
        return result instanceof Boolean && (boolean) result;
    }

    public String getTaskDescLabelSuffix(int vuid, String pkg) {
        Object result = call(CMD_GET_TASK_DESCRIPTION_LABEL_SUFFIX, new Object[]{vuid, pkg});
        if (result instanceof String) {
            return (String)result;
        }
        return null;
    }

    public void setOverrideMetadata(final int vuid, final String pkg, Bundle metaData) {
        call(CMD_SET_OVERRIDE_META_DATA, new Object[] {vuid, pkg, metaData});
    }

    public Bundle getOverrideMetadata(final int vuid, final String pkg) {
        Object result = call(CMD_GET_OVERRIDE_META_DATA, new Object[] {vuid, pkg});
        if (result instanceof Bundle) {
            return (Bundle)result;
        }
        return null;
    }
}
