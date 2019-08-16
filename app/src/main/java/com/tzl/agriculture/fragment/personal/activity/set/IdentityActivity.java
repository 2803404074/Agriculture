package com.tzl.agriculture.fragment.personal.activity.set;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.google.gson.Gson;
import com.tzl.agriculture.R;
import com.tzl.agriculture.model.IdentityModel;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.TextUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.App;
import config.User;
import okhttp3.Call;

/**
 * 身份认证界面
 */
public class IdentityActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.tv_load)
    TextView tvLoad;

    @BindView(R.id.ll_input)
    LinearLayout mLinearLayoutInput;

    @BindView(R.id.ll_id)
    RelativeLayout mLinearLayoutId;

    private Gson mGson = new Gson();

    @BindView(R.id.text_status)
    TextView mTextViewStatus;

    @BindView(R.id.edit_name)
    EditText mEditTextName;

    @BindView(R.id.et_number)
    EditText mEditTextNumber;

    @BindView(R.id.text_name)
    TextView mTextViewName;

    @BindView(R.id.text_number)
    TextView mTextViewNumber;

    private IdentityModel mIdentityModel;

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
    protected void onResume() {
        getUseIden();
        super.onResume();
    }

    //获取实名认证信息
    private void getUseIden() {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(App.BASE).doPostJson2(User.getIdentity, "", token, new GsonObjectCallback<String>(App.BASE) {
            @Override
            public void onUi(String result) {
                System.out.println("result = [" + result + "]");
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        JSONObject dataObj = object.optJSONObject("data");
                        JSONObject identity = dataObj.getJSONObject("identity");
                        mIdentityModel = mGson.fromJson(identity.toString(), IdentityModel.class);
                        if (mIdentityModel != null) {
                            if (TextUtils.isEmpty(mIdentityModel.getAuditStatus())) {
                                mLinearLayoutInput.setVisibility(View.VISIBLE);
                                mLinearLayoutId.setVisibility(View.GONE);
                            } else {
                                mLinearLayoutInput.setVisibility(View.GONE);
                                mLinearLayoutId.setVisibility(View.VISIBLE);
                                mTextViewName.setText(mIdentityModel.getIdentityName());
                                mTextViewNumber.setText(mIdentityModel.getIdentityNumber());
                                switch (mIdentityModel.getAuditStatus()) {
                                    case "0":
                                        mTextViewStatus.setText("正在审核中");
                                        mTextViewStatus.setTextColor(ContextCompat.getColor(IdentityActivity.this,R.color.colorWhite));
                                        tvLoad.setText("查看证件");
                                        break;
                                    case "1":
                                        mTextViewStatus.setText("您已通过实名认证");
                                        mTextViewStatus.setTextColor(ContextCompat.getColor(IdentityActivity.this,R.color.colorWhite));
                                        tvLoad.setText("查看证件");
                                        break;
                                    case "2":
                                        mTextViewStatus.setText("实名认证未通过，请重新提交");
                                        mTextViewStatus.setTextColor(ContextCompat.getColor(IdentityActivity.this,R.color.liji_material_red_500));
                                        tvLoad.setText("重新提交证件");
                                        break;
                                }
                            }
                        } else {
                            mLinearLayoutInput.setVisibility(View.VISIBLE);
                            mLinearLayoutId.setVisibility(View.GONE);
                            tvLoad.setText("上传证件");
                        }
                    } else {
                        ToastUtil.showShort(IdentityActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }
                } catch (JSONException e) {
                    ToastUtil.showShort(IdentityActivity.this, "解析数据异常");
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                System.out.println("call = [" + call + "], e = [" + e + "]");
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.tv_load:
                if(mIdentityModel==null||TextUtils.isEmpty(mIdentityModel.getAuditStatus())){
                    String s = mEditTextName.getText().toString();
                    if (TextUtils.isEmpty(s)) {
                        ToastUtil.showShort(IdentityActivity.this, "请输入真实姓名");
                        return;
                    }
                    String s1 = mEditTextNumber.getText().toString();
                    if (TextUtils.isEmpty(s1)) {
                        ToastUtil.showShort(IdentityActivity.this, "请输入身份证号");
                        return;
                    }
                    if (s1.length() != 15 && s1.length() != 18) {
                        ToastUtil.showShort(IdentityActivity.this, "身份证号长度为15或则18位");
                        return;
                    }
                    Intent intent = new Intent(IdentityActivity.this, UpLoadIdentifyActivity.class);
                    intent.putExtra("name", s);
                    intent.putExtra("number", s1);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(IdentityActivity.this, UpLoadIdentifyActivity.class);
                    intent.putExtra("name", mIdentityModel.getRelIdentityName());
                    intent.putExtra("number", mIdentityModel.getRelIdentityNumber());
                    intent.putExtra("fontImage", mIdentityModel.getFrontalPic());
                    intent.putExtra("reveserImage", mIdentityModel.getReversePic());
                    intent.putExtra("status", mIdentityModel.getAuditStatus());
                    startActivity(intent);

                }

                break;
            default:
                break;
        }
    }
}
