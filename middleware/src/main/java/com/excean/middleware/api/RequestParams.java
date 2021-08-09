//package com.excean.middleware.api;
//
//import android.content.Context;
//import android.os.Build;
//import android.provider.Settings;
//
//import com.excean.legacy.util.LogUtil;
//import com.excean.legacy.util.PackageUtil;
//import com.excean.legacy.util.SpUtils;
//import com.excean.legacy.util.StaticUtil;
//import com.excean.vphone.config.BuildConfig;
//import com.excean.middleware.account.Account;
//import com.excean.middleware.account.AccountManager;
//
//import org.json.JSONObject;
//
//import java.lang.reflect.Method;
//import java.net.URLEncoder;
//
///**
// * Effective in 2019/12/21
// *
// * @author ydx
// */
//public class RequestParams extends JSONObject {
//
//    private static final String TAG = "RequestParams";
//
//    private RequestParams() {
//    }
//
//    /* time consuming */
//    public static JSONObject generate(Context context) {
//        RequestParams requestParams = new RequestParams();
//        try {
//            requestParams.put("pkg", "com.yiqiang.xmaster");
//            requestParams.put("chid", BuildConfig.mainChId);
//            requestParams.put("subchid", BuildConfig.subChId);
//            requestParams.put("vc", PackageUtil.getVersionCode(context));
//            requestParams.put("vn", PackageUtil.getVersionName(context));
//            String aid = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
//            requestParams.put("aid", aid);
//            requestParams.put("imsi", aid);
//            requestParams.put("imei", aid);
//            requestParams.put("api", Build.VERSION.SDK_INT);
//            requestParams.put("release", Build.VERSION.RELEASE);
//            requestParams.put("abi", Build.CPU_ABI);
////            requestParams.put("abtest", ABTestUtil.getDisplayStyle(context));
//            // user info
//            requestParams.put("uqid", StaticUtil.getUqID(context));
//            requestParams.put("cqid", StaticUtil.getCqID(context));
//            Account account = AccountManager.getDefault().currentAccount().getValue();
//            if (account != null && account.isLogin()) {
//                requestParams.put("rid", account.rid);
//            }
////            requestParams.put("uid", UserUtil.getUserId(context));
//            // 附加字段
//            requestParams.put("brand", URLEncoder.encode(Build.BRAND, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("manufacturer", URLEncoder.encode(Build.MANUFACTURER, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("model", URLEncoder.encode(Build.MODEL, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("product", URLEncoder.encode(Build.PRODUCT, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("buildID", URLEncoder.encode(Build.ID, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("buildDevice", URLEncoder.encode(Build.DEVICE, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("fingerprint", URLEncoder.encode(Build.FINGERPRINT, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("board", URLEncoder.encode(Build.BOARD, "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("abilist", URLEncoder.encode(getsString("ro.product.cpu.abilist"), "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("abilist32", URLEncoder.encode(getsString("ro.product.cpu.abilist32"), "UTF-8").replaceAll("\\+", "%20"));
//            requestParams.put("abilist64", URLEncoder.encode(getsString("ro.product.cpu.abilist64"), "UTF-8").replaceAll("\\+", "%20"));
//
//            if (BuildConfig.GP_VERSION) {
//                requestParams.put("romversion", SpUtils.getInstance(context, SpUtils.GLOBAL_CONFIG).getInt(SpUtils.GLOBAL_LAST_VERSION_CODE, -1));
//            } else {
//                requestParams.put("romversion", SpUtils.getInstance(context, SpUtils.GLOBAL_CONFIG).getInt(SpUtils.GLOBAL_KEY_ROM_VERSION_CODE, -1));
//            }
//            requestParams.put("productId", 10000);
//        } catch (Exception e) {
//            LogUtil.e(TAG, "ex:" + e);
//            e.printStackTrace();
//        }
//        LogUtil.i(TAG, "RequestParams/generate:" + requestParams);
//        return requestParams;
//    }
//
//    public static String getsString(String property) {
//        try {
//            Class<?> SystemProperties = Class.forName("android.os.SystemProperties");
//            Method get = SystemProperties.getDeclaredMethod("get", new Class[]{String.class, String.class});
//            get.setAccessible(true);
//            final String value = (String) get.invoke(null, new Object[]{property, ""});
//            if (value != null) {
//                return value;
//            }
//        } catch (Exception e) {
//        }
//
//        return "";
//    }
//}
