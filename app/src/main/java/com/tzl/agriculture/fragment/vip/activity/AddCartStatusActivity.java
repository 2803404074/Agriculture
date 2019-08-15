package com.tzl.agriculture.fragment.vip.activity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;

/**
 * 提现
 */
public class AddCartStatusActivity extends SetBaseActivity {


    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_add_cartstatus;
    }

    @Override
    public void initView() {
        setTitle("验证手机号");
    }

    @Override
    public void initData() {

    }
}
