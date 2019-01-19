package com.example.vivic.nolost.Login;


import android.arch.lifecycle.ViewModelProviders;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.AppCompatEditText;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;

import com.example.vivic.nolost.GlideApp;
import com.example.vivic.nolost.R;
import com.example.vivic.nolost.activity.BaseActivity;
import com.example.vivic.nolost.bean.MyUser;
import com.example.vivic.nolost.commonUtil.BindEventBus;
import com.example.vivic.nolost.commonUtil.NetworkUtil;
import com.example.vivic.nolost.commonUtil.NoDoubleClickListener;
import com.example.vivic.nolost.commonUtil.pref.CommonPref;
import com.example.vivic.nolost.commonUtil.toastUtil.ToastUtil;
import com.xiasuhuei321.loadingdialog.view.LoadingDialog;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import cn.bmob.v3.exception.BmobException;
import cn.sharesdk.sina.weibo.SinaWeibo;

@BindEventBus
public class LoginActivity extends BaseActivity {
    private static final String TAG = "LoginActivity";

    private LoginViewModel loginViewModel;
    private ImageView ivIcon;
    private String userAccount;
    private String userPassword;
    private AppCompatEditText etAccount;
    private AppCompatEditText etPassword;
    private InputMethodManager inputMethodManager;
    private ILoginCallback<MyUser> iSdkLoginCallback;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        loginViewModel = ViewModelProviders.of(this).get(LoginViewModel.class);
        inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        initview();
        thirdLogin();
        initCallback();
    }


    private void initview() {
        Button btnLogin = findViewById(R.id.btn_login);
        Button btnSign = findViewById(R.id.btn_sign);
        etAccount = findViewById(R.id.et_user_account);
        etPassword = findViewById(R.id.et_password);
        etPassword.setOnEditorActionListener((v, actionId, event) -> {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                inputMethodManager.hideSoftInputFromWindow(etPassword.getWindowToken(), 0);
                normalLogin();
                return true;
            }
            return false;
        });


        btnSign.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (!NetworkUtil.isConnected()) {
                    ToastUtil.showToast("当前无网络");
                    return;
                }
                userAccount = etAccount.getText().toString();
                userPassword = etPassword.getText().toString();
                if (TextUtils.isEmpty(userAccount) || TextUtils.isEmpty(userPassword)) {
                    ToastUtil.showToast("账号或者密码不能为空");
                    return;
                }
                MyUser myUser = new MyUser();
                myUser.setUsername(userAccount);
                myUser.setPassword(userPassword);
                LoginRepository.INSTANCE.signByUser(myUser, new ILoginCallback<MyUser>() {
                    @Override
                    public void success(MyUser result) {
                        ToastUtil.showToast("sign success,welcome:" + result.getUsername());
                        EventBus.getDefault().post(new UpdateUserInfoEvent(true, result));
                    }

                    @Override
                    public void error(Throwable throwable) {
                        BmobException exception = (BmobException) throwable;
                        ToastUtil.showToast("sign fail," + exception.toString());
                    }
                });
            }

            @Override
            protected void onDoubleClick() {

            }
        });
        btnLogin.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                normalLogin();
            }

            @Override
            protected void onDoubleClick() {

            }
        });
        loadingDialog = new LoadingDialog(this);
        loadingDialog.setLoadingText("请稍后")
                .setSuccessText("请求成功")
                .closeFailedAnim()
                .closeSuccessAnim()
                .setFailedText("请求失败");
    }

    private void normalLogin() {
        if (!NetworkUtil.isConnected()) {
            ToastUtil.showToast("当前无网络");
            return;
        }
        userAccount = etAccount.getText().toString();
        userPassword = etPassword.getText().toString();
        if (TextUtils.isEmpty(userAccount) || TextUtils.isEmpty(userPassword)) {
            ToastUtil.showToast("账号或者密码不能为空");
            return;
        }
        MyUser myUser = new MyUser();
        myUser.setUsername(userAccount);
        myUser.setPassword(userPassword);
        LoginRepository.INSTANCE.loginByUser(myUser, new ILoginCallback<MyUser>() {
            @Override
            public void success(MyUser result) {
                ToastUtil.showToast("login success,welcome:" + result.getUsername());
                EventBus.getDefault().post(new UpdateUserInfoEvent(true, result));
            }

            @Override
            public void error(Throwable throwable) {
                BmobException exception = (BmobException) throwable;
                ToastUtil.showToast("login fail," + exception.toString());
            }
        });
    }

    private void thirdLogin() {
        ivIcon = findViewById(R.id.iv_login_icon);
        ImageView ivWeibo = findViewById(R.id.iv_login_weibo);
        ivWeibo.setOnClickListener(new NoDoubleClickListener() {
            @Override
            protected void onNoDoubleClick(View v) {
                if (!NetworkUtil.isConnected()) {
                    ToastUtil.showToast("当前无网络");
                    return;
                }
                LoginRepository.INSTANCE.loginByShareSdk(SinaWeibo.NAME, iSdkLoginCallback);
                loadingDialog.show();
            }

            @Override
            protected void onDoubleClick() {

            }

        });
    }

    private void updateUserInfo(MyUser myUser) {
        Log.i(TAG, "third User: " + myUser.toString());
        LoginRepository.INSTANCE.updateUserByNewUser(myUser, new ILoginCallback<MyUser>() {
            @Override
            public void success(MyUser result) {
                loadingDialog.loadSuccess();
                EventBus.getDefault().post(new UpdateUserInfoEvent(true, result));
            }

            @Override
            public void error(Throwable throwable) {
                loadingDialog.loadFailed();
            }
        });
    }

    private void initCallback() {
        iSdkLoginCallback = new ILoginCallback<MyUser>() {
            @Override
            public void success(MyUser myUser) {
                CommonPref.instance().putString(LoginRepository.INSTANCE.getLAST_PLATFORM(), SinaWeibo.NAME);
                updateUserInfo(myUser);
            }

            @Override
            public void error(Throwable throwable) {
                loadingDialog.loadFailed();
            }
        };
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void LoginCallback(UpdateUserInfoEvent updateUserInfoEvent) {
        if (updateUserInfoEvent.loginResult) {
            GlideApp.with(this).load(updateUserInfoEvent.myUser.avatar).placeholder(R.drawable.icon_default_avatar).into(ivIcon);
            finish();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        iSdkLoginCallback = null;
        loadingDialog.close();
    }
}
