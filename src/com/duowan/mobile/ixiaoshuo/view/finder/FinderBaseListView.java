package com.duowan.mobile.ixiaoshuo.view.finder;

import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.view.EndlessListAdapter;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public abstract class FinderBaseListView extends ViewBuilder implements AbsListView.OnScrollListener, OnItemClickListener {
	protected EndlessListAdapter<Book> mAdapter;

	protected int mPageNo;
	protected final static int PAGE_ITEM_COUNT = 40;
	private boolean mHasNextPage = true;

	public FinderBaseListView(MainActivity activity, int viewId) {
		mActivity = activity;
		mViewId = viewId;
	}

	@Override
	public void init() {
		if (getListView().getAdapter() != null) return;

		mAdapter = new EndlessListAdapter<Book>(mActivity, getListView(), R.layout.contents_loading) {
			@Override
			protected View doGetView(int position, View convertView, ViewGroup parent) {
				return getAdapterView(position, convertView);
			}
		};
		getListView().setAdapter(mAdapter);
		getListView().setOnScrollListener(this);
		getListView().setOnItemClickListener(this);
		loadNextPage();
	}

	protected abstract View getAdapterView(int position, View convertView);

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Book book = (Book) parent.getItemAtPosition(position);
		if (book != null) getActivity().showToastMsg("点击了《" + book.getName() + "》");
	}

	private void loadNextPage() {
		if (!mHasNextPage) return;

		if (!NetService.get().isNetworkAvailable()) {
			getActivity().showToastMsg(R.string.network_disconnect_msg);
			return;
		}

		mAdapter.setIsLoadingData(true);
		NetService.execute(new NetService.NetExecutor<PaginationList<Book>>() {
			public void preExecute() {}

			public PaginationList<Book> execute() {
				return loadData();
			}

			public void callback(PaginationList<Book> bookList) {
				mAdapter.setIsLoadingData(false);
				if (bookList == null || bookList.size() == 0) {
					getActivity().showToastMsg(R.string.without_data);
					mHasNextPage = false;
					return;
				}
				mHasNextPage = bookList.hasNextPage();
				mAdapter.addAll(bookList);
			}
		});
	}

	protected abstract PaginationList<Book> loadData();

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mAdapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
			loadNextPage();
		}
	}

	private ListView getListView() {
		return (ListView) mView;
	}

}
