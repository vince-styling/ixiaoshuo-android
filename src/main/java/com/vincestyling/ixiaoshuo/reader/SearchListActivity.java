package com.vincestyling.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.finder.FinderAmplyUpdatesBookView;

public class SearchListActivity extends BaseActivity {
    public final static String KEYWORD = "keyword";

    private TextView mTxvSearchResultInfo;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        String keyword = getIntent().getStringExtra(KEYWORD);
        if (StringUtil.isBlank(keyword)) {
            showToastMsg(R.string.please_input_key_word);
            finish();
            return;
        }

        setContentView(R.layout.search_result);
        getSupportFragmentManager().beginTransaction().add(
                R.id.lotContent, new AmplySearchBookView(keyword)).commit();

        TextView txvTitle = (TextView) findViewById(R.id.txvTitle);
        txvTitle.setText(String.format(getString(R.string.search_result_head_title), keyword));

        mTxvSearchResultInfo = (TextView) findViewById(R.id.txvSearchResultInfo);

        findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private class AmplySearchBookView extends FinderAmplyUpdatesBookView {
        private String mKeyWord;

        public AmplySearchBookView(String keyword) {
            mKeyWord = keyword;
        }

        @Override
        protected void loadData(int pageNum, final Listener<PaginationList<Book>> listener) {
            Netroid.getBookListByKeyword(mKeyWord, pageNum, new Listener<PaginationList<Book>>() {
                @Override
                public void onSuccess(PaginationList<Book> bookList) {
                    mTxvSearchResultInfo.setText(String.format(
                            getString(R.string.search_result_info), bookList.getTotalItemCount()));
                    mTxvSearchResultInfo.setVisibility(View.VISIBLE);
                    listener.onSuccess(bookList);
                }

                @Override
                public void onError(NetroidError error) {
                    listener.onError(error);
                }

                @Override
                public void onPreExecute() {
                    listener.onPreExecute();
                }

                @Override
                public void onFinish() {
                    listener.onFinish();
                }
            });
        }
    }
}