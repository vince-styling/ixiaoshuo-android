package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.vincestyling.ixiaoshuo.R;

public class WithoutBookStatisticsView extends LinearLayout {
	private int[] mNumRes = {
			R.drawable.book_shelf_num0,
			R.drawable.book_shelf_num1,
			R.drawable.book_shelf_num2,
			R.drawable.book_shelf_num3,
			R.drawable.book_shelf_num4,
			R.drawable.book_shelf_num5,
			R.drawable.book_shelf_num6,
			R.drawable.book_shelf_num7,
			R.drawable.book_shelf_num8,
			R.drawable.book_shelf_num9
	};

	public WithoutBookStatisticsView(Context context) {
		super(context);
	}

	public WithoutBookStatisticsView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public void setBookCount(int bookCount) {
		removeAllViews();
		String bookCountStr = String.valueOf(bookCount);
		for (int i = 0; i < bookCountStr.length(); i++) {
			ImageView numView = new ImageView(getContext());
			numView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			numView.setImageResource(mNumRes[Character.getNumericValue(bookCountStr.charAt(i))]);
			addView(numView);
		}
	}

}
