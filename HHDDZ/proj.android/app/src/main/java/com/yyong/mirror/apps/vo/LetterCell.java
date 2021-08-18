package com.yyong.mirror.apps.vo;


import com.yyong.mirror.BR;
import com.yyong.mirror.R;
import com.zero.support.common.vo.BaseObject;
import com.zero.support.common.widget.recycler.SimpleViewBound;
import com.zero.support.common.widget.recycler.annotation.RecyclerViewBind;
import com.zero.support.common.widget.recycler.manager.StickyHeaders;

@RecyclerViewBind(LetterCell.LetterViewBound.class)
public class LetterCell extends BaseObject implements StickyHeaders {
    private String letter;

    public LetterCell(String letter) {
        this.letter = letter;
    }

    public String getLetter() {
        return letter;
    }

    @Override
    public boolean isStickyHeader(int position) {
        return true;
    }


    public static class LetterViewBound extends SimpleViewBound {

        public LetterViewBound() {
            super(BR.data, R.layout.view_bound_letter);
        }
    }
}
