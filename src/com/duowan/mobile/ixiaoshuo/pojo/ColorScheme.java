package com.duowan.mobile.ixiaoshuo.pojo;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Display;
import android.view.WindowManager;
import com.duowan.mobile.ixiaoshuo.utils.BitmapUtil;

public class ColorScheme {
	private int resourceId, textColor;

	public ColorScheme(int resourceId, int textColor) {
		this.resourceId = resourceId;
		this.textColor = textColor;
	}

	public Bitmap getMenuBitmap(Resources res, int reqWidth, int reqHeight) {
		return BitmapUtil.loadBitmapInRes(res, resourceId, reqWidth, reqHeight);
	}

	private Drawable mReadingDrawable;

	public Drawable getReadingDrawable() {
		return mReadingDrawable;
	}

	public void initReadingDrawable(Context ctx) {
		Display display = ((WindowManager) ctx.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
		mReadingDrawable = new BitmapDrawable(BitmapUtil.loadBitmapInRes(ctx.getResources(), resourceId, display.getWidth(), display.getHeight()));
		mReadingDrawable.setBounds(new Rect(0, 0, display.getWidth(), display.getHeight()));
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}
}
