package com.example.vivic.nolost;

import android.app.Application;
import android.content.Context;

import com.mob.MobSDK;

public class NoLostApplication extends Application {
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        MobSDK.init(this);
    }

    public static Context getContext() {
        return context;
    }
}
