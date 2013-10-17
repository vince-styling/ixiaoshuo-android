package com.duowan.mobile.ixiaoshuo.view.bookshelf;

import java.io.File;

import android.util.Log;

/**
 * 本地文件扫描类
 * @author gaocong
 *
 */
public class Scan {
	private static final String TAG = "YYReader_Scan";
	
	public static String GetSql(String path) {
		File rootDir = new File(path);
		if (!rootDir.isDirectory()) {
			if(path.endsWith(".txt"))
			Log.i(TAG, "path is: " + path);
		} else {
			String[] fileList = rootDir.list();
			for (int i = 0; i < fileList.length; i++) {
				path = rootDir.getAbsolutePath() + "/" + fileList[i];
				GetSql(path);
			}
		}
		return null;
	}

}
