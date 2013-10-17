package com.duowan.mobile.ixiaoshuo.view.reader;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;
import com.duowan.mobile.ixiaoshuo.ui.ReadingMenu;

public class ReadingMenuView implements Animation.AnimationListener {
	private View mLotReadingHeadbar;
	private ReadingMenu mReadingMenu;
	private Animation mHeadInAnim, mHeadOutAnim;

	public ReadingMenuView(final ReaderActivity mActivity) {

		mReadingMenu = (ReadingMenu) mActivity.findViewById(R.id.readingMainMenu);

		mLotReadingHeadbar = mActivity.findViewById(R.id.lotReadingHeadbar);

		mActivity.findViewById(R.id.btnExit).setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				mActivity.onFinish();
				if (!mActivity.isFinishing()) hideMenu();
			}
		});

		View btnGotoVoiceBook = mActivity.findViewById(R.id.btnGotoVoiceBook);
//		boolean isBothType = true;
//		if (isBothType) {
			btnGotoVoiceBook.setVisibility(View.VISIBLE);
			btnGotoVoiceBook.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					mActivity.showToastMsg("正在跳转到有声书籍，敬请期待！");
				}
			});
//		}

		mHeadOutAnim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_out);
		mHeadOutAnim.setAnimationListener(this);
		mHeadInAnim = AnimationUtils.loadAnimation(mActivity, R.anim.slide_top_in);
	}

	public void switchMenu() {
		if (showMenu()) return;
		hideMenu();
	}

	public boolean showMenu() {
		if (mReadingMenu.showMenu()) {
			showHeadBar();
			return true;
		}
		return false;
	}

	public boolean hideMenu() {
		if (mReadingMenu.hideMenu()) {
			hideHeadBar();
			return true;
		}
		return false;
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

}
