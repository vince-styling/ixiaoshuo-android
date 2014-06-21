package com.vincestyling.ixiaoshuo.view.reader;

import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.YYReader;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.view.EndlessListAdapter;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

import java.util.List;

public class ChapterListView extends ViewBuilder implements AdapterView.OnItemClickListener {
	private EndlessListAdapter<Chapter> mAdapter;
	private AsyncTask<Void, Void, List<Chapter>> mAsysTask;

	public ChapterListView(ReaderActivity activity, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		setActivity(activity);

		setOutAnimation(R.anim.fade_out);
		setInAnimation(R.anim.fade_in);

		build();
		init();
	}

	@Override
	protected void build() {
		mView = getActivity().findViewById(R.id.lotChapterList);
	}

	@Override
	public void init() {
		getListView().setOnItemClickListener(this);
		findViewById(R.id.btnClose).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				pushBack();
			}
		});
	}

	@Override
	public void resume() {
		if (mIsInFront) return;

		mAdapter = new EndlessListAdapter<Chapter>() {
			@Override
			protected View getView(int position, View convertView) {
				if (convertView == null) {
					convertView = getActivity().getLayoutInflater().inflate(R.layout.reader_chapter_list_item, null);
				}

				Chapter chapter = getItem(position);

				TextView txvChapterTitle = (TextView) convertView.findViewById(R.id.txvChapterTitle);
				txvChapterTitle.setText(chapter.getTitle());
				txvChapterTitle.setTextColor(
						getActivity().getResources().getColor(
								chapter.isReading() ? R.color.reading_board_chapter_list_item_reading : R.color.reading_board_chapter_list_item
						));
				return convertView;
			}

			@Override
			protected View initProgressView() {
				return getActivity().getLayoutInflater().inflate(R.layout.contents_loading, null);
			}
		};
		getListView().setAdapter(mAdapter);

		super.resume();

		mAsysTask = new AsyncTask<Void, Void, List<Chapter>>() {
			@Override
			protected void onPreExecute() {
				mAdapter.setIsLoadingData(true);
			}

			@Override
			protected List<Chapter> doInBackground(Void... params) {
				return YYReader.getChapterList();
			}

			@Override
			protected void onPostExecute(List<Chapter> chapterList) {
				if (mAdapter == null) return;

				mAdapter.setIsLoadingData(false);
				mAdapter.addLast(chapterList);
				for (int i = 0; i < mAdapter.getItemCount(); i++) {
					if (mAdapter.getItem(i).isReading()) {
						getListView().setSelection(i > 1 ? i - 2 : i);
						break;
					}
				}
			}
		};
		mAsysTask.execute();
	}

	@Override
	public void pushBack() {
		super.pushBack();
		if (mAsysTask != null) {
			mAsysTask.cancel(true);
			mAsysTask = null;
		}
		if (mAdapter != null) {
			mAdapter.clear();
			mAdapter = null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		getActivity().getReadingBoard().turnToChapter((Chapter) parent.getItemAtPosition(position));
		pushBack();
	}

	private ListView getListView() {
		return (ListView) mView.findViewById(R.id.lsvChapterList);
	}

	@Override
	public ReaderActivity getActivity() {
		return (ReaderActivity) super.getActivity();
	}

}
