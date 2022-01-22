package com.yyong.mirror.personal;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;


import com.chinaums.pppay.unify.UnifyPayListener;
import com.chinaums.pppay.unify.UnifyPayPlugin;
import com.chinaums.pppay.unify.UnifyPayRequest;
import com.yyong.middleware.ui.base.TitleActivity;
import com.yyong.mirror.AppHolder;
import com.zero.support.common.AppGlobal;

public class PayActivity extends TitleActivity {
    public static void startActivity(Activity activity,String param, String channel){
        Intent intent = new Intent(activity,PayActivity.class);
        intent.putExtra("param",param);
        intent.putExtra("channel",channel);
        activity.startActivity(intent);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTitle("支付中");
        if (savedInstanceState==null){
            UnifyPayRequest msg = new UnifyPayRequest();
            msg.payData = getIntent().getStringExtra("param");
            msg.payChannel = getIntent().getStringExtra("channel");
            if (msg.payData!=null){
                UnifyPayPlugin.getInstance(this).sendPayRequest(msg);
            }
        }
        UnifyPayPlugin.getInstance(this).setListener(new UnifyPayListener() {
            @Override
            public void onResult(String s, String s1) {
                Log.e("pay", "onResult: "+s+s1 );
                if (UnifyPayListener.ERR_OK.equals(s)){
                    AppGlobal.sendMessage("支付成功");
                    AppHolder.vip.setValue(true);
                    finish();
                }
            }
        });

        if (getIntent().getData()!=null){
            handleIntent();
        }else {
            finish();
        }

    }


    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if(intent.getData()!=null){
            setIntent(intent);
            handleIntent();
        }else {
            finish();
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        UnifyPayPlugin.getInstance(this).getListener().onResult("-1", "取消");
    }

    private void handleIntent() {
        if(getIntent() != null){
            try {
                Uri uri = getIntent().getData();
                //完整路径
                String errCode = uri.getQueryParameter("errCode");
                String errStr = uri.getQueryParameter("errStr");
                UnifyPayPlugin.getInstance(this).getListener().onResult(errCode, errStr);
                finish();
            }catch (Exception e){
                e.getStackTrace();
                UnifyPayPlugin.getInstance(this).getListener().onResult("-1", "未知错误");
            }
        }else {
            AppGlobal.sendMessage("支付失败");
            UnifyPayPlugin.getInstance(this).getListener().onResult("-1", "未知错误");
        }
    }


}
