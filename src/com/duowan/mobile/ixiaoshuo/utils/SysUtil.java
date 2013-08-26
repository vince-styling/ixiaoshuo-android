package com.duowan.mobile.ixiaoshuo.utils;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

public class SysUtil {

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

}
