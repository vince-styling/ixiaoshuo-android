package com.duowan.mobile.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.event.Notifier;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.pojo.ColorScheme;
import com.duowan.mobile.ixiaoshuo.ui.ReadingBoard;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;
import com.duowan.mobile.ixiaoshuo.utils.ViewUtil;

import java.util.Date;

public class ReaderActivity extends BaseActivity {
	private static final String TAG = "ReaderActivity";
	private ReadingBoard mReadingBoard;
	private ListView mLsvChapterList;
	private TextView mTxvCurTime, mTxvReadingInfo, mTxvReadingProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtil.setFullScreen(this);
		setContentView(R.layout.reading_board);

		int bid = Integer.parseInt(getIntent().getAction());
		final Book book = AppDAO.get().getBookForReader(bid);

		if (book == null) {
			showErrorConfirmDialog("书籍不存在！");
			return;
		}
		if (!book.hasChapters()) {
			showErrorConfirmDialog("书籍无可读章节！");
			return;
		}

		try {
			mReadingBoard = (ReadingBoard) findViewById(R.id.readingBoard);
			mReadingBoard.setFocusable(true);
			mReadingBoard.init(book);

			initStatusBar();

			setColorScheme(new ColorScheme(R.drawable.reading_bg_1, 0xff543927));

			mLsvChapterList = (ListView) findViewById(R.id.lsvChapterList);
			mLsvChapterList.setAdapter(new ArrayAdapter<Chapter>(this, 0, book.getChapterList()) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (convertView == null) {
						convertView = getLayoutInflater().inflate(R.layout.book_info_chapter_list_item, null);
					}
					Chapter chapter = getItem(position);
					((TextView) convertView).setText(chapter.getTitle());
					return convertView;
				}
			});
			mLsvChapterList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
				@Override
				public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
					mLsvChapterList.setVisibility(View.GONE);
					mReadingBoard.adjustReadingProgress((Chapter) parent.getItemAtPosition(position));
					mLsvChapterList.setSelection(position);
				}
			});
		} catch (Exception e) {
			showErrorConfirmDialog("初始化失败！");
			Log.e(TAG, e.getMessage(), e);
			return;
		}
	}

	private void initStatusBar() {
		mTxvCurTime = (TextView) findViewById(R.id.txvCurTime);
		mTxvReadingInfo = (TextView) findViewById(R.id.txvReadingInfo);
		mTxvReadingProgress = (TextView) findViewById(R.id.txvReadingProgress);
	}

	public void refreshStatusBar(String readingInfo, float readingProgress) {
		mTxvReadingInfo.setText(readingInfo);
		mTxvCurTime.setText(StringUtil.dfTime.format(new Date()));
		mTxvReadingProgress.setText(StringUtil.TWO_DECIMAL_POINT_DF.format(readingProgress) + '%');
	}

	public void setColorScheme(ColorScheme colorScheme) {
		colorScheme.initReadingDrawable(getApplicationContext());
		mReadingBoard.setColorScheme(colorScheme);

		mTxvCurTime.setTextColor(colorScheme.getTextColor());
		mTxvReadingInfo.setTextColor(colorScheme.getTextColor());
		mTxvReadingProgress.setTextColor(colorScheme.getTextColor());
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (mLsvChapterList.getVisibility() == View.VISIBLE) {
					mLsvChapterList.setVisibility(View.GONE);
				} else {
					finish();
				}
				break;
			case KeyEvent.KEYCODE_MENU:
				if (mLsvChapterList.getVisibility() == View.GONE) {
					mLsvChapterList.setVisibility(View.VISIBLE);
				} else {
					mLsvChapterList.setVisibility(View.GONE);
				}
				break;
		}
		return true;
	}

	ProgressDialog mExitDialog;

	@Override
	public void finish() {
		mExitDialog = ProgressDialog.show(this, null, "正在退出阅读...", false, false);
		super.finish();
	}

	protected void onPause() {
		super.onPause();

		if (mReadingBoard == null) return;
		AppDAO.get().persistReadingStatistics(mReadingBoard.getBook());

		if (isFinishing()) {
			getReaderApplication().getMainHandler().sendMessage(Notifier.NOTIFIER_BOOKSHELF_REFRESH);
		}
	}

	protected void onStop() {
		if(mExitDialog != null) mExitDialog.cancel();
		super.onStop();
	}

	private void showErrorConfirmDialog(String message) {
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage(message);
		builder.setPositiveButton("退出阅读", new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
				finish();
			}
		});
		builder.show();
	}

}
