package com.github.g9527.application.core.utils;

import android.util.Log;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Interceptor;
import okhttp3.MediaType;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.ResponseBody;
import okio.Buffer;

public class OkHttpUtils {
    public static final String TAG = OkHttpUtils.class.getSimpleName();

    static OkHttpUtils okHttpUtil;
    private OkHttpClient.Builder builder;
    private OkHttpClient okHttpClient;

    private OkHttpUtils() {
        builder = new OkHttpClient.Builder();
        okHttpClient = builder.addInterceptor(new RequestLoggerInterceptor())
                .addInterceptor(new ResponseLoggerInterceptor())
                .build();
    }

    public static OkHttpUtils getInstance() {
        if (null == okHttpUtil) {
            synchronized (OkHttpUtils.class) {
                if (null == okHttpUtil) {
                    okHttpUtil = new OkHttpUtils();
                }
            }
        }
        return okHttpUtil;
    }

    public Call newCall(Request request) {
        return okHttpClient.newCall(request);
    }

    /**
     * 请求拦截器
     */
    static class RequestLoggerInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            Log.i(TAG, "url    =  : " + request.url());
            Log.i(TAG, "method =  : " + request.method());
            Log.i(TAG, "headers=  : " + request.headers());
            if (request.body() != null) {
                if (request.body().contentType() != null) {
                    Buffer buffer = new Buffer();
                    request.body().writeTo(buffer);
                    Log.i(TAG, "body   =  : " + buffer.readUtf8());
                }
            }

            return chain.proceed(request);
        }
    }

    /**
     * 响应拦截器
     */
    static class ResponseLoggerInterceptor implements Interceptor {
        @Override
        public Response intercept(Chain chain) throws IOException {
            Response response = chain.proceed(chain.request());

            Log.i(TAG, "code    =  : " + response.code());
            Log.i(TAG, "message =  : " + response.message());
            Log.i(TAG, "protocol=  : " + response.protocol());

            if (response.body() != null && response.body().contentType() != null) {
                MediaType mediaType = response.body().contentType();
                String string = response.body().string();
                Log.i(TAG, "mediaType=  :  " + mediaType.toString());
                Log.i(TAG, "string   =  : " + string);
                ResponseBody responseBody = ResponseBody.create(mediaType, string);
                return response.newBuilder().body(responseBody).build();
            } else {
                return response;
            }
        }
    }
}
