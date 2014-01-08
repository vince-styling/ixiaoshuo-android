package com.vincestyling.ixiaoshuo.event;

import android.content.Context;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public class ChapterDownloadNetTask extends ChapterDownloadTask {

	public ChapterDownloadNetTask(Context ctx, Book book) {
		super(ctx, book);
	}

	@Override
	protected PaginationList<Chapter> loadNextPage() {
		return NetService.get().getSimpleBookChapterList(mBook.getSourceBookId(), mOrder, mPageNo, mPageItemCount);
	}

}
