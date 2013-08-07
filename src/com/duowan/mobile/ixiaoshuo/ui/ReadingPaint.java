package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;

public class ReadingPaint extends Paint {
	private Drawable mBgDrawable;

	public ReadingPaint(Context ctx) {
		super(ANTI_ALIAS_FLAG);

		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		Bitmap readingBg = BitmapUtil.loadBitmapInRes(ctx.getResources(), R.drawable.reading_bg_1, display.getWidth(), display.getHeight());
		mBgDrawable = new BitmapDrawable(readingBg);
		mBgDrawable.setBounds(new Rect(0, 0, display.getWidth(), display.getHeight()));
	}

	public void drawBg(Canvas canvas) {
		mBgDrawable.draw(canvas);
	}

}
