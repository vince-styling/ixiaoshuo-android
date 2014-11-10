package com.vincestyling.ixiaoshuo.view.reader;

import android.content.Context;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.duowan.mobile.netroid.NetroidError;
import com.duowan.mobile.netroid.NetworkResponse;
import com.duowan.mobile.netroid.Response;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.ReaderSupport;
import com.vincestyling.ixiaoshuo.net.request.BasicRequest;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.ui.AbsDialog;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;

import java.util.List;

public class BookChapterListDialog extends AbsDialog implements AdapterView.OnItemClickListener, View.OnClickListener {
    protected ReaderActivity mReaderActivity;
    private EndlessListAdapter<Chapter> mAdapter;
    private ListView mLsvChapterList;

    public BookChapterListDialog(ReaderActivity activity) {
        super(activity);
        mReaderActivity = activity;

        mContentView = (ViewGroup) getLayoutInflater().inflate(R.layout.reading_board_chapter_list, null);

        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        initContentView(new ViewGroup.LayoutParams(display.getWidth(), display.getHeight()));
        init();
    }

    public void init() {
        mContentView.findViewById(R.id.btnClose).setOnClickListener(this);

        mLsvChapterList = (ListView) mContentView.findViewById(R.id.lsvChapterList);
        mLsvChapterList.setOnItemClickListener(this);

        mAdapter = new EndlessListAdapter<Chapter>() {
            @Override
            protected View getView(int position, View convertView) {
                TextView txvChapterTitle;
                if (convertView == null) {
                    convertView = getLayoutInflater().inflate(R.layout.reader_chapter_list_item, null);
                    txvChapterTitle = (TextView) convertView.findViewById(R.id.txvChapterTitle);
                    convertView.setTag(txvChapterTitle);
                } else {
                    txvChapterTitle = (TextView) convertView.getTag();
                }

                Chapter chapter = getItem(position);
                txvChapterTitle.setTextColor(chapter.isReading() ? 0xff76ac1c : 0xff626262);
                txvChapterTitle.setText(chapter.getTitle());

                return convertView;
            }

            @Override
            protected View initProgressView() {
                return getLayoutInflater().inflate(R.layout.contents_loading, null);
            }
        };
        mLsvChapterList.setAdapter(mAdapter);
        mAdapter.setIsLoadingData(true);

        new BasicRequest<List<Chapter>>(new Listener<List<Chapter>>() {
            @Override
            public void onSuccess(List<Chapter> chapterList) {
                mAdapter.setIsLoadingData(false);
                mAdapter.addLast(chapterList);
                for (int i = 0; i < mAdapter.getItemCount(); i++) {
                    if (mAdapter.getItem(i).isReading()) {
                        mLsvChapterList.setSelection(i > 1 ? i - 2 : i);
                        break;
                    }
                }
            }
        }) {
            private List<Chapter> chapterList;

            @Override
            public NetworkResponse perform() {
                chapterList = ReaderSupport.getChapterList();
                return new NetworkResponse(null, null);
            }

            @Override
            protected Response<List<Chapter>> parseNetworkResponse(NetworkResponse response) {
                if (chapterList != null && chapterList.size() > 0)
                    return Response.success(chapterList, response);
                return Response.error(new NetroidError());
            }
        };
    }

    @Override
    public void onClick(View v) {
        cancel();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Chapter chapter = mAdapter.getItem(position);
        chapter.ready(ReaderSupport.getBookId());
        mReaderActivity.getReadingBoard().turnToChapter(chapter);
        cancel();
    }
}
