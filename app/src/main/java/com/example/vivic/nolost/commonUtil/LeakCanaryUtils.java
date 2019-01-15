package com.example.vivic.nolost.commonUtil;

import android.app.Application;

import com.squareup.leakcanary.LeakCanary;
import com.squareup.leakcanary.RefWatcher;

/**
 * Created by xxd on 2018/7/5
 */
public class LeakCanaryUtils {
    private static RefWatcher mWatcher;

    public static void install(Application application) {
        if (LeakCanary.isInAnalyzerProcess(application)) {
            // This process is dedicated to LeakCanary for heap analysis.
            // You should not init your app in this process.
            return;
        }
        mWatcher = LeakCanary.install(application);
    }

    public static void watch(Object reference) {
        if (mWatcher != null) {
            mWatcher.watch(reference);
        }
    }

}
