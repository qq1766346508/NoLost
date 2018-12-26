package com.example.vivic.nolost.commonUtil.pref

import android.content.SharedPreferences
import android.text.TextUtils
import android.util.Log
import java.util.*


private const val TAG = "YSharedPref"
private const val DELIMITER = ","
abstract class YSharedPref(protected val mPref: SharedPreferences) {

    val all: Map<String, *>
        get() = mPref.all

    open fun putString(key: String, value: String) {
        put(key, value)
    }

    fun getString(key: String): String? {
        return get(key)
    }

    fun getString(key: String, defaultValue: String): String? {
        return mPref.getString(key, defaultValue)
    }

    fun putInt(key: String, value: Int) {
        put(key, value.toString())
    }

    fun putBoolean(key: String, value: Boolean) {
        put(key, value.toString())
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        val rawValue = get(key)
        if (TextUtils.isEmpty(rawValue)) {
            return defaultValue
        }
        try {
            return java.lang.Boolean.parseBoolean(rawValue)
        } catch (e: Exception) {
            Log.e(TAG, "failed to parse boolean value for key %s, %s")
            return defaultValue
        }

    }

    @JvmOverloads
    fun getInt(key: String, defaultValue: Int = -1): Int {
        val rawValue = get(key)
        return rawValue.run {
            if (this.isNullOrEmpty()) defaultValue else parseInt(this!!, defaultValue)
        }
    }

    private fun parseInt(value: String, defaultValue: Int): Int {
        try {
            return Integer.parseInt(value)
        } catch (e: NumberFormatException) {
            Log.e(TAG, "lcy failed to parse value for key %s, %s")
            return defaultValue
        }

    }

    fun putLong(key: String, value: Long) {
        put(key, value.toString())
    }

    @JvmOverloads
    fun getLong(key: String, defaultValue: Long = -1L): Long {
        val rawValue = get(key)
        if (rawValue.isNullOrEmpty()) {
            return defaultValue
        } else {
            try {
                return rawValue!!.toLong()
            } catch (e: NumberFormatException) {
                Log.e(TAG,
                        "lcy failed to parse %s as long, for key %s")
                return defaultValue
            }
        }
    }

    fun putIntArray(key: String, values: Array<Int>) {
        putIntList(key, Arrays.asList(*values))
    }

    /**
     * @param key
     * @param outValues For memory reuse, if the result is no greater than this space,
     * will fill into this, the redundant elements won't be touched.
     * If it is null, a new int array will be created if result is
     * not empty.
     * @return The result list, null if no correlated.
     */
    @JvmOverloads
    fun getIntArray(key: String, outValues: IntArray? = null): IntArray? {
        val list = getIntList(key)
        if (list == null || list.isEmpty()) {
            return null
        }

        val ret = if (outValues != null && list.size <= outValues.size)
            outValues
        else
            IntArray(list.size)

        var i = 0
        for (e in list) {
            ret[i++] = e
        }
        return ret
    }

    fun putIntList(key: String, values: List<Int>?) {
        if (values == null || values.size == 0) {
            return
        }

        val value = TextUtils.join(DELIMITER, values)
        put(key, value)
    }

    fun getIntList(key: String): List<Int>? {
        val `val` = get(key)
        if (TextUtils.isEmpty(`val`)) {
            return null
        }

        val values = TextUtils.split(`val`, DELIMITER)
        if (values == null || values.size == 0) {
            return null
        }

        val list = ArrayList<Int>()
        for (e in values) {
            try {
                list.add(Integer.parseInt(e))
            } catch (ex: NumberFormatException) {
                Log.e(TAG, "lcy failed to parse value for key: %s, value: %s")
            }

        }
        return list
    }

    fun put(key: String, value: String) {
        mPref.edit().putString(key, value).apply()
    }

    operator fun get(key: String): String? {
        return mPref.getString(key, null)
    }

    fun remove(key: String) {
        mPref.edit().remove(key).apply()
    }

    fun clear() {
        mPref.edit().clear().apply()
    }

    fun contain(key: String?): Boolean {
        return if (key == null || key.length == 0) {
            false
        } else mPref.contains(key)
    }

//    fun putObject(key: String, obj: Any) {
//        // TODO: 2018/6/13 liaodongming
//        // Gson gson = new Gson();
//        // String json = gson.toJson(obj);
//        // put(key, json);
//    }
//
//    fun getObj(key: String, className: Class<*>): Any? {
//        // TODO: 2018/6/13 liaodongming
//        // Gson gson = new Gson();
//        // String json = getString(key, "");
//        // Object obj = gson.fromJson(json, className);
//        // return obj;
//        return null
//    }
}