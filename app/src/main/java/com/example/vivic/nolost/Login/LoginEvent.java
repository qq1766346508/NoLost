package com.example.vivic.nolost.Login;

import com.example.vivic.nolost.bean.MyUser;

public class LoginEvent {
    public String platform;
    public boolean loginResult;
    public MyUser myUser;

    public LoginEvent(String platform, boolean loginResult, MyUser myUser) {
        this.platform = platform;
        this.loginResult = loginResult;
        this.myUser = myUser;
    }
}
