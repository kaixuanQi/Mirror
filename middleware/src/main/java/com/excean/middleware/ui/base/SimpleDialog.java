package com.excean.middleware.ui.base;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Build;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;

import com.excean.vphone.middleware.R;
import com.excean.vphone.middleware.databinding.DialogSimpleBinding;
import com.zero.support.common.component.CommonDialog;
import com.zero.support.common.component.DialogModel;


public class SimpleDialog extends CommonDialog {
    DialogSimpleBinding binding;

    public SimpleDialog(@NonNull Context context) {
        super(context, R.style.Theme_AppCompat_Dialog);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(Window.FEATURE_NO_TITLE);
        binding = DataBindingUtil.inflate(LayoutInflater.from(getContext()), R.layout.dialog_simple, null, false);
        setContentView(binding.getRoot());
        setCancelable(false);
        getWindow().setBackgroundDrawable(null);
    }


    @Override
    protected void onBindDialogModel(DialogModel model) {
        if (model instanceof SimpleDialogModel) {
            binding.setData((SimpleDialogModel) model);
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
        } else if (which == DialogInterface.BUTTON_NEGATIVE) {
            binding.negative.performClick();
        }
    }
}
