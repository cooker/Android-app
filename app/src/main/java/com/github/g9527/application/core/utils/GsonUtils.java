package com.github.g9527.application.core.utils;

import com.google.gson.Gson;

public class GsonUtils {
    static Gson gson = new Gson();

    public static String toJson(Object obj) {
        return gson.toJson(obj);
    }

    public static <T> T toBean(String json, Class<T> cl) {
        return gson.fromJson(json, cl);
    }
}
