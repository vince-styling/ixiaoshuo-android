package com.duowan.mobile.ixiaoshuo.reader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.BookOnUpdate;
import com.duowan.mobile.ixiaoshuo.view.BookshelfBaseView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfEmulateStyleView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfListStyleView;

import java.util.ArrayList;
import java.util.List;

public class BookshelfActivity extends BaseActivity {
	private BookshelfBaseView mBookshelfView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_shelf);

		mBookshelfView = new BookshelfEmulateStyleView();
		mBookshelfView.init(this, findViewById(R.id.lsvBookShelf), Book.getStaticBookList());

		final RadioGroup btnStyleSwitchGrp = (RadioGroup) findViewById(R.id.btnStyleSwitchGrp);
		btnStyleSwitchGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedBtnId) {
				switch (checkedBtnId) {
					case R.id.btnEmulateStyle:
						mBookshelfView = new BookshelfEmulateStyleView(mBookshelfView);
						break;
					case R.id.btnListStyle:
						mBookshelfView = new BookshelfListStyleView(mBookshelfView);
						break;
				}
			}
		});

		final RadioButton btnCleanArrow = (RadioButton) findViewById(R.id.btnCleanArrow);
		final LinearLayout lotMyBookShelf = (LinearLayout) findViewById(R.id.lotMyBookShelf);
		lotMyBookShelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout lotBookShelfClean = (LinearLayout) findViewById(R.id.lotBookShelfClean);
				if(lotBookShelfClean.getVisibility() == View.GONE) {
					lotBookShelfClean.setVisibility(View.VISIBLE);
					btnCleanArrow.setChecked(true);
				} else {
					lotBookShelfClean.setVisibility(View.GONE);
					btnCleanArrow.setChecked(false);
				}
			}
		});

		Button btnUpdateBookShelf = (Button) findViewById(R.id.btnUpdateBookShelf);
		btnUpdateBookShelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				final List<BookOnUpdate> list = new ArrayList<BookOnUpdate>(4);
				list.add(new BookOnUpdate(1902, 4, 189092));
				list.add(new BookOnUpdate(2912, 2, 189543));
				list.add(new BookOnUpdate(5389, 1, 908211));
				list.add(new BookOnUpdate(8062, 3, 883134));
				list.add(new BookOnUpdate(1489, 1, 998712));
				list.add(new BookOnUpdate(9971, 5, 661832));
				(new AsyncTask<Void, Void, List<BookOnUpdate>>() {
					@Override
					protected List<BookOnUpdate> doInBackground(Void... params) {
						return NetService.get().syncChapterUpdateOnBookshelf(list);
					}
					@Override
					protected void onPostExecute(List<BookOnUpdate> respond) {
						System.out.println(respond);
					}
				}).execute();
			}
		});
	}

}
