package com.example.vivic.nolost.login;

import com.example.vivic.nolost.bean.MyUser;

public class UserEvent {
    public boolean loginResult;
    public MyUser myUser;

    public UserEvent(boolean loginResult, MyUser myUser) {
        this.loginResult = loginResult;
        this.myUser = myUser;
    }
}
