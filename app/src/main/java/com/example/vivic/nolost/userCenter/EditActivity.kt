package com.example.vivic.nolost.userCenter

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import com.example.vivic.nolost.bmob.UserRepository
import com.example.vivic.nolost.bmob.IBmobCallback
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import com.example.vivic.nolost.userCenter.UserCenterActivity.Companion.EDIT_CONTENT
import com.example.vivic.nolost.userCenter.UserCenterActivity.Companion.EDIT_RESULT
import com.example.vivic.nolost.userCenter.UserCenterActivity.Companion.EDIT_TITLE
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_edit.*

class EditActivity : BaseActivity() {

    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }
    private var title: String = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit)
        inputMethodManager.showSoftInput(et_edit_content, 0)
        initView()
    }

    private fun initView() {
        iv_edit_back.setOnClickListener { finish() }
        val intent = intent
        title = intent.getStringExtra(EDIT_TITLE)
        tv_edit_title.text = title
        val content = intent.getStringExtra(EDIT_CONTENT)
        et_edit_content.setText(content)
        et_edit_content.setSelection(content.length)
        tv_edit_save.setOnClickListener {
            updateUserIInfo()
        }

    }

    fun updateUserIInfo() {
        tv_edit_save.isEnabled = false
        val response = et_edit_content.text.toString()
        val editUser = MyUser()
        when (title) {
            getString(R.string.user_center_username) -> {
                editUser.username = response
            }
            getString(R.string.user_center_contact) -> {
                editUser.contact = response
            }
            getString(R.string.user_center_location) -> {
                editUser.location = response
            }
        }

        compositeDisposable.add(UserRepository.updateUserByNewUser(editUser, object : IBmobCallback<MyUser> {

            override fun success(result: MyUser?) {
                tv_edit_save.isEnabled = true
                ToastUtil.showToast("更新成功")
                val intent = Intent()
                intent.putExtra(EDIT_RESULT, et_edit_content.text.toString())
                setResult(Activity.RESULT_OK, intent)
                finish()
            }

            override fun error(throwable: Throwable?) {
                tv_edit_save.isEnabled = true
                ToastUtil.showToast("更新失败" + throwable.toString())
            }

        }))
    }
}
