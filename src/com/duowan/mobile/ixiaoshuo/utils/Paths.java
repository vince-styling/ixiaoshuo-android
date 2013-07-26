package com.duowan.mobile.ixiaoshuo.utils;

import java.io.File;
import java.io.FilenameFilter;

import android.os.Environment;

public abstract class Paths {
	private static String appStoragePath;

	public static String cardDirectory() {
		if(appStoragePath != null) return appStoragePath;
		String appDirectory = "/ixiaoshuo/";

		File extDir = Environment.getExternalStorageDirectory();
		if (extDir.canRead() && extDir.canWrite()) {
			return appStoragePath = extDir.getPath() + appDirectory;
		}

		// sdcard名字可能有不同，例如：sdcard-ext
		File[] directors = extDir.getParentFile().listFiles(new FilenameFilter() {
			public boolean accept(File directory, String fileName) {
				return fileName.contains("sdcard");
			}
		});
		for (File dir : directors) {
			if (dir.canRead() && dir.canWrite()) {
				return appStoragePath = dir.getPath() + appDirectory;
			}
		}

		return appStoragePath = extDir.getParentFile().getPath() + appDirectory;
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
