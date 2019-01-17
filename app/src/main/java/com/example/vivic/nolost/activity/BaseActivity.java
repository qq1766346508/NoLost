package com.example.vivic.nolost.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.example.vivic.nolost.R;

import org.greenrobot.eventbus.EventBus;

public class BaseActivity extends AppCompatActivity {
    private static final String TAG = BaseActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "onCreate: " + getClass().getSimpleName());
        EventBus.getDefault().register(this);
        overridePendingTransition(R.anim.activity_right_to_left_in, R.anim.activity_right_to_left_out);
    }

    @Override
    public void finish() {
        super.finish();
        EventBus.getDefault().unregister(this);
        overridePendingTransition(R.anim.activity_left_to_right_in, R.anim.activity_left_to_right_out);
    }
}
