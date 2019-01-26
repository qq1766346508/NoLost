package com.example.vivic.nolost.bmob

import android.util.Log
import cn.bmob.v3.BmobObject
import cn.bmob.v3.BmobQuery
import cn.bmob.v3.exception.BmobException
import cn.bmob.v3.listener.FindListener
import cn.bmob.v3.listener.QueryListener
import cn.bmob.v3.listener.SaveListener
import com.example.vivic.nolost.bean.Goods
import com.example.vivic.nolost.bean.MyUser
import io.reactivex.disposables.Disposable
import org.json.JSONArray

object DataRepository {

    val TAG = DataRepository::class.java.simpleName

    /**
     * 增加数据
     */
    fun saveData(bmobObject: BmobObject, iBmobCallback: IBmobCallback<String>): Disposable {
        return bmobObject.save(object : SaveListener<String>() {
            override fun done(objectId: String?, bmobException: BmobException?) {
                if (bmobException == null) {
                    iBmobCallback.success(objectId)
                    Log.i(TAG, "saveData success,objectId = $objectId")
                } else {
                    iBmobCallback.error(bmobException)
                    Log.i(TAG, "saveData failed,BmobException = $bmobException")
                }
            }
        })
    }

    fun <T> queryData(bmobQuery: BmobQuery<T>, findListener: FindListener<T>): Disposable {
        return bmobQuery.findObjects(findListener)
    }

    /**
     * 根据表明查询
     */
    fun queryDataByTable(bmobQuery: BmobQuery<*>, iBmobCallback: IBmobCallback<JSONArray>) {
        bmobQuery.findObjectsByTable(object : QueryListener<JSONArray>() {
            override fun done(jsonArray: JSONArray, bmobException: BmobException?) {
                if (bmobException == null) {
                    Log.d(TAG, "queryDataByTable done: $jsonArray")
                    iBmobCallback.success(jsonArray)
                } else {
                    Log.d(TAG, "queryDataByTable failed: $bmobException")
                    iBmobCallback.error(bmobException)
                }
            }
        })
    }

    /**
     * 根据objectId查询
     */
    fun <T> queryByObjdectId(bmobQuery: BmobQuery<T>){
        bmobQuery.findObjects(object :FindListener<T>(){
            override fun done(p0: MutableList<T>?, p1: BmobException?) {

            }

        })
    }
}