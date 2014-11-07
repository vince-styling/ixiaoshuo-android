package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.view.View;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;

public class BookshelfLocalListView extends BookshelfBaseListView {
    @Override
    protected int getWithoutBookUILayout() {
        return R.layout.book_shelf_without_book_local;
    }

    @Override
    protected void initWithoutBookUI() {
        ((TextView) mLotWithoutBook.findViewById(R.id.txvTopTip)).setText(R.string.without_local_book_tips);

        mLotWithoutBook.findViewById(R.id.lotGoScan).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getBaseActivity().showToastMsg(R.string.without_book_local_unconstruct);
            }
        });
    }
}
