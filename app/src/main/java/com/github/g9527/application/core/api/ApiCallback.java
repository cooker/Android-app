package com.github.g9527.application.core.api;

import okhttp3.Response;

public interface ApiCallback {
    void onSuccess(Response response);

    void onFailure(Exception e);
}
