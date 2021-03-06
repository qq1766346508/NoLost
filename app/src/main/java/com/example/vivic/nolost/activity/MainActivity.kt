package com.example.vivic.nolost.activity

import android.content.Intent
import android.graphics.drawable.Drawable
import android.os.Bundle
import android.support.constraint.ConstraintLayout
import android.support.v4.view.GravityCompat
import android.util.Log
import android.view.Gravity
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import cn.bmob.newim.BmobIM
import cn.bmob.newim.core.ConnectionStatus
import cn.bmob.newim.listener.ConnectStatusChangeListener
import cn.bmob.v3.BmobUser
import com.bumptech.glide.request.target.SimpleTarget
import com.bumptech.glide.request.transition.Transition
import com.example.vivic.nolost.GlideApp
import com.example.vivic.nolost.R
import com.example.vivic.nolost.bean.MyUser
import com.example.vivic.nolost.bmob.ChatRepository
import com.example.vivic.nolost.bmob.IBmobCallback
import com.example.vivic.nolost.bmob.UserRepository
import com.example.vivic.nolost.chat.FriendActivity
import com.example.vivic.nolost.commonUtil.BindEventBus
import com.example.vivic.nolost.commonUtil.DataCleanManager
import com.example.vivic.nolost.commonUtil.NetworkUtil
import com.example.vivic.nolost.commonUtil.confirmDialog.ConfirmDialog
import com.example.vivic.nolost.commonUtil.dimensUtil.DimensUtils
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil
import com.example.vivic.nolost.login.LogOutEvent
import com.example.vivic.nolost.login.LoginActivity
import com.example.vivic.nolost.login.UserEvent
import com.example.vivic.nolost.lost.activity.HistoryActivity
import com.example.vivic.nolost.lost.activity.PublishActivity
import com.example.vivic.nolost.lost.fragment.LoadMode
import com.example.vivic.nolost.lost.fragment.LostFragment
import com.example.vivic.nolost.search.SearchActivity
import com.example.vivic.nolost.userCenter.GenderHelper
import com.example.vivic.nolost.userCenter.UserCenterActivity
import kotlinx.android.synthetic.main.activity_main.*
import org.greenrobot.eventbus.EventBus
import org.greenrobot.eventbus.Subscribe
import org.greenrobot.eventbus.ThreadMode

@BindEventBus
class MainActivity : BaseActivity() {
    companion object {

        private val TAG = MainActivity::class.java.simpleName
    }

    //DrawerView的子控件实例无法直接通过id获取
    private var headerView: View? = null
    private var ivAvatar: ImageView? = null
    private var ivGender: ImageView? = null
    private var tvNickname: TextView? = null
    private var itemLogout: MenuItem? = null
    private var clBackground: ConstraintLayout? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initView()
        setSwipeBackEnable(false) //禁止左滑退出
    }

    private fun initView() {
        setSupportActionBar(tl_main_toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)
        tl_main_toolbar.setOnMenuItemClickListener { item ->
            when (item.itemId) {
                R.id.menu_friend -> {
                    if (BmobUser.getCurrentUser(MyUser::class.java) == null) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this@MainActivity, FriendActivity::class.java))
                    }
                }
                R.id.menu_search -> startActivity(Intent(this@MainActivity, SearchActivity::class.java))
                R.id.menu_add -> if (BmobUser.getCurrentUser(MyUser::class.java) != null) {
                    startActivity(Intent(this@MainActivity, PublishActivity::class.java))
                } else {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                }
                else -> {
                }
            }

            true
        }
        supportFragmentManager.beginTransaction().replace(R.id.fl_main_container, LostFragment.newInstance(LoadMode.LOAD_MODE_NORMAL)).commitAllowingStateLoss()
        initNavigationView()
    }

    private fun initNavigationView() {
        nav_main_view.setNavigationItemSelectedListener { item ->
            when (item.itemId) {
                R.id.nav_user -> if (BmobUser.getCurrentUser(MyUser::class.java) == null) {
                    startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                } else {
                    UserCenterActivity.goToActivity(this@MainActivity, BmobUser.getCurrentUser(MyUser::class.java).objectId)
                }
                R.id.nav_contact -> {
                    if (BmobUser.getCurrentUser(MyUser::class.java) == null) {
                        startActivity(Intent(this@MainActivity, LoginActivity::class.java))
                    } else {
                        startActivity(Intent(this@MainActivity, FriendActivity::class.java))
                    }
                }
                R.id.nav_setting -> {
                    Log.d(TAG, "cleanInternalCache:${DataCleanManager.getCacheSize(cacheDir)},cleanExternalCache:${DataCleanManager.getCacheSize(externalCacheDir)}}")
                    ConfirmDialog.Builder()
                            .content("确认清理缓存(${DataCleanManager.getFormatSize(DataCleanManager.getFolderSize(cacheDir) + DataCleanManager.getFolderSize(externalCacheDir))})")
                            .confirmText("确定")
                            .confirmListener(object : ConfirmDialog.Builder.ConfirmListener() {
                                override fun onConfirm() {
                                    DataCleanManager.cleanInternalCache(this@MainActivity)
                                    DataCleanManager.cleanExternalCache(this@MainActivity)
                                }
                            })
                            .build()
                            .show(this@MainActivity)
                }
                R.id.nav_logout -> ConfirmDialog.Builder()
                        .content("确定注销？")
                        .confirmListener(object : ConfirmDialog.Builder.ConfirmListener() {
                            override fun onConfirm() {
                                UserRepository.logOut()
                                EventBus.getDefault().post(LogOutEvent())
                                ToastUtil.showToast("已退出")
                            }
                        }).build().show(this@MainActivity)
                else -> {
                }
            }
            false
        }
        headerView = nav_main_view.getHeaderView(0)
        ivAvatar = headerView?.findViewById(R.id.iv_nav_avatar)
        ivGender = headerView?.findViewById(R.id.iv_nav_gender)
        ivAvatar?.setOnClickListener { it ->
            if (BmobUser.getCurrentUser(MyUser::class.java) == null) {
                startActivity(Intent(this@MainActivity, LoginActivity::class.java))
            } else {
                HistoryActivity.getActivity(this@MainActivity, BmobUser.getCurrentUser(MyUser::class.java).objectId)
            }
        }
        tvNickname = headerView?.findViewById(R.id.tv_nav_nickname)
        clBackground = headerView?.findViewById(R.id.cl_background)
        val menu = nav_main_view.menu
        itemLogout = menu.findItem(R.id.nav_logout)
        //进入主页面初始化抽屉
        val currentUser = BmobUser.getCurrentUser(MyUser::class.java)
        if (currentUser != null) {
            Log.i(TAG, "currentUser: $currentUser")
            updateNavigation(UserEvent(true, currentUser))
        } else {
            Log.i(TAG, "currentUser == null ")
            updateNavigation(UserEvent(false, null))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun loginCallback(userEvent: UserEvent) {
        updateNavigation(userEvent)
    }

    private fun updateNavigation(userEvent: UserEvent) {
        if (userEvent.loginResult) {
            GlideApp.with(this).load(if (userEvent.myUser.avatar == null) R.drawable.icon_default_avatar else userEvent.myUser.avatar)
                    .circleCrop().placeholder(R.drawable.icon_default_avatar).into(ivAvatar!!)
            tvNickname?.text = userEvent.myUser.username
            itemLogout?.isVisible = true
            ivGender?.visibility = View.VISIBLE
            userEvent.myUser.gender?.let {
                when {
                    GenderHelper.formatGender(it).equals(GenderHelper.MAN, ignoreCase = true) -> ivGender?.setImageResource(R.drawable.icon_boy)
                    GenderHelper.formatGender(it).equals(GenderHelper.FEMALE, ignoreCase = true) -> ivGender?.setImageResource(R.drawable.icon_girl)
                    else -> ivGender?.setImageResource(R.drawable.icon_gender_secret)
                }
            }
            GlideApp.with(this).asDrawable().override(DimensUtils.dip2pixel(250f), DimensUtils.dip2pixel(180f)).centerCrop().load(userEvent.myUser.background).into(object : SimpleTarget<Drawable>() {
                override fun onResourceReady(resource: Drawable, transition: Transition<in Drawable>?) {
                    clBackground?.background = resource
                }
            })
            connectIM(userEvent.myUser)
        } else {
            GlideApp.with(this).load(R.drawable.icon_default_avatar).circleCrop().into(ivAvatar!!)
            tvNickname?.text = ""
            itemLogout?.isVisible = false
            ivGender?.visibility = View.INVISIBLE
            clBackground?.setBackgroundColor(resources.getColor(R.color.standard_color))
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    fun logout(logOutEvent: LogOutEvent) {
        updateNavigation(UserEvent(false, null))
        ChatRepository.disConnect()
    }

    private fun connectIM(myUser: MyUser) {
        Log.d(TAG, "currentStatus::${BmobIM.getInstance().currentStatus.msg}")
        if (BmobIM.getInstance().currentStatus != (ConnectionStatus.CONNECTED)) {
            //更新单一本地用户信息
            ChatRepository.connect(myUser.objectId, object : IBmobCallback<String> {
                override fun success(result: String?) {
                    ChatRepository.updateUserInfo(myUser.objectId, myUser.username, myUser.avatar)
                }

                override fun error(throwable: Throwable?) {
                    ToastUtil.showToast(throwable.toString())
                }

            })
        } else if (BmobIM.getInstance().currentStatus == ConnectionStatus.CONNECTED) {
            ChatRepository.updateUserInfo(myUser.objectId, myUser.username, myUser.avatar)
        }
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            dl_main_drawer.openDrawer(Gravity.START)
        }
        return true
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.toolbar_menu, menu)
        return true
    }


    override fun onResume() {
        super.onResume()
        BmobIM.getInstance().setOnConnectStatusChangeListener(object : ConnectStatusChangeListener() {
            override fun onChange(status: ConnectionStatus) {
                Log.d(TAG, "ConnectionStatus.msg : ${status.msg}")
                if (BmobUser.getCurrentUser(MyUser::class.java) != null && status == ConnectionStatus.DISCONNECT) {
                    if (!NetworkUtil.isConnected()) {
                        ToastUtil.showToast("当前无网络")
                        return
                    }
                    ChatRepository.connect(BmobUser.getCurrentUser(MyUser::class.java).objectId, null) //重连
                }
            }
        })
    }

    override fun onBackPressed() { //处理返回键先关闭抽屉
        if (dl_main_drawer.isDrawerOpen(GravityCompat.START)) {
            dl_main_drawer.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun finish() {
        super.finish()
        overridePendingTransition(0, 0)
    }

    override fun onDestroy() {
        super.onDestroy()
        ChatRepository.disConnect()
    }
}
