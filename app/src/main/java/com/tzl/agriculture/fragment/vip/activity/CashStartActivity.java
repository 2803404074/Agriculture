package com.tzl.agriculture.fragment.vip.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tzl.agriculture.R;
import com.tzl.agriculture.application.AppManager;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;

import butterknife.BindView;

/**
 * 提现
 */
public class CashStartActivity extends SetBaseActivity {

    @BindView(R.id.tv_ok)
    TextView tvOk;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_cash_start;
    }

    @Override
    public void initView() {
        setTitle("提现进度");

        tvOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                AppManager.getAppManager().finishActivity(CashActivity.class);
            }
        });
    }

    @Override
    public void initData() {

    }
}
