package com.duowan.mobile.ixiaoshuo.view;

import android.os.Handler;
import android.os.Message;
import android.view.ViewGroup;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;

public class BookshelfView extends ViewBuilder {
	private BookshelfBaseView mBookshelfView;

	public BookshelfView(MainActivity activity) {
		this.mViewId = R.id.lotBookshelf;
		this.mActivity = activity;
	}

	protected void build() {
		mView = (ViewGroup) mActivity.getLayoutInflater().inflate(R.layout.book_shelf, null);

		mBookshelfView = new BookshelfListStyleView();
		mBookshelfView.init(mActivity, findViewById(R.id.lsvBookShelf));

//		ToggleButton btnStyleSwitch = (ToggleButton) findViewById(R.id.btnStyleSwitch);
//		btnStyleSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//			@Override
//			public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//				if (isChecked) {
//					mBookshelfView = new BookshelfEmulateStyleView(mBookshelfView);
//				} else {
//					mBookshelfView = new BookshelfListStyleView(mBookshelfView);
//				}
//			}
//		});

		mActivity.setBookShelfRefreshHandler(new Handler() {
			@Override
			public void handleMessage(Message msg) {
				mBookshelfView.notifyDataSetChanged();
			}
		});

//		final RadioButton btnCleanArrow = (RadioButton) findViewById(R.id.btnCleanArrow);
//		final LinearLayout lotMyBookShelf = (LinearLayout) findViewById(R.id.lotMyBookShelf);
//		lotMyBookShelf.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				LinearLayout lotBookShelfClean = (LinearLayout) findViewById(R.id.lotBookShelfClean);
//				if (lotBookShelfClean.getVisibility() == View.GONE) {
//					lotBookShelfClean.setVisibility(View.VISIBLE);
//					btnCleanArrow.setChecked(true);
//				} else {
//					lotBookShelfClean.setVisibility(View.GONE);
//					btnCleanArrow.setChecked(false);
//				}
//			}
//		});

//		Button btnUpdateBookShelf = (Button) findViewById(R.id.btnUpdateBookShelf);
//		btnUpdateBookShelf.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				NetService.execute(new NetService.SimpleNetExecutor() {
//					public void execute() {
//						NetService.get().getVersionUpdateInfo();
//					}
//				});
//			}
//		});
	}

}
