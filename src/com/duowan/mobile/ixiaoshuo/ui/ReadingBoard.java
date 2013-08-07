package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.util.AttributeSet;
import android.view.View;

public class ReadingBoard extends View {
	ReadingPaint mPaint;

	public ReadingBoard(Context context, AttributeSet attrs) {
		super(context, attrs);
		mPaint = new ReadingPaint(context);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		mPaint.drawBg(canvas);
	}

}
