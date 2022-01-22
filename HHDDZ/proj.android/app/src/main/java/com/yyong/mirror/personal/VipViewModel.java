package com.yyong.mirror.personal;

import android.util.Log;
import android.view.View;

import androidx.databinding.ObservableField;
import androidx.databinding.ObservableInt;
import androidx.lifecycle.Observer;

import com.chinaums.pppay.unify.UnifyPayRequest;
import com.yyong.middleware.api.Api;
import com.yyong.mirror.api.ApiService;
import com.yyong.mirror.api.OrderResponse;
import com.yyong.mirror.personal.vo.VipPrice;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.CommonViewModel;
import com.zero.support.work.Response;

public class VipViewModel extends CommonViewModel {
    ObservableField<VipPrice> price = new ObservableField<>();
    ObservableInt pay = new ObservableInt(1);

    public ObservableInt getPayWay() {
        return pay;
    }

    public void setPrice(VipPrice price) {
        this.price.set(price);
    }

    public ObservableField<VipPrice> getPrice() {
        return price;
    }


    public void onClickPay(View view) {
        if (pay.get() == 1) {
            requestPayByAli();
//            AppGlobal.sendMessage("暂未开通");
        } else if (pay.get() == 2) {
            requestPayByWechat();
        }

    }

    public void requestPayByWechat() {
        postLoading("生成订单中");
//        Api.getService(ApiService.class).requestOrderByWechatTest(String.valueOf(price.get().getPrice())).observe(new com.zero.support.work.Observer<Response<String>>() {
//            @Override
//            public void onChanged(Response<String> stringResponse) {
//                Log.e("xgf", "onChanged: "+stringResponse );
//
//                Log.e("xgf", "onChanged: "+ new String(Base64.decode(URLDecoder.decode(stringResponse.data()), Base64.CRLF)));
//            }
//        });
        Api.getService(ApiService.class).
                requestOrderByWechat(String.valueOf(price.get().getPrice()))
                .asLive()
                .observe(this, new Observer<Response<OrderResponse>>() {
                    @Override
                    public void onChanged(Response<OrderResponse> orderResponseResponse) {
                        postDismiss();
                        if (orderResponseResponse.isSuccessful()) {
                            PayActivity.startActivity(requireActivity(), orderResponseResponse.data().appPayRequest, UnifyPayRequest.CHANNEL_WEIXIN);
                        } else {
                            AppGlobal.sendMessage("订单生成失败");
                        }
                    }
                });


    }

    public void requestPayByAli() {
        postLoading("生成订单中");
        Api.getService(ApiService.class).
                requestOrderByAli(String.valueOf(price.get().getPrice()))
                .asLive()
                .observe(this, new Observer<Response<OrderResponse>>() {
                    @Override
                    public void onChanged(Response<OrderResponse> orderResponseResponse) {
                        postDismiss();
                        if (orderResponseResponse.isSuccessful()) {
                            PayActivity.startActivity(requireActivity(), orderResponseResponse.data().appPayRequest, UnifyPayRequest.CHANNEL_ALIPAY_MINI_PROGRAM);
                        } else {
                            AppGlobal.sendMessage("订单生成失败");
                        }
                    }
                });


    }
}
