package com.excean.mirror;

import com.zero.support.common.AppGlobal;
import com.zero.support.common.util.SharedPreferenceObservable;

public class AppHolder {
    public static final SharedPreferenceObservable<Boolean> userGuide = AppGlobal.sharedPreferences("user_guide","privacy",false);

    public static final SharedPreferenceObservable<Boolean> userGuideProducer = AppGlobal.sharedPreferences("user_guide","producer",false);
    public static final SharedPreferenceObservable<Boolean> userGuideProducerClick = AppGlobal.sharedPreferences("user_guide","producer_click",false);


}
