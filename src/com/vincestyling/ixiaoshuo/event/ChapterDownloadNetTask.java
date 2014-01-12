package com.vincestyling.ixiaoshuo.event;

import android.content.Context;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

public abstract class ChapterDownloadNetTask extends ChapterDownloadTask {

	public ChapterDownloadNetTask(Context ctx, Book book) {
		super(ctx, book);
	}

	@Override
	protected void loadNextPage() {
		Netroid.getBookChapterList(mBook.getBookId(), mPageNo, new Response.Listener<PaginationList<Chapter>>() {
			@Override
			public void onResponse(PaginationList<Chapter> chapterList) {
				mChapterList = AppDAO.get().getSimpleBookChapterList(mBook.getBookId(), mPageNo, 100);
				execute();
			}

			@Override
			public void onErrorResponse(NetroidError netroidError) {
			}
		});
	}

}
