package com.vincestyling.ixiaoshuo.event;

import android.content.Context;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public abstract class ChapterDownloadDBTask extends ChapterDownloadTask {

	public ChapterDownloadDBTask(Context ctx, Book book) {
		super(ctx, book);
	}

	@Override
	protected PaginationList<Chapter> loadNextPage() {
		return AppDAO.get().getSimpleBookChapterList(mBook.getBookId(), mPageNo, 100);
	}

}
