package com.duowan.mobile.ixiaoshuo.event;

import android.content.Context;
import android.content.Intent;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;

import java.util.List;

public class YYReader {
	// 书籍章节的阅读状态
	public static final int READSTATUS_UNREAD       = 0;  /** 未读 */
	public static final int READSTATUS_READING      = 1;  /** 在读 */
	public static final int READSTATUS_READED       = 2;  /** 已读 */

	// 书籍章节的下载状态
	public static final int CHAPTERSTATUS_NO           = 0;  /** 本书没有这个章节了 */
	public static final int CHAPTERSTATUS_NOT_DOWNLOAD = 1;  /** 本书的这个章节为未下载状态 */
	public static final int CHAPTERSTATUS_READY        = 2;  /** 本书的这个章节为可用状态 */

	// 文件的信息
	public static final int FILE_NORMAL = 0;  /** 文件为正常的txt */
	public static final int FILE_GZIP   = 1;  /** 文件为gzip压缩过的文件 */

	/** 书籍的章节信息 */
	public static class ChapterInfo {
		public int mId;             /** 章节信息ID */
		public int mReadStatus;     /** 章节的阅读状态 */
		public int mDownloadStatus; /** 章节的下载状态 */
		public int mFileInfo;       /** 文件的压缩等状态 */
		public int mPosition;       /** 章节之前阅读到的位置 */
		public int mCapacity;       /** 章节的字数 */
		public String mName;        /** 章节的名称 */
		public String mLocation;    /** 章节的本地路径(文件未解压) */
	}

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
		public ChapterInfo onGetCurrentChapterInfo();

		/** 获取当前章节的上一个章节信息 */
		public ChapterInfo onGetPrevChapterInfo();

		/** 获取当前章节的下一个章节信息 */
		public ChapterInfo onGetNextChapterInfo();

		/** 通过ID获取某一章节的信息 */
		public ChapterInfo onGetChapterInfoById(int chapterId);

		/** 获取本书的整个章节信息列表(此方法较耗资源，尽量少用) */
		public List<ChapterInfo> onGetChapterList();

		/** 标记当前正在阅读的章节，同时在翻页时调用，记录阅读进度 */
		public void onReadingChapter(ChapterInfo chapterInfo);

		/** 判断书籍是否已经加入书架 */
		public boolean isBookOnShelf();

		/** 将书籍加入书架 */
		public boolean onAddToBookShelf();

		/** 从书架上移除书籍 */
		boolean onRemoveInBookShelf();

		/** 下载某个章节(注意：下载逻辑应在非UI线程内执行) */
		public boolean onDownloadOneChapter(ChapterInfo chapterInfo, OnDownloadChapterListener listener);
	}

	/** 下载某个章节的Listener */
	public interface OnDownloadChapterListener {
		/**
		 * 开始下载某个章节
		 * @param chapterInfo  下载的章节信息
		 */
		public void onDownloadStart(ChapterInfo chapterInfo);

		/**
		 * 完成下载某个章节
		 * @param chapterInfo  下载的章节信息
		 */
		public void onDownloadComplete(ChapterInfo chapterInfo);
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

	public static ChapterInfo getCurrentChapterInfo() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetCurrentChapterInfo() : null;
	}

	public static ChapterInfo getPrevChapter() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetPrevChapterInfo() : null;
	}

	public static ChapterInfo getNextChapterInfo() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetNextChapterInfo() : null;
	}

	public static ChapterInfo getChapterInfoById(int chapterId) {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetChapterInfoById(chapterId) : null;
	}

	public static List<ChapterInfo> getChapterList() {
		return mOnYYReaderListener != null ? mOnYYReaderListener.onGetChapterList() : null;
	}

	public static void onReadingChapter(ChapterInfo chapterInfo) {
		if (mOnYYReaderListener != null) {
			mOnYYReaderListener.onReadingChapter(chapterInfo);
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

	public static boolean downloadOneChapter(ChapterInfo chapterInfo, OnDownloadChapterListener listener) {
		return mOnYYReaderListener != null && mOnYYReaderListener.onDownloadOneChapter(chapterInfo, listener);
	}

}
