package com.duowan.mobile.ixiaoshuo.view;

import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.BookshelfActivity;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;
import com.duowan.mobile.ixiaoshuo.utils.Paths;

import java.util.List;

public class BookshelfEmulateView {
	private int itemPerLine = 3;
	private List<Book> mBookList;
	private ListView mLsvBookShelf;
	private BookshelfActivity mActivity;

	public BookshelfEmulateView(BookshelfActivity activity, View lsvBookShelf) {
		this.mActivity = activity;
		this.mLsvBookShelf = (ListView) lsvBookShelf;
	}

	public void initBookShelf(List<Book> bookList) {
		this.mBookList = bookList;
		BaseAdapter bookShelfAdapter = new BaseAdapter() {
			private Integer[] itemIndexPerLine = new Integer[itemPerLine];
			private int avaliableCount;
			@Override
			public int getCount() {
				return mBookList.size() / itemPerLine + 1;
			}

			@Override
			public Integer[] getItem(int position) {
				avaliableCount = 0;
				itemIndexPerLine[0] = position * itemPerLine;
				itemIndexPerLine[1] = itemIndexPerLine[0] + 1;
				itemIndexPerLine[2] = itemIndexPerLine[1] + 1;
				for (int i = 0; i < itemIndexPerLine.length; i++) {
					avaliableCount++;
					if (itemIndexPerLine[i] >= mBookList.size()) {
						itemIndexPerLine[i] = -1;
						break;
					}
				}
				return itemIndexPerLine;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				GridView grvBookShelf;
				if(convertView == null) {
					convertView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf_emulate_list_item, null);
					grvBookShelf = (GridView) convertView.findViewById(R.id.grvBookShelf);
					grvBookShelf.setNumColumns(itemPerLine);
					convertView.setTag(grvBookShelf);
				} else {
					grvBookShelf = (GridView) convertView.getTag();
				}

				grvBookShelf.setAdapter(new ArrayAdapter<Integer>(mActivity, 0, getItem(position)) {
					@Override
					public int getCount() {
						return avaliableCount;
					}

					@Override
					public View getView(int position, View convertView, ViewGroup parent) {
						// gridview单行单列，不需要滑动，所以只需要设置一次就可以了
						if (convertView == null) {
							convertView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf_emulate_list_book_item, null);
							ScalableImageView imvBookCover = (ScalableImageView) convertView.findViewById(R.id.imvBookCover);
							TextView txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);

							int index = getItem(position);
							if (index == -1) {
								txvBookName.setText(null);
								Bitmap coverBitmap = BitmapUtil.loadBitmapInRes(R.drawable.book_shelf_addbook, imvBookCover);
								imvBookCover.setImageBitmap(coverBitmap);
							} else {
								Book book = mBookList.get(index);
								txvBookName.setText(book.getName());

								Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(Paths.getCoversDirectoryPath() + book.getCoverFileName(), imvBookCover);
								imvBookCover.setImageBitmap(coverBitmap);
							}
						}
						return convertView;
					}
				});

				return convertView;
			}
		};
		mLsvBookShelf.setAdapter(bookShelfAdapter);
	}

}
