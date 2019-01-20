package com.example.vivic.nolost.userCenter

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import cn.bmob.v3.BmobUser
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.Login.IBmobCallback
import com.example.vivic.nolost.Login.UpdateUserInfoEvent
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener
import com.example.vivic.nolost.commonUtil.bottomDialog.CommonBottomDialog
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_user_center.*
import org.greenrobot.eventbus.EventBus

class UserCenterActivity : BaseActivity() {

    companion object {
        val TAG = UserCenterActivity::class.java.simpleName
        val REQUEST_CODE_CONTACT = 0
        val REQUEST_CODE_LOCATION = 1
        val REQUEST_CODE_USERNAME = 2
    }

    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_user_center)
        loadUserInfo()
        initView()
    }


    private fun loadUserInfo() {
        Log.i(TAG, "currentUse :$currentUser")
        GlideApp.with(this).load(currentUser.avatar).into(iv_user_center_avatar)
        tv_user_center_username.text = currentUser.username
        currentUser.gender?.let {
            when (GenderHelper.formatGender(currentUser.gender)) {
                GenderHelper.MAN -> iv_user_center_gender.setImageResource(R.drawable.icon_boy)
                GenderHelper.FEMALE -> iv_user_center_gender.setImageResource(R.drawable.icon_girl)
                GenderHelper.SECRET -> iv_user_center_gender.setImageResource(R.drawable.icon_gender_secret)
            }
        }
        currentUser.contact?.let { tv_user_contact.text = it }
        currentUser.location?.let { tv_user_location.text = it }
        iv_user_center_back.setOnClickListener { finish() }
    }

    private fun initView() {
        fl_user_center_gender.setOnClickListener {
            fl_user_center_gender.isEnabled = false
            genderDialog.show()
        }
        fl_user_username.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val intent = Intent(this@UserCenterActivity, EditActivity::class.java)
                intent.putExtra("edit_title", getString(R.string.user_center_username))
                intent.putExtra("edit_content", tv_user_center_username.text.toString())
                startActivityForResult(intent, REQUEST_CODE_USERNAME)
            }

            override fun onDoubleClick() {
            }

        })
        fl_user_contact.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val intent = Intent(this@UserCenterActivity, EditActivity::class.java)
                intent.putExtra("edit_title", getString(R.string.user_center_contact))
                intent.putExtra("edit_content", tv_user_contact.text.toString())
                startActivityForResult(intent, REQUEST_CODE_CONTACT)
            }

            override fun onDoubleClick() {
            }

        })
        fl_user_location.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View?) {
                val intent = Intent(this@UserCenterActivity, EditActivity::class.java)
                intent.putExtra("edit_title", getString(R.string.user_center_location))
                intent.putExtra("edit_content", tv_user_location.text.toString())
                startActivityForResult(intent, REQUEST_CODE_LOCATION)
            }

            override fun onDoubleClick() {
            }

        })
        genderDialog.setOnCancelListener {
            fl_user_center_gender.isEnabled = true
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == Activity.RESULT_OK) {
            when (requestCode) {
                REQUEST_CODE_USERNAME -> tv_user_center_username.text = data?.getStringExtra("edit_response")
                REQUEST_CODE_CONTACT -> tv_user_contact.text = data?.getStringExtra("edit_response")
                REQUEST_CODE_LOCATION -> tv_user_location.text = data?.getStringExtra("edit_response")
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }

    fun updateGender(gender: String) {
        val myUser = MyUser()
        myUser.gender = gender
        compositeDisposable.add(UserRepository.updateUserByNewUser(myUser, object : IBmobCallback<MyUser> {
            override fun success(result: MyUser?) {
                fl_user_center_gender.isEnabled = true
                when (gender) {
                    GenderHelper.MAN -> iv_user_center_gender.setImageResource(R.drawable.icon_boy)
                    GenderHelper.FEMALE -> iv_user_center_gender.setImageResource(R.drawable.icon_girl)
                    GenderHelper.SECRET -> iv_user_center_gender.setImageResource(R.drawable.icon_gender_secret)
                }
                ToastUtil.showToast("更新成功")
            }

            override fun error(throwable: Throwable?) {
                fl_user_center_gender.isEnabled = true
                ToastUtil.showToast("失败" + throwable.toString())
            }

        }))
    }

    override fun onDestroy() {
        super.onDestroy()
        EventBus.getDefault().post(UpdateUserInfoEvent(true, BmobUser.getCurrentUser(MyUser::class.java)))
        compositeDisposable.clear()
    }
}
