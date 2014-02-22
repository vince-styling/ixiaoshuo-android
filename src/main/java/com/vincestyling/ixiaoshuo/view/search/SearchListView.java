package com.vincestyling.ixiaoshuo.view.search;

import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

public class SearchListView extends ViewBuilder implements AbsListView.OnScrollListener, OnItemClickListener {
	private EndlessListAdapter<Book> mAdapter;
	private View mLotNetworkUnavaliable;
	private String mKeyword;

	private boolean mHasNextPage = true;
	private int mPageNo = 1;

	public SearchListView(MainActivity activity, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		setActivity(activity);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.finder_book_listview, null);
		mView.setId(mViewId);
	}

	@Override
	public void init() {
		if (getListView().getAdapter() != null) return;

		mLotNetworkUnavaliable = getActivity().findViewById(R.id.lotNetworkUnavaliable);

		mAdapter = new EndlessListAdapter<Book>() {
			@Override
			protected View getView(int position, View convertView) {
				Holder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.finder_book_list_item, null);

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
				holder.txvBookSummary.setText(book.getSummary());
				holder.txvBookCapacity.setText(book.getCapacityStr());

				holder.txvBookStatus1.setVisibility(book.isFinished() ? View.VISIBLE : View.GONE);
				holder.txvBookStatus2.setVisibility(book.isBothType() ? View.VISIBLE : View.GONE);
				holder.lotBookStatus.setVisibility(holder.txvBookStatus1.getVisibility() == View.VISIBLE || holder.txvBookStatus2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
				holder.imvBookStatusSplit.setVisibility(holder.txvBookStatus1.getVisibility() == View.VISIBLE && holder.txvBookStatus2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

				holder.txvBookTips.setText(book.getReaderCount() + "人看过");

				if (!mHasNextPage) {
					int posDiffer = mAdapter.getItemCount() - position;
					holder.lotDivider.setVisibility(posDiffer == 1 ? View.GONE : View.VISIBLE);
				}

				return convertView;
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
	}

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

	public void doSearch(String keyword) {
		mHasNextPage = true;
		mKeyword = keyword;
		mAdapter.clear();
		mPageNo = 1;
		loadNextPage();
	}

	public void setSearKey(String searchWord) {
		mKeyword = searchWord;
	}

	protected void loadNextPage() {
		if (!mHasNextPage) return;

		Netroid.getBookListByKeyword(mKeyword, mPageNo, new Listener<PaginationList<Book>>() {
			@Override
			public void onPreExecute() {
				mLotNetworkUnavaliable.setVisibility(View.GONE);
				mAdapter.setIsLoadingData(true);
			}

			@Override
			public void onFinish() {
				mAdapter.setIsLoadingData(false);
			}

			@Override
			public void onSuccess(PaginationList<Book> bookList) {
				mHasNextPage = bookList.hasNextPage();
				mAdapter.addAll(bookList);
				mPageNo++;
			}

			@Override
			public void onError(NetroidError error) {
				if (isInFront()) {
					if (mAdapter.getItemCount() > 0) {
						getActivity().showToastMsg(R.string.without_data);
					} else {
						mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
					}
				}
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Book book = (Book) parent.getItemAtPosition(position);
		if (book != null) {
			Intent intent = new Intent(getActivity(), BookInfoActivity.class);
			intent.putExtra(Const.BOOK_ID, book.getBookId());
			intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			getActivity().startActivity(intent);
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

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

	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

}
