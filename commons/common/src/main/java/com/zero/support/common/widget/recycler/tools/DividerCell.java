package com.zero.support.common.widget.recycler.tools;

import android.view.View;

import com.zero.support.common.widget.recycler.Cell;
import com.zero.support.common.widget.recycler.ItemViewHolder;

public class DividerCell implements Cell {

    @Override
    public void onItemClick(View view, ItemViewHolder holder) {

    }

    @Override
    public boolean onLongItemClick(View view, ItemViewHolder holder) {
        return false;
    }

}
