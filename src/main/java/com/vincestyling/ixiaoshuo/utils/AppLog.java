package com.vincestyling.ixiaoshuo.utils;

import android.util.Log;

public class AppLog {
    private static final String TAG = "ixiaoshuo_console";

    public static void d(String msg) {
        Log.d(TAG, msg);
    }

    public static void d(String msg, Throwable tr) {
        Log.d(TAG, msg, tr);
    }

    public static void i(String msg) {
        Log.i(TAG, msg);
    }

    public static void i(String msg, Throwable tr) {
        Log.i(TAG, msg, tr);
    }

    public static void e(String msg) {
        Log.e(TAG, msg);
    }

    public static void e(String format, Object... args) {
        Log.e(TAG, String.format(format, args));
    }

    public static void e(String msg, Throwable tr) {
        Log.e(TAG, msg, tr);
    }

    public static void e(Throwable tr) {
        Log.e(TAG, tr.getMessage(), tr);
    }

    public static void w(String msg) {
        Log.w(TAG, msg);
    }

    public static void w(String msg, Throwable tr) {
        Log.w(TAG, msg, tr);
    }

    public static void v(String msg) {
        Log.v(TAG, msg);
    }

    public static void v(String msg, Throwable tr) {
        Log.v(TAG, msg, tr);
    }

}
