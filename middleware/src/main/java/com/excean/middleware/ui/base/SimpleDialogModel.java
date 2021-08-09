package com.excean.middleware.ui.base;

import android.content.DialogInterface;
import android.text.Html;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.ClickableSpan;
import android.text.style.URLSpan;
import android.view.View;

import androidx.databinding.ObservableBoolean;

import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.CommonDialog;
import com.zero.support.common.component.DialogModel;
import com.zero.support.work.Observable;


public class SimpleDialogModel extends DialogModel {
    public static final String GROUP_DIALOG = "dialogs";
    protected final String negative;
    protected final String positive;
    protected final String content;
    protected final String title;
    protected final boolean enableNotify;
    protected final ObservableBoolean neverNotify = new ObservableBoolean();
    protected final String notify;
    protected final Object[] args;
    protected final String key;
    protected final int times;
    protected final int currentTimes;
    Observable<Integer> preferences;
    protected int contentViewId;
    protected SimpleDialogModel(Builder builder) {
        super(builder.name);
        this.contentViewId = builder.contentViewId;
        this.negative = builder.negative;
        this.positive = builder.positive;
        this.title = builder.title;
        this.content = builder.content;

        this.notify = builder.notify;
        this.args = builder.args;
        this.key = builder.key;
        this.times = builder.times;
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
        return negative;
    }

    public String getPositive() {
        return positive;
    }

//    @Override
//    public void handleTag(boolean opening, String tag, Editable output, XMLReader xmlReader) {
//        if (opening){
//            if (tag.equals("app")){
//                output.append(requireViewModel().requireActivity().getText(R.string.app_name));
//            }
//        }
//    }

    public Spanned getContent() {
        if (content==null){
            return null;
        }
        String text;
        if (args != null) {
            text = String.format(content, args);
        } else {
            text = content;
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
                        SimpleDialogModel.this.onClickSpan(widget, span.getURL());
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
        WebViewActivity.open(requireViewModel().requireActivity(), url);
    }

    public String getTitle() {
        return title;
    }

    public boolean isEnableNotify() {
        return enableNotify;
    }

    public ObservableBoolean getNeverNotify() {
        return neverNotify;
    }

    public String getNotify() {
        return notify;
    }

    @Override
    protected CommonDialog onCreateDialog(CommonActivity activity) {
        return new SimpleDialog(activity);
    }

    public static class Builder {
        private String negative = "取消";
        private String positive = "确认";
        private String content;
        private String title = "提示";
        private boolean enableNotify;
        private String notify = "不再提醒";
        private Object[] args;
        private String key;
        private int times;
        private String name;
        private int contentViewId;

        public Builder name(String name) {
            this.name = name;
            return this;
        }

        public Builder negative(String negative) {
            this.negative = negative;
            return this;
        }

        public Builder positive(String positive) {
            this.positive = positive;
            return this;
        }

        public Builder content(String content) {
            this.content = content;
            return this;
        }

        public Builder content(String content, Object[] args) {
            this.content = content;
            this.args = args;
            return this;
        }
        public Builder contentView(int content) {
            this.contentViewId = content;
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

        public Builder notify(String notify) {
            this.notify = notify;
            this.enableNotify = true;
            return this;
        }

        public Builder title(String title) {
            this.title = title;
            return this;
        }

        public SimpleDialogModel build() {
            return new SimpleDialogModel(this);
        }
    }
}
