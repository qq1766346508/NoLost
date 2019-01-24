package com.example.vivic.nolost.commonUtil

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import com.example.vivic.nolost.commonUtil.TakePhotoActivity.TakeMode.PickFromGalleryWithCrop
import com.example.vivic.nolost.commonUtil.TakePhotoActivity.TakeMode.PickMultiple
import com.example.vivic.nolost.commonUtil.TakePhotoActivity.TakeMode.TAKE_MODE
import com.example.vivic.nolost.commonUtil.TakePhotoActivity.TakeMode.TAKE_RESULT
import com.example.vivic.nolost.commonUtil.TakePhotoActivity.TakeMode.multipleLimit
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.app.TakePhotoImpl
import org.devio.takephoto.model.CropOptions
import org.devio.takephoto.model.InvokeParam
import org.devio.takephoto.model.TContextWrap
import org.devio.takephoto.model.TResult
import org.devio.takephoto.permission.InvokeListener
import org.devio.takephoto.permission.PermissionManager
import org.devio.takephoto.permission.TakePhotoInvocationHandler
import java.io.File

class TakePhotoActivity : Activity(), TakePhoto.TakeResultListener, InvokeListener {
    private var invokeParam: InvokeParam? = null
    private val myTakePhoto: TakePhoto by lazy {
        TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, this)) as TakePhoto
    }
    private val cropOptions: CropOptions by lazy {
        CropOptions.Builder().setWithOwnCrop(true).create();
    }
    private var outputUri: Uri? = null

    companion object {

        private val TAG = TakePhotoActivity::class.java.simpleName

    }

    object TakeMode {
        val TAKE_MODE = "take_mode"
        val TAKE_RESULT = "take_result"
        val PickFromGalleryWithCrop = 0
        val PickFromGallery = 1
        val PickMultiple = 2
        val multipleLimit = 9
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        outputUri = Uri.fromFile(File(externalCacheDir, System.currentTimeMillis().toString() + ".jpg"))
        when (intent.getIntExtra(TAKE_MODE, 0)) {
            PickFromGalleryWithCrop -> myTakePhoto.onPickFromGalleryWithCrop(outputUri, cropOptions)
            PickMultiple -> myTakePhoto.onPickMultiple(multipleLimit)
        }
    }

    override fun takeSuccess(result: TResult) {
        val list = result.images
        val resultList = ArrayList<String>()
        for (i in list.indices) {
            resultList.add(list[i].originalPath)
            Log.d(TAG, "takeSuccess: " + list[i].originalPath)
        }
        val intent = Intent().apply {
            this.putStringArrayListExtra(TAKE_RESULT, resultList)
        }
        setResult(Activity.RESULT_OK, intent)
        finish()
    }

    override fun takeFail(result: TResult, msg: String) {
        Log.d(TAG, "takeFail: $msg")
        finish()
    }

    override fun takeCancel() {
        Log.d(TAG, "takeCancel: ")
        finish()
    }

    override fun invoke(invokeParam: InvokeParam): PermissionManager.TPermissionType? {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.method)
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        myTakePhoto.onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        val type = PermissionManager.onRequestPermissionsResult(requestCode, permissions, grantResults)
        PermissionManager.handlePermissionsResult(this, type, invokeParam, this)
    }
}
