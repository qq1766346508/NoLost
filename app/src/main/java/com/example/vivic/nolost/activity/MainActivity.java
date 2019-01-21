package com.example.vivic.nolost.activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
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
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.example.vivic.nolost.GlideApp;
import com.example.vivic.nolost.Login.LogOutEvent;
import com.example.vivic.nolost.Login.LoginActivity;
import com.example.vivic.nolost.Login.UserEvent;
import com.example.vivic.nolost.Lost.activity.PublishActivity;
import com.example.vivic.nolost.Lost.fragment.LostFragment;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.bean.MyUser;
import com.example.vivic.nolost.commonUtil.BindEventBus;
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener;
import com.example.vivic.nolost.commonUtil.confirmDialog.ConfirmDialog;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;
import com.example.vivic.nolost.search.SearchActivity;
import com.example.vivic.nolost.userCenter.GenderHelper;
import com.example.vivic.nolost.userCenter.UserCenterActivity;
import com.example.vivic.nolost.userCenter.UserRepository;

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
    private ImageView ivGender;
    private TextView tvNickname;
    private MenuItem itemLogout;
    private ConstraintLayout clBackground;


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
        toolbar.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.menu_search:
                    startActivity(new Intent(MainActivity.this, SearchActivity.class));
                    break;
                case R.id.menu_add:
                    if (BmobUser.getCurrentUser(MyUser.class) != null) {
                        startActivity(new Intent(MainActivity.this, PublishActivity.class));
                    } else {
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    }
                default:
                    break;
            }
            return true;
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
                            startActivity(new Intent(MainActivity.this, UserCenterActivity.class));
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
                                UserRepository.INSTANCE.logOut();
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
        IvAvatar = headerView.findViewById(R.id.iv_nav_avatar);
        ivGender = headerView.findViewById(R.id.iv_nav_gender);
        IvAvatar.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (BmobUser.getCurrentUser(MyUser.class) == null) {
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                }
            }

            @Override
            protected void onDoubleClick() {

            }
        });
        tvNickname = headerView.findViewById(R.id.tv_nav_nickname);
        clBackground = headerView.findViewById(R.id.cl_background);
        Menu menu = navigationView.getMenu();
        itemLogout = menu.findItem(R.id.nav_logout);

        MyUser currentUser = BmobUser.getCurrentUser(MyUser.class);
        if (currentUser != null) {
            Log.i(TAG, "currentUser: " + currentUser.toString());
            LoginCallback(new UserEvent(true, currentUser));
        } else {
            Log.i(TAG, "currentUser == null ");
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginCallback(UserEvent userEvent) {
        if (userEvent.loginResult) {
            GlideApp.with(this).load(userEvent.myUser.getAvatar()).placeholder(R.drawable.icon_default_avatar).into(IvAvatar);
            tvNickname.setText(userEvent.myUser.getUsername());
            itemLogout.setVisible(true);
            ivGender.setVisibility(View.VISIBLE);
            if (userEvent.myUser.getGender() != null) {
                if (GenderHelper.INSTANCE.formatGender(userEvent.myUser.getGender()).equalsIgnoreCase(GenderHelper.MAN)) {
                    ivGender.setImageResource(R.drawable.icon_boy);
                } else if (GenderHelper.INSTANCE.formatGender(userEvent.myUser.getGender()).equalsIgnoreCase(GenderHelper.FEMALE)) {
                    ivGender.setImageResource(R.drawable.icon_girl);
                } else {
                    ivGender.setImageResource(R.drawable.icon_gender_secret);
                }
            }
            GlideApp.with(this).asDrawable().load(userEvent.myUser.getBackground()).centerCrop().into(new SimpleTarget<Drawable>() {
                @Override
                public void onResourceReady(@NonNull Drawable resource, @Nullable Transition<? super Drawable> transition) {
                    clBackground.setBackground(resource);
                }
            });
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void Logout(LogOutEvent logOutEvent) {
        Glide.with(this).load(R.drawable.icon_default_avatar).into(IvAvatar);
        tvNickname.setText("");
        itemLogout.setVisible(false);
        ivGender.setVisibility(View.INVISIBLE);
        clBackground.setBackgroundColor(getResources().getColor(R.color.standard_color));
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

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(this).clearMemory();
    }
}
