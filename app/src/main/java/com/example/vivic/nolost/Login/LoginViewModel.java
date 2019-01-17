package com.example.vivic.nolost.Login;

import android.arch.lifecycle.ViewModel;
import android.util.Log;

import com.example.vivic.nolost.bean.MyUser;
import com.example.vivic.nolost.commonUtil.pref.CommonPref;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;

import org.greenrobot.eventbus.EventBus;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;

public class LoginViewModel extends ViewModel {
    private static final String TAG = "LoginViewModel";
    private static final String HAS_LOGIN = "HAS_LOGIN";
    public MyUser myUser;

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
        if (!plat.isAuthValid()) {
            plat.setPlatformActionListener(new PlatformActionListener() {
                @Override
                public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) { //成功回调在子线程，ui修改要回到主线程
                    Log.d(TAG, "onComplete: ");
                    myUser = new MyUser();
                    myUser.thirdId = plat.getDb().getUserId();
                    myUser.nickName = plat.getDb().getUserName();
                    myUser.sex = plat.getDb().getUserGender();
                    myUser.avatar = plat.getDb().getUserIcon();
                    Log.d(TAG, "onComplete: " + myUser.toString());
                    EventBus.getDefault().post(new LoginEvent(platform.getName(), true, myUser));
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

