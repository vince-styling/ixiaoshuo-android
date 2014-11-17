package com.vincestyling.ixiaoshuo.pojo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

public class ColorScheme {
    private String name;
    private int resourceId, textColor;
    private boolean isPureColor;

    public ColorScheme(String name, int resourceId, int textColor, boolean isPureColor) {
        this.name = name;
        this.isPureColor = isPureColor;
        this.resourceId = resourceId;
        this.textColor = textColor;
    }

    public Drawable getSchemeDrawable(Resources res) {
        if (isPureColor) {
            return new ColorDrawable(resourceId);
        } else {
            return new BitmapDrawable(BitmapFactory.decodeResource(res, resourceId));
        }
    }

    public Bitmap getResizeSchemeBitmap(Resources res, int dstWidth, int dstHeight) {
        if (isPureColor) {
            Bitmap bitmap = Bitmap.createBitmap(dstWidth, dstHeight, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(resourceId);
            return bitmap;
        }
        return Bitmap.createScaledBitmap(
                BitmapFactory.decodeResource(res, resourceId), dstWidth, dstHeight, false);
    }

    public int getTextColor() {
        return textColor;
    }

    public String getName() {
        return name;
    }
}
