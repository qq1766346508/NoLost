package com.example.vivic.nolost;

import android.app.Application;
import android.content.Context;

import com.example.vivic.nolost.userCenter.UserRepository;
import com.example.vivic.nolost.commonUtil.LeakCanaryUtils;
import com.goyourfly.multi_picture.ImageLoader;
import com.goyourfly.multi_picture.MultiPictureView;
import com.mob.MobSDK;

import cn.bmob.v3.Bmob;

public class NoLostApplication extends Application {
    public static final String Application_ID = "6875708b252486ebb6581bbae5aaf99f";
    private static Context context;

    @Override
    public void onCreate() {
        super.onCreate();
        context = getApplicationContext();
        Bmob.initialize(this, Application_ID);
        MobSDK.init(this);
        LeakCanaryUtils.install(this);
        MultiPictureView.setImageLoader((ImageLoader) (imageView, uri) -> GlideApp.with(imageView.getContext()).load(uri).centerCrop().into(imageView));
        UserRepository.INSTANCE.fetchUserInfo();
    }

    public static Context getContext() {
        return context;
    }
}
