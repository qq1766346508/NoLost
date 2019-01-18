package com.example.vivic.nolost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.vivic.nolost.GlideApp;
import com.example.vivic.nolost.Login.LogOutEvent;
import com.example.vivic.nolost.Login.LoginActivity;
import com.example.vivic.nolost.Login.LoginEvent;
import com.example.vivic.nolost.Login.LoginRepository;
import com.example.vivic.nolost.Lost.activity.PublishActivity;
import com.example.vivic.nolost.Lost.fragment.LostFragment;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.MyUser;
import com.example.vivic.nolost.commonUtil.BindEventBus;
import com.example.vivic.nolost.commonUtil.confirmDialog.ConfirmDialog;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;
import com.example.vivic.nolost.search.SearchActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bmob.v3.BmobUser;

@BindEventBus
public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PUBLISH = 0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private View headerView;
    private ImageView IvAvatar;
    private TextView tvNickname;
    private MenuItem itemLogout;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_search:
                        startActivity(new Intent(MainActivity.this, SearchActivity.class));
                        break;
                    case R.id.menu_add:
                        startActivityForResult(new Intent(MainActivity.this, PublishActivity.class), REQUEST_CODE_PUBLISH);
                    default:
                        break;
                }
                return true;
            }
        });
        drawerLayout = findViewById(R.id.drawer);

        FrameLayout container = findViewById(R.id.container);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, LostFragment.newInstance(null)).commitAllowingStateLoss();

        navigationViewInit();

    }

    private void navigationViewInit() {
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_user:
                        if (BmobUser.getCurrentUser(MyUser.class) == null) {
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        } else {
                            ToastUtil.showToast("用户中心");
                        }
                        break;
                    case R.id.nav_weather:
                        ToastUtil.showToast("天气");
                        break;
                    case R.id.nav_setting:
                        ToastUtil.showToast("设置");
                        break;
                    case R.id.nav_logout:
                        new ConfirmDialog.Builder().content("确定注销？").confirmListener(new ConfirmDialog.Builder.ConfirmListener() {
                            @Override
                            public void onConfirm() {
                                LoginRepository.INSTANCE.logOut();
                                EventBus.getDefault().post(new LogOutEvent());
                                ToastUtil.showToast("已退出");
                            }
                        }).build().show(MainActivity.this);
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        headerView = navigationView.getHeaderView(0);
        IvAvatar = headerView.findViewById(R.id.avatar);
        tvNickname = headerView.findViewById(R.id.nickname);
        Menu menu = navigationView.getMenu();
        itemLogout = menu.findItem(R.id.nav_logout);

        MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
        if (currentUser != null) {
            Log.i(TAG, "currentUser: " + currentUser.toString());
            itemLogout.setVisible(true);
            GlideApp.with(this).load(currentUser.avatar).placeholder(R.drawable.icon_default_avatar).into(IvAvatar);
            tvNickname.setText(currentUser.getUsername());
        } else {
            Log.i(TAG, "currentUser == null ");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginCallback(LoginEvent loginEvent) {
        if (loginEvent.loginResult) {
            GlideApp.with(this).load(loginEvent.myUser.avatar).placeholder(R.drawable.icon_default_avatar).into(IvAvatar);
            tvNickname.setText(loginEvent.myUser.getUsername());
            itemLogout.setVisible(true);
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Logout(LogOutEvent logOutEvent) {
        Glide.with(this).load(R.drawable.icon_default_avatar).into(IvAvatar);
        tvNickname.setText("");
        itemLogout.setVisible(false);
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            drawerLayout.openDrawer(Gravity.START);
        }
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return true;
    }

    @Override
    public void onBackPressed() { //处理返回键先关闭抽屉
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }
}
