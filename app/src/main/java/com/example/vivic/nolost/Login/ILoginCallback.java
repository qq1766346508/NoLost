package com.example.vivic.nolost.Login;

import cn.bmob.v3.exception.BmobException;

public interface ILoginCallback<T> {
    void success(T result);

    void error(BmobException exception);

}
