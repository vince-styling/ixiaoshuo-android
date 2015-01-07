package com.vincestyling.ixiaoshuo.reader;

import android.content.Intent;
import android.graphics.drawable.StateListDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.pojo.BookByLocation;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.ui.ComplexBookNameView;
import com.vincestyling.ixiaoshuo.ui.PullToLoadPage;
import com.vincestyling.ixiaoshuo.ui.PullToLoadPageListView;
import com.vincestyling.ixiaoshuo.ui.RoundedRepeatBackgroundDrawable;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.PaginationAdapter;

public class DetectorResultActivity extends BaseActivity implements PullToLoadPageListView.OnLoadingPageListener,
        AdapterView.OnItemClickListener, ComplexBookNameView.ConditionSatisficer<BookByLocation> {

    private PaginationAdapter<BookByLocation> mAdapter;
    private PullToLoadPageListView mLsvContent;

    private boolean mHasNextPage = true;
    private int mPageNum;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detector_result);

        TextView txvLocation = (TextView) findViewById(R.id.txvLocation);
        txvLocation.setText(String.format(getResources().getString(R.string.detector_result_bookcount), StringUtil.nextRandInt(30, 200)));

        RoundedRepeatBackgroundDrawable drawable = new RoundedRepeatBackgroundDrawable();
        drawable.setBackgroundDrawable(getResources().getDrawable(R.drawable.book_shelf_deep_bg));
        drawable.setCornerRadius(getResources().getDimension(R.dimen.detector_result_head_bg_corner_radius));
        drawable.setBorderWidth(getResources().getDimension(R.dimen.detector_result_head_bg_border));
        drawable.setBorderColor(getResources().getColor(R.color.detector_result_head_bg_border));
        findViewById(R.id.lotResultHeader).setBackgroundDrawable(drawable);

        findViewById(R.id.imvRelocation).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });


        mLsvContent = (PullToLoadPageListView) findViewById(R.id.lsvContent);

        View rootView = getLayoutInflater().inflate(R.layout.finder_list_pull_to_load, null);
        mLsvContent.setPullToLoadNextPageView(new PullToLoadPage(R.string.pull_to_load_next_page, rootView) {
            @Override
            public int takePageNum() {
                return mPageNum;
            }
        });

        mLsvContent.setOnLoadingPageListener(this);

        mAdapter = new PaginationAdapter<BookByLocation>() {
            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                return adapterGetView(position, convertView);
            }
        };

        mLsvContent.setAdapter(mAdapter);
        mLsvContent.setOnItemClickListener(this);
    }

    private View adapterGetView(int position, View convertView) {
        Holder holder;
        if (convertView == null) {
            convertView = getLayoutInflater().inflate(R.layout.detector_result_item, null);
            holder = new Holder();
            holder.txvBookName = (ComplexBookNameView) convertView.findViewById(R.id.txvBookName);
            holder.txvMemberInfo = (TextView) convertView.findViewById(R.id.txvMemberInfo);
            holder.txvDistance = (TextView) convertView.findViewById(R.id.txvDistance);
            convertView.setTag(holder);
        } else {
            holder = (Holder) convertView.getTag();
        }

        BookByLocation book = mAdapter.getItem(position);

        holder.txvBookName.setBook(book);
        holder.txvBookName.setSatisficer(this);


        if (book.getDistance() >= 1000) {
            String distance = StringUtil.ONE_DECIMAL_POINT_DF.format(book.getDistance() / 1000f);
            holder.txvDistance.setText(String.format(getString(R.string.detector_result_distance_unit_km), distance));
        } else {
            holder.txvDistance.setText(String.format(getString(R.string.detector_result_distance_unit_m), book.getDistance()));
        }


        holder.txvMemberInfo.setText(String.format(
                getResources().getString(R.string.detector_result_book_membership), book.getMemberId()));


        StateListDrawable states = new StateListDrawable();
        convertView.setBackgroundDrawable(states);
        if (position % 2 == 0) {
            states.addState(new int[]{android.R.attr.state_pressed}, getResources().getDrawable(R.color.finder_booklist_bg_pressed));
            states.addState(new int[]{android.R.attr.state_enabled}, getResources().getDrawable(R.color.detector_result_bg));
        } else {
            states.addState(new int[]{android.R.attr.state_pressed}, getResources().getDrawable(R.color.finder_booklist_bg_pressed));
            states.addState(new int[]{android.R.attr.state_enabled}, getResources().getDrawable(R.color.detector_result_item_even_bg));
        }

        return convertView;
    }

    @Override
    public String getBookName(BookByLocation book) {
        return book.getName();
    }

    @Override
    public boolean isFirstConditionSatisfy(BookByLocation book) {
        return book.isFinished();
    }

    @Override
    public int firstConditionLabel(BookByLocation book) {
        return R.string.book_status_tip_finished;
    }

    @Override
    public boolean isSecondConditionSatisfy(BookByLocation book) {
        return true;
    }

    @Override
    public int secondConditionLabel(BookByLocation book) {
        if (book.isBothType()) return R.string.book_status_tip_both_type;
        return book.isTextBook() ? R.string.book_status_tip_text_book : R.string.book_status_tip_voice_book;
    }

    class Holder {
        ComplexBookNameView<BookByLocation> txvBookName;
        TextView txvMemberInfo, txvDistance;
    }

    @Override
    public void onResume() {
        if (mAdapter.getItemCount() == 0) mLsvContent.triggerLoadNextPage();
        super.onResume();
    }

    @Override
    public boolean onLoadNextPage() {
        if (mHasNextPage) {
            Netroid.getBookListByLocation(++mPageNum, new Listener<PaginationList<BookByLocation>>() {
                @Override
                public void onFinish() {
                    mLsvContent.finishLoadNextPage();
                }

                @Override
                public void onSuccess(PaginationList<BookByLocation> bookList) {
                    mHasNextPage = bookList.hasNextPage();
                    mAdapter.addLast(bookList);
                }

                @Override
                public void onError(NetroidError error) {
                    showToastMsg(R.string.without_data);
                    mPageNum--;
                }
            });
            return true;
        }
        return false;
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        BookByLocation book = (BookByLocation) parent.getItemAtPosition(position);
        if (book != null) {
            Intent intent = new Intent(this, BookInfoActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra(Const.BOOK_ID, book.getId());
            startActivity(intent);
        }
    }

    @Override
    public boolean onLoadPrevPage() {
        return false;
    }

    @Override
    public boolean hasPrevPage() {
        return false;
    }

    @Override
    public boolean hasNextPage() {
        return mHasNextPage;
    }
}