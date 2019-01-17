package com.example.vivic.nolost.Login;

import android.arch.lifecycle.ViewModel;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.example.vivic.nolost.commonUtil.pref.CommonPref;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private static final String HAS_LOGIN = "HAS_LOGIN";
    public String userID;
    public String userName;
    public String userSex;
    public String userAvatar;
    private Handler mainHandler = new android.os.Handler(Looper.getMainLooper());


    public static void login() {
        CommonPref.instance().putBoolean(HAS_LOGIN, true);
    }

    public static void logout() {
        CommonPref.instance().putBoolean(HAS_LOGIN, false);
    }

    public static boolean getLoginStatus() {
        return CommonPref.instance().getBoolean(HAS_LOGIN, false);
    }

    public void loginByThird(String platform) {
        Platform plat = ShareSDK.getPlatform(platform);
        if (plat.isAuthValid()) {
            ToastUtil.showToast("已成功登录:" + plat.getName());
            Log.d(TAG, "userID=" + userID + ",username=" + userName + ",sex= " + userSex + ",avatar=" + userAvatar);
        } else {
            plat.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) { //成功回调在子线程，ui修改要回到主线程
                    Log.d(TAG, "onComplete: ");
                    userID = plat.getDb().getUserId();
                    userName = plat.getDb().getUserName();
                    userSex = plat.getDb().getUserGender();
                    userAvatar = plat.getDb().getUserIcon();
                    Log.d(TAG, "userID=" + userID + ",username=" + userName + ",sex= " + userSex + ",avatar=" + userAvatar);
                    mainHandler.post(() -> {
                        ToastUtil.showToast("授权成功,欢迎你 " + userName);
                    });
                }

                @Override
                public void onError(Platform platform, int i, Throwable throwable) {
                    Log.d(TAG, "onError: code = " + throwable);
                    ToastUtil.showToast("授权失败");
                }

                @Override
                public void onCancel(Platform platform, int i) {
                    Log.d(TAG, "onCancel: ");
                    ToastUtil.showToast("取消授权");
                }
            });
        }
        plat.SSOSetting(false);
        plat.showUser(null);
    }
}

