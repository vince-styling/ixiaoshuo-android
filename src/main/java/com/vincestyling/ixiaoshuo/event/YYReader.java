package com.vincestyling.ixiaoshuo.event;

import com.vincestyling.ixiaoshuo.pojo.Chapter;

import java.util.List;

public class YYReader {

    /**
     * 阅读页面回调接口，供阅读页面获取相应的信息
     */
    public interface OnYYReaderListener {
        /**
         * 获取书名
         */
        public String onGetBookName();

        /**
         * 获取本书章节总数
         */
        public int onGetTotalChapterCount();

        /**
         * 获取本书未读章节数目
         */
        public int onGetUnReadChapterCount();

        /**
         * 获取当前阅读章节的序号
         */
        public int onGetCurrentChapterIndex();

        /**
         * 获取当前章节信息
         */
        public Chapter onGetCurrentChapter();

        /**
         * 获取当前章节的上一个章节信息
         */
        public Chapter onGetPrevChapter();

        /**
         * 获取当前章节的下一个章节信息
         */
        public Chapter onGetNextChapter();

        /**
         * 获取本书的整个章节信息列表(此方法较耗资源，尽量少用)
         */
        public List<Chapter> onGetChapterList();

        /**
         * 标记当前正在阅读的章节，同时在翻页时调用，记录阅读进度
         */
        public void onReadingChapter(Chapter chapter);

        /**
         * 标记当前书籍阅读的百分比
         */
        public void onReadingPercent(float percent);

        /**
         * 退出阅读需要调用的方法
         */
        public void onCancelRead();

        /**
         * 判断书籍是否已经加入书架
         */
        public boolean onIsBookOnShelf();

        /**
         * 将书籍加入书架
         */
        public boolean onAddToBookShelf();

        /**
         * 从书架上移除书籍
         */
        public void onRemoveInBookShelf();

        /**
         * 下载某个章节
         */
        public boolean onDownloadOneChapter(Chapter chapter, OnDownloadChapterListener listener);

        /**
         * 下载多个章节
         */
        public boolean onDownloadChapters();
    }

    /**
     * 下载某个章节的Listener
     */
    public interface OnDownloadChapterListener {
        /**
         * 开始下载某个章节
         *
         * @param chapter 下载的章节信息
         */
        public void onDownloadStart(Chapter chapter);

        /**
         * 完成下载某个章节
         *
         * @param chapter 下载的章节信息
         */
        public void onDownloadComplete(Chapter chapter);
    }

    private static OnYYReaderListener mOnYYReaderListener;

    /**
     * 构造方法
     */
    private YYReader() {
    }

    /**
     * 初始化
     */
    public static void init(OnYYReaderListener onYYReaderListener) {
        mOnYYReaderListener = onYYReaderListener;
    }

    public static String getBookName() {
        return mOnYYReaderListener != null ? mOnYYReaderListener.onGetBookName() : null;
    }

    public static int getTotalChapterCount() {
        return mOnYYReaderListener != null ? mOnYYReaderListener.onGetTotalChapterCount() : -1;
    }

    public static int getUnReadChapterCount() {
        return mOnYYReaderListener != null ? mOnYYReaderListener.onGetUnReadChapterCount() : -1;
    }

    public static int onGetCurrentChapterIndex() {
        return mOnYYReaderListener != null ? mOnYYReaderListener.onGetCurrentChapterIndex() : -1;
    }

    public static Chapter getCurrentChapter() {
        return mOnYYReaderListener != null ? mOnYYReaderListener.onGetCurrentChapter() : null;
    }

    public static Chapter getPrevChapter() {
        return mOnYYReaderListener != null ? mOnYYReaderListener.onGetPrevChapter() : null;
    }

    public static Chapter getNextChapter() {
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

    public static void onReadingPercent(float percent) {
        if (mOnYYReaderListener != null) {
            mOnYYReaderListener.onReadingPercent(percent);
        }
    }

    public static void onCancelRead() {
        if (mOnYYReaderListener != null) {
            mOnYYReaderListener.onCancelRead();
        }
    }

    public static boolean isBookOnShelf() {
        return mOnYYReaderListener != null && mOnYYReaderListener.onIsBookOnShelf();
    }

    public static boolean addToBookShelf() {
        return mOnYYReaderListener != null && mOnYYReaderListener.onAddToBookShelf();
    }

    public static void removeInBookShelf() {
        if (mOnYYReaderListener != null) mOnYYReaderListener.onRemoveInBookShelf();
    }

    public static boolean downloadOneChapter(Chapter chapter, OnDownloadChapterListener listener) {
        return mOnYYReaderListener != null && mOnYYReaderListener.onDownloadOneChapter(chapter, listener);
    }

    public static boolean downloadChapters() {
        return mOnYYReaderListener != null && mOnYYReaderListener.onDownloadChapters();
    }

}
