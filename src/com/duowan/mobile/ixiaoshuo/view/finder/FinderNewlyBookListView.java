package com.duowan.mobile.ixiaoshuo.view.finder;

import android.view.View;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;

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
		return NetService.get().getNewlyBookList(Book.TYPE_TEXT, ++mPageNo, PAGE_ITEM_COUNT);
	}

	@Override
	protected View getAdapterView(int position, View convertView) {
		Holder holder;
		if (convertView == null) {
			convertView = getActivity().getLayoutInflater().inflate(R.layout.finder_newly_book_list_item, null);
			holder = new Holder();
			holder.txvBookSummary = (TextView) convertView.findViewById(R.id.txvBookSummary);
			holder.txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		Book book = mAdapter.getItem(position);

		holder.txvBookName.setText(book.getName());
		holder.txvBookSummary.setText(book.getSummary());

		return convertView;
	}

	class Holder {
		TextView txvBookName;
		TextView txvBookSummary;
	}

}
