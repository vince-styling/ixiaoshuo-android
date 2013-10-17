package com.duowan.mobile.ixiaoshuo.view.bookshelf;

import java.io.File;
import java.util.ArrayList;
import java.util.UUID;

import com.duowan.mobile.ixiaoshuo.pojo.Book;

import android.util.Log;

/**
 * 本地文件扫描类
 * 
 * @author gaocong
 * 
 */
public class ScanFile {
	private static final String TAG = "YYReader_Scan";
	private static ArrayList<Book> list = new ArrayList<Book>();
	public static ArrayList<Book> GetFiles(String path) {
		
		File rootDir = new File(path);
		if (!rootDir.isDirectory()) {
			if (path.endsWith(".txt")) {
				Book book = new Book();
				book.setLocaPath(path);
				list.add(book);
				Log.i(TAG, "path is: " + path);
			}
		} else {
			String[] fileList = rootDir.list();
			for (int i = 0; i < fileList.length; i++) {
				path = rootDir.getAbsolutePath() + "/" + fileList[i];
				GetFiles(path);
			}
		}
		return list;
	}

}
