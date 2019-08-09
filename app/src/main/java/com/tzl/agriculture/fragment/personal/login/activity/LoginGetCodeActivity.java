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
import com.tzl.agriculture.util.TextUtil;
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
                getCodeMeth();
            }
        });
    }

    private void getCodeMeth() {
        Map<String, String> map = new HashMap<>();

        String strTag = "";
        if (StringUtils.isEmpty(getIntent().getStringExtra("regist"))){
            strTag = "login";
        }else {
            strTag = "register";
        }

        map.put("tempCode", strTag);
        map.put("phoneNum", phoneNumber);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.sendSms, str, getToken(),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                myCountDownTimer.start();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(LoginGetCodeActivity.this, "请求超时");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(LoginGetCodeActivity.this, "网络异常");
                            }
                        });
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
            spinKitView.setVisibility(View.GONE);
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("phoneNum", phoneNumber);
        map.put("code", etCode.getText().toString());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson(User.login, str, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        String token = dataObj.optString("token");
                        SPUtils.instance(LoginGetCodeActivity.this,1).put("token",token);
                        Intent intent = new Intent(LoginGetCodeActivity.this, MainActivity.class);
                        startActivity(intent);

                        finish();
                        if (LoginActivity.instance!=null){
                            LoginActivity.instance.finish();
                        }
                    }
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
        if (StringUtils.isEmpty(etCode.getText().toString())){
            ToastUtil.showShort(this,"验证码不能为空");
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("phoneNum", phoneNumber);
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
                            Intent intent = new Intent(LoginGetCodeActivity.this, RegistMessActivity.class);
                            intent.putExtra("phone", phoneNumber);
                            startActivity(intent);
                            finish();
                        }
                    }else {
                        ToastUtil.showShort(LoginGetCodeActivity.this,"状态码:"+object.optString("code , ")+object.optString("msg"));
                    }
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
