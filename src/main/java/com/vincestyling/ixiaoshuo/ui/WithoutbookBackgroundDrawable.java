package com.vincestyling.ixiaoshuo.ui;

import android.content.res.Resources;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import com.vincestyling.ixiaoshuo.R;

import java.lang.ref.WeakReference;

public class WithoutbookBackgroundDrawable extends Drawable {
	private final Paint mBGPaint;
	private BitmapDrawable mDrawable;
	private final Paint mBorderPaint;

	private final RectF mDrawableRect = new RectF();
	private final RectF mBorderRect = new RectF();
	private final Rect mBounds = new Rect();

	private float mCornerRadius;
	private float mBorderWidth;

	public WithoutbookBackgroundDrawable(Resources res) {
		mDrawable = (BitmapDrawable) res.getDrawable(R.drawable.book_shelf_without_book_stripe);
		mDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);

		mCornerRadius = res.getDimension(R.dimen.without_book_container_bg_corner_radius);
		mBorderWidth = res.getDimension(R.dimen.without_book_container_bg_border);

		mBGPaint = new Paint();
		mBGPaint.setAntiAlias(true);
		mBGPaint.setStyle(Paint.Style.FILL);

		mBorderPaint = new Paint();
		mBorderPaint.setAntiAlias(true);
		mBorderPaint.setStyle(Paint.Style.STROKE);
		mBorderPaint.setStrokeWidth(mBorderWidth);
		mBorderPaint.setColor(res.getColor(R.color.without_book_container_bg_border));
	}

	@Override
	protected void onBoundsChange(Rect bounds) {
		super.onBoundsChange(bounds);
		mBounds.set(bounds);
		mInitiated = false;

		mBorderRect.set(mBounds);
		mBorderRect.inset(mBorderWidth / 2, mBorderWidth / 2);
		mDrawableRect.set(mBorderRect);
	}

	private WeakReference<Bitmap> mBackgroundRef;
	private boolean mInitiated;

	private void redrawIfNecessary() {
		if (mInitiated && mBackgroundRef != null && mBackgroundRef.get() != null) {
			if (!mBackgroundRef.get().isRecycled()) return;
		}

		Bitmap bitmap = Bitmap.createBitmap(mBounds.width(), mBounds.height(), Bitmap.Config.RGB_565);
		Canvas drawCanvas = new Canvas(bitmap);
		mDrawable.setBounds(mBounds);
		mDrawable.draw(drawCanvas);

		mBackgroundRef = new WeakReference<Bitmap>(bitmap);
		mBGPaint.setShader(new BitmapShader(bitmap, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP));
		mInitiated = true;
	}

	@Override
	public void draw(Canvas canvas) {
		redrawIfNecessary();
		canvas.drawRoundRect(mDrawableRect, mCornerRadius, mCornerRadius, mBGPaint);
		canvas.drawRoundRect(mBorderRect, mCornerRadius, mCornerRadius, mBorderPaint);
	}

	@Override
	public int getOpacity() {
		return PixelFormat.TRANSLUCENT;
	}

	@Override
	public void setAlpha(int alpha) {
		mBGPaint.setAlpha(alpha);
		invalidateSelf();
	}

	@Override
	public void setColorFilter(ColorFilter cf) {
		mBGPaint.setColorFilter(cf);
		invalidateSelf();
	}

	@Override
	public void setDither(boolean dither) {
		mBGPaint.setDither(dither);
		invalidateSelf();
	}

	@Override
	public void setFilterBitmap(boolean filter) {
		mBGPaint.setFilterBitmap(filter);
		invalidateSelf();
	}

	@Override
	public int getIntrinsicWidth() {
		return mBounds.width();
	}

	@Override
	public int getIntrinsicHeight() {
		return mBounds.height();
	}

	public void setCornerRadius(float radius) {
		mCornerRadius = radius;
	}

	public void setBorderWidth(float width) {
		mBorderPaint.setStrokeWidth(width);
		mBorderWidth = width;
	}

	public void setBorderColor(int color) {
		mBorderPaint.setColor(color);
	}
}
