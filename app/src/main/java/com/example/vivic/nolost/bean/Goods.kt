package com.example.vivic.nolost.bean

import cn.bmob.v3.BmobObject

class Goods : BmobObject() {

    var name: String? = null
    var location: String? = null
    var detail: String? = null
    var photoList: List<String>? = null
    var type: String? = null
    var creatorObjectId: String? = null
    var creatorName: String? = null
    var creatorAvatar: String? = null


    companion object {
        const val TYPE_ALL = ""
        const val TYPE_LOST = "lost"
        const val TYPE_FOUND = "found"
    }

    override fun toString(): String {
        return "\n Goods:" +
                "type:$type" +
                ",name:$name" +
                ",location:$location" +
                ",detail:$detail" +
                ",photoList:$photoList" +
                "\ncreatorObjectId:${creatorObjectId}creatorName:$creatorName,creatorAvatar:$creatorAvatar \n"
    }
}
