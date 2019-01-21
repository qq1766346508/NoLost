package com.example.vivic.nolost.bmob

interface IBmobCallback<T> {
    fun success(result: T?)

    fun error(throwable: Throwable?)

}
