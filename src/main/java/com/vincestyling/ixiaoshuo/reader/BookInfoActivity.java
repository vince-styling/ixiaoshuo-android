package com.vincestyling.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.*;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.event.ChapterDownloader;
import com.vincestyling.ixiaoshuo.event.Notifier;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.ui.EllipseEndTextView;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import com.vincestyling.ixiaoshuo.utils.Paths;
import com.vincestyling.ixiaoshuo.utils.SysUtil;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;

import java.io.File;

public class BookInfoActivity extends BaseActivity implements View.OnClickListener,
		AbsListView.OnScrollListener, ChapterDownloader.OnDownLoadListener {

	private static final int STATUS_DOWNLOAD_ALL = 0;
	private static final int STATUS_DOWNLOAD_PAUSE = 1;
	private static final int STATUS_DOWNLOAD_GOON = 2;

	private static final int AM_NOTIFY_LIST_CHANGE = 0;

	private class Holder {
		TextView txvChapterTitle;
		Button btnChapterOperation;
	}

	private String mBookDirectoryPath;
	private int mBookId;
	private int mDownloadButtonStatus;
	private boolean mHasNextPage = true;
	private int mPageNo = 1;
	private int mTotalChapterCount;

	private Book mBook;
	private InnerHandler mHandler;

	private ListView mListMain;
	private ImageView mImageBookCover;
	private TextView mTextBookName;
	private TextView mTextBookStatus;
	private TextView mTextBookAuthor;
	private TextView mTextBookCategory;
	private TextView mTextBookCapacity;
	private Button mButtonAnotherType;
	private Button mButtonGotoRead;
	private Button mButtonDownload;

	private EllipseEndTextView mBookSummary;
	private View mLotBookSummary;
	private TextView mBtnSummaryExpand;

	private TextView mTxvUpdateTime;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_info);

		mBookId = getIntent().getIntExtra(Const.BOOK_ID, -1);
		if (mBookId == -1) {
			finish();
			return;
		}

		mHandler = new InnerHandler();
		mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBookId);

		mListMain = (ListView) findViewById(R.id.lsvBookInfoChapterList);
		initHeaderView();

		mImageBookCover = (ImageView) findViewById(R.id.imvBookCover);
		mTextBookName = (TextView) findViewById(R.id.txvBookName);
		mTextBookStatus = (TextView) findViewById(R.id.txvBookStatus);
		mTextBookAuthor = (TextView) findViewById(R.id.txvBookAuthor);
		mTextBookCategory = (TextView) findViewById(R.id.txvBookCategory);
		mTextBookCapacity = (TextView) findViewById(R.id.txvBookCapacity);
		mButtonAnotherType = (Button) findViewById(R.id.imvBookAnotherType);
		mButtonGotoRead = (Button) findViewById(R.id.btnGotoRead);
		mButtonDownload = (Button) findViewById(R.id.btnDownloadAll);
		mTxvUpdateTime = (TextView) findViewById(R.id.txvUpdateTime);

		mBookSummary = (EllipseEndTextView) findViewById(R.id.txvBookSummary);
		mBtnSummaryExpand = (TextView) findViewById(R.id.btnSummaryExpand);
		mLotBookSummary = findViewById(R.id.lotBookSummary);

		mButtonGotoRead.setOnClickListener(this);
		mButtonDownload.setOnClickListener(this);

		requestBookInfo();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
	}

	@Override
	public void onClick(View view) {
		if (view.equals(mButtonGotoRead)) {
			processReadBook();
		}
		else if (view.equals(mButtonDownload)) {
			processDownloadAll();
		}
		else if (view.equals(mLotBookSummary)) {
			mBookSummary.setOnMeasureDoneListener(new EllipseEndTextView.OnMeasureDoneListener() {
				@Override
				public void onMeasureDone(View v) {
					int resId = mBookSummary.isExpanded() ? R.string.book_detail_summary_collapse : R.string.book_detail_summary_expand;
					mBtnSummaryExpand.setText(resId);
					resId = mBookSummary.isExpanded() ? R.drawable.book_detail_arrow_up : R.drawable.book_detail_arrow_down;
					mBtnSummaryExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
				}
			});
			mBookSummary.elipseSwitch();
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		mListMain.setVerticalScrollBarEnabled(firstVisibleItem > 0);
		if (mAdapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
			loadNextPage();
		}
	}

	@Override
	public void onChapterComplete(int bookId, int chapterId) {
		mHandler.sendEmptyMessage(AM_NOTIFY_LIST_CHANGE);
	}

	@Override
	public void onDownloadComplete(int bookId) {
		updateDownloadStatus();
	}

	private void initHeaderView() {
		View mHeadView = getLayoutInflater().inflate(R.layout.book_info_head, null);
		Display display = getWindowManager().getDefaultDisplay();
		mHeadView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
		mListMain.addHeaderView(mHeadView);
	}

	private void updateDownloadStatus() {
		if (ChapterDownloader.get().taskIsStarted(mBookId)) {
			mDownloadButtonStatus = STATUS_DOWNLOAD_PAUSE;

		} else {
			File file = new File(mBookDirectoryPath);
			if (file.exists()) {
				String[] files = file.list();
				if (files.length == 0) {
					// 书籍完全没有下载
					mDownloadButtonStatus = STATUS_DOWNLOAD_ALL;

				} else if (files.length < mTotalChapterCount) {
					// 书籍下载到一半
					mDownloadButtonStatus = STATUS_DOWNLOAD_GOON;

				}
			} else {
				// 书籍文件夹不存在
				mDownloadButtonStatus = STATUS_DOWNLOAD_ALL;
			}
		}
	}

	private void updateDownloadButtonStatus() {
		Resources resources = getResources();
		switch (mDownloadButtonStatus) {
			case STATUS_DOWNLOAD_ALL:
				mButtonDownload.setText(resources.getString(R.string.download_all));
				break;

			case STATUS_DOWNLOAD_PAUSE:
				mButtonDownload.setText(resources.getString(R.string.download_pause));
				break;

			case STATUS_DOWNLOAD_GOON:
				mButtonDownload.setText(resources.getString(R.string.download_goon));
				break;

			default:
				break;
		}
	}

	private void requestBookInfo() {
		Netroid.getBookDetail(mBookId, new Listener<Book>() {
			ProgressDialog mProgressDialog;

			@Override
			public void onPreExecute() {
				mProgressDialog = ProgressDialog.show(BookInfoActivity.this, null, getString(R.string.loading_tip_msg), false, true);
			}

			@Override
			public void onFinish() {
				mProgressDialog.cancel();
			}

			@Override
			public void onSuccess(Book book) {
				mBook = book;
				updateUIWithBookInfo();
			}

			@Override
			public void onError(NetroidError error) {
				getReaderApplication().showToastMsg(R.string.without_data);
			}
		});
	}

	private void updateUIWithBookInfo() {
		Netroid.displayImage(mBook.getCoverUrl(), mImageBookCover, 0, 0);

		mTextBookName.setText(mBook.getName());
		mTextBookStatus.setText(mBook.getUpdateStatusStr());

		String author = getString(R.string.book_author) + mBook.getAuthor();
		mTextBookAuthor.setText(author);

		String category = getString(R.string.book_category) + mBook.getCatName();
		mTextBookCategory.setText(category);

		String capacity = getString(R.string.book_capacity) + mBook.getCapacityStr();
		mTextBookCapacity.setText(capacity);

		mBookSummary.setOnMeasureDoneListener(new EllipseEndTextView.OnMeasureDoneListener() {
			@Override
			public void onMeasureDone(View v) {
				if (mBookSummary.isExpanded()) {
					mBtnSummaryExpand.setVisibility(View.GONE);
				} else {
					mLotBookSummary.setOnClickListener(BookInfoActivity.this);
				}
				mBookSummary.setOnMeasureDoneListener(null);
			}
		});
		mBookSummary.setText(String.format(getString(R.string.book_detail_summary_prefix), mBook.getSummary()));

		mTxvUpdateTime.setText(mBook.getLastUpdateTime());

		if (mBook.isBothType()) {
			mButtonAnotherType.setVisibility(View.VISIBLE);
		}

		mListMain.setAdapter(mAdapter);
		mListMain.setOnScrollListener(this);
		loadNextPage();
	}

	private void loadNextPage() {
		if (!mHasNextPage) return;

		Netroid.getBookChapterList(mBook.getBookId(), mPageNo, new Listener<PaginationList<Chapter>>() {
			@Override
			public void onPreExecute() {
				mAdapter.setIsLoadingData(true);
			}

			@Override
			public void onFinish() {
				mAdapter.setIsLoadingData(false);
			}

			@Override
			public void onSuccess(PaginationList<Chapter> chapterList) {
				mTotalChapterCount = chapterList.getTotalItemCount();
				mHasNextPage = chapterList.hasNextPage();
				mAdapter.addLast(chapterList);
				mPageNo++;

				updateDownloadStatus();
				updateDownloadButtonStatus();
				mButtonDownload.setVisibility(View.VISIBLE);
			}

			@Override
			public void onError(NetroidError error) {
				showToastMsg(R.string.without_data);
			}
		});
	}

	private void processReadBook() {
		if (!mHasNextPage) {
			gotoReader();
			return;
		}

		Netroid.getBookChapterList(mBook.getBookId(), mPageNo, new Listener<PaginationList<Chapter>>() {
			ProgressDialog mProgressDialog;

			@Override
			public void onPreExecute() {
				mProgressDialog = ProgressDialog.show(BookInfoActivity.this, null, getString(R.string.loading_all_chapters_msg), false, true);
			}

			@Override
			public void onFinish() {
				mProgressDialog.cancel();
			}

			@Override
			public void onSuccess(PaginationList<Chapter> chapterList) {
				mTotalChapterCount = chapterList.getTotalItemCount();
				mHasNextPage = chapterList.hasNextPage();
				mAdapter.addLast(chapterList);
				mPageNo++;

				boolean isShowing = mProgressDialog.isShowing();
				if (isShowing) processReadBook();
			}
		});
	}

	private void downloadAllChapter() {
		if (ChapterDownloader.get().schedule(BookInfoActivity.this, mBook, true, BookInfoActivity.this)) {
			mDownloadButtonStatus = STATUS_DOWNLOAD_PAUSE;
			updateDownloadButtonStatus();

			int bid = AppDAO.get().addBook(mBook, false);
			if (bid > 0) {
				AppDAO.get().saveBookChapters(bid, mAdapter.getData());
				getReaderApplication().getMainHandler().sendMessage(Notifier.NOTIFIER_BOOKSHELF_REFRESH);

				String format = getString(R.string.added_to_bookshelf);
				String prompt = String.format(format, mBook.getName());
				showToastMsg(prompt);
			}
		} else {
			showToastMsg(R.string.chapter_downloader_limit_msg);
		}
	}

	private void processDownloadAll() {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(R.string.network_download_prompt);

		switch (mDownloadButtonStatus) {
			case STATUS_DOWNLOAD_ALL:
				if (SysUtil.isMobileDataConnected(this)) {
					builder.setNegativeButton(R.string.goon, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
							downloadAllChapter();
						}
					});
					builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					builder.show();

				} else {
					downloadAllChapter();
				}
				break;

			case STATUS_DOWNLOAD_PAUSE:
				ChapterDownloader.get().cancel(mBookId);
				mDownloadButtonStatus = STATUS_DOWNLOAD_GOON;
				updateDownloadButtonStatus();
				break;

			case STATUS_DOWNLOAD_GOON:
				if (SysUtil.isMobileDataConnected(this)) {
					builder.setNegativeButton(R.string.goon, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							mDownloadButtonStatus = STATUS_DOWNLOAD_PAUSE;
							updateDownloadButtonStatus();
							dialog.cancel();

							if (ChapterDownloader.get().schedule(BookInfoActivity.this, mBook, true, BookInfoActivity.this)) {
								mDownloadButtonStatus = STATUS_DOWNLOAD_PAUSE;
								updateDownloadButtonStatus();

							} else {
								showToastMsg(R.string.chapter_downloader_limit_msg);
							}
						}
					});
					builder.setPositiveButton(R.string.cancel, new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							dialog.cancel();
						}
					});
					builder.show();

				} else {
					if (ChapterDownloader.get().schedule(BookInfoActivity.this, mBook, true, BookInfoActivity.this)) {
						mDownloadButtonStatus = STATUS_DOWNLOAD_PAUSE;
						updateDownloadButtonStatus();

					} else {
						showToastMsg(R.string.chapter_downloader_limit_msg);
					}
				}
				break;

			default:
				break;
		}
	}

	private void gotoReader() {
		if (!mAdapter.hasItems()) {
			showToastMsg("无可读章节！");
			return;
		}

		int bookId = AppDAO.get().addBook(mBook, true);
		if (bookId > 0) {
			AppDAO.get().saveBookChapters(bookId, mAdapter.getData());
			Intent intent = new Intent(this, ReaderActivity.class);
			intent.putExtra(Const.BOOK_ID, bookId);
			startActivity(intent);
		} else {
			showToastMsg("抱歉，无法添加书籍！");
		}
	}

	private EndlessListAdapter<Chapter> mAdapter = new EndlessListAdapter<Chapter>() {
		@Override
		protected View getView(int position, View convertView) {
			Holder holder;
			if (convertView == null) {
				convertView = getLayoutInflater().inflate(R.layout.book_info_chapter_list_item, null);
				holder = new Holder();
				holder.txvChapterTitle = (TextView) convertView.findViewById(R.id.txvChapterTitle);
				holder.btnChapterOperation = (Button) convertView.findViewById(R.id.btnChapterOperation);
				convertView.setTag(holder);
				convertView.setLayoutParams(new AbsListView.LayoutParams(
						mListMain.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
			} else {
				holder = (Holder) convertView.getTag();
			}

			final Chapter chapter = getItem(position);
			holder.txvChapterTitle.setText(chapter.getTitle());
			View.OnClickListener clickListener = new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					onItemClick(chapter);
				}
			};
			holder.txvChapterTitle.setOnClickListener(clickListener);

			File chapterFile = new File(mBookDirectoryPath + chapter.getChapterId());
			if (chapterFile.exists()) {
				holder.btnChapterOperation.setBackgroundResource(R.drawable.book_info_chapter_btn_goto_read_selector);
				holder.btnChapterOperation.setOnClickListener(clickListener);
			} else {
				holder.btnChapterOperation.setBackgroundResource(R.drawable.book_info_chapter_btn_download_selector);
				holder.btnChapterOperation.setOnClickListener(new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						Netroid.downloadChapterContent(mBook.getBookId(), chapter.getChapterId(), new Listener<Void>() {
							@Override
							public void onPreExecute() {
								showToastMsg("正在下载：" + chapter.getTitle());
							}

							@Override
							public void onSuccess(Void r) {
								mAdapter.notifyDataSetChanged();
							}
						});
					}
				});

			}

			return convertView;
		}

		@Override
		protected View initProgressView() {
			View progressView = getLayoutInflater().inflate(R.layout.contents_loading, null);
			Display display = getWindowManager().getDefaultDisplay();
			progressView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
			return progressView;
		}
	};

	public void onItemClick(Chapter chapter) {
		for (Chapter chapt : mAdapter.getData()) {
			chapt.setReadStatus(Chapter.READSTATUS_UNREAD);
			chapt.setReadPosition(0);
		}
		chapter.setReadStatus(Chapter.READSTATUS_READING);
		processReadBook();
	}

	private class InnerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
				case AM_NOTIFY_LIST_CHANGE:
					mAdapter.notifyDataSetChanged();
					break;

				default:
					break;
			}
		}
	}
}
