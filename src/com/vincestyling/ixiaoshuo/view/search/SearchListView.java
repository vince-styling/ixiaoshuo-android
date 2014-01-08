package com.vincestyling.ixiaoshuo.view.search;

import android.util.Log;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

/**
 * 搜索结果listView
 *
 * @author gaocong
 */
public class SearchListView extends SearchBaseListView {
	private String mSearchKey;
	private int mSearchType;
	private static final String TAG = "YYReader_SearchListView";

	public SearchListView(MainActivity activity, OnShowListener onShowListener) {
		super(activity, R.id.search_hot_key_search_result, onShowListener);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.finder_book_listview, null);
		mView.setId(mViewId);
	}

	@Override
	protected PaginationList<Book> loadData() {
		Log.i(TAG, "loadData: " + mSearchKey);
		return NetService.get().getBookListBySearch(mSearchType, mPageNo, PAGE_ITEM_COUNT, mSearchKey);
	}

	@Override
	protected void setBookTips(TextView txvBookTips, Book book) {
		txvBookTips.setText(book.getReaderCount() + "人看过");
	}

	public void doSearch(String searchWord) {
		mSearchKey = searchWord;
		super.mHasNextPage = true;
		mPageNo = 1;
		mAdapter.clear();
		super.loadNextPage();
	}

	public void setSearKey(String searchWord, int searchType) {
		mSearchKey = searchWord;
		mSearchType = searchType;
	}


}
