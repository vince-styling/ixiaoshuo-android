package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;

public class FinderAmplyHottestBookView extends FinderAmplyBaseBookView {

    @Override
    protected void loadData(int pageNum, Listener<PaginationList<Book>> listener) {
        Netroid.getHottestBookList(pageNum, listener);
    }

    @Override
    protected void setBookTips(TextView txvBookTips, Book book) {
        txvBookTips.setText(String.format(
                getResources().getString(R.string.finder_booklist_hottest), book.getReaderCount()));
    }

}
