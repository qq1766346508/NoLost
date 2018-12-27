package com.example.vivic.nolost.Login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.activity.BaseActivity;
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener;

public class LoginActivity extends BaseActivity {

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

    }
}
