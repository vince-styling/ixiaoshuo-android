package com.vincestyling.ixiaoshuo.view.bookshelf;

import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class BookshelfTextListView extends BookshelfBaseListView {
    public static final int PAGER_INDEX = 0;

    @Override
    public void loadData() {
        mBookList = AppDAO.get().getBookListOnBookShelf(Book.TYPE_TEXT);
    }

}
