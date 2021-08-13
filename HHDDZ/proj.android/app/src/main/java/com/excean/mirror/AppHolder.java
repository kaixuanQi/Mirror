package com.excean.mirror;

import android.content.SharedPreferences;

import com.excean.middleware.api.Api;
import com.excean.mirror.api.ApiService;
import com.excean.mirror.api.VirtualAttribute;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.SharedPreferenceObservable;
import com.zero.support.work.AppExecutor;
import com.zero.support.work.Response;

import java.util.List;

public class AppHolder {
    public static final SharedPreferenceObservable<Boolean> userGuide = AppGlobal.sharedPreferences("user_guide", "privacy", false);

    public static final SharedPreferenceObservable<Boolean> userGuideProducer = AppGlobal.sharedPreferences("user_guide", "producer", false);
    public static final SharedPreferenceObservable<Boolean> userGuideProducerClick = AppGlobal.sharedPreferences("user_guide", "producer_click", false);


    public static long getVirtualAttribute(String packageName) {
        SharedPreferences preferences = AppGlobal.sharedPreferences("virtual_attr");
        return preferences.getLong(packageName, 0L);
    }

    public static void updateVirtualAttribute(List<VirtualAttribute> attributes) {
        if (attributes == null) {
            return;
        }
        SharedPreferences preferences = AppGlobal.sharedPreferences("virtual_attr");
        SharedPreferences.Editor editor = preferences.edit();
        for (VirtualAttribute attr : attributes) {
            try {
                editor.putLong(attr.packageName, Long.parseLong(attr.attribute, 2));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        editor.putLong("time", System.currentTimeMillis());
        editor.apply();
    }

    public static void fetchVirtualAttributes(List<String> list) {
        AppExecutor.async().execute(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = AppGlobal.sharedPreferences("virtual_attr");
                long time = preferences.getLong("time", 0);
                long cur = System.currentTimeMillis();
                if ((cur - time) > 30 * 60 * 1000) {
                    Response<List<VirtualAttribute>> response = Api.getService(ApiService.class).requestVirtualAttribute(list).getFuture().getValue();
                    if (response.isSuccessful()) {
                        updateVirtualAttribute(response.data());
                    }
                }

            }
        });

    }
}
