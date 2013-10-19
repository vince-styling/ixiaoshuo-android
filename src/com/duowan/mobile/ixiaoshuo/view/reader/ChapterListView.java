package com.duowan.mobile.ixiaoshuo.view.reader;

import android.os.AsyncTask;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.event.YYReader;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;
import com.duowan.mobile.ixiaoshuo.view.EndlessListAdapter;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

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
				mAdapter.addAll(chapterList);
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
		getActivity().getReadingBoard().adjustReadingProgress((Chapter) parent.getItemAtPosition(position));
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
