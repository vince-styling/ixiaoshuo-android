package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.duowan.mobile.ixiaoshuo.R;

public class TitleBarLayout extends LinearLayout {
	public TitleBarLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
	}

	public TitleBarLayout(Context context) {
		super(context);
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		repeat(canvas, R.drawable.title_bg);
		repeat(canvas, R.drawable.title_bg_wrap_line);
	}

	private void repeat(Canvas canvas, int resId) {
		Bitmap bgBitmap = BitmapFactory.decodeResource(getResources(), resId);
		Rect dstRect = new Rect();

		dstRect.top = getHeight() - bgBitmap.getHeight();
		if (dstRect.top < 0) dstRect.top = 0;
		dstRect.bottom = getHeight();

		int repeatCount = (getWidth() - 1) / bgBitmap.getWidth() + 1;
		for (int i = 0; i < repeatCount; i++) {
			dstRect.left = i * bgBitmap.getWidth();
			dstRect.right = dstRect.left + bgBitmap.getWidth();
			canvas.drawBitmap(bgBitmap, null, dstRect, null);
		}
	}

}
