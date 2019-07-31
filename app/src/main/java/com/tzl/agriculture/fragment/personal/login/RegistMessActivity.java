package com.tzl.agriculture.fragment.personal.login;

import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.fragment.personal.login.activity.LoginGetCodeActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Article;
import config.User;
import okhttp3.Call;

public class RegistMessActivity extends SetBaseActivity {

    @BindView(R.id.tv_phone)
    TextView tvPhone;

    @BindView(R.id.tv_regist)
    TextView tvRegist;

    @BindView(R.id.et_nickname)
    EditText etNickName;

    @BindView(R.id.tv_date)
    TextView tvDate;

    @BindView(R.id.radioGroup)
    RadioGroup radioGroup;

    @BindView(R.id.tv_log)
    TextView tvLog;

    @BindView(R.id.invCode)
    EditText tvInvCode;//邀请码

    private TimePickerView pvTime1;
    @Override
    public int setLayout() {
        return R.layout.activity_regist_mess;
    }

    @Override
    public void initView() {
        setTitle("基本信息");
        tvPhone.setText(getIntent().getStringExtra("phone"));
        tvRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (StringUtils.isEmpty(etNickName.getText().toString())){
                    ToastUtil.showShort(RegistMessActivity.this,"请设置您的昵称");
                }
                regist();
            }
        });

        tvDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //时间选择器
                pvTime1 = new TimePickerView.Builder(RegistMessActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        tvDate.setText(DateUtil.dateToStr(date));
                    }
                }).setType(new boolean[]{true, true, true, false, false, false}) //年月日时分秒 的显示与否，不设置则默认全部显示
                        .isCenterLabel(false)
                        .setDividerColor(Color.RED)
                        .setTextColorCenter(getResources().getColor(R.color.colorPrimaryDark))//设置选中项的颜色
                        .setTextColorOut(getResources().getColor(R.color.colorGri2))//设置没有被选中项的颜色
                        .setContentSize(21)
                        .setLineSpacingMultiplier(1.2f)
                        .setTextXOffset(-5, 0,5, 0, 0, 0)//设置X轴倾斜角度[ -90 , 90°]
                        .setDecorView(null)
                        .build();

                pvTime1.show();
            }
        });
    }

    @Override
    public void initData() {

    }

    private void regist(){
        Map<String,String>map = new HashMap<>();
        map.put("phone",getIntent().getStringExtra("phone"));
        map.put("nickName",etNickName.getText().toString());
        map.put("sex",radioGroup.getCheckedRadioButtonId() == R.id.rb_n ? "男":"女");
        map.put("age",tvDate.getText().toString());
        map.put("invCode", TextUtil.checkStr2Str(tvInvCode.getText().toString()));
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson(User.register, str, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject dataObj = object.optJSONObject("data");
                        String token = dataObj.optString("token");
                        if (!StringUtils.isEmpty(token)){
                            Intent intent = new Intent(RegistMessActivity.this, MainActivity.class);
                            SPUtils.instance(RegistMessActivity.this,1).put("login","1");
                            SPUtils.instance(RegistMessActivity.this,1).put("token",token);
                            startActivity(intent);

                            finish();

                            if (null !=LoginGetCodeActivity.instance){
                                LoginGetCodeActivity.instance.finish();
                            }

                            if (null!=PhoneRegistActivity.getActivityInstance()){
                                PhoneRegistActivity.getActivityInstance().finish();
                            }
                            LoginActivity.instance.finish();
                        }
                    }else {
                        tvLog.setText("状态码"+object.optString("code")+","+object.optString("msg"));

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
