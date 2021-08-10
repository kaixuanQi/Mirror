package com.excean.mirror;

import android.content.SharedPreferences;

import com.excean.mirror.api.VirtualAttribute;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.SharedPreferenceObservable;

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
            editor.putLong(attr.packageName, Long.parseLong(attr.attribute));
        }
        editor.apply();
    }
}
