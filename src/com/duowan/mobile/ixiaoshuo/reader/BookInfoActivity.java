package com.duowan.mobile.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.Display;
import android.view.View;
import android.widget.*;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.event.BookCoverLoader;
import com.duowan.mobile.ixiaoshuo.event.ChapterDownloader;
import com.duowan.mobile.ixiaoshuo.event.Notifier;
import com.duowan.mobile.ixiaoshuo.net.BaseNetService;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.pojo.Constants;
import com.duowan.mobile.ixiaoshuo.service.ReaderService;
import com.duowan.mobile.ixiaoshuo.ui.EllipseEndTextView;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.utils.Paths;
import com.duowan.mobile.ixiaoshuo.utils.SysUtil;
import com.duowan.mobile.ixiaoshuo.view.EndlessListAdapter;

import java.io.File;
import java.util.List;

public class BookInfoActivity extends BaseActivity implements View.OnClickListener,
        AbsListView.OnScrollListener, ChapterDownloader.OnDownLoadListener {
    private final static int PAGE_ITEM_COUNT = 40;
    private static final String ORDER_ASC = "asc";
    private static final String ORDER_DESC = "desc";

    private static final int STATUS_DOWNLOAD_ALL = 0;
    private static final int STATUS_DOWNLOAD_PAUSE = 1;
    private static final int STATUS_DOWNLOAD_GOON = 2;
    private static final int STATUS_DOWNLOAD_CHECK = 3;

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
    private String mOrder = ORDER_ASC;
    private int mTotalChapterCount;

    private Book mBook;
    private InnerHandler mHandler;

    private ListView mListMain;
    private View mHeadView;
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
    private Button mButtonListReverse;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);

        mBookId = getIntent().getIntExtra(Constants.BOOK_ID, -1);
        if (mBookId == -1) {
            finish();
            return;
        }

        mHandler = new InnerHandler();
        mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBookId);

        mListMain = (ListView)findViewById(R.id.lsvBookInfoChapterList);
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
        mBookSummary = (EllipseEndTextView) findViewById(R.id.txvBookSummary);
        mButtonListReverse = (Button) findViewById(R.id.btnChapterListReverse);

        mButtonGotoRead.setOnClickListener(this);
        mButtonDownload.setOnClickListener(this);
        mButtonListReverse.setOnClickListener(this);

        requestBookInfo();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mButtonGotoRead)) {
            // 阅读
            processReadBook();

        } else if (view.equals(mButtonDownload)) {
            // 下载全部
            processDownloadAll();

        } else if (view.equals(mButtonListReverse)) {
            // 目录按钮
            mListMain.setSelection(0);

            mOrder = mOrder.equals(ORDER_DESC) ? ORDER_ASC : ORDER_DESC;
            int arrowDrawableResId = mOrder.equals(ORDER_DESC) ?
                    R.drawable.book_info_chapter_arrow_up : R.drawable.book_info_chapter_arrow_down;
            Button button = (Button)view;
            button.setCompoundDrawablesWithIntrinsicBounds(0, 0, arrowDrawableResId, 0);

            mPageNo = 1;
            mHasNextPage = true;
            mAdapter.clear();
            loadNextPage();
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
        mHeadView = getLayoutInflater().inflate(R.layout.book_info_head, null);
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

                } else {
                    // 书籍下载完成
                    mDownloadButtonStatus = STATUS_DOWNLOAD_CHECK;
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

            case STATUS_DOWNLOAD_CHECK:
                mButtonDownload.setText(resources.getString(R.string.download_check));
                break;

            default:
                break;
        }
    }

    private void requestBookInfo() {
        NetService.execute(new NetService.NetExecutor<Book>() {
            ProgressDialog mProgressDialog;

            public void preExecute() {
                Context context = BookInfoActivity.this;
                String prompt = context.getString(R.string.loading_tip_msg);
                mProgressDialog = ProgressDialog.show(context, null, prompt, false, true);
            }

            public Book execute() {
                return NetService.get().getBookDetail(mBookId);
            }

            public void callback(Book book) {
                mProgressDialog.cancel();
                if (book == null) {
                    getReaderApplication().showToastMsg(R.string.without_data);
                } else {
                    mBook = book;
                    updateUIWithBookInfo();
                }
            }
        });
    }

    private void updateUIWithBookInfo() {
        BookCoverLoader.loadCover(this, mBook, mImageBookCover);
        mTextBookName.setText(mBook.getName());
        mTextBookStatus.setText(mBook.getSimpleUpdateStatusStr());

        String author = getString(R.string.book_author) + mBook.getAuthor();
        mTextBookAuthor.setText(author);

        String category = getString(R.string.book_category) + mBook.getCatName();
        mTextBookCategory.setText(category);

        String capacity = getString(R.string.book_capacity) + mBook.getCapacityStr();
        mTextBookCapacity.setText(capacity);

        String summary = mBook.getFormattedSummary();
        if (TextUtils.isEmpty(summary)) {
            summary = getString(R.string.no_summary);
        }
        mBookSummary.setText(summary);

        if (mBook.isBothType()) {
            mButtonAnotherType.setVisibility(View.VISIBLE);
        }

        mListMain.setAdapter(mAdapter);
        mListMain.setOnScrollListener(this);
        loadNextPage();
    }

    private void loadNextPage() {
        if (!mHasNextPage) {
            return;
        }

        if (!NetService.get().isNetworkAvailable()) {
            showToastMsg(R.string.network_disconnect_msg);
            return;
        }

        mAdapter.setIsLoadingData(true);
        NetService.execute(new NetService.NetExecutor<PaginationList<Chapter>>() {
            public void preExecute() {
            }

            public PaginationList<Chapter> execute() {
                return NetService.get().getBookChapterList(mBook.getBookId(), mOrder, mPageNo, PAGE_ITEM_COUNT);
            }

            public void callback(PaginationList<Chapter> chapterList) {
				mAdapter.setIsLoadingData(false);
				if (chapterList == null || chapterList.size() == 0) {
					showToastMsg(R.string.without_data);
					return;
				}

				mTotalChapterCount = chapterList.getTotalItemCount();
				mHasNextPage = chapterList.hasNextPage();
                mAdapter.addAll(chapterList);
                mPageNo++;

                updateDownloadStatus();
                updateDownloadButtonStatus();
                mButtonDownload.setVisibility(View.VISIBLE);
            }
        });
    }

    private void processReadBook() {
        if (!mHasNextPage) {
            gotoReader();
            return;
        }

        NetService.execute(new BaseNetService.NetExecutor<List<Chapter>>() {
            ProgressDialog mProgressDialog;

            @Override
            public void preExecute() {
                mProgressDialog = ProgressDialog.show(BookInfoActivity.this, null, getString(R.string.loading_all_chapters_msg), false, true);
            }

            @Override
            public List<Chapter> execute() {
                return NetService.get().getBookNewlyChapters(mBookId, mAdapter.getLastItem().getId());
            }

            @Override
            public void callback(List<Chapter> chapterList) {
                boolean isShowing = mProgressDialog.isShowing();
                mProgressDialog.cancel();

                if (chapterList != null) {
                    mAdapter.addAll(chapterList);
                }
                if (isShowing) {
                    gotoReader();
                }
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

            case STATUS_DOWNLOAD_CHECK:
                NetService.execute(new NetService.NetExecutor<PaginationList<Chapter>>() {
                    public void preExecute() {
                    }

                    public PaginationList<Chapter> execute() {
                        return NetService.get().getBookChapterList(mBook.getBookId(), mOrder, 1, 1);
                    }

                    public void callback(PaginationList<Chapter> chapterList) {
                        int count = chapterList.getTotalItemCount();
                        if (count > mTotalChapterCount) {
                            // 有更新
                            if (ChapterDownloader.get().schedule(BookInfoActivity.this, mBook, true, BookInfoActivity.this)) {
                                mDownloadButtonStatus = STATUS_DOWNLOAD_PAUSE;
                                updateDownloadButtonStatus();

                                showToastMsg(R.string.chapter_has_update);
                            } else {
                                showToastMsg(R.string.chapter_downloader_limit_msg);
                            }

                        } else {
                            // 无更新
                            showToastMsg(R.string.chapter_no_update);
                        }
                    }
                });
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

        int bid = AppDAO.get().addBook(mBook, true);
        if (bid > 0) {
            AppDAO.get().saveBookChapters(bid, mAdapter.getData());
			new ReaderService().startReader(this, bid);
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

            File chapterFile = new File(mBookDirectoryPath + chapter.getId());
            if (chapterFile.exists()) {
                holder.btnChapterOperation.setBackgroundResource(R.drawable.book_info_chapter_btn_goto_read_selector);
                holder.btnChapterOperation.setOnClickListener(clickListener);

            } else {
                holder.btnChapterOperation.setBackgroundResource(R.drawable.book_info_chapter_btn_download_selector);
                holder.btnChapterOperation.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        NetService.execute(new BaseNetService.NetExecutor<Boolean>() {
                            @Override
                            public void preExecute() {
                                showToastMsg("正在下载：" + chapter.getTitle());
                            }

                            @Override
                            public Boolean execute() {
                                return NetService.get().downloadChapterContent(mBookId, chapter.getId());
                            }

                            @Override
                            public void callback(Boolean result) {
                                if (result) mAdapter.notifyDataSetChanged();
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
            chapt.setBeginPosition(0);
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
