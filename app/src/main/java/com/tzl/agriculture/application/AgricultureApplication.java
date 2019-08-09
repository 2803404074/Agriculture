package com.tzl.agriculture.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mob.MobSDK;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

/**
 * 牛逼
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
    }
}
