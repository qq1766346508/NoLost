package com.example.vivic.nolost.Login;

import com.example.vivic.nolost.bean.MyUser;

public class LoginEvent {
    public boolean loginResult;
    public MyUser myUser;

    public LoginEvent(boolean loginResult, MyUser myUser) {
        this.loginResult = loginResult;
        this.myUser = myUser;
    }
}
