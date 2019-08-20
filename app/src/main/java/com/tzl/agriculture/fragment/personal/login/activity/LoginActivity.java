package com.tzl.agriculture.fragment.personal.login.activity;

import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.github.ybq.android.spinkit.SpinKitView;
import com.tencent.mm.opensdk.modelmsg.SendAuth;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzl.agriculture.R;
import com.tzl.agriculture.application.AgricultureApplication;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.DrawableSizeUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;
import com.tzl.agriculture.view.BaseActivity;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.User;
import okhttp3.Call;

/**
 * 登陆页面
 */
public class LoginActivity extends BaseActivity implements View.OnClickListener {

    public static LoginActivity instance;

    @BindView(R.id.spin_kit)
    SpinKitView spinKitView;


    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.tv_wechat)
    TextView tvWechat;

    @BindView(R.id.tv_regist)
    TextView tvRegist;

    @BindView(R.id.tv_next)
    TextView tvNext;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @Override
    public int setLayout() {
        return R.layout.activity_login;
    }

    @Override
    public void initView() {
        instance = this;

        tvWechat.setOnClickListener(this);

        DrawableSizeUtil drawableSizeUtil = new DrawableSizeUtil(this);
        drawableSizeUtil.setImgSize(79, 79, 1, tvWechat, R.mipmap.wechat);


        tvRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this, PhoneRegistActivity.class);
                intent.putExtra("phone",TextUtil.checkStr2Str(etPhone.getText().toString()));
                startActivity(intent);
            }
        });

        etPhone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                if (DateUtil.isPhoneNumber(etPhone.getText().toString())) {
                    judgePhoneNumber(etPhone.getText().toString());
                    spinKitView.setVisibility(View.VISIBLE);
                }
            }
        });

        tvNext.setClickable(false);

        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getCodeMeth();
            }
        });
    }

    private void getCodeMeth() {
        Map<String, String> map = new HashMap<>();
        map.put("tempCode", "login");
        map.put("phoneNum", etPhone.getText().toString());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.sendSms, str, getToken(this),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                Intent intent = new Intent(LoginActivity.this, LoginGetCodeActivity.class);
                                intent.putExtra("phone",etPhone.getText().toString());
                                startActivity(intent);
                            }
                            ToastUtil.showShort(LoginActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(LoginActivity.this, "请求超时");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(LoginActivity.this, "网络异常");
                            }
                        });
                    }
                });
    }

    @Override
    public void initData() {

    }


    /**
     * 输入完整的手机号，请求后端判断是否已存在
     *
     * @param number
     */
    private void judgePhoneNumber(String number) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", number);
        String strJson = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson(User.checkPhone, strJson, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                System.out.println("result = [" + result + "]");
                spinKitView.setVisibility(View.GONE);
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 401) {
                        tvTips.setVisibility(View.VISIBLE);
                        tvTips.setText(object.optString("msg"));
                        tvNext.setClickable(false);
                    }
                    if (object.optInt("code") == -1) {
                        tvTips.setVisibility(View.VISIBLE);
                        tvTips.setText(object.optString("msg"));
                        tvNext.setClickable(false);
                    }
                    //请求成功
                    if (object.optInt("code") == 0) {
                        if (!object.optBoolean("data")) {//该手机未注册，不存在
                            tvTips.setVisibility(View.VISIBLE);
                            tvTips.setText("该手机号未注册，请前往注册");
                            etPhone.setTextColor(getResources().getColor(R.color.colorAccentButton));
                            tvNext.setBackgroundResource(R.drawable.shape_login_blue_hide);
                            tvNext.setClickable(false);
                        } else if (object.optBoolean("data")) {
                            tvTips.setVisibility(View.GONE);
                            etPhone.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                            tvNext.setBackgroundResource(R.drawable.shape_login_blue);
                            tvNext.setClickable(true);
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                System.out.println("call = [" + call + "], e = [" + e + "]");
                spinKitView.setVisibility(View.GONE);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_wechat:
                wechatLogin();
                break;
            default:
                break;
        }
    }

    /**
     * 微信登陆
     */
    private void wechatLogin() {
        if (AgricultureApplication.api == null) {
            AgricultureApplication.api = WXAPIFactory.createWXAPI(this, AgricultureApplication.APP_ID, true);
        }
        if (!AgricultureApplication.api.isWXAppInstalled()) {
            ToastUtil.showShort(this, "您手机尚未安装微信，请安装后再登录");
            return;
        }
        AgricultureApplication.api.registerApp(AgricultureApplication.APP_ID);
        SendAuth.Req req = new SendAuth.Req();
        req.scope = "snsapi_userinfo";
        req.state = "wechat_sdk_xb_live_state";//官方说明：用于保持请求和回调的状态，授权请求后原样带回给第三方。该参数可用于防止csrf攻击（跨站请求伪造攻击），建议第三方带上该参数，可设置为简单的随机数加session进行校验
        AgricultureApplication.api.sendReq(req);
    }
}
