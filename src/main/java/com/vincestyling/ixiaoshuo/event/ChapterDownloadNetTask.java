package com.vincestyling.ixiaoshuo.event;

import android.content.Context;
import com.duowan.mobile.netroid.Listener;
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
        Netroid.getBookChapterList(mBook.getBookId(), mPageNo, new Listener<PaginationList<Chapter>>() {
            @Override
            public void onSuccess(PaginationList<Chapter> chapterList) {
                mChapterList = chapterList;
                execute();
            }
        });
    }

}
