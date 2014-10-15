package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class BookshelfVoiceListView extends BookshelfBaseListView {
    public static final int PAGER_INDEX = 1;

    @Override
    public void loadData() {
        mBookList = AppDAO.get().getBookListOnBookShelf(Book.TYPE_VOICE);
    }

    @Override
    protected void setFinderTip(TextView txvFinderTip) {
        txvFinderTip.setText(R.string.without_book_finder_tip3);
    }

}
