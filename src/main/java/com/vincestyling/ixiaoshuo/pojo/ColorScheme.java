package com.vincestyling.ixiaoshuo.pojo;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
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

    public Bitmap getResizeSchemeBitmap(Resources res, int width, int height) {
        if (isPureColor) {
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
            Canvas canvas = new Canvas(bitmap);
            canvas.drawColor(resourceId);
            return bitmap;
        } else {
            Bitmap bitmap = BitmapFactory.decodeResource(res, resourceId);
            Matrix matrix = new Matrix();
            matrix.postScale(((float) width) / bitmap.getWidth(), ((float) height) / bitmap.getHeight());
            return Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, false);
        }
    }

    public int getTextColor() {
        return textColor;
    }

    public String getName() {
        return name;
    }

}
