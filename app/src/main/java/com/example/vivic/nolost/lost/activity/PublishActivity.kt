package com.example.vivic.nolost.lost.activity

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import cn.bmob.v3.BmobUser
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.bmob.DataRepository
import com.example.vivic.nolost.bmob.IBmobCallback
import com.example.vivic.nolost.commonUtil.CommonTakePhotoActivity
import com.example.vivic.nolost.commonUtil.CommonTakePhotoActivity.TakeMode.PickMultiple
import com.example.vivic.nolost.commonUtil.CommonTakePhotoActivity.TakeMode.TAKE_MODE
import com.example.vivic.nolost.commonUtil.MultiPhotoAdapter
import com.example.vivic.nolost.commonUtil.NetworkUtil
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import kotlinx.android.synthetic.main.activity_publish.*


class PublishActivity : BaseActivity() {


    companion object {
        val TAG = PublishActivity::class.java.simpleName
        const val REQUEST_CODE_TAKE_PHOTO = 0
    }

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    private var photoAdapter: MultiPhotoAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_publish)
        initView()
    }

    private fun initView() {
        iv_publish_back.setOnClickListener { finish() }
        rg_publish_goodstype.check(rb_publish_goods_lost.id) //默认选中丢失
        et_publish_goodsname.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
            }

            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                til_publish_goodsname.isErrorEnabled = false
            }

        })
        btn_publish_save.setOnClickListener {
            if (!NetworkUtil.isConnected()) {
                ToastUtil.showToast("当前无网络")
                return@setOnClickListener
            }
            if (et_publish_goodsname.text.toString().isEmpty()) {
                til_publish_goodsname.error = "物品名称不能为空"
                return@setOnClickListener
            }
            upLoadMultiPhoto(photoAdapter?.photoPathList!!)
        }
        ll_publish_root.setOnClickListener { inputMethodManager.hideSoftInputFromWindow(et_publish_goodsname.windowToken, 0) }

        val gridLayoutManager = GridLayoutManager(this, 3)
        rv_publish_photo.layoutManager = gridLayoutManager
        photoAdapter = MultiPhotoAdapter(this)
        rv_publish_photo.adapter = photoAdapter
        ll_add_photo.setOnClickListener {
            val intent = Intent(this@PublishActivity, CommonTakePhotoActivity::class.java).apply {
                this.putExtra(TAKE_MODE, PickMultiple)
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO)
        }
    }

    /**
     * 先上传完图片再提交物品
     */
    private fun upLoadMultiPhoto(photoList: MutableList<String>?) {
        SaveGoodsInfo()
    }

    private fun SaveGoodsInfo() {
        val goods = Goods().apply {
            this.creatorObjectId = BmobUser.getCurrentUser(MyUser::class.java).objectId
            this.creatorName = BmobUser.getCurrentUser(MyUser::class.java).username
            this.creatorAvatar = BmobUser.getCurrentUser(MyUser::class.java).avatar
            this.name = et_publish_goodsname.text.toString()
            this.location = et_publish_goodslocation.text.toString()
            this.detail = et_publish_goodsdetail.text.toString()
            this.type = if (rg_publish_goodstype.checkedRadioButtonId == rb_publish_goods_lost.id) Goods.TYPE_LOST else Goods.TYPE_FOUND
        }
        Log.i(TAG, goods.toString())
        addSubscribe(DataRepository.saveData(goods, object : IBmobCallback<String> {
            override fun success(result: String?) {
                ToastUtil.showToast("提交成功")
                finish()
            }

            override fun error(throwable: Throwable?) {
                ToastUtil.showToast("提交失败,${throwable.toString()}")
            }

        }))
    }

    private fun getPhotoPath(photoList: ArrayList<String>?) {
        photoList?.let {
            for (item in photoList) {
                Log.i(TAG, "take photo :success = $item}")
            }
            photoAdapter?.addPhotoPath(it)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_TAKE_PHOTO -> getPhotoPath(data?.getStringArrayListExtra(CommonTakePhotoActivity.TakeMode.TAKE_RESULT))
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }
}
