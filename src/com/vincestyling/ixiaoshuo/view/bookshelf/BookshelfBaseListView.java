package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Parcelable;
import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.event.BookCoverLoader;
import com.vincestyling.ixiaoshuo.event.ChapterDownloader;
import com.vincestyling.ixiaoshuo.net.BaseNetService;
import com.vincestyling.ixiaoshuo.net.NetService;
import com.vincestyling.ixiaoshuo.pojo.*;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.service.ReaderService;
import com.vincestyling.ixiaoshuo.ui.CommonMenuDialog;
import com.vincestyling.ixiaoshuo.ui.MainMenuGridView;
import com.vincestyling.ixiaoshuo.ui.WithoutBookStatisticsView;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public abstract class BookshelfBaseListView extends ViewBuilder implements OnItemLongClickListener, OnItemClickListener {
	protected BaseAdapter mAdapter;
	protected List<Book> mBookList;
	private boolean mIsSyncUpdate;
	protected View mLotWithoutBooks;

	public BookshelfBaseListView(MainActivity activity, int viewId, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		setActivity(activity);
		mViewId = viewId;
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.book_shelf_listview, null);
		mView.setId(mViewId);
	}

	@Override
	public void init() {
		mLotWithoutBooks = getActivity().findViewById(R.id.lotWithoutBooks);
	}

	@Override
	public void resume() {
		loadData();
		mIsSyncUpdate = false;
		super.resume();
	}

	public abstract void loadData();

	@Override
	protected void bringToFront() {
		if (mBookList != null && mBookList.size() > 0) {
			initListView();
			mAdapter.notifyDataSetChanged();
			mView.setVisibility(View.VISIBLE);
			mLotWithoutBooks.setVisibility(View.GONE);
			syncChapterUpdate();
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
		BookCoverLoader.loadCover(getActivity(), book, holder.imvBookCover);

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
				getActivity().showMenuView(MainMenuGridView.MENU_FINDER);
			}
		});

		mLotWithoutBooks.findViewById(R.id.lotGoDetector).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().showMenuView(MainMenuGridView.MENU_DETECTOR);
			}
		});

		WithoutBookStatisticsView statisticsView = (WithoutBookStatisticsView) mLotWithoutBooks.findViewById(R.id.lotBookStatistics);
		statisticsView.setBookCount(100000 + new Random().nextInt(600000));


		mIsInitWithoutBook = true;
	}

	protected void setFinderTip(TextView txvFinderTip) {
		txvFinderTip.setText(R.string.without_book_finder_tip2);
	}

	private synchronized void syncChapterUpdate() {
		if (mIsSyncUpdate) return;
		mIsSyncUpdate = true;

//		if (NetService.get().isNetworkAvailable()) {
//			NetService.execute(new BaseNetService.NetExecutor<List<BookUpdateInfo>>() {
//				@Override
//				public void preExecute() {}
//
//				@Override
//				public List<BookUpdateInfo> execute() {
//					List<BookOnUpdate> bookOnUpdateList = new ArrayList<BookOnUpdate>(mBookList.size());
//					for (Book book : mBookList) {
//						Chapter chapter = AppDAO.get().getBookLastChapter(book.getBookId());
//						if (chapter != null) {
//							bookOnUpdateList.add(new BookOnUpdate(book.getSourceBookId(), chapter.getChapterId()));
//						}
//					}
//					return NetService.get().getBookUpdateInfo(bookOnUpdateList);
//				}
//
//				@Override
//				public void callback(List<BookUpdateInfo> bookUpdateInfoList) {
//					if (bookUpdateInfoList != null && bookUpdateInfoList.size() > 0) {
//						for (BookUpdateInfo updateInfo : bookUpdateInfoList) {
//							for (Book book : mBookList) {
//								if (updateInfo.getBookId() == book.getSourceBookId()) {
//									book.setUpdateChapterCount(updateInfo.getUpdateChapterCount());
//									break;
//								}
//							}
//						}
//						mAdapter.notifyDataSetChanged();
//					}
//				}
//			});
//		}
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
                        intentActivity.putExtra(Constants.BOOK_ID, book.getBookId());
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
				new CommonMenuDialog.MenuItem("下载全部", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						if (!ChapterDownloader.get().schedule(getActivity(), book, false, null)) {
							getActivity().showToastMsg(R.string.chapter_downloader_limit_msg);
						}
						menuDialog.cancel();
					}
				}),
				new CommonMenuDialog.MenuItem("发送到桌面", new View.OnClickListener() {
					@Override
					public void onClick(View v) {
						// TODO : 待重构！！！！！！！！！！！！
						Intent thisIntent = new Intent();
						thisIntent.setClass(getActivity(), ReaderActivity.class);
						thisIntent.setAction(String.valueOf(book.getBookId()));
						Intent addShortcut = new Intent("com.android.launcher.action.INSTALL_SHORTCUT");
						addShortcut.putExtra(Intent.EXTRA_SHORTCUT_NAME, book.getName());
						addShortcut.putExtra(Intent.EXTRA_SHORTCUT_INTENT, thisIntent);

						String path = book.getLocalCoverPath();
						if (path != null && new File(path).length() > 0) {
							Bitmap cover = BitmapFactory.decodeFile(path);
							addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON, cover);
						} else {
							Parcelable icon = Intent.ShortcutIconResource.fromContext(getActivity(), R.drawable.logo);
							addShortcut.putExtra(Intent.EXTRA_SHORTCUT_ICON_RESOURCE, icon);
						}

						getActivity().sendBroadcast(addShortcut);
						getActivity().showToastMsg("发送到桌面成功");
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
			new ReaderService().startReader(getActivity(), book.getBookId());
		}
	}

	private ListView getListView() {
		return (ListView) mView.findViewById(R.id.lsvBookShelf);
	}

	@Override
	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

}
