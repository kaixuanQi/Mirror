package com.zwang.clouds.wxapi;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.modelbiz.WXLaunchMiniProgram;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.yyong.mirror.personal.PayActivity;

public class WXEntryActivity extends AppCompatActivity implements IWXAPIEventHandler {
    private static final String TAG = "xgf";

    private IWXAPI api;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"WXEntryActivity --- onCreate"+" "+getTaskId());


        api = WXAPIFactory.createWXAPI(this,UnifyPayPlugin.getInstance(this).getAppId());

        api.handleIntent(getIntent(), this);

    }
    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        Log.d(TAG,"WXEntryActivity --- onNewIntent");
        setIntent(intent);
        api.handleIntent(getIntent(), this);
    }
    @Override
    public void onReq(BaseReq baseReq) {
        Log.d(TAG,"WXEntryActivity --- onReq");
    }

    @Override
    public void onResp(BaseResp baseResp) {
        Log.d(TAG,"WXEntryActivity --- onResp");
        if (baseResp.getType() == ConstantsAPI.COMMAND_LAUNCH_WX_MINIPROGRAM) {
            WXLaunchMiniProgram.Resp launchMiniProResp = (WXLaunchMiniProgram.Resp) baseResp;
            String extraData =launchMiniProResp.extMsg; //对应小程序组件 <button open-type="launchApp"> 中的 app-parameter 属性
            Log.d(TAG,"onResp   ---   " + extraData);
            String msg = "onResp   ---   errStr：" + baseResp.errStr + " --- errCode： " + baseResp.errCode + " --- transaction： "
                    + baseResp.transaction + " --- openId：" + baseResp.openId + " --- extMsg：" + launchMiniProResp.extMsg;
            Log.d(TAG,msg);
            Toast.makeText(this,msg,Toast.LENGTH_LONG).show();
            Intent intent = new Intent(this, PayActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//            intent.addFlags(Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
            startActivity(intent);
            Log.e(TAG, "launch onResp: "+intent );
            UnifyPayPlugin.getInstance(this).getWXListener().onResponse(this, baseResp);
            finish();
        }
    }
}
