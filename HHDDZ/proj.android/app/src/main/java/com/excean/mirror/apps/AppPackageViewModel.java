package com.excean.mirror.apps;

import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.provider.Settings;

import androidx.databinding.ObservableBoolean;

import com.excean.middleware.ui.base.LocalDialogModel;
import com.excean.mirror.R;
import com.excean.mirror.apps.vo.LetterCell;
import com.excean.mirror.vo.MirrorPackage;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.ActivityResultEvent;
import com.zero.support.common.component.ActivityResultModel;
import com.zero.support.common.component.DataViewModel;
import com.zero.support.common.vo.Resource;
import com.zero.support.common.widget.SlideBar;
import com.zero.support.common.widget.recycler.Cell;
import com.zero.support.work.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class AppPackageViewModel extends DataViewModel<String, List<Cell>> {
    private static final Set<String> support = new HashSet<>();
    private final ObservableBoolean prepared = new ObservableBoolean();

    static {
        support.add("com.tencent.mm");
        support.add("com.tencent.mobileqq");
        support.add("com.taobao.taobao");
        support.add("com.eg.android.AlipayGphone");
        support.add("com.xunmeng.pinduoduo");
        support.add("com.ss.android.ugc.aweme");
        support.add("com.smile.gifmaker");
        support.add("com.kuaishou.nebula");
        support.add("com.tencent.tmgp.sgame");
        support.add("com.hero.sm.android.hero");
    }

    @Override
    protected void onViewModelCreated() {
        super.onViewModelCreated();
        notifyDataSetChanged(null);
    }

    public ObservableBoolean getPrepared() {
        return prepared;
    }

    @Override
    protected void onResourceChanged(Resource<List<Cell>> resource) {
        super.onResourceChanged(resource);
        if (resource.isSuccess()) {
            if (resource.isEmpty()) {
                requestDialog(new LocalDialogModel.Builder()
                        .content("尚未读取到应用信息，请求授权开启“读取应用列表”权限")
                        .negative(R.string.dialog_install_negative)
                        .positive(R.string.dialog_permission_positive)
                        .build()).click().observe(event -> {
                    if (event.isPositive()) {
                        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        intent.setData(Uri.parse("package:"+AppGlobal.getApplication().getPackageName()));
                        requestActivityResult(new ActivityResultModel(intent)).result().observe(new com.zero.support.work.Observer<ActivityResultEvent>() {
                            @Override
                            public void onChanged(ActivityResultEvent event) {
                                notifyDataSetChanged(null);
                            }
                        });
                    } else {
                        requireActivity().finish();
                        event.dismiss();
                    }
                });
            } else {
                prepared.set(true);
            }

        }
    }

    @Override
    protected Response<List<Cell>> performExecute(String s) {
        PackageManager pm = AppGlobal.getApplication().getPackageManager();
        List<PackageInfo> list = pm.getInstalledPackages(PackageManager.GET_META_DATA);
        Map<String, List<MirrorPackage>> map = new HashMap<>(list.size());
        Map<String, PackageInfo> set = new HashMap<>();
        int size = 0;
        for (PackageInfo info : list) {
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0) {
                continue;
            }
            if ((info.applicationInfo.flags & ApplicationInfo.FLAG_UPDATED_SYSTEM_APP) != 0) {
                continue;
            }
            if (info.packageName.endsWith(".mirror0")) {
                set.put(info.packageName.replace(".mirror0", ""), info);
                continue;
            }
            MirrorPackage mirrorPackage = new MirrorPackage(info, info.applicationInfo.loadLabel(pm).toString());
            List<MirrorPackage> mirrorPackages = map.get(mirrorPackage.getLetter());

            if (mirrorPackages == null) {
                mirrorPackages = new ArrayList<>();
                map.put(mirrorPackage.getLetter(), mirrorPackages);
                size++;
            }
            mirrorPackages.add(mirrorPackage);
        }

        List<Cell> cells = new ArrayList<>(size);
        for (String letter : SlideBar.LETTERS) {
            List<MirrorPackage> packages = map.get(letter);
            if (packages == null) {
                continue;
            }
            cells.add(new LetterCell(letter));
            cells.addAll(packages);
            for (MirrorPackage p : packages) {
                p.setMirrorPackageInfo(set.get(p.getPackageInfo().packageName));
            }
        }
        return Response.success(cells);
    }

    public int indexOf(List<Cell> cells, String letter) {
        for (int i = 0; i < cells.size(); i++) {
            Cell cell = cells.get(i);
            if (cell instanceof LetterCell) {
                if (((LetterCell) cell).getLetter().equalsIgnoreCase(letter)) {
                    return i;
                }
            }
        }
        return 0;
    }
}
