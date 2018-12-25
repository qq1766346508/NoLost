package com.example.vivic.nolost.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.EditText;

import com.example.vivic.nolost.R;

import java.util.HashMap;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class LoginActivity extends AppCompatActivity {

    private EditText edPhone;
    private EditText edCode;
    private Button btnReq;
    private Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        initview();
    }

    private void initview() {
        edPhone = findViewById(R.id.ed_phone);
        edCode = findViewById(R.id.ed_code);
        btnReq = findViewById(R.id.btn_request_code);
        btnLogin = findViewById(R.id.btn_login);
    }
}
