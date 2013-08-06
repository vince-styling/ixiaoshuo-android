package com.duowan.mobile.ixiaoshuo.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.ui.CommonMenuDialog;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;
import com.duowan.mobile.ixiaoshuo.utils.Paths;

import java.util.List;

public abstract class BookshelfBaseView implements OnItemLongClickListener, OnItemClickListener, AbsListView.OnScrollListener {
	protected BaseAdapter mAdapter;
	protected List<Book> mBookList;
	protected ListView mLsvBookShelf;
	protected MainActivity mActivity;
//	private BitmapLruCache mBitmapLruCache;

	public void build(BookshelfBaseView bookshelfView) {
		this.mLsvBookShelf = bookshelfView.mLsvBookShelf;
		this.mActivity = bookshelfView.mActivity;
		this.mBookList = bookshelfView.mBookList;
		initBookShelf();
	}

	public void init(MainActivity activity, View lsvBookShelf, List<Book> bookList) {
		this.mLsvBookShelf = (ListView) lsvBookShelf;
		this.mActivity = activity;
		this.mBookList = bookList;
		initBookShelf();
	}

	private void initBookShelf() {
		initListView();
//		mBitmapLruCache = new BitmapLruCache();
		mLsvBookShelf.setOnScrollListener(this);
	}

	protected abstract void initListView();

	protected void notifyDataSetChanged() {
		mAdapter.notifyDataSetChanged();
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final Book book = (Book) parent.getItemAtPosition(position);
		if (book == null) return true;

		final CommonMenuDialog menuDialog = new CommonMenuDialog(mActivity, '《' + book.getName() + '》');
		menuDialog.initContentView(new CommonMenuDialog.MenuItem[] {
				new CommonMenuDialog.MenuItem("查看详情", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mActivity.showToastMsg("点击了查看详情");
					}
				}),
				new CommonMenuDialog.MenuItem("删除书籍", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
						builder.setMessage("是否确认删除该书籍？");
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								mBookList.remove(book);
								notifyDataSetChanged();
								menuDialog.cancel();
								dialog.cancel();
							}
						});
						builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								dialog.cancel();
							}
						});
						builder.show();
					}
				}),
				new CommonMenuDialog.MenuItem("更换站点", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						mActivity.showToastMsg("点击了更换站点");
					}
				}),
		});
		menuDialog.show();

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Book book = (Book) parent.getItemAtPosition(position);
		if (book != null) {
			mActivity.showToastMsg("单击：" + book.getName());
		}
	}

	@Override
	public void onScrollStateChanged(AbsListView view, int scrollState) {
		switch (scrollState) {
			case AbsListView.OnScrollListener.SCROLL_STATE_IDLE :
				int start = view.getFirstVisiblePosition();
				System.out.println(start);
				break;
		}
	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {}

	protected void setImageBitmap(ImageView imvBookCover, Book book) {
//		mBitmapLruCache.loadBitmap(book.getBookId(), Paths.getCoversDirectoryPath() + book.getCoverFileName(), imvBookCover);
		Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(Paths.getCoversDirectoryPath() + book.getCoverFileName(), imvBookCover);
		imvBookCover.setImageBitmap(coverBitmap);
	}

}
