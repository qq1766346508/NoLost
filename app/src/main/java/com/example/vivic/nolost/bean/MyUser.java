package com.example.vivic.nolost.bean;

import cn.bmob.v3.BmobUser;

public class MyUser extends BmobUser {

    public static final String SEX_MAN = "man";
    public static final String SEX_WOMAN = "woman";

    public String thirdId;
    public String nickName;
    public String sex;
    public String school;
    public String avatar;
    public String contact;


    @Override
    public String toString() {
        return " MyUser:" +
                ", thirdId:" + thirdId +
                ", nickName:" + nickName +
                ", sex:" + sex +
                ", school:" + school +
                ", avatar:" + avatar +
                ", contact:" + contact;
    }
}
