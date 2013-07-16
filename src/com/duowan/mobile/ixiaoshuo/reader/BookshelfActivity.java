package com.duowan.mobile.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.View;
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
	}

}
