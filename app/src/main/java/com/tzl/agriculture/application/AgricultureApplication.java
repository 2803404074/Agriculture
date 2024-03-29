package com.tzl.agriculture.application;

import android.app.Application;

import androidx.multidex.MultiDex;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mob.MobSDK;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;
import com.tzl.agriculture.util.CrashHandlerUtils;

import cn.jpush.android.api.JPushInterface;

/**
 *
 */
public class AgricultureApplication extends Application {

    public static IWXAPI api;
    public static String APP_ID="wx9253e5b4ad426487";

    @Override
    public void onCreate() {
        super.onCreate();
        //分享、短信
        MobSDK.init(this);
        //圆图
        Fresco.initialize(this);

        //微信
        api = WXAPIFactory.createWXAPI(this,APP_ID,true);
        api.registerApp(APP_ID);
        CrashHandlerUtils.getInstance().init(this);

        //极光推送
        JPushInterface.setDebugMode(true);
        JPushInterface.init(this);
    }


    @Override
    protected void attachBaseContext(android.content.Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }



}
