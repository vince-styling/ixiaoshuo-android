package com.duowan.mobile.ixiaoshuo.event;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.pojo.Constants;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.utils.Paths;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

import java.io.File;

public abstract class ChapterDownloadTask extends Thread {
	private Context mCtx;
	protected Book mBook;
	private int mNotiId;
	private String mBookDirectoryPath;
	private NotificationCompat.Builder mBuilder;

	private int mChapterCount;
	private int mExecutedCount;
	private boolean mIsCancelled;
	private boolean mIsStarted;

	protected int mPageNo = 1;
	protected String mOrder = "asc";
	protected int mPageItemCount = 50;
	protected boolean mHasNextPage = true;

	public ChapterDownloadTask(Context ctx, Book book) {
		mCtx = ctx;
		mBook = book;
		mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBook.getBookId());
		mNotiId = Constants.NOTIFICATION_DOWNLOAD_ALL_CHAPTER + mBook.getBookId();

		mBuilder = new NotificationCompat.Builder(mCtx);
        mBuilder.setAutoCancel(false);
        mBuilder.setOnlyAlertOnce(true);
        mBuilder.setOngoing(true);
        mBuilder.setContentTitle(buildBookName());
        mBuilder.setSmallIcon(R.drawable.icon);

		Intent intent = new Intent(mCtx, ChapterDownloadNotificationBroadcastReceiver.class);
		intent.setAction("detail");
		intent.putExtra("BookId", book.getBookId());
		PendingIntent pIntent = PendingIntent.getBroadcast(mCtx, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
		mBuilder.setContentIntent(pIntent);

		setPriority(Thread.MIN_PRIORITY);
	}

	@Override
	public void run() {
		onStart();
		onRun();
		if (!mIsCancelled) {
            onFinished();
        }
	}

	private void onRun() {
		if (mIsCancelled) {
            return;
        }

		mOrder = "asc";
		mPageItemCount = 50;
		boolean result;
		File chapterFile;
		PaginationList<Chapter> chapterList;

		while (mHasNextPage) {
			if (mIsCancelled) return;

			chapterList = loadNextPage();
			if (chapterList == null || chapterList.size() == 0) return;

			mChapterCount = chapterList.getTotalItemCount();
			mHasNextPage = chapterList.hasNextPage();
			mPageNo++;

			for (Chapter chapter : chapterList) {
				mExecutedCount++;

				chapterFile = new File(mBookDirectoryPath + chapter.getId());
				result = chapterFile.exists() || NetService.get().downloadChapterContent(mBook.getBookId(), chapter.getId());
				if (!result || mIsCancelled) {
                    return;
                }

				onProgressUpdate(mBook.getBookId(), chapter.getId());
			}
		}
	}

	protected abstract PaginationList<Chapter> loadNextPage();

	private float calculateProgressPercent() {
		float percentage = mExecutedCount * 1.0f / mChapterCount * 100f;
		return percentage > 100f ? 100f : percentage;
	}

	private void onStart() {
		mIsStarted = true;
		mBuilder.setTicker(buildBookName());
        String info = mCtx.getResources().getString(R.string.download_notify_start);
        mBuilder.setContentText(info);
		getNotificationManager().notify(mNotiId, mBuilder.build());
	}

	private void onProgressUpdate(int bookId, int chapterId) {
        if (mExecutedCount % 10 == 0) {
            String format = mCtx.getResources().getString(R.string.download_notify);
            String info = String.format(format, calculateProgressPercent());
            mBuilder.setContentText(info);
            getNotificationManager().notify(mNotiId, mBuilder.build());
        }
        onChapterFinish(bookId, chapterId);
	}

	private void onFinished() {
		mIsStarted = false;
		onDone(mBook.getBookId());
		mBuilder.setContentText("下载完成");
        mBuilder.setOngoing(false);
        mBuilder.setAutoCancel(true);
		getNotificationManager().notify(mNotiId, mBuilder.build());
	}

	public void onCancel() {
		getNotificationManager().cancel(mNotiId);
		onDone(mBook.getBookId());
		mIsCancelled = true;
	}

    public void onChapterFinish(int bookId, int chapterId) {

    }

	public void onDone(int bookId) {

    }

	private String buildBookName() {
		return '《' + mBook.getName() + '》';
	}

	public boolean isCancelled() {
		return mIsCancelled;
	}

	public boolean isStarted() {
		return mIsStarted;
	}

	public int getBookId() {
		return mBook.getBookId();
	}

	private NotificationManager getNotificationManager() {
		return (NotificationManager) mCtx.getSystemService(Service.NOTIFICATION_SERVICE);
	}

}
