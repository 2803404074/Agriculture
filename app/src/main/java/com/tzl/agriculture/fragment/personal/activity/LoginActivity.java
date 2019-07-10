package com.tzl.agriculture.fragment.personal.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.login.presenter.Presenter;
import com.tzl.agriculture.fragment.personal.login.view.IView;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.DrawableSizeUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;

import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;


public class LoginActivity extends BaseActivity<IView, Presenter> implements IView{

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.tv_wechat)
    TextView tvWechat;

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    public Presenter createPersenter() {
        return new Presenter();
    }

    @Override
    public void initView() {
        DrawableSizeUtil drawableSizeUtil = new DrawableSizeUtil(this);
        drawableSizeUtil.setImgSize(79, 79, 1, tvWechat, R.mipmap.wechat);

        EventHandler eh = new EventHandler() {
            @Override
            public void afterEvent(int event, int result, Object data) {
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                mHandler.sendMessage(msg);
            }
        };
        SMSSDK.registerEventHandler(eh);
    }

    @Override
    public void initData() {
        p.setSignIn("","");
    }



    public void getCode(View view){
        if(DateUtil.isPhoneNumber(etPhone.getText().toString())){
            SMSSDK.getVerificationCode("86",etPhone.getText().toString());
            Intent intent = new Intent(this,LoginGetCodeActivity.class);
            intent.putExtra("phone",etPhone.getText().toString());
            startActivity(intent);
        }else {
            ToastUtil.showShort(LoginActivity.this,"手机号输入有误,请从新输入");
        }
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "验证码发送失败", Toast.LENGTH_SHORT).show();
            }
            return false;
        }
    });

    @Override
    public void showToast(String msg) {

    }
}
