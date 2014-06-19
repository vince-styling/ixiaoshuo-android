package com.vincestyling.ixiaoshuo.view.finder;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;

public abstract class FinderBaseListView extends BaseFragment implements AbsListView.OnScrollListener, OnItemClickListener {
	protected EndlessListAdapter<Book> mAdapter;
	private View mLotNetworkUnavaliable;
	private ListView mListView;

	private boolean shouldRestore;
	private int index, top;

	private static final int PAGE_SIZE = 20;
	protected boolean mHasNextPage = true;
	protected int mPageNum = 1;
	// TODO : this variable should modify when adding header drap load previous page!!
	protected int mHeadMissPageCount;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLotNetworkUnavaliable = getActivity().findViewById(R.id.lotFinderNetworkUnavaliable);
		mListView = (ListView) getActivity().getLayoutInflater().inflate(R.layout.finder_book_listview, null);
		return mListView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
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
							FinderBaseListView.this.getView().getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
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

				setBookTips(holder.txvBookTips, book);

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

		mListView.setOnItemClickListener(this);
		mListView.setOnScrollListener(this);
		mListView.setAdapter(mAdapter);

		if (savedInstanceState != null) {
			mPageNum = savedInstanceState.getInt(PAGE_NUM, 1);
			mHeadMissPageCount = mPageNum - 1;

			index = savedInstanceState.getInt(INDEX, -1);
			top = savedInstanceState.getInt(TOP, -1);

			shouldRestore = index >= 0;
		}
	}

	protected abstract void setBookTips(TextView txvBookTips, Book book);

	@Override
	public void onResume() {
		super.onResume();

		if (mAdapter.getItemCount() > 0) {
			mLotNetworkUnavaliable.setVisibility(View.GONE);
		} else {
			loadNextPage();
		}
	}

	private void loadNextPage() {
		if (mHasNextPage) loadData();
	}

	protected Listener<PaginationList<Book>> getListener() {
		return new Listener<PaginationList<Book>>() {
			@Override
			public void onPreExecute() {
				mLotNetworkUnavaliable.setVisibility(View.GONE);
			}

			@Override
			public void onNetworking() {
				mAdapter.setIsLoadingData(true);
			}

			@Override
			public void onFinish() {
				mAdapter.setIsLoadingData(false);
				shouldRestore = false;
			}

			@Override
			public void onSuccess(PaginationList<Book> bookList) {
				mHasNextPage = bookList.hasNextPage();
				mAdapter.addAll(bookList);
				mPageNum++;

				if (shouldRestore) mListView.setSelectionFromTop(index, top);
			}

			@Override
			public void onError(NetroidError error) {
				if (mAdapter.getItemCount() > 0) {
					getBaseActivity().showToastMsg(R.string.without_data);
				} else {
					mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
				}
			}
		};
	}

	protected abstract void loadData();

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

	@Override
	public void onSaveInstanceState(Bundle outState) {
		// solution by http://stackoverflow.com/a/16753664/1294681
		// NOTE : index and top values always are positive number
		int index = mListView.getFirstVisiblePosition();
		View child = mListView.getChildAt(0);
		int top = (child == null) ? 0 : child.getTop();

		child = mListView.getChildAt(1);
		if (top < 0 && child != null) {
			top = child.getTop();
			index++;
		}

		int pageNum = 0;
		while (index < pageNum++ * PAGE_SIZE || index > pageNum * PAGE_SIZE - 1);
		index -= (pageNum - 1) * PAGE_SIZE;
		pageNum += mHeadMissPageCount;

		outState.putInt(PAGE_NUM, pageNum);
		outState.putInt(INDEX, index);
		outState.putInt(TOP, top);

		Log.e("InstanceState", "index : " + index + " top : " + top + " pageNum : " + pageNum);
	}

	public static final String PAGE_NUM = "page_num";
	public static final String INDEX = "list_index";
	public static final String TOP = "list_top";
}
