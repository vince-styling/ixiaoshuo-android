package com.duowan.mobile.ixiaoshuo.service;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.event.YYReader;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.utils.Paths;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ReaderService implements YYReader.OnYYReaderListener {
	private Book mBook;
	private YYReader.ChapterInfo mReadingChapter;
	private String mBookDirectoryPath;

	public void startReader(Context ctx, int bid) {
		mBook = AppDAO.get().getBookOnReading(bid);
		mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBook.getBookId());

		Chapter chapter = AppDAO.get().getReadingChapter(mBook.getBid());
		mReadingChapter = convertChapterInfo(chapter);

		YYReader.startReader(ctx, this);
	}

	@Override
	public String onGetBookName() {
		return mBook.getName();
	}

	@Override
	public int onGetTotalChapterCount() {
		return AppDAO.get().getBookChapterCount(mBook.getBid());
	}

	@Override
	public int onGetUnReadChapterCount() {
		return onGetTotalChapterCount() - onGetCurrentChapterIndex() - 1;
	}

	@Override
	public int onGetCurrentChapterIndex() {
		return AppDAO.get().getBookChapterIndex(mBook.getBid(), mReadingChapter.mId);
	}

	@Override
	public YYReader.ChapterInfo onGetCurrentChapterInfo() {
		return mReadingChapter;
	}

	@Override
	public YYReader.ChapterInfo onGetPrevChapterInfo() {
		Chapter chapter = AppDAO.get().getPreviousChapter(mBook.getBid(), mReadingChapter.mId);
		return convertChapterInfo(chapter);
	}

	@Override
	public YYReader.ChapterInfo onGetNextChapterInfo() {
		Chapter chapter = AppDAO.get().getNextChapter(mBook.getBid(), mReadingChapter.mId);
		return convertChapterInfo(chapter);
	}

	@Override
	public YYReader.ChapterInfo onGetChapterInfoById(int chapterId) {
		return null;
	}

	@Override
	public List<YYReader.ChapterInfo> onGetChapterList() {
		List<Chapter> list = AppDAO.get().getBookChapters(mBook.getBid());
		List<YYReader.ChapterInfo> chapInfoList = new ArrayList<YYReader.ChapterInfo>(list.size());
		for (Chapter chapter : list) {
			chapInfoList.add(convertChapterInfo(chapter));
		}
		return chapInfoList;
	}

	@Override
	public void onReadingChapter(YYReader.ChapterInfo chapterInfo) {
		AppDAO.get().makeReadingChapter(mBook.getBid(), chapterInfo);
		mReadingChapter = chapterInfo;
	}

	@Override
	public boolean isBookOnShelf() {
		return AppDAO.get().isBookOnShelf(mBook.getBid());
	}

	@Override
	public boolean onAddToBookShelf() {
		return AppDAO.get().addToBookShelf(mBook.getBid());
	}

	@Override
	public boolean onRemoveInBookShelf() {
		return AppDAO.get().deleteBook(mBook);
	}

	@Override
	public boolean onDownloadOneChapter(final YYReader.ChapterInfo chapterInfo, final YYReader.OnDownloadChapterListener listener) {
		if (chapterInfo == null || chapterInfo.mDownloadStatus == YYReader.CHAPTERSTATUS_READY || listener == null) {
			return false;
		}

		if (!NetService.get().isNetworkAvailable()) {
			return false;
		}

		NetService.execute(new NetService.NetExecutor<Boolean>() {
			public void preExecute() {
				listener.onDownloadStart(chapterInfo);
			}

			public Boolean execute() {
				return NetService.get().downloadChapterContent(mBook.getBookId(), chapterInfo.mId);
			}

			public void callback(Boolean result) {
				if (result) chapterInfo.mDownloadStatus = YYReader.CHAPTERSTATUS_READY;
				listener.onDownloadComplete(chapterInfo);
			}
		});

		return true;
	}

	private YYReader.ChapterInfo convertChapterInfo(Chapter chapter) {
		if (chapter != null) {
			YYReader.ChapterInfo chapInfo = new YYReader.ChapterInfo();
			chapInfo.mId = chapter.getId();
			chapInfo.mName = chapter.getTitle();
			chapInfo.mReadStatus = chapter.getReadStatus();

			chapInfo.mLocation = mBookDirectoryPath + chapInfo.mId;
			File chapterFile = new File(chapInfo.mLocation);
			if (chapterFile.exists()) {
				chapInfo.mDownloadStatus = YYReader.CHAPTERSTATUS_READY;
			} else {
				chapInfo.mDownloadStatus = YYReader.CHAPTERSTATUS_NOT_DOWNLOAD;
			}

			chapInfo.mFileInfo = YYReader.FILE_GZIP;
			chapInfo.mPosition = chapter.getBeginPosition();
			return chapInfo;
		}
		return null;
	}

}
