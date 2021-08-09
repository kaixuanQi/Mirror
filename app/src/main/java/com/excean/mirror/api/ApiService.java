package com.excean.mirror.api;

import com.excean.mirror.version.VersionInfo;
import com.zero.support.work.Observable;
import com.zero.support.work.Response;

import retrofit2.http.Body;
import retrofit2.http.POST;

public interface ApiService {

    @POST("feedback.php")
    Observable<Response<String>> getFeedback(@Body Feedback feedback);

    @POST("upgrade.php")
    Observable<Response<VersionInfo>> requestUpdate();
}
