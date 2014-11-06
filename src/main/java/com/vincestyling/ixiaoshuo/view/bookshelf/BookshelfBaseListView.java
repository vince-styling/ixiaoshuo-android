package com.vincestyling.ixiaoshuo.view.bookshelf;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.image.NetworkImageView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.event.ChapterDownloader;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.net.request.DeleteBookDBRequest;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.ui.CommonMenuDialog;
import com.vincestyling.ixiaoshuo.ui.ComplexBookNameView;
import com.vincestyling.ixiaoshuo.ui.RoundedRepeatBackgroundDrawable;
import com.vincestyling.ixiaoshuo.ui.WithoutBookStatisticsView;
import com.vincestyling.ixiaoshuo.view.BaseFragment;

import java.util.List;
import java.util.Random;

public abstract class BookshelfBaseListView extends BaseFragment implements OnItemClickListener,
        OnItemLongClickListener, ComplexBookNameView.ConditionSatisficer<Book> {
    private ListView mLsvBookShelf;
    protected BaseAdapter mAdapter;
    protected List<Book> mBookList;

    protected View mLotWithoutBooks;
    protected View mLotBookShelf;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.book_shelf_content, null);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mLsvBookShelf = (ListView) view.findViewById(R.id.lsvBookShelf);
        mLotWithoutBooks = view.findViewById(R.id.lotWithoutBooks);

        RoundedRepeatBackgroundDrawable drawable = new RoundedRepeatBackgroundDrawable();
        drawable.setBackgroundDrawable(getResources().getDrawable(R.drawable.book_shelf_without_book_stripe));
        drawable.setCornerRadius(getResources().getDimension(R.dimen.without_book_container_bg_corner_radius));
        drawable.setBorderWidth(getResources().getDimension(R.dimen.without_book_container_bg_border));
        drawable.setBorderColor(getResources().getColor(R.color.without_book_container_bg_border));
        mLotWithoutBooks.findViewById(R.id.lotWithoutBookBanner).setBackgroundDrawable(drawable);

        mLotBookShelf = view.findViewById(R.id.lotBookShelf);
    }

    @Override
    public void onResume() {
        super.onResume();
        refreshData();
    }

    protected void refreshData() {
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

    private void initListView() {
        if (mLsvBookShelf.getAdapter() != null) return;
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
                Holder holder;
                if (convertView == null) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.book_shelf_list_item, null);
                    holder = new Holder();
                    holder.txvBookName = (ComplexBookNameView<Book>) convertView.findViewById(R.id.txvBookName);
                    holder.imvBookCover = (NetworkImageView) convertView.findViewById(R.id.imvBookCover);
                    holder.txvBookUpdateLabel = convertView.findViewById(R.id.txvBookUpdateLabel);
                    holder.txvBookDesc1 = (TextView) convertView.findViewById(R.id.txvBookDesc1);
                    holder.txvBookDesc2 = (TextView) convertView.findViewById(R.id.txvBookDesc2);
                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                Book book = mBookList.get(position);
                Netroid.displayImage(book.getCoverUrl(), holder.imvBookCover,
                        R.drawable.book_cover_default, R.drawable.book_cover_default);

                holder.txvBookName.setSatisficer(BookshelfBaseListView.this);
                holder.txvBookName.setBook(book);

                holder.txvBookUpdateLabel.setVisibility(book.hasNewChapter() ? View.GONE : View.VISIBLE);

                setBookDesc1(book, holder.txvBookDesc1);
                setBookDesc2(book, holder.txvBookDesc2);

                return convertView;
            }
        };
        mLsvBookShelf.setAdapter(mAdapter);
        mLsvBookShelf.setOnItemClickListener(this);
        mLsvBookShelf.setOnItemLongClickListener(this);
    }

    protected void setBookDesc1(Book book, TextView txvBookDesc) {
        int unreadChapterCount = book.getUnreadChapterCount();
        if (unreadChapterCount == -1) {
            unreadChapterCount = AppDAO.get().getBookUnreadChapterCount(book.getId());
            book.setUnreadChapterCount(unreadChapterCount);
        }

        txvBookDesc.setText(unreadChapterCount > 0 ?
                String.format(getActivity().getString(R.string.unread_chapters_tip), unreadChapterCount) :
                getActivity().getString(R.string.havent_unread_chapters_tip));
    }

    protected void setBookDesc2(Book book, TextView txvBookDesc) {
        String chapterTitle = book.getLastChapterTitle();
        if (chapterTitle == null) {
            chapterTitle = AppDAO.get().getBookLastChapterTitle(book.getId());
            book.setLastChapterTitle(chapterTitle);
        }
        txvBookDesc.setText(String.format(getActivity().getString(R.string.last_chapter_tip), chapterTitle));
    }

    class Holder {
        ComplexBookNameView<Book> txvBookName;
        NetworkImageView imvBookCover;
        TextView txvBookDesc1;
        TextView txvBookDesc2;
        View txvBookUpdateLabel;
    }

    @Override
    public String getBookName(Book book) {
        return book.getName();
    }

    @Override
    public boolean isFirstConditionSatisfy(Book book) {
        return book.isFinished();
    }

    @Override
    public int firstConditionLabel(Book book) {
        return R.string.book_status_tip_finished;
    }

    @Override
    public boolean isSecondConditionSatisfy(Book book) {
        return book.isBothType();
    }

    @Override
    public int secondConditionLabel(Book book) {
        return R.string.book_status_tip_both_type;
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

        final CommonMenuDialog menuDialog = new CommonMenuDialog(getActivity(),
                String.format(getResources().getString(R.string.book_with_quote), book.getName()));
        menuDialog.initContentView(
                new CommonMenuDialog.MenuItem(R.string.book_op_check_detail, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intentActivity = new Intent(getActivity(), BookInfoActivity.class);
                        intentActivity.putExtra(Const.BOOK_ID, book.getId());
                        intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        getActivity().startActivity(intentActivity);
                        menuDialog.cancel();
                    }
                }),
                new CommonMenuDialog.MenuItem(R.string.book_op_remove_from_bookshelf, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                        builder.setMessage(R.string.book_op_remove_book_confirm);
                        builder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final ProgressDialog prgsDialog = ProgressDialog.show(getActivity(), null,
                                        String.format(getResources().getString(R.string.book_op_removeing_book), book.getName()), false, false);
                                menuDialog.cancel();
                                dialog.cancel();

                                new DeleteBookDBRequest(book, new Listener<Boolean>() {
                                    @Override
                                    public void onFinish() {
                                        prgsDialog.cancel();
                                    }

                                    @Override
                                    public void onSuccess(Boolean response) {
                                        refreshData();
                                    }
                                });
                            }
                        });
                        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        builder.show();
                    }
                }),
                new CommonMenuDialog.MenuItem(R.string.book_op_download_all, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!ChapterDownloader.get().schedule(getActivity(), book, false, null)) {
                            getBaseActivity().showToastMsg(R.string.chapter_downloader_limit_msg);
                        }
                        menuDialog.cancel();
                    }
                })
        );
        menuDialog.show();

        return true;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book book = (Book) parent.getItemAtPosition(position);
        if (book != null) {
            Intent intent = new Intent(getActivity(), ReaderActivity.class);
            intent.putExtra(Const.BOOK_ID, book.getId());
            getActivity().startActivity(intent);
        }
    }
}
