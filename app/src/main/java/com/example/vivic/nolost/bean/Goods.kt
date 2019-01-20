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
        val TYPE_LOST = "type_lost"
        val TYPE_FOUND = "type_found"
    }

    override fun toString(): String {
        return "Goods:name:$name" +
                ",location:$location" +
                ",detail:$detail" +
                ",photoList:$photoList" +
                ",createor:${createor.toString()}" +
                ",type:$type"
    }
}
