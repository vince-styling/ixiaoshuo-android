package com.duowan.mobile.ixiaoshuo.event;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;

public class ChapterDownloadDBTask extends ChapterDownloadTask {

	public ChapterDownloadDBTask(Context ctx, Book book) {
		super(ctx, book);
	}

	@Override
	protected PaginationList<Chapter> loadNextPage() {
		return AppDAO.get().getSimpleBookChapterList(mBook.getBid(), mPageNo, mPageItemCount);
	}

}
