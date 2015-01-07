package com.vincestyling.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.finder.FinderAmplyHottestBookView;
import com.vincestyling.ixiaoshuo.view.finder.FinderBaseListView;
import com.vincestyling.ixiaoshuo.view.finder.FinderSimplyHottestBookView;
import com.vincestyling.ixiaoshuo.view.finder.FinderView;

public class CategoryBookListActivity extends BaseActivity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String categoryName = getIntent().getStringExtra(Const.CATEGORY_NAME);
        int categoryId = getIntent().getIntExtra(Const.CATEGORY_ID, 0);
        if (categoryId <= 0 || StringUtil.isEmpty(categoryName)) {
            finish();
            return;
        }

        FinderBaseListView finderView = FinderView.isOnSimplyStyle() ?
                new SimplyCategoryBookView(categoryId) : new AmplyCategoryBookView(categoryId);

        setContentView(R.layout.finder_category_booklist);
        getSupportFragmentManager().beginTransaction().add(R.id.lotContent, finderView).commit();

        TextView txtTitle = (TextView) findViewById(R.id.txvTitle);
        txtTitle.setText(categoryName);

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class SimplyCategoryBookView extends FinderSimplyHottestBookView {
        private int mCategoryId;

        public SimplyCategoryBookView(int categoryId) {
            mCategoryId = categoryId;
        }

        @Override
        protected void loadData(int pageNum, Listener<PaginationList<Book>> listener) {
            Netroid.getBookListByCategory(mCategoryId, pageNum, listener);
        }
    }

    private class AmplyCategoryBookView extends FinderAmplyHottestBookView {
        private int mCategoryId;

        public AmplyCategoryBookView(int categoryId) {
            mCategoryId = categoryId;
        }

        @Override
        protected void loadData(int pageNum, Listener<PaginationList<Book>> listener) {
            Netroid.getBookListByCategory(mCategoryId, pageNum, listener);
        }
    }
}