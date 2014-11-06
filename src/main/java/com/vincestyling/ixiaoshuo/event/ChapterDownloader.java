package com.vincestyling.ixiaoshuo.event;

import android.content.Context;
import com.vincestyling.ixiaoshuo.pojo.Book;

import java.util.LinkedList;

public class ChapterDownloader {
    private int mMaxTaskCount = 2;

    private static ChapterDownloader mInstance;

    public static synchronized ChapterDownloader get() {
        if (mInstance == null) mInstance = new ChapterDownloader();
        return mInstance;
    }

    /**
     * 下载状态回调类
     */
    public interface OnDownLoadListener {
        /**
         * 一个章节下载完成回调方法
         *
         * @param bookId    正在下载的书籍
         * @param chapterId 下载完成的章节
         */
        public void onChapterComplete(int bookId, int chapterId);

        /**
         * 全部下载完成回调方法
         *
         * @param bookId 下载完成的书籍ID
         */
        public void onDownloadComplete(int bookId);
    }

    private LinkedList<ChapterDownloadTask> mBookTaskList;

    public ChapterDownloader() {
        mBookTaskList = new LinkedList<ChapterDownloadTask>();
    }

    public synchronized boolean schedule(Context ctx, Book book, boolean isNetTask, final OnDownLoadListener listener) {
        if (mBookTaskList.size() >= mMaxTaskCount) return false;

        ChapterDownloadTask bookTask = new ChapterDownloadTask(ctx, book, isNetTask) {
            @Override
            public void onChapterFinish(int bookId, int chapterId) {
                if (listener != null) {
                    listener.onChapterComplete(bookId, chapterId);
                }
            }

            @Override
            public void onDone(int bookId) {
                for (ChapterDownloadTask task : mBookTaskList) {
                    if (bookId == task.getBookId()) {
                        mBookTaskList.remove(task);

                        if (listener != null)
                            listener.onDownloadComplete(bookId);

                        break;
                    }
                }
            }
        };

        mBookTaskList.add(bookTask);
        bookTask.onStart();
        bookTask.schedule();
        return true;
    }

    public void cancel(int bookId) {
        for (ChapterDownloadTask task : mBookTaskList) {
            if (bookId == task.getBookId()) {
                task.onCancel();
                break;
            }
        }
    }

    public void cancelAll() {
        for (ChapterDownloadTask task : mBookTaskList) {
            task.onCancel();
        }
    }

    public boolean taskIsStarted(int bookId) {
        for (ChapterDownloadTask task : mBookTaskList) {
            if (bookId == task.getBookId()) {
                return task.isStarted();
            }
        }
        return false;
    }

    public void setMaxTaskCount(int maxTaskCount) {
        mMaxTaskCount = maxTaskCount;
    }

}
