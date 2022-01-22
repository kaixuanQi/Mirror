package com.yyong.mirror.personal;

import android.graphics.Paint;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.lifecycle.Observer;

import com.yyong.middleware.ui.base.TitleActivity;
import com.yyong.mirror.AppHolder;
import com.yyong.mirror.R;
import com.yyong.mirror.databinding.ActivityVipBinding;
import com.yyong.mirror.personal.vo.VipPrice;

public class VipActivity extends TitleActivity {
    private View mCurrent;
    private VipPrice vipPrice;
    private VipPrice vipPrice2;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        vipPrice = new VipPrice(1000,"10");
        vipPrice2 = new VipPrice(1680,"16.8");


        ActivityVipBinding binding = setBindingContentView(R.layout.activity_vip);
        setTitle("开通会员");
        VipViewModel viewModel = attachSupportViewModel(VipViewModel.class);
        binding.setViewModel(viewModel);
        binding.originPrice1.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        binding.originPrice2.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);
        binding.price1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrent!=v){
                    if (mCurrent!=null){
                        mCurrent.setSelected(false);
                    }
                    mCurrent = v;
                    v.setSelected(true);
                    viewModel.setPrice(vipPrice);
                }

            }
        });
        binding.price2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mCurrent!=v){
                    if (mCurrent!=null){
                        mCurrent.setSelected(false);
                    }
                    mCurrent = v;
                    v.setSelected(true);
                    viewModel.setPrice(vipPrice2);
                }

            }
        });
        binding.price1.performClick();


        binding.ali.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getPayWay().set(1);
            }
        });
        binding.wechat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewModel.getPayWay().set(2);
            }
        });
        AppHolder.vip.asLive().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(Boolean aBoolean) {
                if (aBoolean){
                    finish();
                }
            }
        });
    }
}
