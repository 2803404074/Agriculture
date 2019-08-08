package com.tzl.agriculture.fragment.personal.activity.set;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;

import com.tzl.agriculture.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 账户安全页面
 */
public class AccountSaveActivity extends SetBaseActivity implements View.OnClickListener {

    //身份认证
    @BindView(R.id.rl_identify)
    RelativeLayout rlIdentify;

    //账号关联
    @BindView(R.id.rl_accountRelation)
    RelativeLayout rlAccountRelation;

    //修改/绑定手机号
    @BindView(R.id.rl_bindPhone)
    RelativeLayout rlBindPhone;

    //设备管理
    @BindView(R.id.rl_device_manage)
    RelativeLayout rlDeviceManage;


    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_acount_save;
    }

    @Override
    public void initView() {
        setTitle("账号安全");
        rlIdentify.setOnClickListener(this);
        rlAccountRelation.setOnClickListener(this);
        rlBindPhone.setOnClickListener(this);
        rlDeviceManage.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.rl_identify:
                Intent intent1 = new Intent(AccountSaveActivity.this, IdentityActivity.class);
                startActivity(intent1);
                break;
            case R.id.rl_accountRelation:
                Intent intent2 = new Intent(AccountSaveActivity.this, AccountRelationActivity.class);
                startActivity(intent2);
                break;
            case R.id.rl_bindPhone:
                Intent intent3 = new Intent(AccountSaveActivity.this, BindPhoneActivity.class);
                startActivity(intent3);
                break;
            case R.id.rl_device_manage:
                Intent intent4 = new Intent(AccountSaveActivity.this, DeviceManageActivity.class);
                startActivity(intent4);
                break;
                default:break;
        }
    }
}
