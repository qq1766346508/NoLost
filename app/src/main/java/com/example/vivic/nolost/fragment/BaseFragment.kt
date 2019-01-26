package com.example.vivic.nolost.fragment

import android.os.Bundle
import android.support.v4.app.Fragment
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.commonUtil.BindEventBus
import com.example.vivic.nolost.commonUtil.LeakCanaryUtils
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import org.greenrobot.eventbus.EventBus

open class BaseFragment : Fragment() {

    companion object {
        private val TAG = "BaseFragment"
    }

    private var compositeDisposable: CompositeDisposable? = null

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
        unSubscribe()
    }

    fun addSubscribe(disposable: Disposable?) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        disposable?.let {
            compositeDisposable?.add(it)
        }
    }

    /**
     * 如果不进行addSubscribe操作，compositeDisposable为空
     */
    fun unSubscribe() {
        compositeDisposable?.let {
            if (!it.isDisposed) {
                it.dispose()
            }
        }
    }

}
