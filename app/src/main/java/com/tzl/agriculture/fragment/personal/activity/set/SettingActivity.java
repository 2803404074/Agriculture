package com.tzl.agriculture.fragment.personal.activity.set;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.facebook.drawee.view.SimpleDraweeView;
import com.shehuan.niv.NiceImageView;
import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.personal.login.activity.LoginActivity;
import com.tzl.agriculture.main.MainActivity;
import com.tzl.agriculture.util.SPUtils;
import com.tzl.agriculture.util.ToastUtil;

import java.io.IOException;

import Utils.GsonObjectCallback;
import Utils.OkHttp3Utils;
import butterknife.BindView;
import config.Article;
import config.User;
import okhttp3.Call;

/**
 * 个人中心，设置页面
 */
public class SettingActivity extends SetBaseActivity implements View.OnClickListener {

    //昵称
    @BindView(R.id.tv_name)
    TextView tvNickName;

    //头像
    @BindView(R.id.drawee_img)
    SimpleDraweeView draweeView;

    //点击头像昵称区域
    @BindView(R.id.ll_mes)
    LinearLayout llMes;

    //账号与安全
    @BindView(R.id.tv_account_save)
    TextView tvAccountSave;

    //功能反馈
    @BindView(R.id.tv_function_response)
    TextView tvFunctionResponse;

    //关于app
    @BindView(R.id.tv_aboutApp)
    TextView tvAboutApp;

    @BindView(R.id.tv_logout)
    TextView tvLogout;

    @BindView(R.id.tv_address)
    TextView tvAddress;

    @Override
    public void backFinish() {
        finish();
    }


    @Override
    public int setLayout() {
        return R.layout.activity_setting;
    }

    @Override
    public void initView() {
        setTitle("设置");
        tvNickName.setText(getIntent().getStringExtra("name"));
        draweeView.setImageURI(getIntent().getStringExtra("head"));
        llMes.setOnClickListener(this);
        tvAccountSave.setOnClickListener(this);
        tvFunctionResponse.setOnClickListener(this);
        tvAboutApp.setOnClickListener(this);
        tvLogout.setOnClickListener(this);
        tvAddress.setOnClickListener(this);
    }

    @Override
    public void initData() {

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.ll_mes:
                Intent intent1 = new Intent(this, UserMessActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_account_save:
                Intent intent2 = new Intent(this, AccountSaveActivity.class);
                startActivity(intent2);
                break;
            case R.id.tv_function_response:
                Intent intent3 = new Intent(this, FunctionResponseActivity.class);
                startActivity(intent3);
                break;
            case R.id.tv_aboutApp:
                Intent intent4 = new Intent(this, AboutAppActivity.class);
                startActivity(intent4);
                break;
            case R.id.tv_logout:
                showTwo();
                break;
            case R.id.tv_address:
                Intent intent = new Intent(this, AddressActivity.class);
                startActivity(intent);
                break;
            default:
                break;
        }
    }

    /**
     * 退出登陆
     */
    private void logOut() {
        String token = (String) SPUtils.instance(this, 1).getkey("token", "");
        OkHttp3Utils.getInstance(User.BASE).doPostJson2(User.logout, "", token, new GsonObjectCallback<String>(User.BASE) {
            @Override
            public void onUi(String result) {
                ToastUtil.showShort(SettingActivity.this, "退出成功");
                if (MainActivity.instance != null) {
                    MainActivity.instance.finish();
                }
                SPUtils.instance(SettingActivity.this, 1).remove("token");
                Intent intent = new Intent(SettingActivity.this, LoginActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onFailed(Call call, IOException e) {
            }
        });
    }

    private void showTwo() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this).setIcon(R.mipmap.application).setTitle("趣乡服务")
                .setMessage("是否要退出登陆？").setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        logOut();
                        dialogInterface.dismiss();
                    }
                }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        dialogInterface.dismiss();
                    }
                });
        builder.create().show();
    }
}
