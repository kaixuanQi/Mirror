package com.zero.support.quick;

import android.app.Dialog;
import android.content.DialogInterface;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

import androidx.appcompat.app.AlertDialog;
import androidx.databinding.ObservableBoolean;

import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.DialogModel;
import com.zero.support.work.Observable;


public class LocalDialogModel extends DialogModel {
    public static final String GROUP_DIALOG = "dialogs";
    protected final int negative;
    protected final int positive;
    protected final int content;
    protected final int title;
    protected final boolean enableNotify;
    protected final ObservableBoolean neverNotify = new ObservableBoolean();
    protected final int notify;
    protected final Object[] args;
    protected final String key;
    protected final int times;
    protected final int currentTimes;
    Observable<Integer> preferences;
    private ClickInterceptor interceptor;
    protected int contentViewId;

    protected LocalDialogModel(Builder builder) {
        super(builder.name);
        this.negative = builder.negative;
        this.positive = builder.positive;
        this.title = builder.title;
        this.content = builder.content;

        this.notify = builder.notify;
        this.args = builder.args;
        this.key = builder.key;
        this.times = builder.times;
        this.interceptor = builder.interceptor;
        if (!TextUtils.isEmpty(key)) {
            preferences = AppGlobal.sharedPreferences(GROUP_DIALOG, key, 0);
            currentTimes = preferences.getValue();
            if (currentTimes >= times) {
                enableNotify = true;
            } else {
                enableNotify = false;
            }
        } else {
            this.enableNotify = builder.enableNotify;
            currentTimes = 0;
        }
    }


    @Override
    public void onClick(View view, int which) {
        super.onClick(view, which);
        if (preferences != null) {
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                preferences.setValue(currentTimes + 1);
            }
        }


    }

    public String getNegative() {
        return requireViewModel().requireActivity().getString(negative);
    }

    public String getPositive() {
        return requireViewModel().requireActivity().getString(positive);
    }

    public Spanned getContent() {
        String text;
        if (args != null) {
            text = requireViewModel().requireActivity().getString(content, args);
        } else {
            text = requireViewModel().requireActivity().getString(content);
        }
        try {
            SpannableStringBuilder spanned = (SpannableStringBuilder) Html.fromHtml(text);
            URLSpan[] urlSpans = spanned.getSpans(0, spanned.length(), URLSpan.class);
            for (final URLSpan span : urlSpans) {
                int start = spanned.getSpanStart(span);
                int end = spanned.getSpanEnd(span);
                int flag = spanned.getSpanFlags(span);
                ClickableSpan clickableSpan = new URLSpan(span.getURL()) {
                    @Override
                    public void onClick(View widget) {
                        String url = span.getURL();
                        if (interceptor != null) {
                            url = interceptor.intercept(url);
                        }
                        LocalDialogModel.this.onClickSpan(widget, url);
                    }
                };
                spanned.removeSpan(span);
                spanned.setSpan(clickableSpan, start, end, flag);

            }
            return spanned;

        } catch (Exception e) {
            e.printStackTrace();
        }
        return new SpannableString(text);
    }

    public void onClickSpan(View widget, String url) {

    }

    public String getTitle() {
        return requireViewModel().requireActivity().getString(title);
    }

    public boolean isEnableNotify() {
        return enableNotify;
    }

    public ObservableBoolean getNeverNotify() {
        return neverNotify;
    }

    public String getNotify() {
        return requireViewModel().requireActivity().getString(notify);
    }

    @Override
    protected Dialog onCreateDialog(CommonActivity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String value = getTitle();
        if (value != null) {
            builder.setTitle(value);
        }

        if (contentViewId != 0) {
            builder.setView(contentViewId);
        } else {
            Spanned spanned = getContent();
            builder.setMessage(spanned);
        }
        DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dispatchClickEvent(null, which);
            }
        };
        builder.setNegativeButton(getNegative(), listener);
        builder.setPositiveButton(getPositive(), listener);
        return builder.create();
    }

    public static class Builder {
        private int negative;
        private int positive;
        private int content;
        private int title;
        private boolean enableNotify;
        private int notify;
        private Object[] args;
        private String key;
        private int times;
        private String name;
        private ClickInterceptor interceptor;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder negative(int negative) {
            this.negative = negative;
            return this;
        }

        public Builder positive(int positive) {
            this.positive = positive;
            return this;
        }

        public Builder content(int content) {
            this.content = content;
            return this;
        }

        public Builder clickInterceptor(ClickInterceptor interceptor) {
            this.interceptor = interceptor;
            return this;
        }

        public Builder content(int content, Object[] args) {
            this.content = content;
            this.args = args;
            return this;
        }

        public Builder enableNotify() {
            this.enableNotify = true;
            return this;
        }

        public Builder enableNotify(String key, int times) {
            this.enableNotify = true;
            this.key = key;
            this.times = times;
            return this;
        }

        public Builder notify(int notify) {
            this.notify = notify;
            this.enableNotify = true;
            return this;
        }

        public Builder title(int title) {
            this.title = title;
            return this;
        }

        public LocalDialogModel build() {
            return new LocalDialogModel(this);
        }
    }
}
