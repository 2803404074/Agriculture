package com.tzl.agriculture.fragment.personal.activity.set;

import android.content.Intent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.util.UserData;

import butterknife.BindView;

/**
 * 昵称设置页面
 */
public class SetTextActivity extends SetBaseActivity {

    @BindView(R.id.tv_quit)
    TextView tvQuit;
    @BindView(R.id.et_nickname)
    EditText etNickName;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_set_text;
    }

    @Override
    public void initView() {
        setTitle("昵称");
        tvQuit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                UserData.instance(SetTextActivity.this).updateUserInfo(getToken(),3,etNickName.getText().toString());
                Intent intent = new Intent(SetTextActivity.this,UserMessActivity.class);
                intent.putExtra("name",etNickName.getText().toString());
                setResult(200, intent);
                finish();
            }
        });
    }

    @Override
    public void initData() {
        etNickName.setText(getIntent().getStringExtra("name"));
    }
}
