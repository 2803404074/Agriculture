package com.tzl.agriculture.fragment.personal.activity;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.tzl.agriculture.R;
import com.tzl.agriculture.databinding.ActivityLoginGetCodeBinding;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import butterknife.ButterKnife;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import config.DyUrl;
import okhttp3.Call;

public class LoginGetCodeActivity extends AppCompatActivity {

    @BindView(R.id.tv_code_check)
    TextView tvCheck;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.tv_recode)
    TextView tvReGet;


    private MyCountDownTimer myCountDownTimer;
    private String phoneNumber;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityLoginGetCodeBinding binding = DataBindingUtil.setContentView(this,R.layout.activity_login_get_code);
        phoneNumber = getIntent().getStringExtra("phone");
        binding.setInfo(phoneNumber);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {

        myCountDownTimer = new MyCountDownTimer(60000, 1000);
        myCountDownTimer.start();


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
        tvCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                JudgeCode();
            }
        });
        //重新发送
        tvReGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SMSSDK.getVerificationCode("86", phoneNumber);
                myCountDownTimer.start();
            }
        });

    }

    private void JudgeCode() {
        Map<String, String> map = new HashMap<>();
        map.put("appkey", "2b90c67bade82");
        map.put("phone", phoneNumber);
        map.put("zone", "86");
        map.put("code", etCode.getText().toString());

        OkHttp3Utils.getInstance(DyUrl.BASE).doPost(DyUrl.PHONE_CODE, map, new GsonObjectCallback<String>(DyUrl.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
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


    /**
     * 倒计时函数
     */
    private class MyCountDownTimer extends CountDownTimer {

        public MyCountDownTimer(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        //计时过程
        @Override
        public void onTick(long l) {
            //防止计时过程中重复点击
            tvReGet.setClickable(false);
            tvReGet.setText(l / 1000 + "秒");

        }

        //计时完毕的方法
        @Override
        public void onFinish() {
            //重新给Button设置文字
            tvReGet.setText("重新获取");
            //设置可点击
            tvReGet.setClickable(true);
        }
    }
}
