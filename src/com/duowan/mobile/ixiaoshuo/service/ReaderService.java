package com.duowan.mobile.ixiaoshuo.service;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.event.YYReader;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;

import java.util.ArrayList;
import java.util.List;

public class ReaderService implements YYReader.OnYYReaderListener {
	private Book mBook;
	private Chapter mReadingChapter;

	public void startReader(Context ctx, int bookId) {
		mBook = AppDAO.get().getBookOnReading(bookId);
		mReadingChapter = AppDAO.get().getReadingChapter(mBook.getBookId());
		mReadingChapter.ready(mBook.getSourceBookId());
		YYReader.startReader(ctx, this);
	}

	@Override
	public String onGetBookName() {
		return mBook.getName();
	}

	@Override
	public int onGetTotalChapterCount() {
		// TODO : onGetTotalChapterCount 应该不用每次都查数据库
		return AppDAO.get().getBookChapterCount(mBook.getBookId());
	}

	@Override
	public int onGetUnReadChapterCount() {
		return onGetTotalChapterCount() - onGetCurrentChapterIndex() - 1;
	}

	@Override
	public int onGetCurrentChapterIndex() {
		// TODO : onGetCurrentChapterIndex 应该不用每次都查数据库
		return AppDAO.get().getBookChapterIndex(mBook.getBookId(), mReadingChapter.getChapterId());
	}

	@Override
	public Chapter onGetCurrentChapter() {
		return mReadingChapter;
	}

	@Override
	public Chapter onGetPrevChapter() {
		Chapter chapter = AppDAO.get().getPreviousChapter(mBook.getBookId(), mReadingChapter.getChapterId());
		if (chapter != null) chapter.ready(mBook.getSourceBookId());
		return chapter;
	}

	@Override
	public Chapter onGetNextChapter() {
		Chapter chapter = AppDAO.get().getNextChapter(mBook.getBookId(), mReadingChapter.getChapterId());
		if (chapter != null) chapter.ready(mBook.getSourceBookId());
		return chapter;
	}

	@Override
	public List<Chapter> onGetChapterList() {
		List<Chapter> list = AppDAO.get().getBookChapters(mBook.getBookId());
		List<Chapter> chapInfoList = new ArrayList<Chapter>(list.size());
		for (Chapter chapter : list) {
			chapInfoList.add(chapter);
		}
		return chapInfoList;
	}

	@Override
	public void onReadingChapter(Chapter chapter) {
		AppDAO.get().makeReadingChapter(mBook.getBookId(), chapter);
		mReadingChapter = chapter;
	}

	@Override
	public boolean isBookOnShelf() {
		return AppDAO.get().isBookOnShelf(mBook.getBookId());
	}

	@Override
	public boolean onAddToBookShelf() {
		return AppDAO.get().addToBookShelf(mBook.getBookId());
	}

	@Override
	public boolean onRemoveInBookShelf() {
		return AppDAO.get().deleteBook(mBook);
	}

	@Override
	public boolean onDownloadOneChapter(final Chapter chapter, final YYReader.OnDownloadChapterListener listener) {
		if (chapter == null || chapter.isNativeChapter() || listener == null) {
			return false;
		}

		if (!NetService.get().isNetworkAvailable()) {
			return false;
		}

		NetService.execute(new NetService.NetExecutor<Boolean>() {
			public void preExecute() {
				listener.onDownloadStart(chapter);
			}

			public Boolean execute() {
				return NetService.get().downloadChapterContent(mBook.getSourceBookId(), chapter.getChapterId());
			}

			public void callback(Boolean result) {
				if (result) chapter.ready(mBook.getSourceBookId());
				listener.onDownloadComplete(chapter);
			}
		});

		return true;
	}

}
