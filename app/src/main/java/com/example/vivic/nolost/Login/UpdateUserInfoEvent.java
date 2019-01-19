package com.example.vivic.nolost.Login;

import com.example.vivic.nolost.bean.MyUser;

public class UpdateUserInfoEvent {
    public boolean loginResult;
    public MyUser myUser;

    public UpdateUserInfoEvent(boolean loginResult, MyUser myUser) {
        this.loginResult = loginResult;
        this.myUser = myUser;
    }
}
