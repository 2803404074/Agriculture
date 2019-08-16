package com.tzl.agriculture.main;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.model.HomeMo;
import com.tzl.agriculture.util.JsonUtil;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.StatusBarUtil;
import com.tzl.agriculture.util.ToastUtil;

import org.apache.commons.lang.StringUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import config.Article;
import config.User;
import okhttp3.Call;

public class WellcomActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //获取首页数据
        getHomeData();
        StatusBarUtil.setRootViewFitsSystemWindows(this, true);
        //设置状态栏透明
        StatusBarUtil.setTranslucentStatus(this);

        //一般的手机的状态栏文字和图标都是白色的, 可如果你的应用也是纯白色的, 或导致状态栏文字看不清
        //所以如果你是这种情况,请使用以下代码, 设置状态使用深色文字图标风格, 否则你可以选择性注释掉这个if内容
        if (!StatusBarUtil.setStatusBarDarkTheme(this, true)) {
            //如果不支持设置深色风格 为了兼容总不能让状态栏白白的看不清, 于是设置一个状态栏颜色为半透明,
            //这样半透明+白=灰, 状态栏的文字能看得清
            StatusBarUtil.setStatusBarColor(this, getResources().getColor(R.color.colorW));
        }
        setContentView(R.layout.activity_wellcom);

//        ImageView imageView = findViewById(R.id.application_head);
//        Glide.with(WellcomActivity.this).load("http://img.zcool.cn/community/01c02b55b9bc386ac7253f364dcf82.jpg@1280w_1l_2o_100sh.jpg").into(imageView);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
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
            }
        }, 1500);
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
                            SPUtils.instance(WellcomActivity.this,1).putObjectByInput("home_data_index",mData);
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
}
