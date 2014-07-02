package com.vincestyling.ixiaoshuo.view.finder;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.ui.PullToLoadPage;
import com.vincestyling.ixiaoshuo.ui.PullToLoadPageListView;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.PaginationAdapter;

public abstract class FinderBaseListView extends BaseFragment implements
		OnItemClickListener, PullToLoadPageListView.OnLoadingPageListener {

	protected PaginationAdapter<Book> mAdapter;
	private View mLotNetworkUnavaliable;
	private PullToLoadPageListView mListView;

	private static final int PAGE_SIZE = 20;
	private boolean mHasNextPage = true;
	private int mStartPageNum = 1;
	private int mEndPageNum;

	private int index, top, additionalPage;
	private boolean shouldRestore;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mLotNetworkUnavaliable = getActivity().findViewById(R.id.lotFinderNetworkUnavaliable);
		mListView = (PullToLoadPageListView) getActivity().getLayoutInflater().inflate(R.layout.finder_book_listview, null);
		return mListView;
	}

	@Override
	public void onViewCreated(View view, Bundle savedInstanceState) {
		if (savedInstanceState != null) {
			mHasNextPage = savedInstanceState.getBoolean(HAS_NEXT_PAGE, true);
			additionalPage = savedInstanceState.getInt(ADDITIONAL_PAGE, 0);
			mStartPageNum = savedInstanceState.getInt(PAGE_NUM, 1);
			mEndPageNum = mStartPageNum - 1;

			index = savedInstanceState.getInt(INDEX, -1);
			top = savedInstanceState.getInt(TOP, -1);

			shouldRestore = index >= 0 && top >= 0;
		}

		if (mStartPageNum > 1) {
			View rootView = getActivity().getLayoutInflater().inflate(R.layout.finder_list_pull_to_load, null);
			mListView.setPullToLoadPrevPageView(new PullToLoadPage(R.string.pull_to_load_prev_page, rootView) {
				@Override
				public int takePageNum() {
					return mStartPageNum;
				}
			});
		}

		if (mHasNextPage) {
			View rootView = getActivity().getLayoutInflater().inflate(R.layout.finder_list_pull_to_load, null);
			mListView.setPullToLoadNextPageView(new PullToLoadPage(R.string.pull_to_load_next_page, rootView) {
				@Override
				public int takePageNum() {
					return mEndPageNum;
				}
			});
		}

		mListView.setOnLoadingPageListener(this);

		mAdapter = new PaginationAdapter<Book>(PAGE_SIZE) {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
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
					// TODO : test when reach last page then scroll back situation.
					int posDiffer = mAdapter.getItemCount() - position;
					holder.lotDivider.setVisibility(posDiffer == 1 ? View.GONE : View.VISIBLE);
				}

				return convertView;
			}
		};

		mListView.setAdapter(mAdapter);
		mListView.setOnItemClickListener(this);
	}

	protected abstract void setBookTips(TextView txvBookTips, Book book);

	@Override
	public void onResume() {
		if (mAdapter.getItemCount() == 0) mListView.triggerLoadNextPage();
		mLotNetworkUnavaliable.setVisibility(View.GONE);
		super.onResume();
	}

	private Listener<PaginationList<Book>> getListener(final boolean isLoadNextPage) {
		return new Listener<PaginationList<Book>>() {
			@Override
			public void onPreExecute() {
				mLotNetworkUnavaliable.setVisibility(View.GONE);
			}

			@Override
			public void onFinish() {
				if (isLoadNextPage) {
					mListView.finishLoadNextPage();
				} else {
					mListView.finishLoadPrevPage();
				}

				if (additionalPage > 0) {
					additionalPage = 0;
					mListView.triggerLoadNextPage();
				} else if (additionalPage < 0) {
					additionalPage = 0;
					mListView.triggerLoadPrevPage();
				} else if (shouldRestore) {
					new Handler().postDelayed(new Runnable() {
						@Override
						public void run() {
							mListView.setSelectionFromTop(index, top);
							shouldRestore = false;
						}
					}, 50);
				}
			}

			@Override
			public void onSuccess(PaginationList<Book> bookList) {
				if (isLoadNextPage) {
					mHasNextPage = bookList.hasNextPage();
					mAdapter.addLast(bookList);
				} else {
					mAdapter.addFirst(bookList);
				}
			}

			@Override
			public void onError(NetroidError error) {
				if (mAdapter.getItemCount() > 0) {
					getBaseActivity().showToastMsg(R.string.without_data);
				} else {
					mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
				}

				shouldRestore = false;
				additionalPage = 0;

				if (isLoadNextPage) {
					mEndPageNum--;
				} else {
					mStartPageNum++;
				}
			}
		};
	}

	protected abstract void loadData(int pageNum, Listener<PaginationList<Book>> listener);

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
	public boolean onLoadNextPage() { // don't call this method directly
		if (mHasNextPage) {
			loadData(++mEndPageNum, getListener(true));
			return true;
		}
		return false;
	}

	@Override
	public boolean hasNextPage() {
		return mHasNextPage;
	}

	@Override
	public boolean onLoadPrevPage() { // don't call this method directly
		if (mStartPageNum > 1) {
			loadData(--mStartPageNum, getListener(false));
			return true;
		}
		return false;
	}

	@Override
	public boolean hasPrevPage() {
		return mStartPageNum > 1;
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
		// index and top calculation from http://stackoverflow.com/a/16753664/1294681
		int upFillItemCount = 0;
		int additionalPage = 0;
		int indexOfPage = 0;

		// NOTE : index and top values always are positive number.
		int index = mListView.getFirstVisiblePosition();
		View child = mListView.getChildAt(0);
		int top = (child == null) ? 0 : child.getTop();

		child = mListView.getChildAt(1);
		if (top < 0 && child != null) {
			top = child.getTop();
			upFillItemCount++;
			indexOfPage++;
			index++;
		}

		// Decrease when index calculation included the header.
		if (mStartPageNum > 1 && index > 0) index--;


		// Calculate which page was index on.
		int pageNum = 0;
		while (index < pageNum++ * PAGE_SIZE || index > pageNum * PAGE_SIZE - 1);

		// Index relative to current page.
		index -= (pageNum - 1) * PAGE_SIZE;
		pageNum += mStartPageNum - 1;

		// Calculate how much items in the up and bottom of current position enough to fill ListView.
		int visibleChildCount = mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition() + 1;
		upFillItemCount = index == 0 && upFillItemCount == 1 ? 1 : 0;
		int downFillItemCount = visibleChildCount - indexOfPage - 1;


		// If the last child view wasn't footer, we determine if current page
		// have enough items to fill remaning gap after current index.
		child = mListView.getChildAt(mListView.getChildCount() - 1);
		if (child.findViewById(R.id.txvBookName) != null) {
			if (index + downFillItemCount >= PAGE_SIZE) {
				additionalPage = 1;
			}
		}


		// index between two pages, need previous page.
		if (index - upFillItemCount < 0) {
			additionalPage = -1;
			index += PAGE_SIZE;
		}


		int restoredStartPageNum = Math.min(pageNum, pageNum + additionalPage);
		// If pages include the header while next restoring state.
		if (restoredStartPageNum > 1) index++;


		outState.putBoolean(HAS_NEXT_PAGE, mHasNextPage);
		outState.putInt(ADDITIONAL_PAGE, additionalPage);
		outState.putInt(PAGE_NUM, pageNum);
		outState.putInt(INDEX, index);
		outState.putInt(TOP, top);

		AppLog.e("top : " + top + " index : " + index + " pageNum : " + pageNum + " additional : " + additionalPage + " up : " + upFillItemCount + " down : " + downFillItemCount);
	}

	public static final String ADDITIONAL_PAGE = "additional_page";
	public static final String HAS_NEXT_PAGE = "has_next_page";
	public static final String PAGE_NUM = "page_num";
	public static final String INDEX = "list_index";
	public static final String TOP = "list_top";
}
