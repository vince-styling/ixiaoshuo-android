package com.vincestyling.ixiaoshuo.pojo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import com.vincestyling.ixiaoshuo.utils.BitmapUtil;

public class ColorScheme {
	private int resourceId, textColor;
	private boolean isPureColor;

	public ColorScheme(int resourceId, int textColor, boolean isPureColor) {
		this.isPureColor = isPureColor;
		this.resourceId = resourceId;
		this.textColor = textColor;
	}

	public Bitmap getMenuBitmap(Resources res, int reqWidth, int reqHeight) {
		return BitmapUtil.loadBitmapInRes(res, resourceId, reqWidth, reqHeight);
	}

	private Drawable readingDrawable;

	public Drawable getReadingDrawable(Resources res, int reqWidth, int reqHeight) {
		if (readingDrawable == null) {
			if (isPureColor) {
				readingDrawable = new ColorDrawable(resourceId);
			} else {
				readingDrawable = new BitmapDrawable(BitmapUtil.loadBitmapInRes(res, resourceId, reqWidth, reqHeight));
			}
		}
		return readingDrawable;
	}

	public int getTextColor() {
		return textColor;
	}

	public void setTextColor(int textColor) {
		this.textColor = textColor;
	}

}
