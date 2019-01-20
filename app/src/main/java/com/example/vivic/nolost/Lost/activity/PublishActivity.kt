package com.example.vivic.nolost.Lost.activity

import android.os.Bundle
import android.util.Log
import cn.bmob.v3.BmobUser
import com.example.vivic.nolost.Login.IBmobCallback
import com.example.vivic.nolost.Lost.DataRepository
import com.example.vivic.nolost.Lost.GoodsEvent
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_publish.*
import org.greenrobot.eventbus.EventBus


class PublishActivity : BaseActivity() {

    private val TAG = PublishActivity::class.java.simpleName
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish)
        initView()
    }

    private fun initView() {
        iv_publish_back.setOnClickListener { finish() }
        btn_publish_save.setOnClickListener {
            val goods = Goods()
            goods.createor = BmobUser.getCurrentUser(MyUser::class.java)
            goods.name = et_publish_goodsname.text.toString()
            goods.location = et_publish_goodslocation.text.toString()
            goods.detail = et_publish_goodsdetail.text.toString()
            goods.type = if (rg_publish_goodstype.checkedRadioButtonId == rb_publish_goods_lost.id) Goods.TYPE_LOST else Goods.TYPE_FOUND
            Log.i(TAG, goods.toString())

            if (goods.name.isNullOrEmpty() || goods.type.isNullOrEmpty()) {
                ToastUtil.showToast("物品状态、名称不能为空")
                return@setOnClickListener
            }

            compositeDisposable.add(DataRepository.saveData(goods, object : IBmobCallback<String> {
                override fun success(result: String?) {
                    ToastUtil.showToast("数据提交成功")
                    EventBus.getDefault().post(GoodsEvent(goods, GoodsEvent.operate.save))
                    finish()
                }

                override fun error(throwable: Throwable?) {
                    ToastUtil.showToast("数据提交失败,${throwable.toString()}")
                }

            }))
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        compositeDisposable.clear()
    }
}
