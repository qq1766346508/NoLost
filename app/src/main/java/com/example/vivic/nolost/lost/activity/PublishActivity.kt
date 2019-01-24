package com.example.vivic.nolost.lost.activity

import android.content.Context
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
import com.example.vivic.nolost.commonUtil.MultiPhotoAdapter
import com.example.vivic.nolost.commonUtil.NetworkUtil
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import kotlinx.android.synthetic.main.activity_publish.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.app.TakePhotoImpl
import org.devio.takephoto.model.InvokeParam
import org.devio.takephoto.model.TContextWrap
import org.devio.takephoto.model.TImage
import org.devio.takephoto.model.TResult
import org.devio.takephoto.permission.InvokeListener
import org.devio.takephoto.permission.PermissionManager
import org.devio.takephoto.permission.TakePhotoInvocationHandler
import java.util.*


class PublishActivity : BaseActivity(), TakePhoto.TakeResultListener, InvokeListener {
    private var invokeParam: InvokeParam? = null
    private var takePhoto: TakePhoto? = null
    private var photoList: ArrayList<TImage>? = null


    companion object {
        val TAG = PublishActivity::class.java.simpleName
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
            if (goods.name.isNullOrEmpty() || goods.type.isNullOrEmpty()) {
                til_publish_goodsname.error = "物品名称不能为空"
                return@setOnClickListener
            }

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
        ll_publish_root.setOnClickListener { inputMethodManager.hideSoftInputFromWindow(et_publish_goodsname.windowToken, 0) }
        val gridLayoutManager = GridLayoutManager(this, 3)
        rv_publish_photo.layoutManager = gridLayoutManager
        photoAdapter = MultiPhotoAdapter(this)
        rv_publish_photo.adapter = photoAdapter
        takePhoto = getTakePhoto()
    }


    override fun takeSuccess(result: TResult?) {
        photoList = result?.getImages()
        for (item in photoList!!) {
            Log.d(TAG, "TakePhoto,Success: " + item.originalPath)
        }
    }

    override fun takeCancel() {
        Log.d(TAG, "TakePhoto,Cancel: ")
    }

    override fun takeFail(result: TResult?, msg: String?) {
        Log.d(TAG, "TakePhoto,Fail: $msg")
    }

    override fun invoke(invokeParam: InvokeParam): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod())
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }

    private fun getTakePhoto(): TakePhoto {
        if (takePhoto == null) {
            takePhoto = TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, this)) as TakePhoto
        }
        return takePhoto as TakePhoto
    }
}
