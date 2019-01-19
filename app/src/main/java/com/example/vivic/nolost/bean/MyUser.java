package com.example.vivic.nolost.bean;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {


    public String gender;
    public String location;
    public String avatar;
    public String contact;
    public String background;

    @Override
    public String toString() {
        return " MyUser:" +
                ", Username:" + getUsername() +
                ", gender:" + gender +
                ", location:" + location +
                ", avatar:" + avatar +
                ", background:" + background +
                ", contact:" + contact;
    }


}
