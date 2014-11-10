package com.vincestyling.ixiaoshuo.view.reader;

import android.content.pm.ActivityInfo;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.OnGridItemClickListener;
import com.vincestyling.ixiaoshuo.reader.ReaderActivity;
import com.vincestyling.ixiaoshuo.ui.GridView;

public class ReadingMenuView implements Animation.AnimationListener, View.OnTouchListener {
    public static final int MENU_CATALOG = 10;
    public static final int MENU_BRIGHTNESS = 20;
    public static final int MENU_ORIENTATION = 30;
    public static final int MENU_FONT = 40;
    public static final int MENU_STYLE = 50;
    public static final int MENU_MORE = 60;

    private ReaderActivity mReaderActivity;

    private boolean mIsReadingMenuAnimationDone = true;
    private GridView mReadingMenu;
    private ViewGroup mLotSecondaryMenu;
    private Animation mInAnim, mOutAnim;

    private View mLotReadingHeadbar;
    private Animation mHeadInAnim, mHeadOutAnim;

    public ReadingMenuView(ReaderActivity readerActivity) {
        mReaderActivity = readerActivity;
    }

    private void init() {
        if (mReadingMenu != null) return;

        mReadingMenu = (GridView) mReaderActivity.findViewById(R.id.readingMainMenu);

        mReadingMenu.setHighlightDrawable(mReaderActivity.getResources().getDrawable(R.drawable.reading_board_menu_item_pressed));

        mReadingMenu.putItem(new GridView.GridItem(R.drawable.reading_board_setting_font_on,
                R.drawable.reading_board_setting_font_off, new OnGridItemClickListener(MENU_FONT) {
            public void onGridItemClick() {
                new FontSetting(mReaderActivity).showView(mLotSecondaryMenu, mInAnim);
            }
        }));

        mReadingMenu.putItem(new GridView.GridItem(R.drawable.reading_board_setting_style_on,
                R.drawable.reading_board_setting_style_off, new OnGridItemClickListener(MENU_STYLE) {
            public void onGridItemClick() {
                new StyleSetting(mReaderActivity).showView(mLotSecondaryMenu, mInAnim);
            }
        }));

        mReadingMenu.putItem(new GridView.GridItem(R.drawable.reading_board_setting_brightness_on,
                R.drawable.reading_board_setting_brightness_off, new OnGridItemClickListener(MENU_BRIGHTNESS) {
            public void onGridItemClick() {
                new BrightnessSetting(mReaderActivity).showView(mLotSecondaryMenu, mInAnim);
            }
        }));

        mReadingMenu.putItem(new GridView.GridItem(R.drawable.reading_board_setting_catalog_on,
                R.drawable.reading_board_setting_catalog_off, new OnGridItemClickListener(MENU_CATALOG) {
            public void onGridItemClick() {
                new BookChapterListDialog(mReaderActivity).show();
                hideMenu();
            }
        }));

        mReadingMenu.putItem(new GridView.GridItem(R.drawable.reading_board_setting_orientation_on,
                R.drawable.reading_board_setting_orientation_off, new OnGridItemClickListener(MENU_ORIENTATION) {
            public void onGridItemClick() {
				if (mReaderActivity.getPreferences().isPortMode()) {
                    mReaderActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    mReaderActivity.getPreferences().setIsPortMode(false);
				} else {
                    mReaderActivity.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    mReaderActivity.getPreferences().setIsPortMode(true);
				}
            }
        }));

        mReadingMenu.putItem(new GridView.GridItem(R.drawable.reading_board_setting_more_on,
                R.drawable.reading_board_setting_more_off, new OnGridItemClickListener(MENU_MORE) {
            public void onGridItemClick() {
                // TODO : implement global settings
//				Intent intent = new Intent(getActivity(), SettingActivity.class);
//				getActivity().startActivity(intent);
                hideMenu();
            }
        }));

        mOutAnim = AnimationUtils.loadAnimation(mReaderActivity, R.anim.slide_bottom_out);
        mOutAnim.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                mIsReadingMenuAnimationDone = false;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                mReadingMenu.setVisibility(View.GONE);
                mIsReadingMenuAnimationDone = true;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        mInAnim = AnimationUtils.loadAnimation(mReaderActivity, R.anim.slide_bottom_in);

        mLotSecondaryMenu = (ViewGroup) mReaderActivity.findViewById(R.id.lotSecondaryMenu);
        // avoid touch event pass to ReadingBoard, trigger turn page operation
        mLotSecondaryMenu.setOnTouchListener(this);


        mLotReadingHeadbar = mReaderActivity.findViewById(R.id.lotReadingHeadbar);

        mReaderActivity.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mReaderActivity.onFinish();
                if (!mReaderActivity.isFinishing()) hideMenu();
            }
        });

        mHeadOutAnim = AnimationUtils.loadAnimation(mReaderActivity, R.anim.slide_top_out);
        mHeadOutAnim.setAnimationListener(this);
        mHeadInAnim = AnimationUtils.loadAnimation(mReaderActivity, R.anim.slide_top_in);
    }

    public void switchMenu() {
        if (showMenu()) return;
        hideMenu();
    }

    private boolean showMenu() {
        init();
        if (showReadingMenu()) {
            showHeadBar();
            return true;
        }
        return false;
    }

    public boolean hideMenu() {
        if (mReadingMenu != null && hideReadingMenu()) {
            hideHeadBar();
            return true;
        }
        return false;
    }

    private boolean showReadingMenu() {
        if (mReadingMenu.getVisibility() == View.GONE) {
            mReadingMenu.setVisibility(View.VISIBLE);
            mReadingMenu.startAnimation(mInAnim);
            return true;
        }
        return false;
    }

    private boolean hideReadingMenu() {
        if (mReadingMenu.getVisibility() == View.VISIBLE) {
            if (mIsReadingMenuAnimationDone) mReadingMenu.startAnimation(mOutAnim);
            mReadingMenu.selectItem(0);
            hideSecondaryMenu();
            return true;
        }
        return false;
    }

    private void hideSecondaryMenu() {
        if (mLotSecondaryMenu.getVisibility() == View.VISIBLE) {
            mLotSecondaryMenu.setVisibility(View.GONE);
        }
    }

    private void showHeadBar() {
        if (mLotReadingHeadbar.getVisibility() == View.GONE) {
            mLotReadingHeadbar.setVisibility(View.VISIBLE);
            mLotReadingHeadbar.startAnimation(mHeadInAnim);
        }
    }

    private void hideHeadBar() {
        if (mLotReadingHeadbar.getVisibility() == View.VISIBLE && mIsAnimationDone) {
            mLotReadingHeadbar.startAnimation(mHeadOutAnim);
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
        mLotReadingHeadbar.setVisibility(View.GONE);
    }

    @Override
    public void onAnimationRepeat(Animation animation) {
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return true;
    }
}
