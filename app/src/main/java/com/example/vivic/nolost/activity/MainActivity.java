package com.example.vivic.nolost.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vivic.nolost.Login.LoginActivity;
import com.example.vivic.nolost.Lost.activity.PublishActivity;
import com.example.vivic.nolost.Lost.fragment.LostFragment;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;
import com.example.vivic.nolost.search.SearchActivity;


public class MainActivity extends BaseActivity {

    private static final String TAG = "MainActivity";
    private static final int REQUEST_CODE_PUBLISH = 0;

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    private View headerView;

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
                        startActivity(new Intent(MainActivity.this, LoginActivity.class));
                        break;
                    case R.id.nav_weather:
                        ToastUtil.showToast("天气");
                        break;
                    case R.id.nav_setting:
                        ToastUtil.showToast("设置");
                        break;
                    default:
                        break;
                }
                return false;
            }
        });
        headerView = navigationView.getHeaderView(0);
        ImageView IvAvatar = headerView.findViewById(R.id.avatar);
        TextView tvNickname = headerView.findViewById(R.id.nickname);

        tvNickname.setText("xiaoming");

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
}
