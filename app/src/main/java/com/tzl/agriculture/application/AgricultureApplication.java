package com.tzl.agriculture.application;

import android.app.Application;

import com.facebook.drawee.backends.pipeline.Fresco;
import com.mob.MobSDK;

/**
 * 牛逼
 */
public class AgricultureApplication extends Application {


    @Override
    public void onCreate() {
        super.onCreate();
       //MobSDK.init(this);
        Fresco.initialize(this);
    }
}
