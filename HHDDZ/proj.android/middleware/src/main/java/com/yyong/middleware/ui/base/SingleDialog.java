package com.yyong.middleware.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.yyong.vphone.middleware.R;
import com.yyong.vphone.middleware.databinding.DialogCommonBinding;
import com.yyong.vphone.middleware.databinding.DialogSingleBinding;
import com.zero.support.common.component.CommonDialog;
import com.zero.support.common.component.DialogModel;


public class SingleDialog extends CommonDialog {
    DialogSingleBinding binding;

    public SingleDialog(@NonNull Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_single, null, false);
        setContentView(binding.getRoot());
        setCancelable(false);
        getWindow().setBackgroundDrawable(null);

    }

    protected int getDialogHeight(WindowManager windowManager) {
        return ViewGroup.LayoutParams.WRAP_CONTENT;
    }

    @Override
    protected void onBindDialogModel(DialogModel model) {
        if (model instanceof SingleDialogModel) {
            binding.setData((SingleDialogModel) model);
            binding.content.setMovementMethod(LinkMovementMethod.getInstance());
        }

    }




    @Override
    protected void performClicked(int which) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            create();
        }
        if (which == DialogInterface.BUTTON_POSITIVE) {
            binding.positive.performClick();
        }
    }
}
