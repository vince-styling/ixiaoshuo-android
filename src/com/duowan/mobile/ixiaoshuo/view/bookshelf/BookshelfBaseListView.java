package com.duowan.mobile.ixiaoshuo.view.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;
import com.duowan.mobile.ixiaoshuo.ui.CommonMenuDialog;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

import java.util.List;

public abstract class BookshelfBaseListView extends ViewBuilder implements OnItemLongClickListener, OnItemClickListener {
	protected BaseAdapter mAdapter;
	protected List<Book> mBookList;

	protected View mLotWithoutBooks;

	public BookshelfBaseListView(MainActivity activity, int viewId, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		mActivity = activity;
		mViewId = viewId;
	}

	@Override
	public void init() {
		mLotWithoutBooks = mActivity.findViewById(R.id.lotWithoutBooks);
		resume();
	}

	@Override
	public void resume() {
		mBookList = AppDAO.get().getBookList();
		super.resume();
	}

	@Override
	protected void bringToFront() {
		if (mIsInFront) return;

		if (mBookList != null && mBookList.size() > 0) {
			initListView();
			mAdapter.notifyDataSetChanged();
			mView.setVisibility(View.VISIBLE);
			mLotWithoutBooks.setVisibility(View.GONE);
		} else {
			initWithoutBookLayout();
			mView.setVisibility(View.GONE);
			mLotWithoutBooks.setVisibility(View.VISIBLE);
		}
		if (mShowListener != null) mShowListener.onShow();
		mIsInFront = true;
	}

	private void initListView() {
		if (getListView().getAdapter() != null) return;
		mAdapter = new BaseAdapter() {
			@Override
			public int getCount() {
				return mBookList != null ? mBookList.size() : 0;
			}

			@Override
			public Book getItem(int position) {
				return mBookList.get(position);
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				return getAdapterView(position, convertView);
			}
		};
		getListView().setAdapter(mAdapter);
		getListView().setOnItemClickListener(this);
		getListView().setOnItemLongClickListener(this);
	}

	protected abstract View getAdapterView(int position, View convertView);

	private boolean mIsInitWithoutBook;
	private void initWithoutBookLayout() {
		if (mIsInitWithoutBook) return;

		mLotWithoutBooks.findViewById(R.id.lotGoFinder).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().showToastMsg("正在前往发现模块");
			}
		});

		mLotWithoutBooks.findViewById(R.id.lotGoDetector).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().showToastMsg("正在前往雷达界面");
			}
		});

		mIsInitWithoutBook = true;
	}

	@Override
	public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
		final Book book = (Book) parent.getItemAtPosition(position);
		if (book == null) return true;

		final CommonMenuDialog menuDialog = new CommonMenuDialog(getActivity(), '《' + book.getName() + '》');
		menuDialog.initContentView(new CommonMenuDialog.MenuItem[] {
				new CommonMenuDialog.MenuItem("查看详情", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						getActivity().showToastMsg("点击了查看详情");
					}
				}),
				new CommonMenuDialog.MenuItem("删除书籍", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("是否确认删除该书籍？");
						builder.setPositiveButton("确认", new DialogInterface.OnClickListener() {
							@Override
							public void onClick(DialogInterface dialog, int which) {
								AppDAO.get().deleteBook(book);
								menuDialog.cancel();
								dialog.cancel();
								resume();
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
						getActivity().showToastMsg("点击了更换站点");
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
			Intent intent = new Intent(getActivity(), ReaderActivity.class);
			intent.setAction(String.valueOf(book.getBid()));
			getActivity().startActivity(intent);
		}
	}

	private ListView getListView() {
		return (ListView) mView;
	}

}
