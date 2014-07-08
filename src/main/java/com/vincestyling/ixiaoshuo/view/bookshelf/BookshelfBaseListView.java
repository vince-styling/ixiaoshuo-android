package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.event.ChapterDownloader;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.ui.CommonMenuDialog;
import com.vincestyling.ixiaoshuo.ui.WithoutBookStatisticsView;
import com.vincestyling.ixiaoshuo.ui.WithoutbookBackgroundDrawable;
import com.vincestyling.ixiaoshuo.view.BaseFragment;

import java.util.List;
import java.util.Random;

public abstract class BookshelfBaseListView extends BaseFragment implements OnItemLongClickListener, OnItemClickListener {
	protected BaseAdapter mAdapter;
	protected List<Book> mBookList;

	protected View mLotWithoutBooks;
	protected View mLotBookShelf;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = getActivity().getLayoutInflater().inflate(R.layout.book_shelf_content, null);

		mLotWithoutBooks = view.findViewById(R.id.lotWithoutBooks);
		mLotWithoutBooks.findViewById(R.id.lotWithoutBookBanner)
				.setBackgroundDrawable(new WithoutbookBackgroundDrawable(getResources()));

		mLotBookShelf = view.findViewById(R.id.lotBookShelf);

		return view;
	}

	@Override
	public void onResume() {
		loadData();
		super.onResume();
		if (mBookList != null && mBookList.size() > 0) {
			initListView();
			mAdapter.notifyDataSetChanged();
			mLotBookShelf.setVisibility(View.VISIBLE);
			mLotWithoutBooks.setVisibility(View.GONE);
		} else {
			initWithoutBookLayout();
			mLotBookShelf.setVisibility(View.GONE);
			mLotWithoutBooks.setVisibility(View.VISIBLE);
		}
	}

	public abstract void loadData();

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

	private View getAdapterView(int position, View convertView) {
		Holder holder;
		if (convertView == null) {
			convertView = getActivity().getLayoutInflater().inflate(R.layout.book_shelf_list_item, null);
			holder = new Holder();
			holder.imvBookCover = (ImageView) convertView.findViewById(R.id.imvBookCover);
			holder.txvBookName = (TextView) convertView.findViewById(R.id.txvBookName);
			holder.txvBookDesc1 = (TextView) convertView.findViewById(R.id.txvBookDesc1);
			holder.txvBookDesc2 = (TextView) convertView.findViewById(R.id.txvBookDesc2);
			holder.txvBookStatus1 = (TextView) convertView.findViewById(R.id.txvBookStatus1);
			holder.txvBookStatus2 = (TextView) convertView.findViewById(R.id.txvBookStatus2);
			holder.imvBookStatusSplit = (ImageView) convertView.findViewById(R.id.imvBookStatusSplit);
			holder.lotBookStatus = convertView.findViewById(R.id.lotBookStatus);
			holder.txvBookLabel = convertView.findViewById(R.id.txvBookLabel);
			convertView.setTag(holder);
		} else {
			holder = (Holder) convertView.getTag();
		}

		Book book = mBookList.get(position);
		Netroid.displayImage(book.getCoverUrl(), holder.imvBookCover, 0, 0);

		holder.txvBookName.setText(book.getName());
		holder.txvBookLabel.setVisibility(book.hasNewChapter() ? View.GONE : View.VISIBLE);

		holder.txvBookStatus1.setVisibility(book.isFinished() ? View.VISIBLE : View.GONE);
		holder.txvBookStatus2.setVisibility(book.isBothType() ? View.VISIBLE : View.GONE);
		holder.lotBookStatus.setVisibility(holder.txvBookStatus1.getVisibility() == View.VISIBLE || holder.txvBookStatus2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);
		holder.imvBookStatusSplit.setVisibility(holder.txvBookStatus1.getVisibility() == View.VISIBLE && holder.txvBookStatus2.getVisibility() == View.VISIBLE ? View.VISIBLE : View.GONE);

		setBookDesc1(book, holder.txvBookDesc1);
		setBookDesc2(book, holder.txvBookDesc2);

		return convertView;
	}

	protected void setBookDesc1(Book book, TextView txvBookDesc) {
		int unreadCount = AppDAO.get().getBookUnreadChapterCount(book.getBookId());
		if (unreadCount > 0) {
			txvBookDesc.setText(unreadCount + "章未读");
		} else {
			txvBookDesc.setText("");
		}
	}

	protected void setBookDesc2(Book book, TextView txvBookDesc) {
		Chapter chapter = AppDAO.get().getBookLastChapter(book.getBookId());
		if (chapter != null) {
			txvBookDesc.setText("更新至：" + chapter.getTitle());
		} else {
			txvBookDesc.setText("");
		}
	}

	private ListView getListView() {
		return (ListView) getView().findViewById(R.id.lsvBookShelf);
	}

	class Holder {
		ImageView imvBookCover;
		TextView txvBookName;
		TextView txvBookDesc1;
		TextView txvBookDesc2;
		TextView txvBookStatus1, txvBookStatus2;
		ImageView imvBookStatusSplit;
		View lotBookStatus;
		View txvBookLabel;
	}

	private boolean mIsInitWithoutBook;
	private void initWithoutBookLayout() {
		setFinderTip((TextView) mLotWithoutBooks.findViewById(R.id.txvFinderTip));

		if (mIsInitWithoutBook) return;

		mLotWithoutBooks.findViewById(R.id.lotGoFinder).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				getActivity().showMenuView(MainMenuGridView.MENU_FINDER);
			}
		});

		mLotWithoutBooks.findViewById(R.id.lotGoDetector).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
//				getActivity().showMenuView(MainMenuGridView.MENU_DETECTOR);
			}
		});

		WithoutBookStatisticsView statisticsView = (WithoutBookStatisticsView) mLotWithoutBooks.findViewById(R.id.lotBookStatistics);
		statisticsView.setStatCount(100000 + new Random().nextInt(600000));


		mIsInitWithoutBook = true;
	}

	protected void setFinderTip(TextView txvFinderTip) {
		txvFinderTip.setText(R.string.without_book_finder_tip2);
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
                        Intent intentActivity = new Intent(getActivity(), BookInfoActivity.class);
                        intentActivity.putExtra(Const.BOOK_ID, book.getBookId());
                        intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intentActivity);
						menuDialog.cancel();
					}
				}),
				new CommonMenuDialog.MenuItem("移出书架", new View.OnClickListener() {
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
								loadData();
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
				new CommonMenuDialog.MenuItem("下载全部", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!ChapterDownloader.get().schedule(getActivity(), book, false, null)) {
							getBaseActivity().showToastMsg(R.string.chapter_downloader_limit_msg);
						}
						menuDialog.cancel();
					}
				})
		});
		menuDialog.show();

		return true;
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		Book book = (Book) parent.getItemAtPosition(position);
		if (book != null) {
			Intent intent = new Intent(getActivity(), ReaderActivity.class);
			intent.putExtra(Const.BOOK_ID, book.getBookId());
			getActivity().startActivity(intent);
		}
	}

}
