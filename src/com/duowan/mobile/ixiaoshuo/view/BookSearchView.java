package com.duowan.mobile.ixiaoshuo.view;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;
import com.duowan.mobile.ixiaoshuo.utils.BookCoverDownloder;

import java.util.List;

public class BookSearchView extends ViewBuilder implements View.OnFocusChangeListener, View.OnClickListener {

	public BookSearchView(MainActivity activity) {
		this.mViewId = R.id.lotBookSearch;
		this.mActivity = activity;
	}

	EditText mEdtSearchKeyword;
	ToggleButton mBtnFinishType;
	ImageButton mBtnGoSearch;
	View mScvKeywords;
	ListView mLsvBookList;
	List<Book> mBookList;
	BaseAdapter mAdapter;

	@Override
	protected void build() {
		mView = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.book_search, null);
		mView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT));

		mEdtSearchKeyword = (EditText) findViewById(R.id.edtSearchKeyword);
		mEdtSearchKeyword.setOnFocusChangeListener(this);

		mBtnFinishType = (ToggleButton) findViewById(R.id.btnFinishType);
		mBtnFinishType.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mEdtSearchKeyword.requestFocus();
			}
		});

		mScvKeywords = findViewById(R.id.scvKeywords);
		mLsvBookList = (ListView) findViewById(R.id.lsvBookList);

		mBtnGoSearch = (ImageButton) findViewById(R.id.btnGoSearch);

		NetService.execute(new NetService.NetExecutor<String[]>() {
			ProgressDialog mPrgreDialog;
			public void preExecute() {
				if (NetService.get().isNetworkAvailable()) {
					mPrgreDialog = ProgressDialog.show(mActivity, null, mActivity.getString(R.string.loading_tip_msg));
				} else {
					mActivity.showToastMsg(R.string.network_disconnect_msg);
				}
			}

			public String[] execute() {
				return NetService.get().getHotKeyWords();
			}

			public void callback(String[] keywords) {
				if (mPrgreDialog != null) {
					if (!mPrgreDialog.isShowing()) return;
					mPrgreDialog.cancel();
				}

				if (keywords == null || keywords.length == 0) {
					mActivity.showToastMsg(R.string.without_data);
					return;
				}

				LinearLayout lotKeywordPanel = (LinearLayout) findViewById(R.id.lotKeywordPanel);
				for (String keyword : keywords) {
					Button btnKeyword = (Button) mActivity.getLayoutInflater().inflate(R.layout.book_search_keyword_item, null);
					btnKeyword.setText(keyword);
					lotKeywordPanel.addView(btnKeyword);
					btnKeyword.setOnClickListener(BookSearchView.this);
				}
			}
		});

		mBtnGoSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				final String keyword = mEdtSearchKeyword.getText().toString();
				final int updateStatus = mBtnFinishType.isChecked() ? Book.STATUS_CONTINUE : Book.STATUS_FINISHED;

				NetService.execute(new NetService.NetExecutor<List<Book>>() {
					ProgressDialog mPrgreDialog;
					public void preExecute() {
						if (NetService.get().isNetworkAvailable()) {
							mPrgreDialog = ProgressDialog.show(mActivity, null, mActivity.getString(R.string.loading_tip_msg), true, true);
						} else {
							mActivity.showToastMsg(R.string.network_disconnect_msg);
						}
					}

					public List<Book> execute() {
						return NetService.get().bookSearch(keyword, updateStatus, 1, 40);
					}

					public void callback(List<Book> bookList) {
						if (mPrgreDialog != null) {
							if (!mPrgreDialog.isShowing()) return;
							mPrgreDialog.cancel();
						}

						if (bookList == null || bookList.size() == 0) {
							mActivity.showToastMsg(R.string.without_data);
							return;
						}

						mScvKeywords.setVisibility(View.GONE);

						mBookList = bookList;
						mAdapter.notifyDataSetChanged();
						mLsvBookList.setVisibility(View.VISIBLE);
					}
				});
			}
		});

		mAdapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return mBookList == null ? 0 : mBookList.size();
			}

			@Override
			public Book getItem(int position) {
				return mBookList == null ? null : mBookList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				Holder holder;
				if (convertView == null) {
					convertView = mActivity.getLayoutInflater().inflate(R.layout.book_search_list_item, null);
					holder = new Holder();
					holder.txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
					holder.imvBookCover = (ImageView) convertView.findViewById(R.id.imvBookCover);
					holder.txvBookAuthor = (TextView) convertView.findViewById(R.id.txvBookAuthor);
					holder.txvBookSummary = (TextView) convertView.findViewById(R.id.txvBookSummary);
					convertView.setTag(holder);
				} else holder = (Holder) convertView.getTag();

				Book book = getItem(position);
				holder.txvBookName.setText(book.getName());
				holder.txvBookAuthor.setText("作者：" + book.getAuthor());
				holder.txvBookSummary.setText("书籍简介：" + book.getSummary());

				String bookCoverPath = book.getLocalCoverPath();
				Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(bookCoverPath, holder.imvBookCover);
				if (coverBitmap != null) {
					holder.imvBookCover.setImageBitmap(coverBitmap);
				} else {
					mActivity.getReaderApplication().executeTask(new BookCoverDownloder(book, holder.imvBookCover));

					coverBitmap = BitmapUtil.loadBitmapInRes(R.drawable.cover_less, holder.imvBookCover);
					holder.imvBookCover.setImageBitmap(coverBitmap);
				}

				return convertView;
			}

			class Holder {
				ImageView imvBookCover;
				TextView txvBookName;
				TextView txvBookAuthor;
				TextView txvBookSummary;
			}
		};

		mLsvBookList.setAdapter(mAdapter);
	}

	@Override
	public void onClick(View btnView) {
		mEdtSearchKeyword.setText(((Button) btnView).getText());
		mEdtSearchKeyword.requestFocus();
	}

	@Override
	public void onFocusChange(View view, boolean hasFocus) {
		View parentView = (View) view.getParent();
		parentView.setBackgroundResource(hasFocus ? R.drawable.book_search_bg_pressed : R.drawable.book_search_bg);
	}

}
