package com.example.vivic.nolost.Login


import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import cn.bmob.v3.exception.BmobException
import cn.sharesdk.sina.weibo.SinaWeibo
import cn.sharesdk.tencent.qq.QQ
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R
import com.example.vivic.nolost.activity.BaseActivity
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.commonUtil.BindEventBus
import com.example.vivic.nolost.commonUtil.NetworkUtil
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener
import com.example.vivic.nolost.commonUtil.pref.CommonPref
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import com.example.vivic.nolost.userCenter.UserRepository
import com.xiasuhuei321.loadingdialog.view.LoadingDialog
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.activity_login.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@BindEventBus
class LoginActivity : BaseActivity() {


    companion object {
        val TAG = LoginActivity::class.java.simpleName
    }

    private var loadingDialog: LoadingDialog? = null
    private var currentThirdPlatform: String? = null
    private val compositeDisposable: CompositeDisposable by lazy {
        CompositeDisposable()
    }
    private val inputMethodManager: InputMethodManager by lazy {
        getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initview()
    }


    private fun initview() {
        cl_login_root.setOnClickListener { v -> inputMethodManager.hideSoftInputFromWindow(et_login_account!!.windowToken, 0) }
        et_login_password.setOnEditorActionListener { v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager.hideSoftInputFromWindow(et_login_password!!.windowToken, 0)
                normalLogin()
            }
            false
        }

        btn_sign.setOnClickListener {
            if (!NetworkUtil.isConnected()) {
                ToastUtil.showToast("当前无网络")
                return@setOnClickListener
            }
            if (et_login_account.text.toString().isEmpty() || et_login_password.text.toString().isEmpty()) {
                ToastUtil.showToast("请输入完整信息")
                return@setOnClickListener
            }

            val myUser = MyUser().apply {
                this.username = et_login_account.text.toString()
                this.setPassword(et_login_password.text.toString())
            }
            compositeDisposable.add(UserRepository.signByUser(myUser, object : IBmobCallback<MyUser> {
                override fun success(result: MyUser?) {
                    ToastUtil.showToast("sign success,welcome:" + result?.username)
                    EventBus.getDefault().post(UserEvent(true, result))
                }

                override fun error(throwable: Throwable?) {
                    val exception = throwable as BmobException
                    ToastUtil.showToast("sign fail," + exception.toString())
                }
            }))
        }


        btn_login.setOnClickListener { normalLogin() }
        loadingDialog = LoadingDialog(this)
        loadingDialog!!.setLoadingText("请稍后")
                .setSuccessText("请求成功")
                .closeFailedAnim()
                .closeSuccessAnim()
                .setFailedText("请求失败")

        iv_login_weibo.setOnClickListener {
            if (!NetworkUtil.isConnected()) {
                ToastUtil.showToast("当前无网络")
                return@setOnClickListener
            }
            currentThirdPlatform = SinaWeibo.NAME
            UserRepository.loginByShareSdk(SinaWeibo.NAME, object : IBmobCallback<MyUser> {
                override fun success(result: MyUser?) {
                    updateUserInfo(result!!)
                }

                override fun error(throwable: Throwable?) {
                    loadingDialog?.loadFailed()
                }
            })
            loadingDialog?.show()
        }


        iv_login_qq.setOnClickListener(object : NoDoubleClickListener() {
            override fun onNoDoubleClick(v: View) {
                if (!NetworkUtil.isConnected()) {
                    ToastUtil.showToast("当前无网络")
                    return
                }
                currentThirdPlatform = QQ.NAME
                UserRepository.loginByShareSdk(QQ.NAME, object : IBmobCallback<MyUser> {
                    override fun success(result: MyUser?) {
                        updateUserInfo(result!!)
                    }

                    override fun error(throwable: Throwable?) {
                        loadingDialog?.loadFailed()
                    }
                })
                loadingDialog?.show()
            }

            override fun onDoubleClick() {

            }
        })
    }

    private fun normalLogin() {
        if (!NetworkUtil.isConnected()) {
            ToastUtil.showToast("当前无网络")
            return
        }
        if (et_login_account.text.toString().isEmpty() || et_login_password.text.toString().isEmpty()) {
            ToastUtil.showToast("请输入完整信息")
            return
        }
        val myUser = MyUser().apply {
            this.username = et_login_account.text.toString()
            this.setPassword(et_login_password.text.toString())
        }
        compositeDisposable.add(UserRepository.loginByUser(myUser, object : IBmobCallback<MyUser> {
            override fun success(result: MyUser?) {
                ToastUtil.showToast("login success,welcome:" + result?.username)
                EventBus.getDefault().post(UserEvent(true, result))
            }

            override fun error(throwable: Throwable?) {
                val exception = throwable as BmobException
                ToastUtil.showToast("login fail," + exception.toString())
            }
        }))
    }


    /**
     * 第三方登录成功之后，更改用户信息为第三方平台信息，并抛出通知
     *
     * @param myUser 用户信息为第三方平台信息
     */
    private fun updateUserInfo(myUser: MyUser) {
        Log.i(TAG, "third User: " + myUser.toString())
        compositeDisposable.add(UserRepository.updateUserByNewUser(myUser, object : IBmobCallback<MyUser> {
            override fun success(result: MyUser?) {
                loadingDialog?.loadSuccess()
                CommonPref.instance()?.putString(UserRepository.LAST_PLATFORM, currentThirdPlatform!!)
                EventBus.getDefault().post(UserEvent(true, result))
            }

            override fun error(throwable: Throwable?) {
                loadingDialog?.loadFailed()
            }
        }))
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    fun LoginCallback(userEvent: UserEvent) {
        if (userEvent.loginResult) {
            GlideApp.with(this).load(userEvent.myUser.avatar).placeholder(R.drawable.icon_default_avatar).into(iv_login_icon)
            finish()
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        loadingDialog?.loadFailed()
        compositeDisposable.clear()
    }

    override fun onDestroy() {
        super.onDestroy()
        loadingDialog?.close()
        compositeDisposable.clear()
    }
}
