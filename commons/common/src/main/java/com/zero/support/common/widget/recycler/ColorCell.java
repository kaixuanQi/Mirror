package com.zero.support.common.widget.recycler;

import android.graphics.Color;

import com.zero.support.common.vo.BaseObject;
import com.zero.support.common.widget.recycler.annotation.RecyclerViewBind;

@RecyclerViewBind(SquareViewBinder.class)
public class ColorCell extends BaseObject {
    private int color;

    public ColorCell(int color) {
        this.color = color;
    }

    public ColorCell(String color) {
        this.color = Color.parseColor(color);
    }

    public int getColor() {
        return color;
    }
}
