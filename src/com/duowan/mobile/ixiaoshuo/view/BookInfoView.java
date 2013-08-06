package com.duowan.mobile.ixiaoshuo.view;

import android.view.View;
import android.view.ViewGroup;
import android.widget.*;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.db.AppDAO;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.utils.BookCoverDownloder;

public class BookInfoView extends ViewBuilder implements View.OnClickListener, AdapterView.OnItemClickListener {
	private Book mBook;

	public BookInfoView(MainActivity activity, Book book) {
		this.mViewId = R.id.lotBookInfo;
		this.mActivity = activity;
		this.mBook = book;
	}

	@Override
	protected void build() {
		mView = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.book_info, null);

		ImageView imvBookCover = (ImageView) findViewById(R.id.imvBookCover);
		BookCoverDownloder.loadCover(mActivity, mBook, imvBookCover);

		TextView txvBookName = (TextView) findViewById(R.id.txvBookName);
		txvBookName.setText(mBook.getName());

		TextView txvBookAuthor = (TextView) findViewById(R.id.txvBookAuthor);
		txvBookAuthor.setText(mBook.getAuthor());

		TextView txvBookSummary = (TextView) findViewById(R.id.txvBookSummary);
		txvBookSummary.setText(mBook.getSummary());

		Button btnGoRead = (Button) findViewById(R.id.btnGoRead);
		btnGoRead.setOnClickListener(this);

		if (mBook.hasChapters()) {
			ListView lsvChapterList = (ListView) findViewById(R.id.lsvChapterList);
			lsvChapterList.setAdapter(new ArrayAdapter<Chapter>(mActivity, 0, mBook.getChapterList()) {
				@Override
				public View getView(int position, View convertView, ViewGroup parent) {
					if (convertView == null) {
						convertView = mActivity.getLayoutInflater().inflate(R.layout.book_info_chapter_list_item, null);
					}
					((TextView) convertView).setText(getItem(position).getTitle());
					return convertView;
				}
			});
			lsvChapterList.setOnItemClickListener(this);
		}
	}

	@Override
	public void onClick(View btnView) {
		int bid = AppDAO.get().addBook(mBook);
		if(bid > 0 && mBook.hasChapters()) {
			AppDAO.get().saveBookChapterList(bid, mBook.getChapterList());
		}
		mActivity.showToastMsg("Success Goto Reader!");
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		onClick(view);
	}

	@Override
	public boolean isReusable() {
		return false;
	}

}
