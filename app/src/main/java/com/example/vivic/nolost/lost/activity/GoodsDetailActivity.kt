package com.example.vivic.nolost.lost.activity

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.TextUtils
import android.util.Log
import android.view.View
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.QueryListener
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.GridSpacingItemDecoration
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter.LOAD_INTERNET
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import kotlinx.android.synthetic.main.activity_goods_detail.*

class GoodsDetailActivity : BaseActivity() {

    companion object {
        val TAG = GoodsDetailActivity::class.java.simpleName

        fun getActivity(activity: Activity, clipID: String?) {
            val intent = Intent(activity, GoodsDetailActivity::class.java).apply {
                this.putExtra("clipID", clipID)
            }
            Log.d(TAG, "clipId = $clipID")
            activity.startActivity(intent)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
        setContentView(R.layout.activity_goods_detail)
        queryByClipId()
    }

    private fun queryByClipId() {
        Log.d(TAG, "queryByClipId")
        val clipId = intent.getStringExtra("clipID")
        val query = BmobQuery<Goods>()
        query.getObject(clipId, object : QueryListener<Goods>() {
            override fun done(goods: Goods?, exception: BmobException?) {
                if (exception == null) {
                    Log.d(TAG, "query clipid success ,result = +$goods")
                    loadGoods(goods)
                } else {
                    Log.d(TAG, "query clipid fail ,exception = $exception")
                    ToastUtil.showToast("该遗失物品信息已被删除")
                    finish()
                }
            }
        })
        iv_goods_detail_back.setOnClickListener { finish() }
    }

    private fun loadGoods(goods: Goods?) {
        goods?.let {
            GlideApp.with(this).load(if (goods.creatorAvatar == null) R.drawable.icon_default_avatar else goods.creatorAvatar)
                    .circleCrop().placeholder(R.drawable.icon_default_avatar).into(iv_goods_detail_avatar)
            tv_goods_detail_createor.text = goods.creatorName
            tv_goods_detail_time.text = goods.updatedAt
            tv_goods_detail_name.text = "物品名称：" + goods.name
            tv_goods_detail_details.text = "物品详情：" + goods.detail
            if (goods.location == null || TextUtils.isEmpty(goods.location)) {
                tv_goods_detail_location.visibility = View.GONE
            } else {
                tv_goods_detail_location.visibility = View.VISIBLE
                tv_goods_detail_location.text = goods.location
            }

            val multiPhotoAdapter = MultiPhotoAdapter(this)
            multiPhotoAdapter.setEditable(false)
            multiPhotoAdapter.setLoadMode(LOAD_INTERNET)
            val manager = GridLayoutManager(this, 3)
            rv_goods_detail_photo.setLayoutManager(manager)
            rv_goods_detail_photo.addItemDecoration(GridSpacingItemDecoration(3, 5, true))
            rv_goods_detail_photo.adapter = multiPhotoAdapter
            multiPhotoAdapter.addPhotoPath(goods.photoList)
        }
    }

}
