package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.NinePatchDrawable;
import android.util.AttributeSet;
import android.util.SparseArray;
import android.view.View;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.BaseActivity;

public class MainMenuBar extends View {
	public MainMenuBar(Context context) {
		super(context);
		init();
	}

	public MainMenuBar(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private SparseArray<MenuItem> mMenuItems;
	public static final int MENU_BOOKSHELF	= 10;
	public static final int MENU_FINDER		= 20;
	public static final int MENU_DETECT		= 30;
	public static final int MENU_SEARCH		= 40;

	private int mPadding;
	private int mSelectedItemId;

	private void init() {
		mMenuItems = new SparseArray<MenuItem>(4);

		mMenuItems.put(MENU_BOOKSHELF, new MenuItem(R.drawable.menu_bookshelf_on, R.drawable.menu_bookshelf_off, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().showToastMsg("点击了书架菜单");
			}
		}));

		mMenuItems.put(MENU_FINDER, new MenuItem(R.drawable.menu_finder_on, R.drawable.menu_finder_off, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().showToastMsg("点击了发现菜单");
			}
		}));

		mMenuItems.put(MENU_DETECT, new MenuItem(R.drawable.menu_detect_on, R.drawable.menu_detect_off, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().showToastMsg("点击了雷达菜单");
			}
		}));

		mMenuItems.put(MENU_SEARCH, new MenuItem(R.drawable.menu_search_on, R.drawable.menu_search_off, new OnClickListener() {
			@Override
			public void onClick(View v) {
				getActivity().showToastMsg("点击了搜索菜单");
			}
		}));

		mPadding = getResources().getDimensionPixelSize(R.dimen.globalMenuPadding);

		mSelectedItemId = MENU_BOOKSHELF;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int itemWidth = getWidth() / mMenuItems.size();
		int iconDstHeight = getHeight() - mPadding * 2;

		for (int i = 0; i < mMenuItems.size(); i++) {
			int itemId = mMenuItems.keyAt(i);
			MenuItem menuItem = mMenuItems.get(itemId);

			if (itemId == mSelectedItemId) {
				Drawable highlightDrawable = getResources().getDrawable(R.drawable.menu_bg_pressed);
				highlightDrawable.setBounds(0, 0, itemWidth, getHeight());
				highlightDrawable.draw(canvas);
			}

			Bitmap iconBitmap = BitmapFactory.decodeResource(getResources(),
					itemId == mSelectedItemId ? menuItem.menuOnResId : menuItem.menuOffResId);

			int iconDstWidth = (int) (iconDstHeight * ((double) iconBitmap.getWidth() / iconBitmap.getHeight()));

			Rect srcRect = new Rect();
			srcRect.left = 0;
			srcRect.top = 0;
			srcRect.right = iconBitmap.getWidth();
			srcRect.bottom = iconBitmap.getHeight();

			Rect dstRect = new Rect();
			dstRect.left = (itemWidth - iconDstWidth) / 2;
			dstRect.top = 0;
			dstRect.right = dstRect.left + iconDstWidth;
			dstRect.bottom = iconDstHeight;

			canvas.drawBitmap(iconBitmap, srcRect, dstRect, null);
			canvas.translate(itemWidth, mPadding);
		}
		canvas.restore();
	}

	private static class MenuItem {
		int menuOffResId, menuOnResId;
		OnClickListener clickEvent;
		MenuItem(int menuOnResId, int menuOffResId, OnClickListener clickEvent) {
			this.menuOffResId = menuOffResId;
			this.menuOnResId = menuOnResId;
			this.clickEvent = clickEvent;
		}
	}

	private BaseActivity getActivity() {
		return (BaseActivity) getContext();
	}

}
