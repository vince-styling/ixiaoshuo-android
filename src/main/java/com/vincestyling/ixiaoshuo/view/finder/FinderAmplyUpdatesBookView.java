package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class FinderAmplyUpdatesBookView extends FinderAmplyBaseBookView {

    @Override
    protected void loadData(int pageNum, Listener<PaginationList<Book>> listener) {
        Netroid.getBookListByUpdateStatus(Book.STATUS_FINISHED, pageNum, listener);
    }

    @Override
    protected void setBookTips(TextView txvBookTips, Book book) {
        txvBookTips.setText(book.getCatName());
    }

    @Override
    public boolean isFirstConditionSatisfy(Book book) {
        return false;
    }
}
