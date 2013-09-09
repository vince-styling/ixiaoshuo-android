package com.duowan.mobile.ixiaoshuo.view.finder;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.Display;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.view.BookInfoView;
import com.duowan.mobile.ixiaoshuo.view.EndlessListAdapter;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

public abstract class FinderBaseListView extends ViewBuilder implements AbsListView.OnScrollListener, OnItemClickListener {
	protected EndlessListAdapter<Book> mAdapter;
	private View mLotNetworkUnavaliable;

	protected int mPageNo = 1;
	protected final static int PAGE_ITEM_COUNT = 20;
	protected boolean mHasNextPage = true;
	protected String mBookType;

	public FinderBaseListView(String bookType, MainActivity activity, int viewId, OnShowListener onShowListener) {
		mBookType = bookType;
		mShowListener = onShowListener;
		mActivity = activity;
		mViewId = viewId;
	}

	@Override
	public void init() {
		if (getListView().getAdapter() != null) return;

		mLotNetworkUnavaliable = mActivity.findViewById(R.id.lotNetworkUnavaliable);

		mAdapter = new EndlessListAdapter<Book>() {
			@Override
			protected View getView(int position, View convertView) {
				Holder holder;
				if (convertView == null) {
					convertView = mActivity.getLayoutInflater().inflate(R.layout.finder_book_list_item, null);

					holder = new Holder();
					holder.lotDivider = convertView.findViewById(R.id.lotDivider);

					holder.txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
					holder.txvBookSummary = (TextView) convertView.findViewById(R.id.txvBookSummary);

					holder.lotBookStatus = convertView.findViewById(R.id.lotBookStatus);
					holder.txvBookStatus1 = (TextView) convertView.findViewById(R.id.txvBookStatus1);
					holder.txvBookStatus2 = (TextView) convertView.findViewById(R.id.txvBookStatus2);
					holder.imvBookStatusSplit = (ImageView) convertView.findViewById(R.id.imvBookStatusSplit);

					holder.txvBookTips = (TextView) convertView.findViewById(R.id.txvBookTips);
					holder.txvBookCapacity = (TextView) convertView.findViewById(R.id.txvBookCapacity);

					convertView.setTag(holder);

					convertView.setLayoutParams(new AbsListView.LayoutParams(
							getListView().getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				} else {
					holder = (Holder) convertView.getTag();
				}

				Book book = mAdapter.getItem(position);

				holder.txvBookName.setText(book.getName());
				holder.txvBookSummary.setText(book.getPlainSummary());
				holder.txvBookCapacity.setText(book.getCapacityStr());

				holder.txvBookStatus1.setVisibility(book.isFinished() ? View.VISIBLE : View.GONE);
				holder.txvBookStatus2.setVisibility(book.isBothType() ? View.VISIBLE : View.GONE);
				holder.lotBookStatus.setVisibility(holder.txvBookStatus1.getVisibility() == View.VISIBLE || holder.txvBookStatus2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
				holder.imvBookStatusSplit.setVisibility(holder.txvBookStatus1.getVisibility() == View.VISIBLE && holder.txvBookStatus2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

				setBookTips(holder.txvBookTips, book);

				if (!mHasNextPage) {
					int posDiffer = mAdapter.getItemCount() - position;
					holder.lotDivider.setVisibility(posDiffer == 1 ? View.GONE : View.VISIBLE);
				}

				return convertView;
			}

			@Override
			protected View initProgressView() {
				View progressView = mActivity.getLayoutInflater().inflate(R.layout.contents_loading, null);
				Display display = mActivity.getWindowManager().getDefaultDisplay();
				progressView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				return progressView;
			}
		};

		getListView().setAdapter(mAdapter);
		getListView().setOnScrollListener(this);
		getListView().setOnItemClickListener(this);
	}

	protected abstract void setBookTips(TextView txvBookTips, Book book);

	@Override
	public void resume() {
		Button btnFinderRetry = (Button) mLotNetworkUnavaliable.findViewById(R.id.btnFinderRetry);
		btnFinderRetry.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				loadNextPage();
			}
		});
		super.resume();

		if (mAdapter.getItemCount() > 0) {
			mLotNetworkUnavaliable.setVisibility(View.GONE);
		} else {
			loadNextPage();
		}
	}

	private void loadNextPage() {
		if (!mHasNextPage) return;

		if (!NetService.get().isNetworkAvailable()) {
			if (mAdapter.getItemCount() > 0) {
				mActivity.showToastMsg(R.string.network_disconnect_msg);
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
					if (isInFront()) {
						if (mAdapter.getItemCount() > 0) {
							mActivity.showToastMsg(R.string.without_data);
						} else {
							mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
						}
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
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Book book = (Book) parent.getItemAtPosition(position);
		if (book == null) return;

		if (!NetService.get().isNetworkAvailable()) {
			mActivity.showToastMsg(R.string.network_disconnect_msg);
			return;
		}

		final int bookId = book.getBookId();
		NetService.execute(new NetService.NetExecutor<Book>() {
			ProgressDialog mPrgreDialog;

			public void preExecute() {
				mPrgreDialog = ProgressDialog.show(mActivity, null, mActivity.getString(R.string.loading_tip_msg), false, true);
				mPrgreDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
					@Override
					public void onCancel(DialogInterface dialog) {
						NetService.get().abortLast();
					}
				});
			}

			public Book execute() {
				return NetService.get().getBookDetail(bookId);
			}

			public void callback(Book bookDetail) {
				boolean isShowing = mPrgreDialog.isShowing();
				if (isShowing) mPrgreDialog.cancel(); else return;

				if (bookDetail == null) {
					mActivity.showToastMsg(R.string.without_data);
					return;
				}

				mActivity.showView(new BookInfoView(mActivity, bookDetail));
			}
		});
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mAdapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
			loadNextPage();
		}
	}

	class Holder {
		TextView txvBookName;
		TextView txvBookSummary;
		TextView txvBookStatus1, txvBookStatus2;
		TextView txvBookCapacity;
		TextView txvBookTips;
		View lotDivider;
		ImageView imvBookStatusSplit;
		View lotBookStatus;
	}

	protected ListView getListView() {
		return (ListView) mView;
	}

}
