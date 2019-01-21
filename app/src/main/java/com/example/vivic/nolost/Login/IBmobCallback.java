package com.example.vivic.nolost.Login;

public interface IBmobCallback<T> {
    void success(T result);

    void error(Throwable throwable);

}
