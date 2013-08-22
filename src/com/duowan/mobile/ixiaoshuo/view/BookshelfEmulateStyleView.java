package com.duowan.mobile.ixiaoshuo.view;

public class BookshelfEmulateStyleView extends BookshelfBaseView {
	public BookshelfEmulateStyleView() {}
	public BookshelfEmulateStyleView(BookshelfBaseView bookshelfView) {
		super.build(bookshelfView);
	}

	private int itemPerLine = 3;

	@Override
	protected void initListView() {
//		mAdapter = new BaseAdapter() {
//			@Override
//			public int getCount() {
//				return mBookList != null ? mBookList.size() / itemPerLine + 1 : 0;
//			}
//
//			@Override
//			public int[] getItem(int position) {
//				int[] itemIndexPerLine = new int[itemPerLine];
//				for (int i = 0; i < itemPerLine; i++) {
//					itemIndexPerLine[i] = position * itemPerLine + i;
//					if (itemIndexPerLine[i] >= mBookList.size()) {
//						itemIndexPerLine[i] = -1;
//						break;
//					}
//				}
//				return itemIndexPerLine;
//			}
//
//			public int getAvaliableCount(int[] itemIndexPerLine) {
//				int avaliableCount = 1;
//				for (int i = 0; i < itemPerLine; i++) {
//					if (itemIndexPerLine[i] == -1) break;
//					avaliableCount++;
//				}
//				return avaliableCount;
//			}
//
//			@Override
//			public long getItemId(int position) {
//				return position;
//			}
//
//			@Override
//			public View getView(int position, View convertView, ViewGroup parent) {
//				GridView grvBookShelf;
//				if(convertView == null) {
//					convertView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf_emulate_list_item, null);
//					grvBookShelf = (GridView) convertView.findViewById(R.id.grvBookShelf);
//					grvBookShelf.setOnItemLongClickListener(BookshelfEmulateStyleView.this);
//					grvBookShelf.setOnItemClickListener(BookshelfEmulateStyleView.this);
//					grvBookShelf.setNumColumns(itemPerLine);
//					convertView.setTag(grvBookShelf);
//				} else {
//					grvBookShelf = (GridView) convertView.getTag();
//				}
//
//				final int[] itemIndexPerLine = getItem(position);
//				final int avaliableCount = getAvaliableCount(itemIndexPerLine);
//				grvBookShelf.setAdapter(new BaseAdapter() {
//					@Override
//					public int getCount() {
//						return avaliableCount;
//					}
//
//					@Override
//					public Book getItem(int position) {
//						int index = itemIndexPerLine[position];
//						return index == -1 ? null : mBookList.get(index);
//					}
//
//					@Override
//					public long getItemId(int position) {
//						return position;
//					}
//
//					@Override
//					public View getView(int position, View convertView, ViewGroup parent) {
//						// gridview单行单列，不需要滑动，所以只需要设置一次就可以了
//						if (convertView == null) {
//							convertView = mActivity.getLayoutInflater().inflate(R.layout.book_shelf_list_item, null);
//							ImageView imvBookCover = (ImageView) convertView.findViewById(R.id.imvBookCover);
//							TextView txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
//
//							Book book = getItem(position);
//							if (book == null) {
//								txvBookName.setText(null);
//								Bitmap coverBitmap = BitmapUtil.loadBitmapInRes(R.drawable.book_shelf_addbook, imvBookCover);
//								imvBookCover.setImageBitmap(coverBitmap);
//							} else {
//								setImageBitmap(book, imvBookCover);
//								txvBookName.setText(book.getName());
//							}
//						}
//						return convertView;
//					}
//				});
//
//				return convertView;
//			}
//		};
//		mLsvBookShelf.setAdapter(mAdapter);
//		mLsvBookShelf.setOnItemLongClickListener(null);
//		mLsvBookShelf.setOnItemClickListener(null);
	}

}
