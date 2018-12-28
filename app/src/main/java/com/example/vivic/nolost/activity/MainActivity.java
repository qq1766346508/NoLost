package com.example.vivic.nolost.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.FrameLayout;

import com.example.vivic.nolost.Lost.fragment.LostFragment;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;


public class MainActivity extends BaseActivity {

    private DrawerLayout drawerLayout;
    private NavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        toolbar.setOnMenuItemClickListener(new Toolbar.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.menu_search:
                        ToastUtil.showToast("search");
                        break;
                    case R.id.menu_add:
                        ToastUtil.showToast("add");
                    default:
                        break;
                }
                return true;
            }
        });
        drawerLayout = findViewById(R.id.drawer);

        FrameLayout container = findViewById(R.id.container);
        getSupportFragmentManager().beginTransaction().replace(R.id.container, LostFragment.newInstance(null)).commitAllowingStateLoss();

        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.nav_user:
                        ToastUtil.showToast("用户中心");
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
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
    }
}
