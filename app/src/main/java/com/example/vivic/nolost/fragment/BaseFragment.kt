package com.example.vivic.nolost.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.vivic.nolost.commonUtil.BindEventBus
import com.example.vivic.nolost.commonUtil.LeakCanaryUtils
import org.greenrobot.eventbus.EventBus

open class BaseFragment : Fragment() {

    companion object {
        private val TAG = "BaseFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBus.getDefault().register(this)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        LeakCanaryUtils.watch(this)
        if (this.javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBus.getDefault().unregister(this)
        }
    }


}
