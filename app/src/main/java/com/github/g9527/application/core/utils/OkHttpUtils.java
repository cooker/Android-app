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

public class OkHttpUtils {
    static OkHttpUtils okHttpUtil;
    private OkHttpClient.Builder builder;
    private Request.Builder requestBuilder;
    private OkHttpClient okHttpClient;

    private OkHttpUtils() {
        builder = new OkHttpClient.Builder();
        okHttpClient = builder.addInterceptor(new RequestLoggerInterceptor())
                .addInterceptor(new ResponseLoggerInterceptor())
                .build();
        requestBuilder = new Request.Builder();//省的每次都new  request操作,直接builder出来,随后需要什么往里加,build出来即可
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

    public Request.Builder requestBuilder() {
        return requestBuilder;
    }

    public Call newCall(Request request) {
        return okHttpClient.newCall(request);
    }

    /**
     * 接口用于回调数据
     */
    public interface ICallback {
        void invoke(String string);
    }

    /**
     * 请求拦截器
     */
    static class RequestLoggerInterceptor implements Interceptor {

        @Override
        public Response intercept(Chain chain) throws IOException {
            Request request = chain.request();

            Log.e(this.getClass().getSimpleName(), "url    =  : " + request.url());
            Log.e(this.getClass().getSimpleName(), "method =  : " + request.method());
            Log.e(this.getClass().getSimpleName(), "headers=  : " + request.headers());
            Log.e(this.getClass().getSimpleName(), "body   =  : " + request.body());

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

            Log.e(this.getClass().getSimpleName(), "code    =  : " + response.code());
            Log.e(this.getClass().getSimpleName(), "message =  : " + response.message());
            Log.e(this.getClass().getSimpleName(), "protocol=  : " + response.protocol());

            if (response.body() != null && response.body().contentType() != null) {
                MediaType mediaType = response.body().contentType();
                String string = response.body().string();
                Log.e(this.getClass().getSimpleName(), "mediaType=  :  " + mediaType.toString());
                Log.e(this.getClass().getSimpleName(), "string   =  : " + string);
                ResponseBody responseBody = ResponseBody.create(mediaType, string);
                return response.newBuilder().body(responseBody).build();
            } else {
                return response;
            }
        }
    }
}
