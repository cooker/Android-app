package com.github.g9527.application.core.api;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.github.g9527.application.core.utils.GsonUtils;
import com.github.g9527.application.core.utils.OkHttpUtils;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class Api {
    public static final String TAG = Api.class.getSimpleName();

    private String requestUrl;
    private Headers headers;
    private Object body;
    private static final OkHttpUtils okHttpUtils;

    static {
        okHttpUtils = OkHttpUtils.getInstance();
    }


    public static Api config(String url,
                             Map<String, String> params,
                             Map<String, String> headers,
                             Object body) {
        Api api = new Api();
        Uri.Builder builder = Uri.parse(url).buildUpon();
        if (params != null) {
            for (Map.Entry<String, String> it : params.entrySet()) {
                builder.appendQueryParameter(it.getKey(), it.getValue());
            }
        }
        api.requestUrl = builder.toString();
        api.headers = Headers.of(headers == null ? Collections.EMPTY_MAP : headers);
        api.body = body;
        return api;
    }

    public void post(ApiCallback apiCallback) {
        Request.Builder builder = new Request.Builder()
                .url(requestUrl)
                .headers(headers)
                .addHeader("contentType", "application/json;charset=UTF-8");
        if (body != null) {
            RequestBody requestBody = RequestBody.create(MediaType.parse("application/json;charset=utf-8"), GsonUtils.toJson(body));
            builder.post(requestBody);
        }

        Call call = okHttpUtils.newCall(builder.build());
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                Log.e(TAG, e.getMessage());
                apiCallback.onFailure(e);
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                apiCallback.onSuccess(response);
            }
        });
    }
}
