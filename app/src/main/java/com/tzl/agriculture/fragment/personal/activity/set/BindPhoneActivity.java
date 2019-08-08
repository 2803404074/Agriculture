package com.tzl.agriculture.fragment.personal.activity.set;

import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.util.UserData;

import org.apache.commons.lang.StringUtils;

import butterknife.BindView;

/**
 * 绑定手机
 */
public class BindPhoneActivity extends SetBaseActivity {

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_bind_phone;
    }

    @Override
    public void initView() {
        setTitle("绑定手机");
    }

    @Override
    public void initData() {
        String phone = UserData.instance(this).getUsreInfo().getPhone();
        if (StringUtils.isEmpty(phone)){
            tvPhone.setText("未绑定手机");
        }else {
            tvPhone.setText(phone);
        }
    }
}
