package com.excean.middleware.ui.base;

import com.zero.support.common.component.CommonActivity;
import com.zero.support.common.component.CommonDialog;
import com.zero.support.common.component.DialogModel;


public class BaseActivity extends CommonActivity {
    @Override
    protected CommonDialog onCreateSupportDialog(DialogModel model) {
        if (model instanceof LocalDialogModel){
            return new BaseDialog(this);
        }
        return super.onCreateSupportDialog(model);
    }


}
