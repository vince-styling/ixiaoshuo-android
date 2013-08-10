package com.duowan.mobile.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.ui.ReadingBoard;
import com.duowan.mobile.ixiaoshuo.utils.ViewUtil;

public class ReaderActivity extends BaseActivity {
	private static final String TAG = "ReaderActivity";
	private ReadingBoard mReadingBoard;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtil.setFullScreen(this);
		setContentView(R.layout.reading_board);

		int bid = Integer.parseInt(getIntent().getAction());
		Book mBook = AppDAO.get().getBookForReader(bid);

		if (mBook == null) {
			showErrorConfirmDialog("书籍不存在！");
			return;
		}
		if (!mBook.hasChapters()) {
			showErrorConfirmDialog("书籍无可读章节！");
			return;
		}

		try {
			mReadingBoard = (ReadingBoard) findViewById(R.id.readingBoard);
			mReadingBoard.init(mBook);
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
