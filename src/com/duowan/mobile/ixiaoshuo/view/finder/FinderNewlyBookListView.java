package com.duowan.mobile.ixiaoshuo.view.finder;

import android.view.View;
import android.widget.AbsListView;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

public class FinderNewlyBookListView extends FinderBaseListView {

	public FinderNewlyBookListView(MainActivity activity, OnShowListener onShowListener) {
		super(activity, R.id.lsvFinderNewlyBooks);
		mShowListener = onShowListener;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.finder_newly_book_listview, null);
	}

	@Override
	protected PaginationList<Book> loadData() {
		return NetService.get().getNewlyBookList(Book.TYPE_TEXT, mPageNo, PAGE_ITEM_COUNT);
	}

	@Override
	protected View getAdapterView(int position, View convertView) {
		Holder holder;
		if (convertView == null) {
			convertView = getActivity().getLayoutInflater().inflate(R.layout.finder_newly_book_list_item, null);

			holder = new Holder();
			holder.lotDivider = convertView.findViewById(R.id.lotDivider);

			holder.txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
			holder.txvBookSummary = (TextView) convertView.findViewById(R.id.txvBookSummary);

			holder.txvBookStatus1 = (TextView) convertView.findViewById(R.id.txvBookStatus1);
			holder.txvBookStatus2 = (TextView) convertView.findViewById(R.id.txvBookStatus2);

			holder.txvBookTips = (TextView) convertView.findViewById(R.id.txvBookTips);
			holder.txvBookCapacity = (TextView) convertView.findViewById(R.id.txvBookCapacity);

			convertView.setTag(holder);

			convertView.setLayoutParams(new AbsListView.LayoutParams(
					getListView().getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
		} else {
			holder = (Holder) convertView.getTag();
		}

		Book book = mAdapter.getItem(position);

		holder.txvBookStatus1.setText(book.getUpdateStatusSimpleStr());
		holder.txvBookStatus2.setVisibility(book.isBothType() ? View.VISIBLE : View.GONE);

		holder.txvBookName.setText(book.getName());
		holder.txvBookSummary.setText(book.getSummary());
		holder.txvBookCapacity.setText(book.getCapacityStr());

		holder.txvBookTips.setText(StringUtil.getDiffWithNow(book.getLastUpdateTime()));

		if (!mHasNextPage) {
			int posDiffer = mAdapter.getItemCount() - position;
			holder.lotDivider.setVisibility(posDiffer == 1 ? View.GONE : View.VISIBLE);
		}

		return convertView;
	}

	class Holder {
		TextView txvBookName;
		TextView txvBookSummary;
		TextView txvBookStatus1, txvBookStatus2;
		TextView txvBookCapacity;
		TextView txvBookTips;
		View lotDivider;
	}

}
