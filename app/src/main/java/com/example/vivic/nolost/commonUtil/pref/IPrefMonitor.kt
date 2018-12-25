package com.example.vivic.nolost.commonUtil.pref

interface IPrefMonitor {
    fun onPutOverLengthString(key: String, value: String, pref: String)
}