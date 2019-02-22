package com.example.vivic.nolost;

import android.app.Application;
import android.content.Context;

import com.example.vivic.nolost.bmob.UserRepository;
import com.example.vivic.nolost.chat.NoLostMessageHandler;
import com.example.vivic.nolost.commonUtil.LeakCanaryUtils;
import com.mob.MobSDK;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

import cn.bmob.newim.BmobIM;
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
        UserRepository.INSTANCE.fetchUserInfo();
        //teset
        if (getApplicationInfo().packageName.equals(getMyProcessName())) {
            BmobIM.init(this);
            BmobIM.registerDefaultMessageHandler(new NoLostMessageHandler());
        }
    }

    public static Context getContext() {
        return context;
    }

    /**
     * 获取当前运行的进程名
     *
     * @return
     */
    public static String getMyProcessName() {
        try {
            File file = new File("/proc/" + android.os.Process.myPid() + "/" + "cmdline");
            BufferedReader mBufferedReader = new BufferedReader(new FileReader(file));
            String processName = mBufferedReader.readLine().trim();
            mBufferedReader.close();
            return processName;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
