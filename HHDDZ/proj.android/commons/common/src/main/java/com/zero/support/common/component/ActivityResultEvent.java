package com.zero.support.common.component;

import android.app.Activity;
import android.content.Intent;

public class ActivityResultEvent {
    private final ActivityResultModel model;
    private final int resultCode;
    private final Intent data;

    public ActivityResultEvent(ActivityResultModel model, int resultCode, Intent data) {
        this.model = model;
        this.resultCode = resultCode;
        this.data = data;
    }

    public Intent data() {
        if (data == null) {
            return new Intent();
        }
        return data;
    }

    public int resultCode() {
        return resultCode;
    }

    public boolean isOk() {
        return resultCode == Activity.RESULT_OK;
    }

    @Override
    public String toString() {
        return "ActivityResultEvent{" +
                "model=" + model +
                ", resultCode=" + resultCode +
                ", data=" + data +
                '}';
    }
}
