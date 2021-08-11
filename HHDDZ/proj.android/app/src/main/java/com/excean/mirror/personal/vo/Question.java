package com.excean.mirror.personal.vo;

import androidx.databinding.ObservableBoolean;

import com.zero.support.common.vo.BaseObject;

public class Question extends BaseObject {
    private int title;
    private int content;
    private ObservableBoolean collapse = new ObservableBoolean();

    public Question(int title, int content) {
        this.title = title;
        this.content = content;
    }

    public ObservableBoolean getCollapse() {
        return collapse;
    }

    public int getContent() {
        return content;
    }

    public int getTitle() {
        return title;
    }
}
