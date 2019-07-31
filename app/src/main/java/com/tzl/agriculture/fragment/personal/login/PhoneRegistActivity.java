package com.tzl.agriculture.fragment.personal.login;

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
import com.tzl.agriculture.fragment.personal.login.activity.LoginGetCodeActivity;
import com.tzl.agriculture.util.DateUtil;
import com.tzl.agriculture.util.JsonUtil;
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
import config.Article;
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
    public int setLayout() {
        return R.layout.activity_phone_regist;
    }

    //{"detail":"如果当前appkey对应的包名没有通过审核，每天次appkey+包名最多可以发送20条短信","description":"当前appkey发送短信的数量超过限额"
    Handler mHandler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            int event = msg.arg1;
            int result = msg.arg2;
            //Object data = msg.obj;
            if (result == SMSSDK.RESULT_COMPLETE) {
                if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                    Toast.makeText(getApplicationContext(), "验证码已经发送", Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(getApplicationContext(), "验证码发送失败", Toast.LENGTH_SHORT).show();
//                if (null != LoginGetCodeActivity.getActivityInstance()){
//                    LoginGetCodeActivity.getActivityInstance().finish();
//                }
            }
            return false;
        }
    });

    @Override
    public void initView() {

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

        setTitle("手机号注册");
        List<String> list = new ArrayList<String>();
        list.add("+86");
        list.add("+11");
        list.add("+22");
        list.add("+33");
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,list);

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
                if (DateUtil.isPhoneNumber(etPhone.getText().toString())){
                    judgePhoneNumber(etPhone.getText().toString());
                }
            }
        });

        //发送验证码
        tvNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                number = etPhone.getText().toString();
                SMSSDK.getVerificationCode("86",etPhone.getText().toString());
                Intent intent = new Intent(PhoneRegistActivity.this, LoginGetCodeActivity.class);
                intent.putExtra("phone",number);
                intent.putExtra("regist","1");
                startActivity(intent);
            }
        });
    }

    @Override
    public void initData() {

    }


    /**
     * 输入完整的手机号，请求后端判断是否已存在
     * @param number
     */
    private void judgePhoneNumber(String number){
        Map<String,String>map = new HashMap<>();
        map.put("phone",number);
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(User.BASE).doPostJson(User.checkPhone, str, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optBoolean("data")){//存在用户，该手机号已经被注册
                        tvTips.setVisibility(View.VISIBLE);
                        etPhone.setTextColor(getResources().getColor(R.color.colorAccentButton));
                        tvNext.setClickable(false);
                    }else {
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
