package com.example.vivic.nolost.bean;

import org.jetbrains.annotations.NotNull;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {

    public static final String SEX_MAN = "man";
    public static final String SEX_WOMAN = "woman";

    public String sex;
    public String school;
    public String avatar;
    public String contact;


    @Override
    public String toString() {
        return " MyUser:" +
                ", Username:" + getUsername() +
                ", sex:" + sex +
                ", school:" + school +
                ", avatar:" + avatar +
                ", contact:" + contact;
    }


}
