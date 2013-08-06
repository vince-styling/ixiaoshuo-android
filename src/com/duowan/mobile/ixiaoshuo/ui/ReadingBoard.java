package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;

public class ReadingBoard extends View {
	Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

	public ReadingBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Bitmap readingBg = BitmapUtil.loadBitmapInRes(R.drawable.reading_bg_1, this);
		canvas.drawBitmap(readingBg, 0, 0, mPaint);
	}

}
