package com.example.vivic.nolost.bmob

import android.util.Log
import cn.bmob.v3.BmobObject
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.SaveListener
import io.reactivex.disposables.Disposable

object DataRepository {

    val TAG = DataRepository::class.java.simpleName

    /**
     * 增加数据
     */
    fun saveData(bmobObject: BmobObject, iBmobCallback: IBmobCallback<String>): Disposable {
        return bmobObject.save(object : SaveListener<String>() {
            override fun done(p0: String?, p1: BmobException?) {
                if (p1 == null) {
                    iBmobCallback.success(p0)
                    Log.i(TAG, "saveData success,objectId = $p0")
                } else {
                    iBmobCallback.error(p1)
                    Log.i(TAG, "saveData failed,BmobException = $p1")
                }
            }
        })
    }

//    fun queryData
}