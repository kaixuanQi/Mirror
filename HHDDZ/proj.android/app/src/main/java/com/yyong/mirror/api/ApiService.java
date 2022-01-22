package com.yyong.mirror.api;

import com.yyong.mirror.version.VersionInfo;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

public interface ApiService {

    @POST("feedback.php")
    Observable<Response<String>> getFeedback(@Body Feedback feedback);

    @POST("upgrade.php")
    Observable<Response<VersionInfo>> requestUpdate();

    @POST("checkAttr.php")
    Observable<Response<List<VirtualAttribute>>> requestVirtualAttribute(@Body List<String> packages);

    @FormUrlEncoded
    @POST("https://bus.lightlivetv.com/splitmarker/unionpay/wxpcreate.php")
    Observable<Response<OrderResponse>> requestOrderByWechat(@Field("money") String price);

    @FormUrlEncoded
    @POST("http://test.excean.com/rgs/multiapi32022/splitmarker/unionpay/wxpcreate.php")
    Observable<Response<String>> requestOrderByWechatTest(@Field("money") String price);

    @FormUrlEncoded
    @POST("http://test.excean.com/rgs/multiapi32022/splitmarker/unionpay/alicreate.php")
    Observable<Response<OrderResponse>> requestOrderByAliTest(@Field("money") String price);

    @FormUrlEncoded
    @POST("https://bus.lightlivetv.com/splitmarker/unionpay/alicreate.php")
    Observable<Response<OrderResponse>> requestOrderByAli(@Field("money") String price);
}
