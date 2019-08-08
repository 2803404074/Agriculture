package com.tzl.agriculture.fragment.personal.activity.set;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.tzl.agriculture.R;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 身份认证界面
 */
public class IdentityActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_load)
    TextView tvLoad;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_identity;
    }
    @Override
    public void initView() {
        setTitle("身份认证");
        tvLoad.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_load:
                Intent intent = new Intent(IdentityActivity.this,UpLoadIdentifyActivity.class);
                startActivity(intent);
                break;
                default:break;
        }
    }
}
