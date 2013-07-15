package com.duowan.mobile.ixiaoshuo.reader;

import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.view.BookshelfEmulateStyleView;

public class BookshelfActivity extends BaseActivity {
	private BookshelfEmulateStyleView mBookshelfView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.book_shelf_emulate_style);

		final RadioButton btnCleanArrow = (RadioButton) findViewById(R.id.btnCleanArrow);

		mBookshelfView = new BookshelfEmulateStyleView(this, findViewById(R.id.lsvBookShelf));
		mBookshelfView.initBookShelf(Book.getStaticBookList());

		final RadioGroup btnStyleSwitchGrp = (RadioGroup) findViewById(R.id.btnStyleSwitchGrp);
		btnStyleSwitchGrp.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedBtnId) {
				switch (checkedBtnId) {
					case R.id.btnEmulateStyle:
						getReaderApplication().showToastMsg("btnEmulateStyle");
						btnCleanArrow.toggle();
						btnCleanArrow.setChecked(true);
						break;
					case R.id.btnListStyle:
						getReaderApplication().showToastMsg("btnListStyle");
						btnCleanArrow.setChecked(false);
						break;
				}
			}
		});

		final LinearLayout lotMyBookShelf = (LinearLayout) findViewById(R.id.lotMyBookShelf);
		lotMyBookShelf.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				LinearLayout lotBookShelfClean = (LinearLayout) findViewById(R.id.lotBookShelfClean);
				if(lotBookShelfClean.getVisibility() == View.GONE) {
					lotBookShelfClean.setVisibility(View.VISIBLE);
				} else {
					lotBookShelfClean.setVisibility(View.GONE);
				}
			}
		});
	}

}
