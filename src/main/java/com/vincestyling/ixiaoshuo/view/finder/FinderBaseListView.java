package com.vincestyling.ixiaoshuo.view.finder;

import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.ui.ComplexBookNameView;
import com.vincestyling.ixiaoshuo.ui.PullToLoadPage;
import com.vincestyling.ixiaoshuo.ui.PullToLoadPageListView;
import com.vincestyling.ixiaoshuo.view.BaseFragment;
import com.vincestyling.ixiaoshuo.view.PaginationAdapter;

import java.util.Arrays;

public abstract class FinderBaseListView extends BaseFragment implements
        OnItemClickListener, PullToLoadPageListView.OnLoadingPageListener, ComplexBookNameView.ConditionSatisficer<Book> {

    protected PaginationAdapter<Book> mAdapter;
    private PullToLoadPageListView mListView;
    private View mLotNetworkUnavaliable;

    private static final int PAGE_SIZE = 20;
    protected boolean mHasNextPage = true;
    private int mStartPageNum = 1;
    private int mEndPageNum;

    private int index, top, additionalPage;
    private boolean shouldRestore;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RelativeLayout view = (RelativeLayout) inflater.inflate(R.layout.finder_book_list_content, null);
        RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.MATCH_PARENT);
        view.setLayoutParams(params);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mListView = (PullToLoadPageListView) view.findViewById(R.id.lsvFinder);

        if (savedInstanceState != null) {
            additionalPage = savedInstanceState.getInt(ADDITIONAL_PAGE, 0);
            mStartPageNum = savedInstanceState.getInt(PAGE_NUM, 1);
            mEndPageNum = mStartPageNum - 1;

            index = savedInstanceState.getInt(INDEX, -1);
            top = savedInstanceState.getInt(TOP, -1);

            shouldRestore = index >= 0 && top >= 0;
        }

        if (mStartPageNum > 1) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.finder_list_pull_to_load, null);
            mListView.setPullToLoadPrevPageView(new PullToLoadPage(R.string.pull_to_load_prev_page, rootView) {
                @Override
                public int takePageNum() {
                    return mStartPageNum;
                }
            });
        }

        if (mHasNextPage) {
            View rootView = getActivity().getLayoutInflater().inflate(R.layout.finder_list_pull_to_load, null);
            mListView.setPullToLoadNextPageView(new PullToLoadPage(R.string.pull_to_load_next_page, rootView) {
                @Override
                public int takePageNum() {
                    return mEndPageNum;
                }
            });
        }

        mListView.setOnLoadingPageListener(this);


        StateListDrawable selectorDrwb = new StateListDrawable() {
            private boolean mIsPressed;

            @Override
            protected boolean onStateChange(int[] stateSet) {
                boolean result = super.onStateChange(stateSet);

                int aboveChildIndex = mListView.getMotionPosition() - mListView.getFirstVisiblePosition();
                if (--aboveChildIndex < 0) return result;

                if (Arrays.binarySearch(stateSet, android.R.attr.state_pressed) > -1) {
                    if (mIsPressed) return result;

                    if (aboveChildIndex == 0 && mStartPageNum == 1 || aboveChildIndex > 0) {
                        View child = mListView.getChildAt(aboveChildIndex);
                        Holder holder = (Holder) child.getTag();
                        if (holder != null) {
                            holder.lotDivider.setBackgroundResource(R.drawable.finder_booklist_divider_pressed_layer);
                            mIsPressed = true;
                        }
                    }
                } else if (mIsPressed) {
                    View child = mListView.getChildAt(aboveChildIndex);
                    Holder holder = (Holder) child.getTag();
                    holder.lotDivider.setBackgroundResource(R.drawable.finder_booklist_divider_layer);
                    mIsPressed = false;
                }

                return result;
            }
        };
        selectorDrwb.addState(new int[]{android.R.attr.state_pressed},
                getResources().getDrawable(R.color.finder_booklist_bg_pressed));
        mListView.setSelector(selectorDrwb);


        mAdapter = new PaginationAdapter<Book>(PAGE_SIZE) {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return adapterGetView(position, convertView);
            }
        };

        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(this);

        mLotNetworkUnavaliable = view.findViewById(R.id.lotNetworkUnavaliable);
        mLotNetworkUnavaliable.findViewById(R.id.btnRetry).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mListView.triggerLoadNextPage();
            }
        });
    }

    protected View adapterGetView(int position, View convertView) {
        Holder holder;
        if (convertView == null) {
            convertView = getActivity().getLayoutInflater().inflate(R.layout.finder_book_list_item, null);

            holder = new Holder();
            holder.lotDivider = convertView.findViewById(R.id.lotDivider);

            holder.txvBookName = (ComplexBookNameView<Book>) convertView.findViewById(R.id.txvBookName);
            holder.txvBookSummary = (TextView) convertView.findViewById(R.id.txvBookSummary);

            holder.txvBookTips = (TextView) convertView.findViewById(R.id.txvBookTips);
            holder.txvBookCapacity = (TextView) convertView.findViewById(R.id.txvBookCapacity);

            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        Book book = mAdapter.getItem(position);

        holder.txvBookName.setSatisficer(this);
        holder.txvBookName.setBook(book);

        setBookTips(holder.txvBookTips, book);
        holder.txvBookSummary.setText(book.getSummary());
        holder.txvBookCapacity.setText(book.getCapacityStr());

        if (!mHasNextPage) {
            int posDiffer = mAdapter.getItemCount() - position;
            holder.lotDivider.setVisibility(posDiffer == 1 ? View.GONE : View.VISIBLE);
        }

        return convertView;
    }

    protected abstract void setBookTips(TextView txvBookTips, Book book);

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

    @Override
    public void onResume() {
        if (mAdapter.getItemCount() == 0) mListView.triggerLoadNextPage();
        super.onResume();
    }

    private Listener<PaginationList<Book>> getListener(final boolean isLoadNextPage) {
        return new Listener<PaginationList<Book>>() {
            @Override
            public void onPreExecute() {
                mLotNetworkUnavaliable.setVisibility(View.GONE);
                mListView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onFinish() {
                if (isLoadNextPage) {
                    mListView.finishLoadNextPage();
                } else {
                    mListView.finishLoadPrevPage();
                }

                if (additionalPage > 0) {
                    additionalPage = 0;
                    mListView.triggerLoadNextPage();
                } else if (additionalPage < 0) {
                    additionalPage = 0;
                    mListView.triggerLoadPrevPage();
                } else if (shouldRestore) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            mListView.setSelectionFromTop(index, top);
                            shouldRestore = false;
                        }
                    }, 50);
                }
            }

            @Override
            public void onSuccess(PaginationList<Book> bookList) {
                if (isLoadNextPage) {
                    mHasNextPage = bookList.hasNextPage();
                    mAdapter.addLast(bookList);
                } else {
                    mAdapter.addFirst(bookList);
                }
            }

            @Override
            public void onError(NetroidError error) {
                if (mAdapter.getItemCount() > 0) {
                    getBaseActivity().showToastMsg(R.string.without_data);
                } else {
                    mLotNetworkUnavaliable.setVisibility(View.VISIBLE);
                    mListView.setVisibility(View.GONE);
                }

                shouldRestore = false;
                additionalPage = 0;

                if (isLoadNextPage) {
                    mEndPageNum--;
                } else {
                    mStartPageNum++;
                }
            }
        };
    }

    protected abstract void loadData(int pageNum, Listener<PaginationList<Book>> listener);

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Book book = (Book) parent.getItemAtPosition(position);
        if (book != null) {
            Intent intent = new Intent(getActivity(), BookInfoActivity.class);
            intent.putExtra(Const.BOOK_ID, book.getId());
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            getActivity().startActivity(intent);
        }
    }

    @Override
    public boolean onLoadNextPage() { // don't call this method directly
        if (mHasNextPage) {
            loadData(++mEndPageNum, getListener(true));
            return true;
        }
        return false;
    }

    @Override
    public boolean hasNextPage() {
        return mHasNextPage;
    }

    @Override
    public boolean onLoadPrevPage() { // don't call this method directly
        if (mStartPageNum > 1) {
            loadData(--mStartPageNum, getListener(false));
            return true;
        }
        return false;
    }

    @Override
    public boolean hasPrevPage() {
        return mStartPageNum > 1;
    }

    protected class Holder {
        ComplexBookNameView<Book> txvBookName;
        TextView txvBookSummary;
        TextView txvBookCapacity;
        TextView txvBookTips;
        View lotDivider;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        // index and top calculation from http://stackoverflow.com/a/16753664/1294681
        int upFillItemCount = 0;
        int additionalPage = 0;
        int indexOfPage = 0;

        // NOTE : index and top values always are positive number.
        int index = mListView.getFirstVisiblePosition();
        View child = mListView.getChildAt(0);
        int top = (child == null) ? 0 : child.getTop();

        child = mListView.getChildAt(1);
        if (top < 0 && child != null) {
            top = child.getTop();
            upFillItemCount++;
            indexOfPage++;
            index++;
        }

        // Decrease when index calculation included the header.
        if (mStartPageNum > 1 && index > 0) index--;


        // Calculate which page was index on.
        int pageNum = 0;
        while (index < pageNum++ * PAGE_SIZE || index > pageNum * PAGE_SIZE - 1) ;

        // Index relative to current page.
        index -= (pageNum - 1) * PAGE_SIZE;
        pageNum += mStartPageNum - 1;

        // Calculate how much items in the up and bottom of current position enough to fill ListView.
        int visibleChildCount = mListView.getLastVisiblePosition() - mListView.getFirstVisiblePosition() + 1;
        upFillItemCount = index == 0 && upFillItemCount == 1 ? 1 : 0;
        int downFillItemCount = visibleChildCount - indexOfPage - 1;


        // If the last child view wasn't footer, we determine if current page
        // have enough items to fill remaning gap after current index.
        child = mListView.getChildAt(mListView.getChildCount() - 1);
        if (child.findViewById(R.id.txvBookName) != null) {
            if (index + downFillItemCount >= PAGE_SIZE) {
                additionalPage = 1;
            }
        }


        // index between two pages, need previous page.
        if (index - upFillItemCount < 0) {
            additionalPage = -1;
            index += PAGE_SIZE;
        }


        int restoredStartPageNum = Math.min(pageNum, pageNum + additionalPage);
        // If pages include the header while next restoring state.
        if (restoredStartPageNum > 1) index++;


        outState.putInt(ADDITIONAL_PAGE, additionalPage);
        outState.putInt(PAGE_NUM, pageNum);
        outState.putInt(INDEX, index);
        outState.putInt(TOP, top);

//		AppLog.e("top : " + top + " index : " + index + " pageNum : " + pageNum + " additional : " + additionalPage + " up : " + upFillItemCount + " down : " + downFillItemCount);
    }

    public static final String ADDITIONAL_PAGE = "additional_page";
    public static final String PAGE_NUM = "page_num";
    public static final String INDEX = "list_index";
    public static final String TOP = "list_top";
}
