package com.duowan.mobile.ixiaoshuo.event;

import android.content.Context;
import android.content.Intent;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;

import java.util.List;

public class YYReader {

	/** 阅读页面回调接口，供阅读页面获取相应的信息 */
	public interface OnYYReaderListener {
		/** 获取书名 */
		public String onGetBookName();

		/** 获取本书章节总数 */
		public int onGetTotalChapterCount();

		/** 获取本书未读章节数目 */
		public int onGetUnReadChapterCount();

		/** 获取当前阅读章节的序号 */
		public int onGetCurrentChapterIndex();

		/** 获取当前章节信息 */
		public Chapter onGetCurrentChapter();

		/** 获取当前章节的上一个章节信息 */
		public Chapter onGetPrevChapter();

		/** 获取当前章节的下一个章节信息 */
		public Chapter onGetNextChapter();

		/** 获取本书的整个章节信息列表(此方法较耗资源，尽量少用) */
		public List<Chapter> onGetChapterList();

		/** 标记当前正在阅读的章节，同时在翻页时调用，记录阅读进度 */
		public void onReadingChapter(Chapter chapter);

		/** 判断书籍是否已经加入书架 */
		public boolean isBookOnShelf();

		/** 将书籍加入书架 */
		public boolean onAddToBookShelf();

		/** 从书架上移除书籍 */
		boolean onRemoveInBookShelf();

		/** 下载某个章节(注意：下载逻辑应在非UI线程内执行) */
		public boolean onDownloadOneChapter(Chapter chapter, OnDownloadChapterListener listener);
	}

	/** 下载某个章节的Listener */
	public interface OnDownloadChapterListener {
		/**
		 * 开始下载某个章节
		 * @param chapter  下载的章节信息
		 */
		public void onDownloadStart(Chapter chapter);

		/**
		 * 完成下载某个章节
		 * @param chapter  下载的章节信息
		 */
		public void onDownloadComplete(Chapter chapter);
	}

	private static Context mContext;
	private static OnYYReaderListener mOnYYReaderListener;

	/**
	 * 构造方法
	 */
	private YYReader() {
	}

	/** 开始阅读书籍 */
	public static void startReader(Context ctx, OnYYReaderListener onYYReaderListener) {
		mContext = ctx;
		mOnYYReaderListener = onYYReaderListener;
		Intent intent = new Intent(mContext, ReaderActivity.class);
		intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		mContext.startActivity(intent);
	}

	public static Context getContext() {
		return mContext;
	}

	public static String getBookName() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetBookName() : null;
	}

	public static int getTotalChapterCount() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetTotalChapterCount() : 0;
	}

	public static int getUnReadChapterCount() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetUnReadChapterCount() : 0;
	}

	public static int onGetCurrentChapterIndex() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetCurrentChapterIndex() : -1;
	}

	public static Chapter getCurrentChapterInfo() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetCurrentChapter() : null;
	}

	public static Chapter getPrevChapter() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetPrevChapter() : null;
	}

	public static Chapter getNextChapterInfo() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetNextChapter() : null;
	}

	public static List<Chapter> getChapterList() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetChapterList() : null;
	}

	public static void onReadingChapter(Chapter chapter) {
		if (mOnYYReaderListener != null) {
			mOnYYReaderListener.onReadingChapter(chapter);
		}
	}

	public static boolean isBookOnShelf() {
		return mOnYYReaderListener != null && mOnYYReaderListener.isBookOnShelf();
	}

	public static boolean addToBookShelf() {
		return mOnYYReaderListener != null && mOnYYReaderListener.onAddToBookShelf();
	}

	public static boolean removeInBookShelf() {
		return mOnYYReaderListener != null && mOnYYReaderListener.onRemoveInBookShelf();
	}

	public static boolean downloadOneChapter(Chapter chapter, OnDownloadChapterListener listener) {
		return mOnYYReaderListener != null && mOnYYReaderListener.onDownloadOneChapter(chapter, listener);
	}

}
