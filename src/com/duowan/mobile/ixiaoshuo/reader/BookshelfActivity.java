package com.duowan.mobile.ixiaoshuo.reader;

import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.view.BookshelfBaseView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfEmulateStyleView;
import com.duowan.mobile.ixiaoshuo.view.BookshelfListStyleView;

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
				(new AsyncTask<Void, Void, Void>() {
					@Override
					protected Void doInBackground(Void... params) {
//						List<BookOnUpdate> list = new ArrayList<BookOnUpdate>(4);
//						list.add(new BookOnUpdate(1902, 4, 189092));
//						list.add(new BookOnUpdate(2912, 2, 189543));
//						list.add(new BookOnUpdate(5389, 1, 908211));
//						list.add(new BookOnUpdate(8062, 3, 883134));
//						list.add(new BookOnUpdate(1489, 1, 998712));
//						list.add(new BookOnUpdate(9971, 5, 661832));
//						List<BookUpdateInfo> updateInfoList = NetService.get().syncChapterUpdateOnBookshelf(list);
//						for (BookUpdateInfo updateInfo : updateInfoList) {
//							System.out.println("--- UpdateChapterCount : " + updateInfo.getUpdateChapterCount());
//						}

//						String content = NetService.get().getChapterContent(1984, 5, 197843);
//						System.out.println(content);

//						Book book = NetService.get().getBookDetail(1984, 5);
//						System.out.println(book);

//						List<Book> list0 = NetService.get().bookSearch("斗士", Book.STATUS_CONTINUE, 1, 10);
//						System.out.println(list0);

//						List<Book> list1 = NetService.get().getReadingBookList(1, 10);
//						System.out.println(list1);

//						List<Book> list2 = NetService.get().getBookRanking(Book.RANK_TOTAL, 1, 10);
//						System.out.println(list2);

//						List<Site> list3 = NetService.get().getSiteRankingList();
//						System.out.println(list3);

//						List<Book> list4 = NetService.get().getSiteBookRanking(2, 1, 10);
//						System.out.println(list4);

//						String[] list4 = NetService.get().getHotKeyWords();
//						System.out.println(list4.length);

//						List<Chapter> list5 = NetService.get().syncNewlyChapterOfBook(1892, 3, 190023);
//						System.out.println(list5);

//						boolean result = NetService.get().userFeedBack("非常好", "23431102910231");
//						System.out.println(result);

//						VersionUpdate verUpdate = NetService.get().getVersionInfo(12, "1.2.1");
//						System.out.println(verUpdate.getUpdateInfo());

						return null;
					}
					@Override
					protected void onPostExecute(Void respond) {
						System.out.println(respond);
					}
				}).execute();
			}
		});

		btnUpdateBookShelf.performClick();
	}

}
