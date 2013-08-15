package com.duowan.mobile.ixiaoshuo.view;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.BookCoverDownloder;

import java.util.List;

public class BookSearchView extends ViewBuilder implements View.OnFocusChangeListener, View.OnClickListener,
		AbsListView.OnScrollListener, AdapterView.OnItemClickListener {

	public BookSearchView(MainActivity activity) {
		this.mViewId = R.id.lotBookSearch;
		this.mActivity = activity;
	}

	private int mPageNo = 1;
	private final static int PAGE_SIZE = 40;

	String mKeyword;
	int mUpdateStatus;

	EditText mEdtSearchKeyword;
	ToggleButton mBtnFinishType;
	ImageButton mBtnGoSearch;
	View mScvKeywords;
	ListView mLsvBookList;
	EndlessListAdapter<Book> mAdapter;

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

		if (NetService.get().isNetworkAvailable()) {
			NetService.execute(new NetService.NetExecutor<String[]>() {
				ProgressDialog mPrgreDialog;
				public void preExecute() {
					mPrgreDialog = ProgressDialog.show(mActivity, null, mActivity.getString(R.string.loading_tip_msg), false, true);
					mPrgreDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							NetService.get().abortLast();
						}
					});
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
		} else mActivity.showToastMsg(R.string.network_disconnect_msg);

		mBtnGoSearch.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View view) {
				mPageNo = 1;
				mKeyword = mEdtSearchKeyword.getText().toString();
				mUpdateStatus = mBtnFinishType.isChecked() ? Book.STATUS_CONTINUE : Book.STATUS_FINISHED;

				if (NetService.get().isNetworkAvailable()) {
					NetService.execute(new NetService.NetExecutor<List<Book>>() {
						ProgressDialog mPrgreDialog;
						public void preExecute() {
							mPrgreDialog = ProgressDialog.show(mActivity, null, mActivity.getString(R.string.loading_tip_msg), false, true);
							mPrgreDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
								@Override
								public void onCancel(DialogInterface dialog) {
									NetService.get().abortLast();
								}
							});
						}

						public List<Book> execute() {
							return NetService.get().bookSearch(mKeyword, mUpdateStatus, mPageNo, PAGE_SIZE);
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

							mAdapter.setData(bookList);
							mLsvBookList.setVisibility(View.VISIBLE);
							mScvKeywords.setVisibility(View.GONE);
						}
					});
				} else mActivity.showToastMsg(R.string.network_disconnect_msg);
			}
		});

		mAdapter = new EndlessListAdapter<Book>(mActivity, mLsvBookList, R.layout.contents_loading) {
			@Override
			public View doGetView(int position, View convertView, ViewGroup parent) {
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
				BookCoverDownloder.loadCover(mActivity, book, holder.imvBookCover);

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
		mLsvBookList.setOnScrollListener(this);
		mLsvBookList.setOnItemClickListener(this);
	}

	private void loadNextPage() {
		mAdapter.setIsLoadingData(true);
		NetService.execute(new NetService.NetExecutor<List<Book>>() {
			public void preExecute() {
				NetService.get().abortAll();
			}

			public List<Book> execute() {
				return NetService.get().bookSearch(mKeyword, mUpdateStatus, ++mPageNo, PAGE_SIZE);
			}

			public void callback(List<Book> bookList) {
				mAdapter.setIsLoadingData(false);
				if (bookList == null || bookList.size() == 0) {
					mActivity.showToastMsg(R.string.without_data);
					return;
				}
				mAdapter.addAll(bookList);
			}
		});
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		final Book book = (Book) parent.getItemAtPosition(position);
		if (NetService.get().isNetworkAvailable()) {
			NetService.execute(new NetService.NetExecutor<Book>() {
				ProgressDialog mPrgreDialog;

				public void preExecute() {
					mPrgreDialog = ProgressDialog.show(mActivity, null, mActivity.getString(R.string.loading_tip_msg), false, true);
					mPrgreDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
						@Override
						public void onCancel(DialogInterface dialog) {
							NetService.get().abortLast();
						}
					});
				}

				public Book execute() {
					return NetService.get().getBookDetail(book.getBookId());
				}

				public void callback(Book book) {
					if (mPrgreDialog != null) {
						if (!mPrgreDialog.isShowing()) return;
						mPrgreDialog.cancel();
					}

					if (book == null) {
						mActivity.showToastMsg(R.string.without_data);
						return;
					}

					mActivity.showView(new BookInfoView(mActivity, book));
				}
			});
		} else {
			mActivity.showToastMsg(R.string.network_disconnect_msg);
		}
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

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		if (scrollState == AbsListView.OnScrollListener.SCROLL_STATE_IDLE) {
			mActivity.startTasksExecute();
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mActivity.suspendTaskExecutor();
		if (mAdapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
			loadNextPage();
		}
	}

}
