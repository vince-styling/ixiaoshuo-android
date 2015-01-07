package com.vincestyling.ixiaoshuo.reader;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.view.Display;
import android.view.View;
import android.widget.*;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.ChapterDownloader;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.net.request.AddingBookDBRequest;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.ui.EllipseEndTextView;
import com.vincestyling.ixiaoshuo.utils.Paths;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;

import java.io.File;

public class BookInfoActivity extends BaseActivity implements View.OnClickListener,
        AbsListView.OnScrollListener, ChapterDownloader.OnDownLoadListener {

    private static final int STATUS_DOWNLOAD_COMPLETED = 0;
    private static final int STATUS_DOWNLOAD_ALL       = 1;
    private static final int STATUS_DOWNLOAD_PAUSE     = 2;
    private static final int STATUS_DOWNLOAD_GOON      = 3;

    private String mBookDirectoryPath;
    private int mBookId;
    private int mBatchDownloadStatus;
    private boolean mHasNextPage = true;
    private int mPageNo = 1;
    private int mTotalChapterCount;

    private Book mBook;

    private ListView mListMain;
    private Button mBtnGotoRead;
    private Button mBtnBatchDownload;

    private EllipseEndTextView mBookSummary;
    private View mLotBookSummary;
    private TextView mBtnSummaryExpand;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.book_info);

        mBookId = getIntent().getIntExtra(Const.BOOK_ID, -1);
        if (mBookId == -1) {
            finish();
            return;
        }

        requestBookInfo();
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mBtnGotoRead)) {
            processReadBook();
        } else if (view.equals(mBtnBatchDownload)) {
            processBatchDownload();
        } else if (view.equals(mLotBookSummary)) {
            mBookSummary.setOnMeasureDoneListener(new EllipseEndTextView.OnMeasureDoneListener() {
                @Override
                public void onMeasureDone(View v) {
                    int resId = mBookSummary.isExpanded() ? R.string.summary_collapse : R.string.summary_expand;
                    mBtnSummaryExpand.setText(resId);
                    resId = mBookSummary.isExpanded() ? R.drawable.book_detail_arrow_up : R.drawable.book_detail_arrow_down;
                    mBtnSummaryExpand.setCompoundDrawablesWithIntrinsicBounds(0, 0, resId, 0);
                }
            });
            mBookSummary.elipseSwitch();
        }
    }

    private void initHeaderView() {
        View mHeadView = getLayoutInflater().inflate(R.layout.book_info_head, null);
        Display display = getWindowManager().getDefaultDisplay();
        mHeadView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
        mListMain.addHeaderView(mHeadView);
    }

    private void updateDownloadStatus() {
        if (ChapterDownloader.get().taskIsStarted(mBookId)) {
            mBatchDownloadStatus = STATUS_DOWNLOAD_PAUSE;

        } else {
            File file = new File(mBookDirectoryPath);
            if (file.exists()) {
                String[] files = file.list();
                if (files.length == mTotalChapterCount) {
                    // 书籍下载完整
                    mBatchDownloadStatus = STATUS_DOWNLOAD_COMPLETED;

                } else if (files.length == 0) {
                    // 书籍完全没有下载
                    mBatchDownloadStatus = STATUS_DOWNLOAD_ALL;

                } else {
                    // 书籍未完全下载
                    mBatchDownloadStatus = STATUS_DOWNLOAD_GOON;

                }
            } else {
                // 书籍文件夹不存在
                mBatchDownloadStatus = STATUS_DOWNLOAD_ALL;
            }
        }
        updateDownloadButtonStatus();
    }

    private void updateDownloadButtonStatus() {
        Resources resources = getResources();
        switch (mBatchDownloadStatus) {
            case STATUS_DOWNLOAD_COMPLETED:
                mBtnBatchDownload.setBackgroundResource(R.drawable.book_info_btn_red_pressed);
                mBtnBatchDownload.setText(resources.getString(R.string.already_downloaded));
                break;

            case STATUS_DOWNLOAD_PAUSE:
                mBtnBatchDownload.setText(resources.getString(R.string.download_pause));
                break;

            case STATUS_DOWNLOAD_GOON:
                mBtnBatchDownload.setText(resources.getString(R.string.download_goon));
                break;

            case STATUS_DOWNLOAD_ALL:
                mBtnBatchDownload.setText(resources.getString(R.string.download_all));
                break;
        }
    }

    private void requestBookInfo() {
        Netroid.getBookDetail(mBookId, new Listener<Book>() {
            ProgressDialog mProgressDialog;

            @Override
            public void onNetworking() {
                mProgressDialog = ProgressDialog.show(BookInfoActivity.this, null, getString(R.string.loading_tip_msg), true, false);
            }

            @Override
            public void onFinish() {
                if (mProgressDialog != null) mProgressDialog.cancel();
            }

            @Override
            public void onSuccess(Book book) {
                mBook = book;
                updateUIWithBookInfo();
            }

            @Override
            public void onError(NetroidError error) {
                showToastMsg(R.string.book_detail_loading_failed);
                finish();
            }
        });
    }

    private void updateUIWithBookInfo() {
        mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(mBookId);

        mListMain = (ListView) findViewById(R.id.lsvBookInfo);
        initHeaderView();

        mBtnBatchDownload = (Button) findViewById(R.id.btnBatchDownload);
        mBtnGotoRead = (Button) findViewById(R.id.btnGotoRead);

        mBookSummary = (EllipseEndTextView) findViewById(R.id.txvBookSummary);
        mBtnSummaryExpand = (TextView) findViewById(R.id.btnSummaryExpand);
        mLotBookSummary = findViewById(R.id.lotBookSummary);

        mBtnGotoRead.setOnClickListener(this);
        mBtnBatchDownload.setOnClickListener(this);

        Netroid.displayImage(mBook.getCoverUrl(),
                (ImageView) findViewById(R.id.imvBookCover),
                R.drawable.book_cover_default, R.drawable.book_cover_default);

        ((TextView) findViewById(R.id.txvBookStatus)).setText(mBook.getUpdateStatusStr());
        ((TextView) findViewById(R.id.txvBookName)).setText(mBook.getName());

        ((TextView) findViewById(R.id.txvBookCategory)).setText(
                String.format(getString(R.string.book_category), mBook.getCatName()));

        ((TextView) findViewById(R.id.txvBookAuthor)).setText(
                String.format(getString(R.string.book_author), mBook.getAuthor()));

        ((TextView) findViewById(R.id.txvBookCapacity)).setText(
                String.format(getString(R.string.book_capacity), mBook.getCapacityStr()));

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
        mBookSummary.setText(String.format(getString(R.string.book_detail_summary), mBook.getSummary()));

        ((TextView) findViewById(R.id.txvUpdateTime)).setText(mBook.getLastUpdateTime());

        mListMain.setOnScrollListener(this);
        mListMain.setAdapter(mAdapter);
        loadNextPage();
    }

    private synchronized void loadNextPage() {
        if (!mHasNextPage || mAdapter.isLoadingData()) return;

        mAdapter.setIsLoadingData(true);
        Netroid.getBookChapterList(mBook.getId(), mPageNo, new Listener<PaginationList<Chapter>>() {
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

        Netroid.getBookChapterList(mBook.getId(), mPageNo, new Listener<PaginationList<Chapter>>() {
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

    private void processBatchDownload() {
        switch (mBatchDownloadStatus) {
            case STATUS_DOWNLOAD_ALL:
                if (ChapterDownloader.get().schedule(BookInfoActivity.this, mBook, true, BookInfoActivity.this)) {
                    mBatchDownloadStatus = STATUS_DOWNLOAD_PAUSE;
                    updateDownloadButtonStatus();

                    new AddingBookDBRequest(mBook, false, mAdapter.getData(), new Listener<Integer>() {
                        @Override
                        public void onSuccess(Integer bookId) {
                            showToastMsg(R.string.added_to_bookshelf, mBook.getName());
                        }
                    });
                } else {
                    showToastMsg(R.string.chapter_downloader_limit_msg);
                }
                break;

            case STATUS_DOWNLOAD_PAUSE:
                ChapterDownloader.get().cancel(mBookId);
                mBatchDownloadStatus = STATUS_DOWNLOAD_GOON;
                updateDownloadButtonStatus();
                break;

            case STATUS_DOWNLOAD_GOON:
                if (ChapterDownloader.get().schedule(BookInfoActivity.this, mBook, true, BookInfoActivity.this)) {
                    mBatchDownloadStatus = STATUS_DOWNLOAD_PAUSE;
                    updateDownloadButtonStatus();
                } else {
                    showToastMsg(R.string.chapter_downloader_limit_msg);
                }
                break;
        }
    }

    private void gotoReader() {
        if (!mAdapter.hasItems()) {
            showToastMsg(R.string.havent_enough_chapters);
            return;
        }

        new AddingBookDBRequest(mBook, true, mAdapter.getData(), new Listener<Integer>() {
            @Override
            public void onSuccess(Integer bookId) {
                Intent intent = new Intent(BookInfoActivity.this, ReaderActivity.class);
                intent.putExtra(Const.BOOK_ID, bookId);
                startActivity(intent);
            }
            @Override
            public void onError(NetroidError error) {
                showToastMsg(R.string.failed_to_append_abook_toshelf);
            }
        });
    }

    private EndlessListAdapter<Chapter> mAdapter = new EndlessListAdapter<Chapter>() {
        @Override
        protected View getView(int position, View convertView) {
            Holder holder;
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.book_info_chapter_list_item, null);
                convertView.setLayoutParams(new AbsListView.LayoutParams(
                        mListMain.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
                holder = new Holder();
                holder.txvChapterTitle = (TextView) convertView.findViewById(R.id.txvChapterTitle);
                holder.btnChapterOperation = (Button) convertView.findViewById(R.id.btnChapterOperation);
                convertView.setTag(holder);
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
                        Netroid.downloadChapterContent(mBook.getId(), chapter.getChapterId(), new Listener<Void>() {
                            @Override
                            public void onPreExecute() {
                                showToastMsg(R.string.goingto_download_chapter, chapter.getTitle());
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

        class Holder {
            TextView txvChapterTitle;
            Button btnChapterOperation;
        }

        @Override
        protected View initProgressView() {
            View progressView = getLayoutInflater().inflate(R.layout.contents_loading, null);
            Display display = getWindowManager().getDefaultDisplay();
            progressView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
            return progressView;
        }
    };

    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        mListMain.setVerticalScrollBarEnabled(firstVisibleItem > 0);
        if (mHasNextPage && mAdapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
            loadNextPage();
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
    }

    @Override
    public void onChapterComplete(int bookId, int chapterId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                mAdapter.notifyDataSetChanged();
            }
        });
    }

    @Override
    public void onDownloadComplete(int bookId) {
        updateDownloadStatus();
    }

    public void onItemClick(Chapter chapter) {
        for (Chapter chapt : mAdapter.getData()) {
            chapt.setReadStatus(Chapter.READSTATUS_UNREAD);
            chapt.setReadPosition(0);
        }
        chapter.setReadStatus(Chapter.READSTATUS_READING);
        processReadBook();
    }

    public int getBookId() {
        return mBookId;
    }
}
