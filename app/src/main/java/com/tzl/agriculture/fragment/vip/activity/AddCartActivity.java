package com.tzl.agriculture.fragment.vip.activity;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;

import butterknife.BindView;

/**
 * 提现
 */
public class AddCartActivity extends SetBaseActivity {

    @BindView(R.id.et_number)
    EditText etNumber;

    @BindView(R.id.et_name)
    EditText etName;

    @BindView(R.id.tv_nextx)
    TextView tvNext;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_add_cart;
    }

    @Override
    public void initView() {
        setTitle("添加银行卡");

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkInput();
            }
        });
    }

    private void checkInput() {
        if (StringUtils.isEmpty(etName.getText().toString())){
            ToastUtil.showShort(this,"请输入持卡人");
            return;
        }
        if (StringUtils.isEmpty(etNumber.getText().toString())){
            ToastUtil.showShort(this,"请输入银行卡号码");
            return;
        }

        Intent intent = new Intent(this,ChackCartActivity.class);
        intent.putExtra("number",etNumber.getText().toString());
        intent.putExtra("name",etName.getText().toString());
        startActivity(intent);
        finish();
    }

    @Override
    public void initData() {

    }


}
