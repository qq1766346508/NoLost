package com.example.vivic.nolost.userCenter

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.bmob.v3.BmobUser
import cn.bmob.v3.datatype.BmobFile
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.bmob.FileRepository
import com.example.vivic.nolost.bmob.IBmobCallback
import com.example.vivic.nolost.bmob.UserRepository
import com.example.vivic.nolost.commonUtil.bottomDialog.CommonBottomDialog
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import com.example.vivic.nolost.login.UserEvent
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_user_center.*
import org.devio.takephoto.app.TakePhoto
import org.devio.takephoto.app.TakePhotoImpl
import org.devio.takephoto.model.*
import org.devio.takephoto.permission.InvokeListener
import org.devio.takephoto.permission.PermissionManager
import org.devio.takephoto.permission.TakePhotoInvocationHandler
import org.greenrobot.eventbus.EventBus
import java.io.File
import java.util.*

class UserCenterActivity : BaseActivity(), TakePhoto.TakeResultListener, InvokeListener {

    companion object {
        val TAG = UserCenterActivity::class.java.simpleName
        const val REQUEST_CODE_CONTACT = 0
        const val REQUEST_CODE_LOCATION = 1
        const val REQUEST_CODE_USERNAME = 2
        const val EDIT_TITLE = "edit_title"
        const val EDIT_CONTENT = "edit_content"
        const val EDIT_RESULT = "edit_result"
    }

    private var invokeParam: InvokeParam? = null
    private var takePhoto: TakePhoto? = null
    private var photoList: ArrayList<TImage>? = null
    private val currentUser: MyUser by lazy {
        BmobUser.getCurrentUser(MyUser::class.java)
    }
    private val genderDialog: CommonBottomDialog by lazy {
        CommonBottomDialog.Builder(this)
                .item("男") {
                    updateGender(GenderHelper.MAN)
                }
                .item("女") {
                    updateGender(GenderHelper.FEMALE)
                }
                .item("私密") {
                    updateGender(GenderHelper.SECRET)
                }
                .cancel("取消") {
                    fl_user_center_gender.isEnabled = true
                }
                .build()
    }
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_center)
        loadUserInfo()
        initView()
    }


    private fun loadUserInfo() {
        Log.i(TAG, "currentUse :$currentUser")
        GlideApp.with(this).load(currentUser.avatar).placeholder(R.drawable.icon_default_avatar).into(iv_user_center_avatar)
        tv_user_center_username.text = currentUser.username
        currentUser.gender?.let {
            when (GenderHelper.formatGender(it)) {
                GenderHelper.MAN -> iv_user_center_gender.setImageResource(R.drawable.icon_boy)
                GenderHelper.FEMALE -> iv_user_center_gender.setImageResource(R.drawable.icon_girl)
                GenderHelper.SECRET -> iv_user_center_gender.setImageResource(R.drawable.icon_gender_secret)
            }
        }
        currentUser.contact?.let { tv_user_contact.text = it }
        currentUser.location?.let { tv_user_location.text = it }
    }

    private fun initView() {
        //第三方用户不可更改常规信息
        if (!currentUser.platform.isNullOrEmpty()) {
            fl_user_avatar.isEnabled = false
            fl_user_username.isEnabled = false
            fl_user_center_gender.isEnabled = false
            iv_user_center_avatar_aw.visibility = View.INVISIBLE
            iv_user_center_username_aw.visibility = View.INVISIBLE
            iv_user_center_gender_aw.visibility = View.INVISIBLE
        }
        iv_user_center_back.setOnClickListener { finish() }
        takePhoto = getTakePhoto()
        fl_user_avatar.setOnClickListener {
            fl_user_avatar.isEnabled = false
            val file = File(externalCacheDir, System.currentTimeMillis().toString() + ".jpg")
            val outputUri = Uri.fromFile(file)
            takePhoto?.onPickFromGalleryWithCrop(outputUri, CropOptions.Builder().setWithOwnCrop(true).create())
        }
        fl_user_center_gender.setOnClickListener {
            fl_user_center_gender.isEnabled = false
            genderDialog.show()
        }
        fl_user_username.setOnClickListener {
            val intent = Intent(this@UserCenterActivity, EditActivity::class.java).apply {
                this.putExtra(EDIT_TITLE, getString(R.string.user_center_username))
                this.putExtra(EDIT_CONTENT, tv_user_center_username.text.toString())
            }
            startActivityForResult(intent, REQUEST_CODE_USERNAME)
        }
        fl_user_contact.setOnClickListener {
            val intent = Intent(this@UserCenterActivity, EditActivity::class.java).apply {
                this.putExtra(EDIT_TITLE, getString(R.string.user_center_contact))
                this.putExtra(EDIT_CONTENT, tv_user_contact.text.toString())
            }
            startActivityForResult(intent, REQUEST_CODE_CONTACT)
        }
        fl_user_location.setOnClickListener {
            val intent = Intent(this@UserCenterActivity, EditActivity::class.java).apply {
                this.putExtra(EDIT_TITLE, getString(R.string.user_center_location))
                this.putExtra(EDIT_CONTENT, tv_user_location.text.toString())
            }
            startActivityForResult(intent, REQUEST_CODE_LOCATION)
        }
        genderDialog.setOnCancelListener {
            fl_user_center_gender.isEnabled = true
        }

    }

    override fun takeSuccess(result: TResult?) {
        photoList = result?.getImages()
        for (item in photoList!!) {
            Log.d(TAG, "TakePhoto,Success: " + item.originalPath)
        }
        uploadImage(photoList?.get(0)?.originalPath!!)
    }

    override fun takeCancel() {
        Log.d(TAG, "TakePhoto,Cancel: ")
        fl_user_avatar.isEnabled = true
    }

    override fun takeFail(result: TResult?, msg: String?) {
        Log.d(TAG, "TakePhoto,Fail: $msg")
        fl_user_avatar.isEnabled = true
    }

    override fun invoke(invokeParam: InvokeParam): PermissionManager.TPermissionType {
        val type = PermissionManager.checkPermission(TContextWrap.of(this), invokeParam.getMethod())
        if (PermissionManager.TPermissionType.WAIT == type) {
            this.invokeParam = invokeParam
        }
        return type
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_USERNAME -> tv_user_center_username.text = data?.getStringExtra(EDIT_RESULT)
                REQUEST_CODE_CONTACT -> tv_user_contact.text = data?.getStringExtra(EDIT_RESULT)
                REQUEST_CODE_LOCATION -> tv_user_location.text = data?.getStringExtra(EDIT_RESULT)
            }
        }
        getTakePhoto().onActivityResult(requestCode, resultCode, data)
        super.onActivityResult(requestCode, resultCode, data)
    }

    private fun uploadImage(imagePath: String) {
        val bmobFile = BmobFile(File(imagePath))
        compositeDisposable?.add(FileRepository.uploadFile(bmobFile, object : FileRepository.IFileCallback {
            override fun success(result: String?) {
                updateAvatar(result)
            }

            override fun error(throwable: Throwable?) {
                ToastUtil.showToast("上传图片错误，$throwable")
                fl_user_avatar.isEnabled = true
            }

            override fun progress(int: Int?) {

            }

        }))
    }

    private fun updateAvatar(avatar: String?) {
        compositeDisposable?.add(UserRepository.updateUserByNewUser(MyUser().apply {
            this.avatar = avatar
        }, object : IBmobCallback<MyUser> {
            override fun success(result: MyUser?) {
                GlideApp.with(this@UserCenterActivity).load(avatar).into(iv_user_center_avatar)
                fl_user_avatar.isEnabled = true
            }

            override fun error(throwable: Throwable?) {
                ToastUtil.showToast("更新失败" + throwable.toString())
                fl_user_avatar.isEnabled = true
            }
        }))
    }

    private fun updateGender(gender: String) {
        val myUser = MyUser()
        myUser.gender = gender
        compositeDisposable?.add(UserRepository.updateUserByNewUser(myUser, object : IBmobCallback<MyUser> {
            override fun success(result: MyUser?) {
                fl_user_center_gender.isEnabled = true
                when (gender) {
                    GenderHelper.MAN -> iv_user_center_gender.setImageResource(R.drawable.icon_boy)
                    GenderHelper.FEMALE -> iv_user_center_gender.setImageResource(R.drawable.icon_girl)
                    GenderHelper.SECRET -> iv_user_center_gender.setImageResource(R.drawable.icon_gender_secret)
                }
            }

            override fun error(throwable: Throwable?) {
                fl_user_center_gender.isEnabled = true
                ToastUtil.showToast("更新失败" + throwable.toString())
            }

        }))
    }

    private fun getTakePhoto(): TakePhoto {
        if (takePhoto == null) {
            takePhoto = TakePhotoInvocationHandler.of(this).bind(TakePhotoImpl(this, this)) as TakePhoto
        }
        return takePhoto as TakePhoto
    }

    override fun onBackPressed() {
        super.onBackPressed()
        EventBus.getDefault().post(UserEvent(true, BmobUser.getCurrentUser(MyUser::class.java))) //更新抽屉
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(UserEvent(true, BmobUser.getCurrentUser(MyUser::class.java))) //更新抽屉
        if (!compositeDisposable?.isDisposed!!) {
            compositeDisposable?.clear()
        }
    }
}
