package com.excean.mirror.vo;

import android.content.pm.PackageInfo;
import android.view.View;

import com.excean.mirror.apps.util.PinyinUtil;
import com.zero.support.common.vo.BaseObject;
import com.zero.support.common.widget.recycler.CellAdapter;
import com.zero.support.common.widget.recycler.ItemViewHolder;

import java.io.Serializable;

public class MirrorPackage extends BaseObject implements Serializable {
    private PackageInfo info;
    private String name;
    private String indexKey;
    private String letter;
    private PackageInfo mirrorPackageInfo;

    public void setMirrorPackageInfo(PackageInfo mirrorPackageInfo) {
        this.mirrorPackageInfo = mirrorPackageInfo;
    }

    public boolean isHasMirror() {
        return mirrorPackageInfo != null;
    }

    public PackageInfo getMirrorPackageInfo() {
        return mirrorPackageInfo;
    }

    public MirrorPackage(PackageInfo info, String name) {
        this.info = info;
        this.name = name;
        this.indexKey = PinyinUtil.chineseToSpell(name);
        this.letter = PinyinUtil.formatAlpha(indexKey);
    }

    public String getLetter() {
        return letter;
    }

    public PackageInfo getPackageInfo() {
        return info;
    }

    public String getName() {
        return name;
    }


    @Override
    public void onItemClick(View view, ItemViewHolder holder) {
        if (holder.itemView != view) {
            CellAdapter adapter = holder.getAdapter();
            adapter.performClick(view, holder);
        }
    }
}
