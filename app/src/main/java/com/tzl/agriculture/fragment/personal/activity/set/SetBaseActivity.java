package com.tzl.agriculture.fragment.personal.activity.set;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.tzl.agriculture.R;
import com.tzl.agriculture.mall.activity.OrderActivity;
import com.tzl.agriculture.util.SPUtils;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * 设置
 */
public abstract class SetBaseActivity extends AppCompatActivity {

    public static SetBaseActivity instance;
    @BindView(R.id.tv_title)
    TextView tvTitle;

    @BindView(R.id.iv_back)
    ImageView ivBack;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(setLayout());
        instance = this;
        ButterKnife.bind(this);
        ivBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getIntent().getIntExtra("type",0) == 1){
                    Intent intent = new Intent(SetBaseActivity.this, OrderActivity.class);
                    setResult(RESULT_OK, intent);
                    finish();
                }else {
                    finish();
                }
            }
        });
        initView();
        initData();
    }


    public abstract int setLayout();

    public abstract void initView();

    public abstract void initData();

    public  void setTitle(String title){
        tvTitle.setText(title);
    }

    //获取实例
    public static SetBaseActivity getActivityInstance(){
        if (instance!=null){
            return instance;
        }
        return null;
    }

    /**
     * 修改状态栏颜色
     * @param color
     */
    public void setStatusColor(int color){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {//因为不是所有的系统都可以设置颜色的，在4.4以下就不可以。。有的说4.1，所以在设置的时候要检查一下系统版本是否是4.1以上
            Window window = getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.setStatusBarColor(getResources().getColor(color));
        }
    }

    /**
     *  设置状态栏字体颜色
     *  谷歌特定修改
     *  需要在跟布局加上 android:fitsSystemWindows="true" 这句话
     * @param dark   ture 黑色，false 白色，或者 根据flag去切换状态的颜色，具体的规则还不知道
     */
    public void setAndroidNativeLightStatusBar(boolean dark) {
        View decor = getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    public String getToken(){
        return (String) SPUtils.instance(this,1).getkey("token","");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
        instance = null;
    }
}
