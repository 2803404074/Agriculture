package com.tzl.agriculture.mall.activity;

import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.activity.set.SetBaseActivity;
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
import config.Mall;
import okhttp3.Call;

public class StartCommentActivity extends SetBaseActivity implements View.OnClickListener {

    @BindView(R.id.et_mess)
    EditText editText;

    @BindView(R.id.tv_send)
    TextView tvSend;

    @Override
    public void backFinish() {
        finish();
    }

    @Override
    public int setLayout() {
        return R.layout.activity_start_comment;
    }

    @Override
    public void initView() {
        setTitle("发布评论");
        setAndroidNativeLightStatusBar(true);
        setStatusColor(R.color.colorW);
        tvSend.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.tv_send:
                if (StringUtils.isEmpty(editText.getText().toString())){
                    ToastUtil.showShort(this,"请填写您的评论内容");
                    return;
                }
                sendMess();
                break;
                default:break;
        }
    }

    private void sendMess() {
        Map<String,String>map = new HashMap<>();
        map.put("content",editText.getText().toString());
        map.put("imgUrl","");
        map.put("orderId",getIntent().getStringExtra("orderId"));
        String str = JsonUtil.obj2String(map);
        String token = (String) SPUtils.instance(this,1).getkey("token","");
        OkHttp3Utils.getInstance(Mall.BASE).doPostJson2(Mall.commentSave, str, token, new GsonObjectCallback<String>(Mall.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        ToastUtil.showShort(StartCommentActivity.this,"提交成功");
                        finish();
                    }else {
                        ToastUtil.showShort(StartCommentActivity.this, TextUtil.checkStr2Str(object.optString("msg")));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {

            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(StartCommentActivity.this,"网络连接异常");
                    }
                });
            }
        });

    }
}
