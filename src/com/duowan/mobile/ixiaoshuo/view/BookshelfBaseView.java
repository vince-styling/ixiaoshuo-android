package com.duowan.mobile.ixiaoshuo.view;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.View;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.BookshelfActivity;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;
import com.duowan.mobile.ixiaoshuo.utils.Paths;

import java.util.List;

public abstract class BookshelfBaseView implements AdapterView.OnItemLongClickListener {
	protected List<Book> mBookList;
	protected ListView mLsvBookShelf;
	protected BookshelfActivity mActivity;
	protected BaseAdapter mBookShelfAdapter;

	public void build(BookshelfBaseView bookshelfView) {
		this.mLsvBookShelf = bookshelfView.mLsvBookShelf;
		this.mActivity = bookshelfView.mActivity;
		this.mBookList = bookshelfView.mBookList;
		initBookShelf();
	}

	public void init(BookshelfActivity activity, View lsvBookShelf, List<Book> bookList) {
		this.mLsvBookShelf = (ListView) lsvBookShelf;
		this.mActivity = activity;
		this.mBookList = bookList;
		initBookShelf();
	}

	protected abstract void initBookShelf();

	protected void notifyDataSetChanged() {
		mBookShelfAdapter.notifyDataSetChanged();
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
						mActivity.getReaderApplication().showToastMsg("点击了查看详情");
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
						mActivity.getReaderApplication().showToastMsg("点击了更换站点");
					}
				}),
		});
		menuDialog.show();

		return true;
	}

	protected void setImageBitmap(ImageView imvBookCover, Book book) {
		Bitmap coverBitmap = BitmapUtil.loadBitmapInFile(Paths.getCoversDirectoryPath() + book.getCoverFileName(), imvBookCover);
		imvBookCover.setImageBitmap(coverBitmap);
	}

}
