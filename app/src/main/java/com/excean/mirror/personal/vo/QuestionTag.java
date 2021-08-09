package com.excean.mirror.personal.vo;

import android.view.View;

import androidx.databinding.ObservableBoolean;

import com.excean.mirror.personal.FeedbackViewModel;
import com.zero.support.common.vo.BaseObject;
import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.ItemViewHolder;

public class QuestionTag extends BaseObject {
    private final String tag;
    private final int type;
    private final ObservableBoolean checked = new ObservableBoolean();

    public QuestionTag(int type,String tag) {
        this.tag = tag;
        this.type = type;
    }

    public int getType() {
        return type;
    }


    public String getTag() {
        return tag;
    }

    @Override
    public ObservableBoolean getChecked() {
        return checked;
    }

    @Override
    public void onItemClick(View view, ItemViewHolder holder) {
        super.onItemClick(view, holder);
        FeedbackViewModel viewModel = holder.<CellAdapter>getAdapter().getViewModel();
        viewModel.setCurrentQuestion(this);
    }
}
