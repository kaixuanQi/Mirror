package com.excean.mirror.personal.vo;

import android.view.View;

import com.zero.support.common.vo.BaseObject;
import com.zero.support.common.widget.recycler.ItemViewHolder;

public class ManagerItem extends BaseObject {
    private int icon;
    private int name;
    private View.OnClickListener listener;

    public ManagerItem(int name, int icon, View.OnClickListener listener) {
        this.icon = icon;
        this.name = name;
        this.listener = listener;
    }

    public int getIcon() {
        return icon;
    }

    @Override
    public void onItemClick(View view, ItemViewHolder holder) {
        super.onItemClick(view, holder);
        if (listener != null) {
            listener.onClick(view);
        }
    }


    public int getName() {
        return name;
    }
}
