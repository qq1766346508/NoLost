package com.example.vivic.nolost.commonUtil.pref

import android.content.Context
import android.content.SharedPreferences
import com.example.vivic.nolost.NoLostApplication


/**
 *
 * CommonPref是一个方便使用SharedPreferences的工具库
 *
 * Rule : input key cannot be null.
 */

private const val COMMONREF_NAME = "CommonPref"
private const val OVER_LENGTH_STRING_VALUE = 300

class CommonPref private constructor(preferences: SharedPreferences) : YSharedPref(preferences) {

    private var mMonitor: IPrefMonitor? = null

    fun setCommonPrefMonitor(monitor: IPrefMonitor) {
        mMonitor = monitor
    }

    override fun putString(key: String, value: String) {
        super.putString(key, value)

        if (mMonitor != null) {
            if (value.length > OVER_LENGTH_STRING_VALUE) {
                mMonitor!!.onPutOverLengthString(key, value, COMMONREF_NAME)
            }
        }
    }

    companion object {

        @Volatile
        private var sInst: CommonPref? = null

        @JvmStatic
        fun instance(): CommonPref? {
            if (sInst == null) {
                synchronized(CommonPref::class.java) {
                    if (sInst == null) {
                        sInst = CommonPref(
                                NoLostApplication.getContext()!!.getSharedPreferences(COMMONREF_NAME, Context.MODE_PRIVATE))
                    }
                }
            }

            return sInst
        }
    }
}