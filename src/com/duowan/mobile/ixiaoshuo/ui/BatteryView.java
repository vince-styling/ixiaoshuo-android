package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;

public class BatteryView extends View {
	Paint mPaint = new Paint(Paint.DEV_KERN_TEXT_FLAG);
	private float mBorderWidth;
	private float mBatteryPercentage;

	public BatteryView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mBorderWidth = getResources().getDimension(R.dimen.reading_board_battery_border_width);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		RectF rect1 = new RectF();
		RectF rect2 = new RectF();

		float width = getWidth() - mBorderWidth;
		float height = getHeight();
		rect1.set(0, 0, width, height);
		rect2.set(mBorderWidth, mBorderWidth, width - mBorderWidth, height - mBorderWidth);

		canvas.save(Canvas.CLIP_SAVE_FLAG);
		canvas.clipRect(rect2, Region.Op.DIFFERENCE);
		canvas.drawRect(rect1, mPaint);
		canvas.restore();

		rect2.left += mBorderWidth;
		rect2.right -= mBorderWidth;
		rect2.right = rect2.left + rect2.width() * mBatteryPercentage;
		rect2.top += mBorderWidth;
		rect2.bottom -= mBorderWidth;
		canvas.drawRect(rect2, mPaint);

		int poleHeight = getHeight() / 2;
		rect2.left = rect1.right;
		rect2.top = (height - poleHeight) / 2;
		rect2.right = rect1.right + mBorderWidth;
		rect2.bottom = rect2.top + poleHeight;
		canvas.drawRect(rect2, mPaint);
	}

	public void setColor(int color) {
		mPaint.setColor(color);
	}

	public void setBatteryPercentage(float batteryPercentage) {
		mBatteryPercentage = batteryPercentage;
		invalidate();
	}

}
