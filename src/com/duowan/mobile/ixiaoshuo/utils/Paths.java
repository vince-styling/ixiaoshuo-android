package com.duowan.mobile.ixiaoshuo.utils;

import java.io.File;

import android.os.Environment;

public abstract class Paths {
	
	public static String cardDirectory() {
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
