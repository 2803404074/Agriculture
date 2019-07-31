package com.tzl.agriculture.fragment.personal.activity.set;

import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.tzl.agriculture.R;
import com.tzl.agriculture.util.TextUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.App;
import okhttp3.Call;

public class AboutAppActivity extends SetBaseActivity {

    @BindView(R.id.tv_appName)
    TextView tvAppName;

    @BindView(R.id.tv_appVersion)
    TextView tvAppVersion;

    @BindView(R.id.iv_code)
    ImageView ivCode;

    @BindView(R.id.tv_appContent)
    TextView tvAppContent;

    @Override
    public int setLayout() {
        return R.layout.activity_about_app;
    }

    @Override
    public void initView() {
        setTitle("关于APP");
    }

    @Override
    public void initData() {
        OkHttp3Utils.getInstance(App.BASE).doPostJson(App.getAppAbout, "", new GsonObjectCallback<String>(App.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0){
                        JSONObject data = object.optJSONObject("data");
                        tvAppName.setText(TextUtil.checkStr2Str(data.optString("title")));
                        tvAppVersion.setText(TextUtil.checkStr2Str(data.optString("versionInfo")));
                        tvAppContent.setText(TextUtil.checkStr2Str(data.optString("qrcodeIntroduct")));
                        Glide.with(AboutAppActivity.this).load(data.optString("qrcodeUrl")).into(ivCode);
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
