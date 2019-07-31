package com.tzl.agriculture.main;

import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tzl.agriculture.R;
import com.tzl.agriculture.fragment.home.fragment.HomeFragment;
import com.tzl.agriculture.fragment.personal.framgent.PersonFragment;
import com.tzl.agriculture.fragment.xiangc.fragment.XiangcFragment;
import com.tzl.agriculture.util.DrawableSizeUtil;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    public static MainActivity instance;
    @BindView(R.id.main_radiogrop)
    RadioGroup radioGroup;

    @BindView(R.id.main_home)
    RadioButton radioButton_01;

    @BindView(R.id.main_video)
    RadioButton radioButton_02;

    @BindView(R.id.main_my)
    RadioButton radioButton_03;

    private FragmentManager fragmentManager;
    private FragmentTransaction beginTransaction;

    private HomeFragment fg_00;
    private XiangcFragment fg_01;
    private PersonFragment fg_02;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initView();
    }

    private void initView() {
        instance = MainActivity.this;
        fragmentManager = getSupportFragmentManager();
        radioGroup.setOnCheckedChangeListener(this);
        radioButton_01.setChecked(true);
        radioButton_01.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
        DrawableSizeUtil drawableSizeUtil = new DrawableSizeUtil(this);
        drawableSizeUtil.setImgSize(60,60,1,radioButton_01,R.drawable.select_home);
        drawableSizeUtil.setImgSize(60,60,1,radioButton_02,R.drawable.select_xc);
        drawableSizeUtil.setImgSize(60,60,1,radioButton_03,R.drawable.select_my);
    }

    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {
        switch (checkedId) {
            case R.id.main_home: {
                radioButton_01.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                radioButton_02.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_03.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                changeFragment(0);
                break;
            }
            case R.id.main_video: {
                radioButton_01.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_02.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                radioButton_03.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                changeFragment(1);
                //show(1,"该功能正在努力升级中，敬请期待!");
                break;
            }
            case R.id.main_my: {
                radioButton_01.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_02.setTextColor(getResources().getColor(R.color.smssdk_tv_light_gray));
                radioButton_03.setTextColor(getResources().getColor(R.color.colorPrimaryDark));
                changeFragment(2);
                break;
            }
        }
    }

    public void changeFragment(int index) {

        // 调用FragmentTransaction中的方法来处理这个transaction
        beginTransaction = fragmentManager.beginTransaction();
        hideFragments(beginTransaction);

        // 根据不同的标签页执行不同的操作
        switch (index) {
            case 0:
                /*****/
                if (fg_00 == null) {
                    fg_00 = HomeFragment.getInstance();
                    beginTransaction.add(R.id.fl_container, fg_00);
                } else {
                    beginTransaction.show(fg_00);
                }
                break;
            case 1:
                /*****/
                if (fg_01 == null) {
                    fg_01 = XiangcFragment.getInstance();
                    beginTransaction.add(R.id.fl_container, fg_01);
                } else {
                    beginTransaction.show(fg_01);
                }
                break;
            case 2:
            /*****/
            if (fg_02 == null) {
                fg_02 = PersonFragment.getInstance();
                beginTransaction.add(R.id.fl_container, fg_02);
            } else {
                beginTransaction.show(fg_02);
            }
            break;
            default:
                break;
        }

        //需要提交事务
        beginTransaction.commit();
    }

    private void hideFragments(FragmentTransaction transaction) {
        if (fg_00 != null) {
            transaction.hide(fg_00);
        }
        if (fg_01 != null) {
            transaction.hide(fg_01);
        }
        if (fg_02 != null) {
            transaction.hide(fg_02);
        }
    }

    private long exitTime = 0;

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getAction() == KeyEvent.ACTION_DOWN) {
            if ((System.currentTimeMillis() - exitTime) > 2000) {
                //弹出提示，可以有多种方式
                Toast.makeText(getApplicationContext(), "再按一次退出程序", Toast.LENGTH_SHORT).show();
                exitTime = System.currentTimeMillis();
            } else {
                finish();
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

}







