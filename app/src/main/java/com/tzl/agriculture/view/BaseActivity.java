package com.tzl.agriculture.view;

import android.app.Activity;
import android.os.Bundle;


import com.tzl.agriculture.util.SPUtils;

import butterknife.ButterKnife;

/**
 * Created by 黄仕豪 on 2019/7/03
 */

public abstract class BaseActivity<V,T extends BasePresenter<V>> extends Activity {

    protected T p;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(setLayout());
        ButterKnife.bind(this);
        p=createPersenter();
        p.attchView((V)this);
        initView();
        initData();
    }
    public abstract T createPersenter();

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
    @Override
    protected void onDestroy() {
        super.onDestroy();
        //销毁对象
        p.deatchView();
    }
}
