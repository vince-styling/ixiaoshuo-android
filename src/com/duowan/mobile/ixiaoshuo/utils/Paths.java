package com.duowan.mobile.ixiaoshuo.utils;

import java.io.File;

import android.os.Environment;

public abstract class Paths {

	public static String cardDirectory() {
		// TODO : 要解决 sdcard 未挂载，使用 sdcard-ext 作为默认存储的问题
		return Environment.getExternalStorageDirectory().getPath() + "/ixiaoshuo/";
	}
	
	public static File getCoversDirectory() {
		File dir = new File(cardDirectory() + "covers");
		if (!dir.exists()) dir.mkdirs();
		return dir;
	}
	public static String getCoversDirectoryPath() {
		return getCoversDirectory().getPath() + File.separatorChar;
	}

}
