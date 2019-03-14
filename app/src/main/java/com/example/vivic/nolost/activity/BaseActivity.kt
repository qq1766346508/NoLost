package com.example.vivic.nolost.activity

import android.content.Context
import android.content.ContextWrapper
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.InputMethodManager
import com.example.vivic.nolost.R
import com.example.vivic.nolost.commonUtil.BindEventBus
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import me.imid.swipebacklayout.lib.SwipeBackLayout
import me.imid.swipebacklayout.lib.app.SwipeBackActivity
import org.greenrobot.eventbus.EventBus
import java.lang.reflect.Field
import java.util.*

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

//    override fun onBackPressed() {
//        super.onBackPressed()
//        unSubscribe()
//    }

    override fun onDestroy() {
        unSubscribe()
        super.onDestroy()
        Log.d(TAG, "onDestroy: " + javaClass.simpleName)
        if (javaClass.isAnnotationPresent(BindEventBus::class.java)) {
            EventBus.getDefault().unregister(this)
        }
        fixInputMethodLeak()
    }

    /**
     * 修复输入法内存泄漏问题
     */
    protected fun fixInputMethodLeak() {
        val imm = applicationContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        releaseFieldsAsNull(imm, Arrays.asList("mCurRootView", "mServedView", "mNextServedView"))
    }

    /**
     * 给所有的view去除当前的context引用
     *
     * @param inst
     * @param names
     */
    protected fun releaseFieldsAsNull(inst: Any?, names: List<String>?) {
        if (null == inst || null == names || names.isEmpty()) {
            return
        }
        var fields: Array<Field>? = null
        fields = inst.javaClass.declaredFields
        if (fields != null && fields.isNotEmpty()) {
            for (field in fields) {
                if (names.contains(field.name)) {
                    try {
                        if (!field.isAccessible) {
                            field.isAccessible = true
                        }
                        val objGet = field.get(inst)
                        if (objGet != null && objGet is View) {
                            if (objGet.context === this) { // 被InputMethodManager持有引用的context是想要目标销毁的
                                field.set(inst, null) // 置空，破坏掉path to gc节点
                            } else if (objGet.context is ContextWrapper && (objGet.context as ContextWrapper).baseContext === this) { // 被InputMethodManager持有引用的TintContextWrapper是想要目标销毁的
                                field.set(inst, null) // 置空，破坏掉path to gc节点
                            }
                        }
                    } catch (throwable: Throwable) {
                        throwable.printStackTrace()
                    }

                }
            }
        }
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
