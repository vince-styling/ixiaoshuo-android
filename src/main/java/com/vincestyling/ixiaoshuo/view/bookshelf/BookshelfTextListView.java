package com.vincestyling.ixiaoshuo.view.bookshelf;

import com.vincestyling.ixiaoshuo.db.AppDAO;

public class BookshelfTextListView extends BookshelfBaseListView {
    @Override
    public void refreshData() {
        mBookList = AppDAO.get().getBookListOnBookShelf();
        super.refreshData();
    }
}
