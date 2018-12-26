package com.example.vivic.nolost;

import android.app.Application;
import android.content.Context;

import com.mob.MobSDK;

import cn.bmob.v3.Bmob;

public class NoLostApplication extends Application {
    public static final String Application_ID = "6875708b252486ebb6581bbae5aaf99f";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        MobSDK.init(this);
        Bmob.initialize(this, Application_ID);
    }

    public static Context getContext() {
        return context;
    }
}
