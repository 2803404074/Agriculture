package com.tzl.agriculture;

import android.os.Bundle;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.tzl.agriculture.fragment.home.fragment.HomeFragment;
import com.tzl.agriculture.fragment.personal.framgent.PersonFragment;
import com.tzl.agriculture.fragment.xiangc.fragment.XiangcFragment;

public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup radioGroup;
    private FragmentManager fragmentManager;
    private FragmentTransaction beginTransaction;
    private HomeFragment fg_00;
    private XiangcFragment fg_01;
    private PersonFragment fg_02;

    private RadioButton radioButton_01, radioButton_03, radioButton_04;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        fragmentManager = getSupportFragmentManager();
        radioGroup = findViewById(R.id.main_radiogrop);
        radioGroup.setOnCheckedChangeListener(this);

        radioButton_01 = findViewById(R.id.main_home);

        radioButton_03 = findViewById(R.id.main_video);
        radioButton_04 = findViewById(R.id.main_my);
        radioButton_01.setChecked(true);
    }


    @Override
    public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

        switch (checkedId) {
            case R.id.main_home: {
                changeFragment(0);
                break;
            }
            case R.id.main_video: {
                changeFragment(1);
                //show(1,"该功能正在努力升级中，敬请期待!");
                break;
            }
            case R.id.main_my: {
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
}







