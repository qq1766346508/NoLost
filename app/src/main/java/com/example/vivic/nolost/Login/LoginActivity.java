package com.example.vivic.nolost.Login;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.activity.BaseActivity;
import com.example.vivic.nolost.commonUtil.BindEventBus;
import com.example.vivic.nolost.commonUtil.NetworkUtil;
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;

import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.sharesdk.sina.weibo.SinaWeibo;

@BindEventBus
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    private ImageView ivIcon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        initview();
    }

    private void initview() {
        ivIcon = findViewById(R.id.iv_login_icon);
        ImageView ivWeibo = findViewById(R.id.iv_login_weibo);
        ivWeibo.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (!NetworkUtil.isConnected()) {
                    ToastUtil.showToast("当前无网络");
                    return;
                }
                loginViewModel.loginByThird(SinaWeibo.NAME);
            }

            @Override
            protected void onDoubleClick() {

            }
        });
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginCallback(LoginEvent loginEvent) {
        if (loginEvent.loginResult) {
            ToastUtil.showToast("登录成功，欢迎" + loginEvent.myUser.nickName);
            Glide.with(this).load(loginEvent.myUser.avatar).into(ivIcon);
        }
    }
}
