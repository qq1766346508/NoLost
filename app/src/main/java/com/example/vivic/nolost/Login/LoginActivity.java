package com.example.vivic.nolost.Login;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.activity.BaseActivity;
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;

public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
    }

    private void initview() {

        Button btn = findViewById(R.id.btn_register);
        btn.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                startActivity(new Intent(LoginActivity.this, LoginActivity.class));
            }

            @Override
            protected void onDoubleClick() {

            }
        });

        ImageView ivWeibo = findViewById(R.id.iv_login_weibo);
        ivWeibo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Platform plat = ShareSDK.getPlatform(SinaWeibo.NAME);
                if (plat.isAuthValid()) {
                    String userID = plat.getDb().getUserId();
                    String userName = plat.getDb().getUserName();
                    String userSex = plat.getDb().getUserGender();

                    Log.d(TAG, "onClick: userID=" + userID + ",username=" + userName + ",sex= " + userSex);

                } else {
                    plat.setPlatformActionListener(new PlatformActionListener() {
                        @Override
                        public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                            Log.d(TAG, "onComplete: ");
                            Iterator ite = hashMap.entrySet().iterator();
                            while (ite.hasNext()) {
                                Map.Entry entry = (Map.Entry) ite.next();
                                Object key = entry.getKey();
                                Object value = entry.getValue();
                                Log.d(TAG, "onComplete: key:" + key + ",value:" + value + "\n");
                            }
                        }

                        @Override
                        public void onError(Platform platform, int i, Throwable throwable) {
                            Log.d(TAG, "onError: ");
                        }

                        @Override
                        public void onCancel(Platform platform, int i) {
                            Log.d(TAG, "onCancel: ");
                        }
                    });
                }
                plat.SSOSetting(false);
                plat.showUser(null);
            }
        });
    }
}
