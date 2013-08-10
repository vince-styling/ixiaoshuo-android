package com.duowan.mobile.ixiaoshuo.view;

import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.pojo.Book;

public class BookshelfListStyleView extends BookshelfBaseView {
	public BookshelfListStyleView() {}
	public BookshelfListStyleView(BookshelfBaseView bookshelfView) {
		super.build(bookshelfView);
	}

	@Override
	protected void initListView() {
		mAdapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return mBookList != null ? mBookList.size() : 0;
			}

			@Override
			public Book getItem(int position) {
				return mBookList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Holder holder;
				if (convertView == null) {
					convertView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf_list_item, null);
					holder = new Holder();
					holder.imvBookCover = (ImageView) convertView.findViewById(R.id.imvBookCover);
					holder.txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
					holder.txvBookAuthor = (TextView) convertView.findViewById(R.id.txvBookAuthor);
					holder.txvReadProgress = (TextView) convertView.findViewById(R.id.txvReadProgress);
					holder.txvRemainChapters = (TextView) convertView.findViewById(R.id.txvRemainChapters);
					holder.txvNewlyChapter = (TextView) convertView.findViewById(R.id.txvNewlyChapter);
					holder.txvBookStatus = (Button) convertView.findViewById(R.id.txvBookStatus);
					convertView.setTag(holder);
				} else {
					holder = (Holder) convertView.getTag();
				}

				Book book = getItem(position);
				setImageBitmap(book, holder.imvBookCover);

				holder.txvBookName.setText(book.getName());
				View view = (View) holder.txvBookAuthor.getParent();
				Display display = mActivity.getWindowManager().getDefaultDisplay();
				view.measure(display.getWidth(), display.getHeight());
				holder.txvBookName.setMaxWidth((int) (view.getMeasuredWidth() * 0.9f));

				holder.txvBookAuthor.setText(book.getAuthor() + " 著");
				holder.txvReadProgress.setText("已读80%");
				holder.txvRemainChapters.setText("128章节未读");
				holder.txvNewlyChapter.setText("更新至：第二百二十七章 浮棺里的活死人");
				holder.txvBookStatus.setText(book.getUpdateStatusStr());
				holder.txvBookStatus.setBackgroundResource(R.drawable.blue_cricle_flag);

				return convertView;
			}

			class Holder {
				ImageView imvBookCover;
				TextView txvBookName;
				TextView txvBookAuthor;
				TextView txvReadProgress;
				TextView txvRemainChapters;
				TextView txvNewlyChapter;
				Button txvBookStatus;
			}
		};
		mLsvBookShelf.setAdapter(mAdapter);
		mLsvBookShelf.setOnItemLongClickListener(this);
		mLsvBookShelf.setOnItemClickListener(this);
	}

}
