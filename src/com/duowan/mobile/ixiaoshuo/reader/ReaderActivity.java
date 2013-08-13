package com.duowan.mobile.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.ColorScheme;
import com.duowan.mobile.ixiaoshuo.ui.ReadingBoard;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;
import com.duowan.mobile.ixiaoshuo.utils.ViewUtil;

import java.util.Date;

public class ReaderActivity extends BaseActivity {
	private static final String TAG = "ReaderActivity";
	private ReadingBoard mReadingBoard;
	private TextView mTxvCurTime, mTxvReadingInfo, mTxvReadingProgress;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtil.setFullScreen(this);
		setContentView(R.layout.reading_board);

		int bid = Integer.parseInt(getIntent().getAction());
		Book book = AppDAO.get().getBookForReader(bid);

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
			mReadingBoard.init(book);

			initStatusBar();

			setColorScheme(new ColorScheme(R.drawable.reading_bg_1, 0xff543927));
		} catch (Exception e) {
			showErrorConfirmDialog("初始化失败！");
			Log.e(TAG, e.getMessage(), e);
			return;
		}

		mGestureDetector = new GestureDetector(new ReaderGestureListener());
	}

	@Override
	public boolean onTouchEvent(MotionEvent event) {
		return mGestureDetector.onTouchEvent(event);
	}

	private GestureDetector mGestureDetector;
	class ReaderGestureListener extends SimpleOnGestureListener {
		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			float x = e.getX();
			float y = e.getY();
			float portionWidth = mReadingBoard.getWidth() / 3;
			float portionHeight = mReadingBoard.getHeight() / 3;
			if(x < portionWidth || (x < portionWidth * 2 && y < portionHeight)) {
				if(!mReadingBoard.turnPreviousPageAndRedraw()) {
					if (!mReadingBoard.hasPreviousChapter()) showToastMsg("已到达第一页");
				}
			} else if (x > portionWidth * 2 || (x > portionWidth && y > portionHeight * 2)) {
				if(!mReadingBoard.turnNextPageAndRedraw()) {
					if (!mReadingBoard.hasNextChapter()) showToastMsg("已到达最后一页");
				}
			}
			return true;
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

	ProgressDialog mExitDialog;

	@Override
	public void finish() {
		mExitDialog = ProgressDialog.show(this, null, "正在退出阅读...", false, false);
		super.finish();
	}

	protected void onPause() {
		super.onPause();

		if(mReadingBoard == null) return;
		AppDAO.get().persistReadingStatistics(mReadingBoard.getBook());

		if(isFinishing()) {
			sendBookShelfRefreshMessage();
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
