package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDBOverseer;

public class BookshelfTextListView extends BookshelfBaseListView {
    @Override
    public void refreshData() {
        mBookList = AppDBOverseer.get().getBookListOnBookShelf();
        super.refreshData();
    }

    @Override
    protected void initWithoutBookUI() {
        ((TextView) mLotWithoutBook.findViewById(R.id.txvFinderTip)).setText(R.string.without_book_finder_tip2);
        ((TextView) mLotWithoutBook.findViewById(R.id.txvTopTip)).setText(R.string.without_text_book_tips);
        super.initWithoutBookUI();
    }
}
