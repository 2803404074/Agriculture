package com.tzl.agriculture.fragment.personal.activity.set;

import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.widget.TextView;

import com.tzl.agriculture.R;

import butterknife.BindView;

/**
 * 设备管理
 */
public class DeviceManageActivity extends SetBaseActivity {

    @BindView(R.id.tv_devName)
    TextView tvDevName;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_device_manage;
    }

    @Override
    public void initView() {
        setTitle("设备管理");
    }

    @Override
    public void initData() {
        String model= android.os.Build.MODEL;
        tvDevName.setText(model);

    }

}
