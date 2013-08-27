package com.duowan.mobile.ixiaoshuo.view;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.event.BookCoverLoader;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public class BookshelfVoiceListView extends BookshelfBaseListView {

	public BookshelfVoiceListView(MainActivity activity, OnShowListener onShowListener) {
		super(activity, R.id.lsvBookShelfVoice);
		mShowListener = onShowListener;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf_voice_listview, null);
	}

	@Override
	protected View getAdapterView(int position, View convertView) {
		Holder holder;
		if (convertView == null) {
			convertView = getActivity().getLayoutInflater().inflate(R.layout.book_shelf_list_item, null);
			holder = new Holder();
			holder.imvBookCover = (ImageView) convertView.findViewById(R.id.imvBookCover);
			holder.txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		Book book = mBookList.get(position);
		BookCoverLoader.loadCover(getActivity(), book, holder.imvBookCover);

		holder.txvBookName.setText(book.getName());

		return convertView;
	}

	class Holder {
		ImageView imvBookCover;
		TextView txvBookName;
	}

}
