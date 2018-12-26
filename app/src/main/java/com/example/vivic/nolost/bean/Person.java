package com.example.vivic.nolost.bean;

import cn.bmob.v3.BmobObject;

public class Person extends BmobObject {

    public String nickName;
    public String sex;
    public String school;
    public String portrait;


    @Override
    public String toString() {
        return " Person:" +
                " nickName:" + nickName +
                " sex:" + sex +
                " school:" + school +
                " portrait:" + portrait;
    }
}
