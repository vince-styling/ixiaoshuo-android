package com.duowan.mobile.ixiaoshuo.view;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

/**
 * When you want to stretch the width to 100% of the View, but keep the width/height ratio intact, use it
 * from : http://stackoverflow.com/a/6410692/1294681
 * author : vince
 */
public class ScalableImageView extends ImageView {
	public ScalableImageView(Context context) {
		super(context);
	}

	public ScalableImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ScalableImageView(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		try {
			Drawable drawable = getDrawable();
			if (drawable == null) {
				setMeasuredDimension(0, 0);
			} else {
				int width = MeasureSpec.getSize(widthMeasureSpec);
				int height = width * drawable.getIntrinsicHeight() / drawable.getIntrinsicWidth();
				setMeasuredDimension(width, height);
			}
		} catch (Exception e) {
			super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		}
	}

//	private Bitmap mBitmap;
//	public void setImageBitmap(Bitmap bitmap) {
//		mBitmap = bitmap;
//	}
//
//	@Override
//	protected void onDraw(Canvas canvas) {
//		Rect src = new Rect(0, 0, mBitmap.getWidth(), mBitmap.getHeight());
//		Rect dst = new Rect(0, 0, getWidth(), getHeight());
//		canvas.drawBitmap(mBitmap, src, dst, null);
//	}

}