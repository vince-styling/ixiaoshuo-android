package com.duowan.mobile.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.utils.ViewUtil;

public class ReaderActivity extends BaseActivity {
	private Book mBook;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtil.setFullScreen(this);
		setContentView(R.layout.reading_board);

		init(Integer.parseInt(getIntent().getAction()));
	}

	private void init(int bid) {
		mBook = AppDAO.get().getBookForReader(bid);

		if (mBook == null) {
			showErrorConfirmDialog("书籍不存在！");
			return;
		}
		if (!mBook.hasChapters()) {
			showErrorConfirmDialog("书籍无可读章节！");
			return;
		}

		final Chapter chapter = mBook.getReadingChapter();
		NetService.execute(new NetService.NetExecutor<String>() {
			ProgressDialog mPrgreDialog;

			public void preExecute() {
				if (NetService.get().isNetworkAvailable()) {
					mPrgreDialog = ProgressDialog.show(ReaderActivity.this, null, ReaderActivity.this.getString(R.string.loading_tip_msg), true, true);
				} else {
					ReaderActivity.this.showToastMsg(R.string.network_disconnect_msg);
				}
			}

			public String execute() {
				return NetService.get().getChapterContent(mBook.getBookId(), chapter.getId());
			}

			public void callback(String content) {
				if (mPrgreDialog != null) {
					if (!mPrgreDialog.isShowing()) return;
					mPrgreDialog.cancel();
				}
				Log.e("CONTENT", content);
			}
		});
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
