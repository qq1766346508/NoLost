package com.example.vivic.nolost.bmob

import android.util.Log
import cn.bmob.v3.BmobObject
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import io.reactivex.disposables.Disposable
import org.json.JSONArray

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

    fun <T> queryData(bmobQuery: BmobQuery<T>, findListener: FindListener<T>): Disposable {
        return bmobQuery.findObjects(findListener)
    }

    fun queryDataByTable(bmobQuery: BmobQuery<*>, iBmobCallback: IBmobCallback<JSONArray>) {
        bmobQuery.findObjectsByTable(object : QueryListener<JSONArray>() {
            override fun done(jsonArray: JSONArray, e: BmobException?) {
                if (e == null) {
                    Log.d(TAG, "queryDataByTable done: ${jsonArray.toString()}")
                    iBmobCallback.success(jsonArray)
                } else {
                    Log.d(TAG, "queryDataByTable failed: $e")
                    iBmobCallback.error(e)
                }
            }
        })
    }
}