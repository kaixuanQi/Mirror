package com.excean.middleware.api;

import android.os.Build;
import android.provider.Settings;

import com.excean.middleware.ChannelMeta;
import com.google.gson.annotations.SerializedName;
import com.zero.support.common.AppGlobal;

public class DeviceUser {
    /**
     * $param['chid'] = array_key_exists('chid', $_REQUEST) ? intval($_REQUEST['chid']) : 0; //渠道
     * $param['subchid'] = array_key_exists('subchid', $_REQUEST) ? intval($_REQUEST['subchid']) : 0; // 子渠道
     * $param['aid'] = array_key_exists('aid', $_REQUEST) ? $_REQUEST['aid'] : ''; // 安卓id
     * $param['type'] = array_key_exists('type', $_REQUEST) ? $_REQUEST['type'] : 0; // 反馈类型 7闪退或卡顿 9产品建议 10其他 11功能异常 12应用不支持 13新用户问题
     * $param['apkver'] = array_key_exists('vc', $_REQUEST) ? intval($_REQUEST['vc']) : 0; // 版本号
     * $param['compver'] = array_key_exists('compver', $_REQUEST) ? intval(trim($_REQUEST['compver'])) : 0; // vm版本
     * $param['mainver'] = array_key_exists('mainver', $_REQUEST) ? intval(trim($_REQUEST['mainver'])) : 0; // mainjar版本
     * $param['manufacturer'] = array_key_exists('manufacturer', $_REQUEST) ? trim($_REQUEST['manufacturer']) : ''; // 厂商
     * $param['model'] = array_key_exists('model', $_REQUEST) ? trim($_REQUEST['model']) : ''; // 机型
     * $param['api'] = array_key_exists('api', $_REQUEST) ? intval(trim($_REQUEST['api'])) : 0; // api
     * $param['abTest'] = array_key_exists('abTest', $_REQUEST) ? trim($_REQUEST['abTest']) : ''; // ab分支
     * $param['pkg'] = array_key_exists('pkg', $_REQUEST) ? trim($_REQUEST['pkg']) : '';  //包名
     * $param['product'] = array_key_exists('product', $_REQUEST) ? trim($_REQUEST['product']) : '';
     */
    @SerializedName("chid")
    public String channelId = ChannelMeta.getDefault().mainChannelId;
    @SerializedName("subchid")
    public String subChannelId = ChannelMeta.getDefault().subChannelId;
    @SerializedName("aid")
    public String androidId = Settings.System.getString(AppGlobal.getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);
    @SerializedName("apkver")
    public long version = ChannelMeta.getDefault().versionCode;
    @SerializedName("compver")
    public long virtualEngineVersion = 1;
    @SerializedName("manufacturer")
    public String manufacturer = Build.MANUFACTURER;
    @SerializedName("model")
    public String model = Build.MODEL;
    @SerializedName("api")
    public int api = Build.VERSION.SDK_INT;
    @SerializedName("abTest")
    public String abTest;
    @SerializedName("pkg")
    public String pkg = AppGlobal.getApplication().getPackageName();
    @SerializedName("product")
    public String product = Build.PRODUCT;

}
