package com.example.vivic.nolost.Login;


import android.arch.lifecycle.ViewModelProviders;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.activity.BaseActivity;

import cn.sharesdk.sina.weibo.SinaWeibo;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        initview();
    }

    private void initview() {
        ImageView ivWeibo = findViewById(R.id.iv_login_weibo);
        ivWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginViewModel.loginByThird(SinaWeibo.NAME);
            }
        });
    }
}
