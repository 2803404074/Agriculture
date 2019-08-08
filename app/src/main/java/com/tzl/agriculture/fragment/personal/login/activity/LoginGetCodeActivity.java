package com.tzl.agriculture.fragment.personal.login.activity;

import android.content.Intent;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.github.ybq.android.spinkit.SpinKitView;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.DoubleBounce;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.R;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import config.User;
import okhttp3.Call;

/**
 * 填写验证码页面(手机注册和手机登陆公共的接手验证码activity)
 */
public class LoginGetCodeActivity extends SetBaseActivity {

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;

    public static LoginGetCodeActivity instance;
    @BindView(R.id.tv_code_check)
    TextView tvCheck;

    @BindView(R.id.et_code)
    EditText etCode;

    @BindView(R.id.tv_recode)
    TextView tvReGet;


    private MyCountDownTimer myCountDownTimer;
    private String phoneNumber;

    @BindView(R.id.tv_phone_tips)
    TextView tvPhoneTips;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_login_get_code;
    }

    @Override
    public void initView() {
        instance = this;
        Sprite ddd = new DoubleBounce();
        setTitle("验证码");
        phoneNumber = getIntent().getStringExtra("phone");
        tvPhoneTips.setText("我们已将验证码发送至您"+phoneNumber+",请注意查收请注意查收");

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
                spinKitView.setVisibility(View.VISIBLE);
                spinKitView.setIndeterminateDrawable(ddd);
                tvCheck.setBackgroundResource(R.drawable.shape_login_blue_hide);
                tvCheck.setClickable(false);
                if (StringUtils.isEmpty(getIntent().getStringExtra("regist"))){
                    loginJudge();//登陆的验证
                }else {
                    registJudgeCode();//注册的验证
                }
            }
        });
        //重新发送
        tvReGet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //SMSSDK.getVerificationCode("86", phoneNumber);
                myCountDownTimer.start();
            }
        });

    }

    @Override
    public void initData() {

    }

    //登陆，验证验证码，并保存token，跳转首页
    private void loginJudge() {
        if (StringUtils.isEmpty(phoneNumber)){
            ToastUtil.showShort(this,"手机号不能为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("appkey", "2b95262c53922");
        map.put("phone", phoneNumber);
        map.put("zone", "86");
        map.put("code", etCode.getText().toString());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson(User.login, str, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == -1){
                        ToastUtil.showShort(LoginGetCodeActivity.this,object.optString("msg"));
                        return;
                    }
                    JSONObject dataObj = object.optJSONObject("data");
                    String token = dataObj.optString("token");
                    if (!StringUtils.isEmpty(token)){
                        Intent intent = new Intent(LoginGetCodeActivity.this, MainActivity.class);
                        SPUtils.instance(LoginGetCodeActivity.this,1).put("login","1");
                        SPUtils.instance(LoginGetCodeActivity.this,1).put("token",token);
                        startActivity(intent);
                        finish();
                        if (LoginActivity.instance!=null){
                            LoginActivity.instance.finish();
                        }
                    }
                    tvCheck.setBackgroundResource(R.drawable.shape_login_blue);
                    tvCheck.setClickable(true);

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            @Override
            public void onFailed(Call call, IOException e) {
                Log.e("AAA","异常："+e.getMessage());
            }
        });
    }


    //注册，仅验证验证码
    private void registJudgeCode() {
        if (StringUtils.isEmpty(phoneNumber)){
            ToastUtil.showShort(this,"手机号不能为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("appkey", "2b95262c53922");
        map.put("phone", phoneNumber);
        map.put("zone", "86");
        map.put("code", etCode.getText().toString());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson(User.checkCode, str, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        if (object.optBoolean("data")){
                            //注册信息页面
                            if (!StringUtils.isEmpty(getIntent().getStringExtra("regist"))) {
                                Intent intent = new Intent(LoginGetCodeActivity.this, RegistMessActivity.class);
                                intent.putExtra("phone", phoneNumber);
                                intent.putExtra("openId", getIntent().getStringExtra("openId"));
                                intent.putExtra("userName", getIntent().getStringExtra("userName"));
                                intent.putExtra("sex", getIntent().getIntExtra("sex",1));
                                intent.putExtra("imgUrl", getIntent().getStringExtra("imgUrl"));
                                startActivity(intent);
                                finish();
                            } else {
                                //直接登陆
                                Intent intent = new Intent(LoginGetCodeActivity.this, MainActivity.class);
                                SPUtils.instance(LoginGetCodeActivity.this, 1).put("login", "1");
                                startActivity(intent);
                                finish();
                                LoginActivity.instance.finish();
                            }
                        }
                    }else {
                        ToastUtil.showShort(LoginGetCodeActivity.this,"状态码:"+object.optString("code , ")+object.optString("msg"));
                    }
                    tvCheck.setBackgroundResource(R.drawable.shape_login_blue);
                    tvCheck.setClickable(true);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                Log.e("AAA","异常："+e.getMessage());
            }
        });
    }

    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
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
