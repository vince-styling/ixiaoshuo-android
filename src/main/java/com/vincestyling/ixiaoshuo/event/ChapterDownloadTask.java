package com.vincestyling.ixiaoshuo.event;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.net.request.BookChaptersDBRequest;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.utils.Paths;

import java.io.File;

public abstract class ChapterDownloadTask {
    private Context mCtx;
    private Book mBook;
    private int mNotiId;
    private String mBookDirectoryPath;
    private NotificationCompat.Builder mBuilder;

    private int mChapterCount;
    private int mExecutedCount;
    private boolean mIsCancelled;
    private boolean mIsStarted;
    private boolean mIsNetTask;

    private int mIndex;
    private int mPageNo = 1;
    private boolean mHasNextPage = true;
    private PaginationList<Chapter> mChapterList;

    public ChapterDownloadTask(Context ctx, Book book, boolean isNetTask) {
        mCtx = ctx;
        mBook = book;
        mIsNetTask = isNetTask;
        mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBook.getId());
        mNotiId = Const.NOTIFICATION_DOWNLOAD_ALL_CHAPTER + mBook.getId();

        mBuilder = new NotificationCompat.Builder(mCtx);
        mBuilder.setAutoCancel(false);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setOngoing(true);
        mBuilder.setContentTitle(buildBookName());
        mBuilder.setSmallIcon(R.drawable.icon);

        Intent intent = new Intent(mCtx, ChapterDownloadNotificationBroadcastReceiver.class);
        intent.putExtra(Const.BOOK_ID, book.getId());
        PendingIntent pIntent = PendingIntent.getBroadcast(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mBuilder.setContentIntent(pIntent);
    }

    public void schedule() {
        if (mHasNextPage) {
            loadNextPage();
        } else {
            onFinished();
        }
    }

    private void execute() {
        if (mChapterList == null || mChapterList.size() == 0) {
            onFinished();
            return;
        }

        mChapterCount = mChapterList.getTotalItemCount();
        mHasNextPage = mChapterList.hasNextPage();
        mIndex = 0;
        mPageNo++;

        runNext();
    }

    private void runNext() {
        if (mIsCancelled) return;

        if (mIndex == mChapterList.size()) {
            schedule();
            return;
        }

        mExecutedCount++;
        final Chapter chapter = mChapterList.get(mIndex++);
        File chapterFile = new File(mBookDirectoryPath + chapter.getChapterId());
        if (chapterFile.exists()) {
            runNext();
            return;
        }

        Netroid.downloadChapterContent(mBook.getId(), chapter.getChapterId(), new Listener<Void>() {
            @Override
            public void onSuccess(Void r) {
                onProgressUpdate(mBook.getId(), chapter.getChapterId());
                runNext();
            }
        });
    }

    private void loadNextPage() {
        Listener<PaginationList<Chapter>> listener = new Listener<PaginationList<Chapter>>() {
            @Override
            public void onSuccess(PaginationList<Chapter> chapterList) {
                mChapterList = chapterList;
                execute();
            }
        };
        if (mIsNetTask) Netroid.getBookChapterList(mBook.getId(), mPageNo, listener);
        else new BookChaptersDBRequest(mBook.getId(), mPageNo, listener);
    }

    private float calculateProgressPercent() {
        float percentage = mExecutedCount * 1.0f / mChapterCount * 100f;
        return percentage > 100f ? 100f : percentage;
    }

    protected void onStart() {
        mIsStarted = true;
        mBuilder.setTicker(buildBookName());
        String format = mCtx.getResources().getString(R.string.download_notify);
        mBuilder.setContentText(String.format(format, 0f));
        getNotificationManager().notify(mNotiId, mBuilder.build());
    }

    private void onProgressUpdate(int bookId, int chapterId) {
        if (!mIsCancelled) {
            String format = mCtx.getResources().getString(R.string.download_notify);
            String info = String.format(format, calculateProgressPercent());
            mBuilder.setContentText(info);
            getNotificationManager().notify(mNotiId, mBuilder.build());
        }
        onChapterFinish(bookId, chapterId);
    }

    private void onFinished() {
        mIsStarted = false;
        onDone(mBook.getId());
        mBuilder.setContentText(mCtx.getResources().getString(R.string.download_completed));
        mBuilder.setOngoing(false);
        mBuilder.setAutoCancel(true);
        getNotificationManager().notify(mNotiId, mBuilder.build());
    }

    public void onCancel() {
        getNotificationManager().cancel(mNotiId);
        onDone(mBook.getId());
        mIsCancelled = true;
    }

    public abstract void onChapterFinish(int bookId, int chapterId);

    public abstract void onDone(int bookId);

    private String buildBookName() {
        String format = mCtx.getResources().getString(R.string.book_with_quote);
        return String.format(format, mBook.getName());
    }

    public boolean isCancelled() {
        return mIsCancelled;
    }

    public boolean isStarted() {
        return mIsStarted;
    }

    private NotificationManager getNotificationManager() {
        return (NotificationManager) mCtx.getSystemService(Service.NOTIFICATION_SERVICE);
    }

    public int getBookId() {
        return mBook.getId();
    }
}
