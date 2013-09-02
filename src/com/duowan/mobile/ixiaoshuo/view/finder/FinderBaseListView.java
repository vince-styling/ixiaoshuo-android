package com.duowan.mobile.ixiaoshuo.view.finder;

import android.view.Display;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
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
	private View mLotNetworkUnavaliable;

	protected int mPageNo = 1;
	protected final static int PAGE_ITEM_COUNT = 40;
	protected boolean mHasNextPage = true;

	public FinderBaseListView(MainActivity activity, int viewId) {
		mActivity = activity;
		mViewId = viewId;
	}

	@Override
	public void init() {
		if (getListView().getAdapter() != null) return;

		mLotNetworkUnavaliable = mActivity.findViewById(R.id.lotNetworkUnavaliable);
		Button btnFinderRetry = (Button) mLotNetworkUnavaliable.findViewById(R.id.btnFinderRetry);
		btnFinderRetry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadNextPage();
			}
		});

		mAdapter = new EndlessListAdapter<Book>() {
			@Override
			protected View getView(int position, View convertView) {
				return getAdapterView(position, convertView);
			}
			@Override
			protected View initProgressView() {
				View progressView = getActivity().getLayoutInflater().inflate(R.layout.contents_loading, null);
				Display display = getActivity().getWindowManager().getDefaultDisplay();
				progressView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				return progressView;
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
			if (mAdapter.getItemCount() > 0) {
				getActivity().showToastMsg(R.string.network_disconnect_msg);
			} else {
				mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
			}
			return;
		}

		mAdapter.setIsLoadingData(true);
		mLotNetworkUnavaliable.setVisibility(View.GONE);
		NetService.execute(new NetService.NetExecutor<PaginationList<Book>>() {
			public void preExecute() {}

			public PaginationList<Book> execute() {
				return loadData();
			}

			public void callback(PaginationList<Book> bookList) {
				mAdapter.setIsLoadingData(false);
				if (bookList == null || bookList.size() == 0) {
					if (mAdapter.getItemCount() > 0) {
						getActivity().showToastMsg(R.string.without_data);
					} else {
						mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
					}
					return;
				}
				mHasNextPage = bookList.hasNextPage();
				mAdapter.addAll(bookList);
				mPageNo++;
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

	protected ListView getListView() {
		return (ListView) mView;
	}

}
