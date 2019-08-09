package com.tzl.agriculture.fragment.personal.login.activity;

import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import config.User;
import okhttp3.Call;

/**
 * 13737755124
 * 13558466278
 * 手机号注册
 */
public class PhoneRegistActivity extends SetBaseActivity {

    private String regionStr = "86";
    private String number = "";

    @BindView(R.id.spinner)
    Spinner spinner;

    @BindView(R.id.et_phone)
    EditText etPhone;

    @BindView(R.id.tv_next)
    TextView tvNext;

    @BindView(R.id.tv_tips)
    TextView tvTips;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_phone_regist;
    }


    @Override
    public void initView() {

        setTitle("手机号注册");

        if (!StringUtils.isEmpty(getIntent().getStringExtra("openId"))){
            setTitle("新用户需绑定手机号");
        }

        List<String> list = new ArrayList<String>();
        list.add("+86");
        list.add("+11");
        list.add("+22");
        list.add("+33");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, list);

        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                regionStr = list.get(i);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
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
                }
            }
        });

        //发送验证码
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = etPhone.getText().toString();
                getCodeMeth();
            }
        });
    }

    @Override
    public void initData() {

    }

    private void getCodeMeth() {
        Map<String, String> map = new HashMap<>();
        map.put("tempCode", "register");
        map.put("phoneNum", etPhone.getText().toString());
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.sendSms, str, getToken(),
                new GsonObjectCallback<String>(User.BASE) {
                    @Override
                    public void onUi(String result) {
                        try {
                            JSONObject object = new JSONObject(result);
                            if (object.optInt("code") == 0) {
                                Intent intent = new Intent(PhoneRegistActivity.this, LoginGetCodeActivity.class);
                                intent.putExtra("phone",etPhone.getText().toString());
                                intent.putExtra("regist","1");
                                startActivity(intent);
                            }
                            ToastUtil.showShort(PhoneRegistActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onFailed(Call call, IOException e) {
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(PhoneRegistActivity.this, "请求超时");
                            }
                        });
                    }

                    @Override
                    public void onFailure(Call call, IOException e) {
                        super.onFailure(call, e);
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                ToastUtil.showShort(PhoneRegistActivity.this, "网络异常");
                            }
                        });
                    }
                });
    }

    /**
     * 输入完整的手机号，请求后端判断是否已存在
     *
     * @param number
     */
    private void judgePhoneNumber(String number) {
        Map<String, String> map = new HashMap<>();
        map.put("phone", number);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson(User.checkPhone, str, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optBoolean("data")) {//存在用户，该手机号已经被注册
                        tvTips.setVisibility(View.VISIBLE);
                        etPhone.setTextColor(getResources().getColor(R.color.colorAccentButton));
                        tvNext.setClickable(false);
                    } else {
                        tvTips.setVisibility(View.GONE);
                        etPhone.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                        tvNext.setBackgroundResource(R.drawable.shape_login_blue);
                        tvNext.setClickable(true);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }
        });
    }
}
