package com.yyong.mirror.version;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;

import androidx.lifecycle.MediatorLiveData;

import com.yyong.middleware.ui.base.LocalDialogModel;
import com.yyong.mirror.R;
import com.yyong.mirror.plugin.InstallProvider;
import com.zero.support.common.ActivityManager;
import com.zero.support.common.AppGlobal;
import com.zero.support.common.component.ActivityResultEvent;
import com.zero.support.common.component.ActivityResultModel;
import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.CommonViewModel;
import com.zero.support.common.component.DialogClickEvent;
import com.zero.support.common.component.DialogModel;
import com.zero.support.common.component.RequestViewModel;
import com.zero.support.work.NetObservable;
import com.zero.support.work.Observer;
import com.zero.support.work.PromiseObservable;
import com.zero.support.work.Snapshot;
import com.zero.support.work.WorkErrorCode;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

public class VersionMonitor {
    public final static VersionMonitor INSTANCE = new VersionMonitor();
    private final UpgradeOperation operation;
    private final PromiseObservable<DialogModel> dialog = new PromiseObservable<>();
    private final ProgressDialog progressDialog = new ProgressDialog();
    private final Set<Class<?>> ignores = new HashSet<>();
    private final MediatorLiveData<DialogModel> uiEvent = new MediatorLiveData<>();

    public VersionMonitor() {
        this.operation = UpgradeOperation.getDefault();
        operation.snapshot().observe(new Observer<Snapshot>() {
            @Override
            public void onChanged(Snapshot snapshot) {
                if (snapshot.isRunning()) {
                    progressDialog.setProgress(snapshot.progress().progress());
                } else if (snapshot.isFinished()) {
                    int code = snapshot.code();

                    if (code == 0) {
                        dismissModel(progressDialog);
                        dialog.setValue(createInstallDialog(snapshot.data()));
                    } else if (code == WorkErrorCode.DISABLE_CELL_DATA) {
                        dismissModel(progressDialog);
                        dialog.setValue(createCellDataDialog(operation.getVersionInfo()));
                    } else if (code == WorkErrorCode.STORAGE_OVER_FLOW) {
                        dismissModel(progressDialog);
                        dialog.setValue(createOverFlowDialog());
                    } else if (code == WorkErrorCode.NETWORK_CONNECT_ERROR) {
                        AppGlobal.sendMessage("????????????");
                        waitingNetworkToContinue();
                    } else {
                        waitingNetworkToContinue();
                        AppGlobal.sendMessage("???????????????????????????");
                    }
                }
            }
        });
        operation.versionState().observe(new Observer<VersionState>() {
            @Override
            public void onChanged(VersionState versionState) {
                if (versionState.isPending()) {
                    versionState.getVersionInfo().createFileRequest();
                    dialog.setValue(createUpdateDialog(versionState.getVersionInfo()));
                } else if (versionState.isDownloading()) {
                    if (!dialog.contains(progressDialog)) {
                        progressDialog.setVersionInfo(versionState.getVersionInfo());
                        progressDialog.reset();
                        dialog.setValue(progressDialog);
                    }
                } else if (versionState.isFinish()) {

                }
            }
        });

        final androidx.lifecycle.Observer<DialogModel> source = model -> uiEvent.setValue(model);
        uiEvent.addSource(ActivityManager.getTopObservable().asLive(), activity -> {
            if (activity!=null){
                uiEvent.removeSource(dialog.asLive());
                uiEvent.addSource(dialog.asLive(), source);
            }

        });
        uiEvent.observeForever(model -> {
            if (model == null) {
                return;
            }
            Activity activity = ActivityManager.getTopActivity();
            if (activity == null) {
                return;
            }
            if (ignores.contains(activity.getClass())) {
                return;
            }
            if (model.requireViewModel() != null) {
                if (model.requireViewModel().getActivity() != activity) {
                    model.dismiss();
                    model.reset();
                }
            }
            if (activity instanceof CommonActivity) {
                RequestViewModel viewModel = ((CommonActivity) activity).peekViewModel(RequestViewModel.class);
                viewModel.requestDialog(model);
            }
        });
    }

    private void waitingNetworkToContinue() {
        AppGlobal.netStatus().observe(new NetworkWatcher());
    }

    private class NetworkWatcher implements Observer<Integer> {

        @Override
        public void onChanged(Integer integer) {
            if (integer != NetObservable.NETWORK_NONE) {
                AppGlobal.netStatus().remove(this);
                operation.requestContinued();
            }
        }
    }

    private void dismissModel(DialogModel model) {
        model.dismiss();
        dialog.remove(model);
    }


    public static void inject(Class<?>... activity) {
        INSTANCE.ignores.addAll(Arrays.asList(activity));
    }


    private DialogModel createUpdateDialog(VersionInfo versionInfo) {
        DialogModel model = new LocalDialogModel.Builder()
                .title(R.string.dialog_version_title)
                .content(versionInfo.content)
                .html(false)
                .icon(R.drawable.ic_logo_update)
                .build();
        model.click().observe(new Observer<DialogClickEvent>() {
            @Override
            public void onChanged(DialogClickEvent dialogClickEvent) {
                if (dialogClickEvent.isPositive()) {
                    operation.requestContinued(model.requireViewModel().requireActivity());
                } else {
                    operation.clearVersion();
                    if (versionInfo.force) {
                        ActivityManager.finishAll();
                    }
                }
                dismissModel(model);
            }
        });
        return model;
    }


    private DialogModel createCellDataDialog(VersionInfo versionInfo) {
        DialogModel model = new LocalDialogModel.Builder()
                .name("dialog_cell_data")
                .title(R.string.dialog_title)
                .content(R.string.dialog_error_disable_cell_data)
                .positive(R.string.dialog_error_disable_cell_data_positive)
                .negative(R.string.dialog_install_negative)
                .build();
        model.click().observe(new Observer<DialogClickEvent>() {
            @Override
            public void onChanged(DialogClickEvent dialogClickEvent) {
                if (dialogClickEvent.isPositive()) {
                    operation.requestUpgrade(model.requireViewModel().requireActivity(), true);
                } else {
                    if (versionInfo.force) {
                        ActivityManager.finishAll();
                    }
                    operation.clearVersion();
                }
                dismissModel(model);

            }
        });
        return model;
    }


    private DialogModel createOverFlowDialog() {
        DialogModel model = new LocalDialogModel.Builder()
                .title(R.string.dialog_title)
                .name("dialog_download_error_storage")
                .content(R.string.dialog_error_storage)
                .positive(R.string.dialog_error_storage_positive)
                .negative(R.string.dialog_error_storage_negative)
                .build();
        model.click().observe(new Observer<DialogClickEvent>() {
            @Override
            public void onChanged(DialogClickEvent dialogClickEvent) {
                if (dialogClickEvent.isPositive()) {
                    operation.requestContinued(model.requireViewModel().requireActivity());
                    dismissModel(model);
                } else {
                    Intent intent = new Intent(Settings.ACTION_MANAGE_ALL_APPLICATIONS_SETTINGS);
                    dialogClickEvent.model().requireViewModel().requireActivity().startActivity(intent);
                }
            }
        });
        return model;
    }

    private DialogModel createInstallDialog(File file) {
        DialogModel model = new LocalDialogModel.Builder()
                .title(R.string.dialog_version_title)
                .name("install_dialog")
                .content(R.string.dialog_install_content)
                .positive(R.string.dialog_install_positive)
                .negative(R.string.dialog_install_negative)
                .build();
        model.click().observe(new Observer<DialogClickEvent>() {
            @Override
            public void onChanged(DialogClickEvent dialogClickEvent) {
                if (dialogClickEvent.isPositive()) {
                    RequestViewModel viewModel = ActivityManager.getFirstSupportActivity().attachSupportViewModel(RequestViewModel.class);
                    requestInstall(viewModel, file);
                } else {
                    dismissModel(model);
                    if (operation.getVersionInfo().force) {
                        operation.clearVersion();
                        ActivityManager.finishAll();
                    } else {
                        operation.clearVersion();
                    }

                }
            }
        });
        return model;
    }

    private void requestInstall(CommonViewModel viewModel, File file) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
        Uri uri;
        if (Build.VERSION.SDK_INT >= 24) {
            uri = InstallProvider.getUriForFile(file);
        } else {
            uri = Uri.fromFile(file);
        }
        intent.setDataAndType(uri, "application/vnd.android.package-archive");
        viewModel.requestActivityResult(new ActivityResultModel(intent)).result().observe(new Observer<ActivityResultEvent>() {
            @Override
            public void onChanged(ActivityResultEvent event) {

            }
        });
    }
}
