package com.excean.mirror;

import android.content.Context;
import android.util.Log;

import com.bytedance.applog.AppLog;
import com.bytedance.applog.InitConfig;
import com.bytedance.applog.UriConfig;
import com.bytedance.mpaas.IEncryptor;
import com.excean.middleware.ChannelMeta;
import com.zero.support.common.vo.Resource;

import org.json.JSONException;
import org.json.JSONObject;

public class BIHelper {
    private static final String TAG = "mirror";
    public static final int LAUNCH_HOME = 0;
    public static final int LAUNCH_MAIN = 1;
    public static final int LAUNCH_MANAGER = 2;


    public static void initialize(Context context) {
        final InitConfig config = new InitConfig("10000008", ChannelMeta.getDefault().mainChannelId);
        config.setUriConfig(UriConfig.createByDomain("https://rangers.excelliance.cn", null));
        if (BuildConfig.DEBUG) {
            config.setLogger((msg, t) -> Log.d(TAG, msg, t));
        }
        config.setEncryptor(new IEncryptor() {
            @Override
            public byte[] encrypt(byte[] bytes, int i) {
                return BiAesUtil.encrypt(bytes, i);
            }
        });
        config.setAutoStart(true);
        AppLog.setEncryptAndCompress(true);
        AppLog.init(context, config);
    }

    //todo 应用选择并点击制作应用分身（上报包名）
    public static void reportRequestProduce(String name) {
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("request_produce", object);
    }

    public static void reportProduceStart(String name) {
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", name);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("produce_start", object);
    }

    //todo 任意一款应用点击开始安装（上报包名）
    public static void reportRequestInstall(String pkg) {
        //安装
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", pkg);
            object.put("user", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("request_produce", object);
    }

    public static void reportRequestFix(String pkg) {
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", pkg);
            object.put("user", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("request_fix", object);
    }

    public static void reportRequestUninstall(String pkg) {
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", pkg);
            object.put("user", 0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("request_uninstall", object);
    }

    //todo 任意一款应用制作完成（上报包名）
    public static void reportProduceFinish(String pkg, Resource<?> resource) {
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", pkg);
            object.put("user", 0);
            object.put("code", resource.code);
            object.put("msg", resource.message);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("produce_finish", object);
    }


    public static void reportRequestLaunch(String pkg, int from) {
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", pkg);
            object.put("user", 0);
            object.put("from", from);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("request_launch", object);
    }

    public static void reportLaunchFinish(String pkg, int from, int ret) {
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", pkg);
            object.put("user", 0);
            object.put("from", from);
            object.put("ret", ret);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("launch_finish", object);
    }

    public static void reportPackageChanged(String pkg, String name, int type){
        JSONObject object = new JSONObject();
        try {
            object.put("pkg", pkg);
            object.put("name", name);
            object.put("type", type);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        AppLog.onEventV3("package_change", object);
    }

}
