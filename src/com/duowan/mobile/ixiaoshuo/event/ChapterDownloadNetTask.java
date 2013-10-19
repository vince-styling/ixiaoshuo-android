package com.duowan.mobile.ixiaoshuo.event;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;

public class ChapterDownloadNetTask extends ChapterDownloadTask {

	public ChapterDownloadNetTask(Context ctx, Book book) {
		super(ctx, book);
	}

	@Override
	protected PaginationList<Chapter> loadNextPage() {
		return NetService.get().getSimpleBookChapterList(mBook.getSourceBookId(), mOrder, mPageNo, mPageItemCount);
	}

}
