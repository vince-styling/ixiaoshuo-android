package com.vincestyling.ixiaoshuo.event;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.support.v4.app.NotificationCompat;
import com.duowan.mobile.netroid.Listener;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import com.vincestyling.ixiaoshuo.utils.Paths;

import java.io.File;

public abstract class ChapterDownloadTask {
	private Context mCtx;
	protected Book mBook;
	private int mNotiId;
	private String mBookDirectoryPath;
	private NotificationCompat.Builder mBuilder;

	private int mChapterCount;
	private int mExecutedCount;
	private boolean mIsCancelled;
	private boolean mIsStarted;

	protected int mIndex;
	protected int mPageNo = 1;
	protected boolean mHasNextPage = true;
	protected PaginationList<Chapter> mChapterList;

	public ChapterDownloadTask(Context ctx, Book book) {
		mCtx = ctx;
		mBook = book;
		mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBook.getBookId());
		mNotiId = Const.NOTIFICATION_DOWNLOAD_ALL_CHAPTER + mBook.getBookId();

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
	}

	public void schedule() {
		if (mHasNextPage) {
			loadNextPage();
		} else {
			onFinished();
		}
	}

	protected final void execute() {
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

		Netroid.downloadChapterContent(mBook.getBookId(), chapter.getChapterId(), new Listener<Void>() {
			@Override
			public void onSuccess(Void r) {
				onProgressUpdate(mBook.getBookId(), chapter.getChapterId());
				runNext();
			}
		});
	}

	protected abstract void loadNextPage();

	private float calculateProgressPercent() {
		float percentage = mExecutedCount * 1.0f / mChapterCount * 100f;
		return percentage > 100f ? 100f : percentage;
	}

	protected void onStart() {
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

    public abstract void onChapterFinish(int bookId, int chapterId);

	public abstract void onDone(int bookId);

	private String buildBookName() {
		return '《' + mBook.getName() + '》';
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
		return mBook.getBookId();
	}

}
