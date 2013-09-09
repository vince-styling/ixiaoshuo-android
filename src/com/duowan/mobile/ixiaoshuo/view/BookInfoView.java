package com.duowan.mobile.ixiaoshuo.view;

import android.content.Intent;
import android.view.Display;
import android.view.View;
import android.widget.*;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.event.BookCoverLoader;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;
import com.duowan.mobile.ixiaoshuo.ui.EllipseEndTextView;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.utils.Paths;

import java.io.File;

public class BookInfoView extends ViewBuilder implements AbsListView.OnScrollListener, AdapterView.OnItemClickListener {
	private String mBookDirectoryPath;
	private Book mBook;

	private EndlessListAdapter<Chapter> mAdapter;
	private View mHeadView;

	protected int mPageNo = 1;
	protected final static int PAGE_ITEM_COUNT = 40;
	protected boolean mHasNextPage = true;

	public BookInfoView(MainActivity activity, Book book) {
		mViewId = R.id.lsvBookInfoChapterList;
		mActivity = activity;

		mBookDirectoryPath = Paths.getCacheDirectorySubFolderPath(book.getBookId());
		mBook = book;
	}

	@Override
	protected void build() {
		mView = mActivity.getLayoutInflater().inflate(R.layout.book_info, null);
	}

	@Override
	public void init() {
		mHeadView = mActivity.getLayoutInflater().inflate(R.layout.book_info_head, null);
		Display display = mActivity.getWindowManager().getDefaultDisplay();
		mHeadView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
		getListView().addHeaderView(mHeadView);

		ImageView imvBookCover = (ImageView) findViewById(R.id.imvBookCover);
		BookCoverLoader.loadCover(mActivity, mBook, imvBookCover);

		TextView txvBookName = (TextView) findViewById(R.id.txvBookName);
		txvBookName.setText(mBook.getName());

		TextView txvBookStatus = (TextView) findViewById(R.id.txvBookStatus);
		txvBookStatus.setText(mBook.getSimpleUpdateStatusStr());

		TextView txvBookAuthor = (TextView) findViewById(R.id.txvBookAuthor);
		txvBookAuthor.setText("作者：" + mBook.getAuthor());

		TextView txvBookCategory = (TextView) findViewById(R.id.txvBookCategory);
		txvBookCategory.setText("分类：" + mBook.getCatName());

		TextView txvBookCapacity = (TextView) findViewById(R.id.txvBookCapacity);
		txvBookCapacity.setText("字数：" + mBook.getCapacityStr());


		final EllipseEndTextView txvBookSummary = (EllipseEndTextView) findViewById(R.id.txvBookSummary);
		txvBookSummary.setText(mBook.getFormattedSummary());

		Button btnGotoRead = (Button) findViewById(R.id.btnGotoRead);
		btnGotoRead.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if (mBook.hasChapters()) {
					int bid = AppDAO.get().addBook(mBook);
					if (bid > 0) {
						AppDAO.get().saveBookChapters(bid, mBook.getChapterList());
						Intent intent = new Intent(mActivity, ReaderActivity.class);
						intent.setAction(String.valueOf(bid));
						mActivity.startActivity(intent);
					} else mActivity.showToastMsg("抱歉，无法添加书籍！");
				} else mActivity.showToastMsg("无可读章节！");
			}
		});

		mAdapter = new EndlessListAdapter<Chapter>() {
			@Override
			protected View getView(int position, View convertView) {
				Holder holder;
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.book_info_chapter_list_item, null);
					holder = new Holder();
					holder.txvChapterTitle = (TextView) convertView.findViewById(R.id.txvChapterTitle);
					holder.btnChapterGotoRead = (Button) convertView.findViewById(R.id.btnChapterGotoRead);
					convertView.setTag(holder);
					convertView.setLayoutParams(new AbsListView.LayoutParams(
							getListView().getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				} else {
					holder = (Holder) convertView.getTag();
				}

				Chapter chapter = getItem(position);
				holder.txvChapterTitle.setText(chapter.getTitle());

				File chapterFile = new File(mBookDirectoryPath + chapter.getId());
				if (chapterFile.length() > 100) {
					holder.btnChapterGotoRead.setBackgroundResource(R.drawable.book_info_chapter_btn_goto_read);
				} else {
					holder.btnChapterGotoRead.setBackgroundResource(R.drawable.book_info_chapter_btn_download);
				}

				return convertView;
			}

			@Override
			protected View initProgressView() {
				View progressView = mActivity.getLayoutInflater().inflate(R.layout.contents_loading, null);
				Display display = mActivity.getWindowManager().getDefaultDisplay();
				progressView.setLayoutParams(new AbsListView.LayoutParams(display.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT));
				return progressView;
			}
		};
		getListView().setAdapter(mAdapter);
		getListView().setOnScrollListener(this);
		getListView().setOnItemClickListener(this);
	}

	@Override
	public void resume() {
		super.resume();
		loadNextPage();
	}

	private void loadNextPage() {
		if (!mHasNextPage) return;

		if (!NetService.get().isNetworkAvailable()) {
			mActivity.showToastMsg(R.string.network_disconnect_msg);
			return;
		}

		mAdapter.setIsLoadingData(true);
		NetService.execute(new NetService.NetExecutor<PaginationList<Chapter>>() {
			public void preExecute() {}

			public PaginationList<Chapter> execute() {
				return NetService.get().getBookChapterList(mBook.getBookId(), 0, mPageNo, PAGE_ITEM_COUNT);
			}

			public void callback(PaginationList<Chapter> chapterList) {
				mAdapter.setIsLoadingData(false);
				if (chapterList == null || chapterList.size() == 0) {
					if (isInFront()) mActivity.showToastMsg(R.string.without_data);
					return;
				}
				mHasNextPage = chapterList.hasNextPage();
				mAdapter.addAll(chapterList);
				mPageNo++;
			}
		});
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		getListView().setVerticalScrollBarEnabled(firstVisibleItem > 0);
		if (mAdapter.shouldRequestNextPage(firstVisibleItem, visibleItemCount, totalItemCount)) {
			loadNextPage();
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Chapter chapter = (Chapter) parent.getItemAtPosition(position);
		mActivity.showToastMsg("点击了：" + chapter.getTitle());
	}

	class Holder {
		TextView txvChapterTitle;
		Button btnChapterGotoRead;
	}

	protected ListView getListView() {
		return (ListView) mView;
	}

	@Override
	protected View findViewById(int viewId) {
		return mHeadView.findViewById(viewId);
	}

	@Override
	protected boolean isReusable() {
		return false;
	}

}
