package com.tzl.agriculture.view;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;


import androidx.appcompat.app.AppCompatActivity;

import com.tzl.agriculture.util.SPUtils;

import butterknife.ButterKnife;

/**
 * Created by 黄仕豪 on 2019/7/03
 */

public abstract class BaseActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        ButterKnife.bind(this);
        initView();
        initData();
    }

    public abstract void initView();

    public abstract void initData();

    // 设置布局
    public abstract int setLayout();

    public String getSPKEY(Activity activity,String key,int type){
        return (String) SPUtils.instance(activity,type).getkey(key,"");
    }

    public void setSPKEY(Activity activity,String key,String values,int type){
        SPUtils.instance(activity,type).put(key,values);
    }

    public String getToken (Activity activity){
        return (String) SPUtils.instance(activity,1).getkey("token","");
    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }


    public Context getContext(){
        return this;
    }
}
