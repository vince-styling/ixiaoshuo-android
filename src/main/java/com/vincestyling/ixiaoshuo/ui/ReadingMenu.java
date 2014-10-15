package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.OnGridItemClickListener;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;
import com.vincestyling.ixiaoshuo.view.reader.BrightnessSetting;
import com.vincestyling.ixiaoshuo.view.reader.FontSetting;
import com.vincestyling.ixiaoshuo.view.reader.StyleSetting;

public class ReadingMenu extends GridView implements Animation.AnimationListener {
    public static final int MENU_CATALOG = 10;
    public static final int MENU_BRIGHTNESS = 20;
    public static final int MENU_ORIENTATION = 30;
    public static final int MENU_FONT = 40;
    public static final int MENU_STYLE = 50;
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

    @Override
    protected void initGrid() {
        if (getItemSize() > 0) return;

        putItem(new GridItem(R.drawable.reading_board_setting_font_on, R.drawable.reading_board_setting_font_off, new OnGridItemClickListener(MENU_FONT) {
            public void onGridItemClick() {
                mLotSecondaryMenu.showView(new FontSetting(getActivity(), mShowListener));
            }
        }));

        putItem(new GridItem(R.drawable.reading_board_setting_style_on, R.drawable.reading_board_setting_style_off, new OnGridItemClickListener(MENU_STYLE) {
            public void onGridItemClick() {
                mLotSecondaryMenu.showView(new StyleSetting(getActivity(), mShowListener));
            }
        }));

        putItem(new GridItem(R.drawable.reading_board_setting_brightness_on, R.drawable.reading_board_setting_brightness_off, new OnGridItemClickListener(MENU_BRIGHTNESS) {
            public void onGridItemClick() {
                mLotSecondaryMenu.showView(new BrightnessSetting(getActivity(), mShowListener));
            }
        }));

        putItem(new GridItem(R.drawable.reading_board_setting_catalog_on, R.drawable.reading_board_setting_catalog_off, new OnGridItemClickListener(MENU_CATALOG) {
            public void onGridItemClick() {
                getActivity().showChapterListView();
            }
        }));

        putItem(new GridItem(R.drawable.reading_board_setting_orientation_on, R.drawable.reading_board_setting_orientation_off, new OnGridItemClickListener(MENU_ORIENTATION) {
            public void onGridItemClick() {
                // TODO : apply the orientation setting
//				if (SettingTable.isReadPortMode(getActivity())) {
//					getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//					SettingTable.setIsReadPortMode(getActivity(), false);
//				} else {
//					getActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//					SettingTable.setIsReadPortMode(getActivity(), true);
//				}
            }
        }));

        putItem(new GridItem(R.drawable.reading_board_setting_more_on, R.drawable.reading_board_setting_more_off, new OnGridItemClickListener(MENU_MORE) {
            public void onGridItemClick() {
                // TODO : implement global settings
//				Intent intent = new Intent(getActivity(), SettingActivity.class);
//				getActivity().startActivity(intent);
//				getActivity().getReadingMenu().hideMenu();
            }
        }));

        mHighlightDrawable = getResources().getDrawable(R.drawable.reading_board_menu_item_pressed);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        mLotSecondaryMenu = (ScrollLayout) getActivity().findViewById(R.id.lotSecondaryMenu);
        // avoid touch event pass to ReadingBoard, trigger turn page operation
        mLotSecondaryMenu.setOnTouchListener(new View.OnTouchListener() {
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

    public ReaderActivity getActivity() {
        return (ReaderActivity) getContext();
    }

}
