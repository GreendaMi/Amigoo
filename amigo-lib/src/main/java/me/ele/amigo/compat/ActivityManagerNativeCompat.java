package me.ele.amigo.compat;

import android.os.Build;

import java.lang.reflect.InvocationTargetException;

import me.ele.amigo.reflect.MethodUtils;

public class ActivityManagerNativeCompat {

    private static Class sClass;

    public static Class Class() throws ClassNotFoundException {
        if (sClass == null) {
            if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                sClass = Class.forName("android.app.ActivityManager");
            }else{
                sClass = Class.forName("android.app.ActivityManagerNative");
            }
        }
        return sClass;
    }

    public static Object getDefault() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return MethodUtils.invokeStaticMethod(Class(), "getDefault");
    }

    public static Object getService() throws ClassNotFoundException, NoSuchMethodException,
            IllegalAccessException, InvocationTargetException {
        return MethodUtils.invokeStaticMethod(Class(), "getService");
    }
}
