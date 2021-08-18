package com.yyong.mirror.api;

import com.yyong.mirror.version.VersionInfo;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;

import java.util.List;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("feedback.php")
    Observable<Response<String>> getFeedback(@Body Feedback feedback);

    @POST("upgrade.php")
    Observable<Response<VersionInfo>> requestUpdate();

    @POST("checkAttr.php")
    Observable<Response<List<VirtualAttribute>>> requestVirtualAttribute(@Body List<String> packages);
}
