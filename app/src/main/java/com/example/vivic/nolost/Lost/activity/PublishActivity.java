package com.example.vivic.nolost.Lost.activity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.vivic.nolost.R;
import com.example.vivic.nolost.activity.BaseActivity;
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener;

public class PublishActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish);
        initView();
    }

    private void initView() {
        ImageView ivBack = findViewById(R.id.iv_publish_back);
        ivBack.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                finish();
            }

            @Override
            protected void onDoubleClick() {

            }
        });
    }
}
