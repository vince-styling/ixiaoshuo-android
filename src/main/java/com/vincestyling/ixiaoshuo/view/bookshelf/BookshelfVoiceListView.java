package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;

public class BookshelfVoiceListView extends BookshelfBaseListView {
    @Override
    protected void setFinderTip(TextView txvFinderTip) {
        txvFinderTip.setText(R.string.without_book_finder_tip3);
    }
}
