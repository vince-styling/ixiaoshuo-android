package com.vincestyling.ixiaoshuo.utils;

import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.view.Window;
import android.view.WindowManager;

public class SysUtil {

    public static void setFullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    public static int getVersionCode(Context ctx) {
        try {
            PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pkgInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public static String getVersionName(Context ctx) {
        try {
            PackageInfo pkgInfo = ctx.getPackageManager().getPackageInfo(ctx.getPackageName(), 0);
            return pkgInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return "";
    }

    public static void killAppProcess() {
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    public static void threadSleep(long time) {
        try {
            Thread.sleep(time);
        } catch (Exception e) {
        }
    }

    /**
     * 设置当前Activity屏幕亮度，此方法不会影响系统屏幕亮度设置
     *
     * @param window     Activity当前运行的Window引用
     * @param brightness 屏幕亮度(0(最暗) -- 255(最亮))
     * @return 设置屏幕亮度是否成功
     */
    public static boolean setBrightness(Window window, int brightness) {
        if (brightness < 0 || brightness > 255) {
            return false;
        }

        WindowManager.LayoutParams lp = window.getAttributes();
        lp.screenBrightness = brightness / 255.0f;
        window.setAttributes(lp);

        return true;
    }

    /**
     * 获得当前Activity屏幕亮度
     *
     * @param window Activity当前运行的Window引用
     * @return 当前屏幕亮度(0(最暗) -- 255(最亮))
     */
    public static int getBrightness(Window window) {
        WindowManager.LayoutParams lp = window.getAttributes();
        return Math.round(lp.screenBrightness * 255.0f);
    }

    /**
     * 判断MobileData是否处于连接状态
     * 权限：<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
     *
     * @param context Context
     * @return MobileData是否处于已连接状态
     */
    public static boolean isMobileDataConnected(Context context) {
        try {
            ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
            if (cm == null) {
                return false;
            }

            NetworkInfo networkInfo = cm.getActiveNetworkInfo();
            if (networkInfo == null) {
                return false;
            }

            if (!networkInfo.isConnected()) {
                return false;
            }

            if (networkInfo.getType() != ConnectivityManager.TYPE_MOBILE) {
                return false;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

        return true;
    }
}
