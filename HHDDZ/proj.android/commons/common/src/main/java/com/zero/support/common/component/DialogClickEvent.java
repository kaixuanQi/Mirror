package com.zero.support.common.component;

import android.content.DialogInterface;

public class DialogClickEvent {
    private DialogModel model;
    private int which;

    public DialogClickEvent(DialogModel model, int which) {
        this.which = which;
        this.model = model;
    }

    public <T extends DialogModel> T model() {
        return (T) model;
    }

    public int which() {
        return which;
    }

    public boolean isPositive() {
        return which == DialogInterface.BUTTON_POSITIVE;
    }

    public boolean isNegative() {
        return which == DialogInterface.BUTTON_NEGATIVE;
    }

    public void dismiss() {
        model.dismiss();
    }
}
