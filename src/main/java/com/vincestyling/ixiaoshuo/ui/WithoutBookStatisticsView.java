package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ImageView;
import android.widget.LinearLayout;
import com.vincestyling.ixiaoshuo.R;

public class WithoutBookStatisticsView extends LinearLayout {
    public WithoutBookStatisticsView(Context context) {
        super(context);
    }

    public WithoutBookStatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected int[] getNumRes() {
        return new int[]{
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
    }

    public void setStatCount(int statCount) {
        removeAllViews();
        int[] mNumRes = getNumRes();
        String statCountStr = String.valueOf(statCount);
        for (int i = 0; i < statCountStr.length(); i++) {
            ImageView numView = new ImageView(getContext());
            numView.setLayoutParams(new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            numView.setImageResource(mNumRes[Character.getNumericValue(statCountStr.charAt(i))]);
            addView(numView);
        }
    }

}
