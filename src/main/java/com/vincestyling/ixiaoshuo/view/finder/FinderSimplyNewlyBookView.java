package com.vincestyling.ixiaoshuo.view.finder;

import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.utils.StringUtil;

public class FinderSimplyNewlyBookView extends FinderBaseListView {

    @Override
    protected void loadData(int pageNum, Listener<PaginationList<Book>> listener) {
        Netroid.getNewlyBookList(pageNum, listener);
    }

    @Override
    protected void setBookTips(TextView txvBookTips, Book book) {
        txvBookTips.setText(StringUtil.getDiffWithNow(book.getLastUpdateTime()));
    }
}
