package com.example.vivic.nolost.Login

interface IBmobCallback<T> {
    fun success(result: T?)

    fun error(throwable: Throwable?)

}
