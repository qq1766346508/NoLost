package com.example.vivic.nolost.Login;

public class LoginEvent {
    public String platform;
    public boolean loginResult;

    public LoginEvent(String platform, boolean loginResult) {
        this.platform = platform;
        this.loginResult = loginResult;
    }
}
