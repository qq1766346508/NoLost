package com.example.vivic.nolost.bean

import cn.bmob.v3.BmobUser

class MyUser : BmobUser() {

    var platform: String? = null
    var gender: String? = null
    var location: String? = null
    var avatar: String? = null
    var contact: String? = null
    var background: String? = null

    override fun toString(): String {
        return " MyUser:" +
                ", platform:" + platform +
                ", Username:" + username +
                ", gender:" + gender +
                ", location:" + location +
                ", avatar:" + avatar +
                ", background:" + background +
                ", contact:" + contact
    }

}
