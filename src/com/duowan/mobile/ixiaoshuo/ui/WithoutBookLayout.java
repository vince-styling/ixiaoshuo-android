package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.LinearLayout;
import com.duowan.mobile.ixiaoshuo.R;

import java.util.ArrayList;
import java.util.List;

public class WithoutBookLayout extends LinearLayout {
	public WithoutBookLayout(Context context) {
		super(context);
		setWillNotDraw(false);
	}

	public WithoutBookLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
		setWillNotDraw(false);
	}

	@Override
	protected void onDraw(Canvas canvas) {
		Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
		Canvas drawCanvas = new Canvas(bitmap);

		Drawable bgDrawable = getResources().getDrawable(R.drawable.book_shelf_without_book_bg);
		bgDrawable.setBounds(0, 0, getWidth(), getHeight());
		bgDrawable.draw(drawCanvas);

		List<int[]> transparentPixels = new ArrayList<int[]>(250);
		for (int x = 0; x < bitmap.getWidth(); x++) {
			for (int y = 0; y < bitmap.getHeight(); y++) {
				if (bitmap.getPixel(x, y) == Color.TRANSPARENT) {
					transparentPixels.add(new int[]{x, y});
				} else if (y == 0) break;
			}
		}

		BitmapDrawable stripeDrawable = (BitmapDrawable) getResources().getDrawable(R.drawable.book_shelf_without_book_stripe);
		stripeDrawable.setTileModeXY(Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
		stripeDrawable.setBounds(0, 0, getWidth(), getHeight());
		stripeDrawable.draw(drawCanvas);

		bgDrawable.draw(drawCanvas);

		for (int[] pixel : transparentPixels) {
			bitmap.setPixel(pixel[0], pixel[1], Color.TRANSPARENT);
		}

		canvas.drawBitmap(bitmap, 0, 0, null);
	}

}
