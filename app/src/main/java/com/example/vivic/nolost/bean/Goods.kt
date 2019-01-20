package com.example.vivic.nolost.bean

import cn.bmob.v3.BmobObject

class Goods : BmobObject() {

    var name: String? = null
    var location: String? = null
    var detail: String? = null
    var photoList: List<String>? = null
    var createor: MyUser? = null
    var type: String? = null

    companion object {
        const val TYPE_LOST = "lost"
        const val TYPE_FOUND = "found"
    }

    override fun toString(): String {
        return "Goods:" +
                "type:$type" +
                ",name:$name" +
                ",location:$location" +
                ",detail:$detail" +
                ",photoList:$photoList" +
                "\n createor:${createor.toString()}"
    }
}
