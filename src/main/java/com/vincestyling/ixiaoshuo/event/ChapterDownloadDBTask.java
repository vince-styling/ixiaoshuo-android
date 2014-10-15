package com.vincestyling.ixiaoshuo.event;

import android.content.Context;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Book;

public abstract class ChapterDownloadDBTask extends ChapterDownloadTask {

    public ChapterDownloadDBTask(Context ctx, Book book) {
        super(ctx, book);
    }

    @Override
    protected void loadNextPage() {
        mChapterList = AppDAO.get().getSimpleBookChapterList(mBook.getBookId(), mPageNo, 100);
        execute();
    }

}
