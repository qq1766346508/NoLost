package com.example.vivic.nolost.Login;

import cn.bmob.v3.exception.BmobException;

public interface IBmobCallback<T> {
    void success(T result);

    void error(Throwable throwable);

}
