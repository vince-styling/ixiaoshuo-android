package com.duowan.mobile.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.event.OnGridItemClickListener;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;
import com.duowan.mobile.ixiaoshuo.view.reader.BrightnessSetting;

public class ReadingMenu extends GridView implements Animation.AnimationListener {
//	public static final int MENU_FONT = 10;
//	public static final int MENU_STYLE = 20;
	public static final int MENU_BRIGHTNESS = 30;
	public static final int MENU_CATALOG = 40;
//	public static final int MENU_PROGRESS = 50;
	public static final int MENU_MORE = 60;

	private ScrollLayout mLotSecondaryMenu;
	private ViewBuilder.OnShowListener mShowListener;
	private Animation mInAnim, mOutAnim;

	public ReadingMenu(Context context, AttributeSet attrs) {
		super(context, attrs);
		mOutAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_bottom_out);
		mOutAnim.setAnimationListener(this);
		mInAnim = AnimationUtils.loadAnimation(getActivity(), R.anim.slide_bottom_in);
	}

	private void initMenu() {
		if (getItemSize() > 0) return;
		initGrid();

		mLotSecondaryMenu = (ScrollLayout) getActivity().findViewById(R.id.lotSecondaryMenu);
		// avoid touch event pass to ReadingBoard, trigger turn page operation
		mLotSecondaryMenu.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				return true;
			}
		});
		mShowListener = new ViewBuilder.OnShowListener() {
			@Override
			public void onShow() {
				showSecondaryMenu();
			}
		};
	}

	private void initGrid() {
//		putItem(new GridItem(R.drawable.reading_board_setting_font_on, R.drawable.reading_board_setting_font_off, new OnGridItemClickListener(MENU_FONT) {
//			public void onGridItemClick() {
//				mLotSecondaryMenu.showView(new FontSetting(getActivity(), mShowListener));
//			}
//		}));

//		putItem(new GridItem(R.drawable.reading_board_setting_style_on, R.drawable.reading_board_setting_style_off, new OnGridItemClickListener(MENU_STYLE) {
//			public void onGridItemClick() {
//				mLotSecondaryMenu.showView(new StyleSetting(getActivity(), mShowListener));
//			}
//		}));

		putItem(new GridItem(R.drawable.reading_board_setting_catalog_on, R.drawable.reading_board_setting_catalog_off, new OnGridItemClickListener(MENU_CATALOG) {
			public void onGridItemClick() {
				getActivity().showChapterListView();
			}
		}));

		putItem(new GridItem(R.drawable.reading_board_setting_brightness_on, R.drawable.reading_board_setting_brightness_off, new OnGridItemClickListener(MENU_BRIGHTNESS) {
			public void onGridItemClick() {
				mLotSecondaryMenu.showView(new BrightnessSetting(getActivity(), mShowListener));
			}
		}));

//		putItem(new GridItem(R.drawable.reading_board_setting_progress_on, R.drawable.reading_board_setting_progress_off, new OnGridItemClickListener(MENU_PROGRESS) {
//			public void onGridItemClick() {
//				mLotSecondaryMenu.showView(new ProgressSetting(getActivity(), mShowListener));
//			}
//		}));

		putItem(new GridItem(R.drawable.reading_board_setting_more_on, R.drawable.reading_board_setting_more_off, new OnGridItemClickListener(MENU_MORE) {
			public void onGridItemClick() {
				getActivity().showToastMsg("点击了更多菜单");
				hideSecondaryMenu();
			}
		}));

		mHighlightDrawable = getResources().getDrawable(R.drawable.reading_board_menu_item_pressed);
	}

	@Override
	protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
		initMenu();
		super.onLayout(changed, left, top, right, bottom);
	}

	public boolean showMenu() {
		if (getVisibility() == View.GONE) {
			setVisibility(View.VISIBLE);
			startAnimation(mInAnim);
			return true;
		}
		return false;
	}

	public boolean hideMenu() {
		if (getVisibility() == View.VISIBLE) {
			if (mIsAnimationDone) startAnimation(mOutAnim);
			hideSecondaryMenu();
			selectItem(0);
			return true;
		}
		return false;
	}

	public void showSecondaryMenu() {
		if (mLotSecondaryMenu.getVisibility() == View.GONE) {
			mLotSecondaryMenu.setVisibility(View.VISIBLE);
			mLotSecondaryMenu.startAnimation(mInAnim);
		}
	}

	private void hideSecondaryMenu() {
		if (mLotSecondaryMenu.getVisibility() == View.VISIBLE) {
			mLotSecondaryMenu.pushBack();
		}
	}

	private boolean mIsAnimationDone = true;

	@Override
	public void onAnimationStart(Animation animation) {
		mIsAnimationDone = false;
	}

	@Override
	public void onAnimationEnd(Animation animation) {
		mIsAnimationDone = true;
		setVisibility(View.GONE);
	}

	@Override
	public void onAnimationRepeat(Animation animation) {
	}

	public final ReaderActivity getActivity() {
		return (ReaderActivity) getContext();
	}

}
