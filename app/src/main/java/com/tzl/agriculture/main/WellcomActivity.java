package com.tzl.agriculture.main;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.model.HomeMo;
import com.tzl.agriculture.model.SplashADSModel;
import com.tzl.agriculture.util.DownMediaUtils;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
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
import config.App;
import config.Article;
import config.User;
import okhttp3.Call;

public class WellcomActivity extends AppCompatActivity implements View.OnClickListener {
    private TextView text_version, text_time;
    private ImageView mImageView;
    private int total = 5;
    private List<String> mStrings = new ArrayList<>();
    private String clickType, otherMessage;
    private Handler mHandler = new Handler() {
        @Override
        public void dispatchMessage(@NonNull Message msg) {
            super.dispatchMessage(msg);
            switch (msg.what) {
                case 0x100:
                    String isLogin = (String) SPUtils.instance(WellcomActivity.this, 1).getkey("token", "");
                    if (!StringUtils.isEmpty(isLogin)) {

                        Intent intent = new Intent(WellcomActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                        //initData(isLogin);
                    } else {
                        Intent intent = new Intent(WellcomActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
                    }
                    break;
                case 0x101:
                    if (total <= 0) {
                        sendEmptyMessage(0x100);
                        return;
                    }
                    total--;
                    text_time.setText(total + " 跳过");
                    sendEmptyMessageDelayed(0x101, 1000);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //下载网络图片
        //获取首页数据
        getHomeData();
        getADS();
        setContentView(R.layout.activity_wellcom);
        text_version = this.findViewById(R.id.text_version);
        mImageView = this.findViewById(R.id.ad_image);
        text_time = this.findViewById(R.id.text_time);
        text_version.setText("V" + getVersion());
        text_time.setOnClickListener(this);
        mImageView.setOnClickListener(this);
    }

    //获取网络图片
    private void getADS() {
        OkHttp3Utils.getInstance(User.BASE).doPostJson(App.appads, "", new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    String str = object.optString("data");
                    if (object.optInt("code") == 0 && !TextUtils.isEmpty(str)) {
                        SplashADSModel model = JsonUtil.string2Obj(str, SplashADSModel.class);
                        if (model != null && model.getAdvertise() != null && !TextUtils.isEmpty(model.getAdvertise().getUrl())) {
                            List<String> d = new ArrayList<>();
                            d.add(model.getAdvertise().getUrl());
                            clickType = model.getAdvertise().getLinkType();
                            otherMessage = model.getAdvertise().getLink();
                            DownMediaUtils.getmDownMediaUtils(WellcomActivity.this).setDownMediaLisenter(d, new DownMediaUtils.DownMediaLisenter() {
                                @Override
                                public void downCallBack(List<String> strings) {
                                    if (ContextCompat.checkSelfPermission(WellcomActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == 0 && strings != null && strings.size() > 0) {
                                        mStrings.addAll(strings);
                                        Glide.with(WellcomActivity.this).load(strings.get(0) + "").into(mImageView);
                                        text_time.setVisibility(View.VISIBLE);
                                        if (mHandler != null)
                                            mHandler.sendEmptyMessageDelayed(0x101, 1000);
                                    } else {
                                        if (mHandler != null)
                                            mHandler.sendEmptyMessageDelayed(0x100, 1500);
                                    }
                                }
                            });
                        } else {
                            if (mHandler != null)
                                mHandler.sendEmptyMessageDelayed(0x100, 1500);
                        }
                    } else {
                        if (mHandler != null)
                            mHandler.sendEmptyMessageDelayed(0x100, 1500);
                    }
                } catch (JSONException e) {
                    if (mHandler != null)
                        mHandler.sendEmptyMessageDelayed(0x100, 1500);
                }
            }

            @Override
            public void onFailed(Call call, IOException e) {
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(0x100, 1500);
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                if (mHandler != null)
                    mHandler.sendEmptyMessageDelayed(0x100, 1500);
            }
        });
    }


    protected void initData(String isLogin) {
        //获取用户信息
        OkHttp3Utils.getInstance(User.BASE).doGet(User.getUserInfo2, isLogin, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        Intent intent = new Intent(WellcomActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (object.optInt("code") == -1) {
                        Intent intent = new Intent(WellcomActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    } else {
                        ToastUtil.showShort(WellcomActivity.this, "您的信息已过期，请重新登陆");
                        Intent intent = new Intent(WellcomActivity.this, LoginActivity.class);
                        startActivity(intent);
                        finish();
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
                        ToastUtil.showShort(WellcomActivity.this, "网络异常");
                        Intent intent = new Intent(WellcomActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();

                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                super.onFailure(call, e);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showShort(WellcomActivity.this, "网络异常");
                        Intent intent = new Intent(WellcomActivity.this, MainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                });
            }
        });

    }

    //获取首页数据
    private void getHomeData() {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        if (TextUtils.isEmpty(token)) {
            return;
        }
        Map<String, String> map = new HashMap<>();
        map.put("typeId", "4");
        map.put("positionCode", "home_head");
        map.put("category", "0");
        String str = JsonUtil.obj2String(map);
        OkHttp3Utils.getInstance(Article.BASE).doPostJson2(Article.home, str, token, new GsonObjectCallback<String>(Article.BASE) {
            @Override
            public void onUi(String result) {
                try {
                    JSONObject object = new JSONObject(result);
                    if (object.optInt("code") == 0) {
                        String str = object.optString("data");
                        List<HomeMo> mData = JsonUtil.string2Obj(str, List.class, HomeMo.class);

                        if (mData != null && mData.size() > 0) {
                            SPUtils.instance(WellcomActivity.this, 1).putObjectByInput("home_data_index", mData);
                        }
                    }
                } catch (JSONException e) {

                }

            }

            @Override
            public void onFailed(Call call, IOException e) {
            }

            @Override
            public void onFailure(Call call, IOException e) {
            }
        });
    }

    //获取版本号
    private String getVersion() {
        PackageManager packageManager = getPackageManager();
        PackageInfo packInfo = null;
        try {
            packInfo = packageManager.getPackageInfo(getPackageName(), 0);
            return packInfo.versionName;
        } catch (PackageManager.NameNotFoundException eArp) {
        }
        return "";
    }


    @Override
    protected void onDestroy() {
        if (mHandler != null) {
            mHandler.removeMessages(0x100);
            mHandler.removeMessages(0x101);
        }
        DownMediaUtils.getmDownMediaUtils(this).deteleAllFile(mStrings);
        super.onDestroy();
    }

    @Override
    public void onClick(View viewArp) {
        if (viewArp.getId() != R.id.text_time) {
            SPUtils.instance(this, 1).put("main_type", clickType);
            SPUtils.instance(this, 1).put("main_link", otherMessage);
        }
        if (mHandler != null)
            mHandler.sendEmptyMessageAtTime(0x100, 0);
    }
}
