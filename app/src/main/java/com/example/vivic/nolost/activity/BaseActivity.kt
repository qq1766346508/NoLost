package com.example.vivic.nolost.activity

import android.os.Bundle
import android.util.Log
import com.example.vivic.nolost.R
import com.example.vivic.nolost.commonUtil.BindEventBus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.app.SwipeBackActivity
import org.greenrobot.eventbus.EventBus

open class BaseActivity : SwipeBackActivity() {
    companion object {
        private val TAG = BaseActivity::class.java.simpleName
    }

    private var compositeDisposable: CompositeDisposable? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate: " + javaClass.simpleName)
        if (javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBus.getDefault().register(this)
        }
        swipeBackLayout.setEdgeTrackingEnabled(SwipeBackLayout.EDGE_LEFT)
        overridePendingTransition(R.anim.activity_right_to_left_in, R.anim.activity_right_to_left_out)
        val maxMemory = (Runtime.getRuntime().maxMemory() / (1024 * 1024)).toInt()
        Log.d(TAG, "Max memory is " + maxMemory + "MB")
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(R.anim.activity_left_to_right_in, R.anim.activity_left_to_right_out)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        unSubscribe()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + javaClass.simpleName)
        if (javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBus.getDefault().unregister(this)
        }
        unSubscribe()
    }

    fun addSubscribe(disposable: Disposable) {
        if (compositeDisposable == null) {
            compositeDisposable = CompositeDisposable()
        }
        compositeDisposable?.add(disposable)
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
