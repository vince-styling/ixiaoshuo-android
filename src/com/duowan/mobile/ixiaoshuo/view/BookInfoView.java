package com.duowan.mobile.ixiaoshuo.view;

import android.app.ProgressDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.BookCoverDownloder;

public class BookInfoView extends ViewBuilder {
	private int mBookId;

	public BookInfoView(MainActivity activity, int bookId) {
		this.mViewId = R.id.lotBookInfo;
		this.mActivity = activity;
		this.mBookId = bookId;
	}

	@Override
	protected void build() {
		mView = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.book_info, null);

		NetService.execute(new NetService.NetExecutor<Book>() {
			ProgressDialog mPrgreDialog;

			public void preExecute() {
				if (NetService.get().isNetworkAvailable()) {
					mPrgreDialog = ProgressDialog.show(mActivity, null, mActivity.getString(R.string.loading_tip_msg), true, true);
				} else {
					mActivity.showToastMsg(R.string.network_disconnect_msg);
				}
			}

			public Book execute() {
				return NetService.get().getBookDetail(mBookId);
			}

			public void callback(Book book) {
				if (mPrgreDialog != null) {
					if (!mPrgreDialog.isShowing()) return;
					mPrgreDialog.cancel();
				}

				if (book == null) {
					mActivity.showToastMsg(R.string.without_data);
					return;
				}

				ImageView imvBookCover = (ImageView) findViewById(R.id.imvBookCover);
				BookCoverDownloder.loadCover(mActivity, book, imvBookCover);

				TextView txvBookName = (TextView) findViewById(R.id.txvBookName);
				txvBookName.setText(book.getName());

				TextView txvBookAuthor = (TextView) findViewById(R.id.txvBookAuthor);
				txvBookAuthor.setText(book.getAuthor());

				TextView txvBookSummary = (TextView) findViewById(R.id.txvBookSummary);
				txvBookSummary.setText(book.getSummary());

				if(book.hasChapters()) {
					ListView lsvChapterList = (ListView) findViewById(R.id.lsvChapterList);
					lsvChapterList.setAdapter(new ArrayAdapter<Chapter>(mActivity, 0, book.getChapterList()){
						@Override
						public View getView(int position, View convertView, ViewGroup parent) {
							if(convertView == null) {
								convertView = mActivity.getLayoutInflater().inflate(R.layout.book_info_chapter_list_item, null);
							}
							((TextView) convertView).setText(getItem(position).getTitle());
							return convertView;
						}
					});
				}
			}
		});
	}

	@Override
	public boolean isReusable() {
		return false;
	}

}
