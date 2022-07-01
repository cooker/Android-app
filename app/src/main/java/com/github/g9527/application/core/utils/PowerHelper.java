package com.github.g9527.application.core.utils;

import android.os.Build;
import android.os.StrictMode;
import android.util.Log;

import java.lang.reflect.Method;

public class PowerHelper {
    private static final String TAG = PowerHelper.class.getSimpleName();

    //文件操作权限
    public static final void filePower() {
        if(Build.VERSION.SDK_INT>=24){
            try{
                Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                m.invoke(null);
            }catch(Exception e){
                Log.e(TAG, "开启文件访问权限失败", e);
            }
        }
    }
}
