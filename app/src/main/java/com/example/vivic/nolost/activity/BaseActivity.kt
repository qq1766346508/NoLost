package com.example.vivic.nolost.activity

import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import com.bumptech.glide.Glide

import com.example.vivic.nolost.R
import com.example.vivic.nolost.commonUtil.BindEventBus

import org.greenrobot.eventbus.EventBus

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: " + javaClass.simpleName)
        if (javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBus.getDefault().register(this)
        }
        overridePendingTransition(R.anim.activity_right_to_left_in, R.anim.activity_right_to_left_out)
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_left_to_right_in, R.anim.activity_left_to_right_out)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + javaClass.simpleName)
        if (javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBus.getDefault().unregister(this)
        }
        Glide.get(this).clearMemory()

    }

    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }
}