package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;

public class BookshelfVoiceListView extends BookshelfBaseListView {
    @Override
    protected void initWithoutBookUI() {
        ((TextView) mLotWithoutBook.findViewById(R.id.txvFinderTip)).setText(R.string.without_book_finder_tip3);
        ((TextView) mLotWithoutBook.findViewById(R.id.txvTopTip)).setText(R.string.without_voice_book_tips);
        super.initWithoutBookUI();
    }
}
