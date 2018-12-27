package com.example.vivic.nolost;

import android.app.Application;
import android.content.Context;
import android.net.Uri;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.goyourfly.multi_picture.ImageLoader;
import com.goyourfly.multi_picture.MultiPictureView;
import com.mob.MobSDK;

import org.jetbrains.annotations.NotNull;

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
        MultiPictureView.setImageLoader(new ImageLoader() {
            @Override
            public void loadImage(@NotNull ImageView imageView, @NotNull Uri uri) {
                Glide.with(imageView.getContext()).load(uri).into(imageView);
            }
        });
    }

    public static Context getContext() {
        return context;
    }
}
