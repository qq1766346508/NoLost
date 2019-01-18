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

}

