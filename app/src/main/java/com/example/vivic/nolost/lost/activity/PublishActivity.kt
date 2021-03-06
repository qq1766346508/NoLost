package com.example.vivic.nolost.lost.activity

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.support.v4.app.ActivityCompat
import android.support.v4.content.ContextCompat
import android.support.v7.widget.GridLayoutManager
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.inputmethod.InputMethodManager
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.example.vivic.nolost.NoLostApplication
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.bmob.DataRepository
import com.example.vivic.nolost.bmob.FileRepository
import com.example.vivic.nolost.bmob.IBmobCallback
import com.example.vivic.nolost.commonUtil.CommonTakePhotoActivity
import com.example.vivic.nolost.commonUtil.CommonTakePhotoActivity.TakeMode.PickMultiple
import com.example.vivic.nolost.commonUtil.CommonTakePhotoActivity.TakeMode.TAKE_LIMIT
import com.example.vivic.nolost.commonUtil.CommonTakePhotoActivity.TakeMode.TAKE_MODE
import com.example.vivic.nolost.commonUtil.NetworkUtil
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.GridSpacingItemDecoration
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter
import com.example.vivic.nolost.commonUtil.multiPhotoAdapter.MultiPhotoAdapter.LOAD_FILE
import com.example.vivic.nolost.commonUtil.progressBarDialog.ProgressBarDialog
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import kotlinx.android.synthetic.main.activity_publish.*
import java.lang.ref.WeakReference


class PublishActivity : BaseActivity() {


    companion object {
        val TAG = PublishActivity::class.java.simpleName
        const val REQUEST_CODE_TAKE_PHOTO = 0
        val WRITE_COARSE_LOCATION_REQUEST_CODE = 1
    }

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private val progressBarDialog: ProgressBarDialog by lazy {
        ProgressBarDialog()
    }

    private var photoAdapter: MultiPhotoAdapter? = null
    //声明AMapLocationClient类对象
    var mLocationClient: AMapLocationClient? = null
    private var mLocationListener: WeakReference<AMapLocationListener>? = null


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Log.d(TAG, "onCreate")
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
                et_publish_goodsname.requestFocus()
                return@setOnClickListener
            }
            photoAdapter?.photoPathList?.let {
                //如果没有图片则可以直接上传信息
                if (it.size == 0) {
                    saveGoodsInfo(null)
                } else {
                    upLoadMultiPhoto(photoAdapter?.photoPathList)
                }
            }
        }
        ll_publish_root.setOnClickListener { inputMethodManager.hideSoftInputFromWindow(et_publish_goodsname.windowToken, 0) }

        val gridLayoutManager = GridLayoutManager(this, 3)
        rv_publish_photo.layoutManager = gridLayoutManager
        photoAdapter = MultiPhotoAdapter(this)
        photoAdapter?.setEditable(true)
        photoAdapter?.setLoadMode(LOAD_FILE)
        rv_publish_photo.adapter = photoAdapter
        rv_publish_photo.addItemDecoration(GridSpacingItemDecoration(3, 5, true))
        btn_add_photo.setOnClickListener {
            val intent = Intent(this@PublishActivity, CommonTakePhotoActivity::class.java).apply {
                this.putExtra(TAKE_MODE, PickMultiple)
                this.putExtra(TAKE_LIMIT, 9 - photoAdapter?.photoPathList?.size!!)
            }
            if (photoAdapter?.photoPathList?.size == 9) {
                ToastUtil.showToast("最多选择9张")
                return@setOnClickListener
            }
            startActivityForResult(intent, REQUEST_CODE_TAKE_PHOTO)
        }
        //定位服务
        btn_publish_location.setOnClickListener {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                //申请WRITE_EXTERNAL_STORAGE权限
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION),
                        WRITE_COARSE_LOCATION_REQUEST_CODE);//自定义的code
            } else {
                initLocation()
            }
        }
    }

    fun initLocation() {
        mLocationClient = AMapLocationClient(NoLostApplication.getContext())
        val mLocationOption = AMapLocationClientOption().apply {
            this.isOnceLocation = true
        }
        mLocationClient?.setLocationOption(mLocationOption)
        mLocationListener = WeakReference(AMapLocationListener { amapLocation ->
            amapLocation?.let {
                if (it.errorCode == 0) {
                    Log.d(TAG, "location success,poi:${it.description}")
                    et_publish_goodslocation.setText(it.description)
                    et_publish_goodslocation.setSelection(it.description.length)
                } else {
                    Log.e(TAG, "location Error, ErrCode:${it.errorCode}, errInfo:${it.errorInfo}");
                    ToastUtil.showToast("定位失败")
                }
            }
        })
        mLocationClient?.setLocationListener(mLocationListener?.get())
        mLocationClient?.startLocation()
    }

    /**
     * 先上传完图片再提交物品
     */
    private fun upLoadMultiPhoto(photoList: MutableList<String>?) {
        progressBarDialog.show(this)
        FileRepository.uploadBatchFile(photoList!!, object : FileRepository.IFileBatchCallback {
            override fun success(files: MutableList<BmobFile>, urls: MutableList<String>?) {
                if (photoList.size == urls?.size) {
                    saveGoodsInfo(urls)
                }
            }

            override fun error(statuscode: Int, errormsg: String) {
                ToastUtil.showToast("upLoadMultiPhoto fail,statuscode:$statuscode,errormsg$errormsg")
            }

            override fun progress(curIndex: Int?, curPercent: Int, total: Int, totalPercent: Int) {
                progressBarDialog.setValue(curIndex!!, curPercent, total, totalPercent)
            }

        })

    }

    private fun saveGoodsInfo(photoList: MutableList<String>?) {
        val goods = Goods().apply {
            this.creatorObjectId = BmobUser.getCurrentUser(MyUser::class.java).objectId
            this.creatorName = BmobUser.getCurrentUser(MyUser::class.java).username
            this.creatorAvatar = BmobUser.getCurrentUser(MyUser::class.java).avatar
            this.name = et_publish_goodsname.text.toString()
            this.location = et_publish_goodslocation.text.toString()
            this.detail = et_publish_goodsdetail.text.toString()
            this.type = if (rg_publish_goodstype.checkedRadioButtonId == rb_publish_goods_lost.id) Goods.TYPE_LOST else Goods.TYPE_FOUND
            this.photoList = photoList
        }
        Log.i(TAG, goods.toString())
        addSubscribe(DataRepository.saveData(goods, object : IBmobCallback<String> {
            override fun success(result: String?) {
                ToastUtil.showToast("提交成功")
                if (photoList != null && photoList.size != 0) {
                    progressBarDialog.dismissAllowingStateLoss()
                }
                finish()
            }

            override fun error(throwable: Throwable?) {
                ToastUtil.showToast("提交失败,${throwable.toString()}")
                if (photoList != null && photoList.size != 0) {
                    progressBarDialog.dismissAllowingStateLoss()
                }
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

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            WRITE_COARSE_LOCATION_REQUEST_CODE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    initLocation()
                } else {
                    ToastUtil.showToast("没有权限")
                }
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onStop() {
        super.onStop()
        mLocationClient?.stopLocation();
    }

    override fun onDestroy() {
        mLocationListener = null
        mLocationClient?.onDestroy()
        super.onDestroy()
    }
}
