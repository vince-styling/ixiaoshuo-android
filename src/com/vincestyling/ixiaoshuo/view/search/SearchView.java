package com.vincestyling.ixiaoshuo.view.search;

import android.graphics.Rect;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.ui.ScrollLayout;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

public class SearchView extends ViewBuilder implements View.OnClickListener, View.OnTouchListener {
	public SearchView(MainActivity activity, OnShowListener onShowListener) {
		mViewId = R.id.lotKeywordSearch;
		mShowListener = onShowListener;
		setActivity(activity);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.search_layout, null);
	}

	private ScrollLayout mlotSearchContent;
	private SearchListView mSearchListView;

	private View mLotGoSearch;
	private EditText mEdtSearchKeyword;

	private View mLotBookType;
	private View mImvSearchArrow;
	private TextView mTxvBookType;

	private PopupWindow mSearchTermsPopWin;
	private View mBtnTypeAll;
	private View mBtnTypeText;
	private View mBtnTypeVoice;

	private int mCurrentType;
	private final static int URL_VALUE_ALL = 0;    /** 所有书籍类型 */
	private final static int URL_VALUE_TEXT = 1;   /** 文字书籍类型 */
	private final static int URL_VALUE_VOICE = 2;  /** 有声书籍类型 */

	@Override
	public void init() {
		mLotGoSearch = findViewById(R.id.lotGoSearch);
		mEdtSearchKeyword = (EditText) findViewById(R.id.edtSearchKeyword);

		mTxvBookType = (TextView) findViewById(R.id.txvBookType);
		mImvSearchArrow = findViewById(R.id.imvSearchArrow);
		mLotBookType = findViewById(R.id.lotBookType);
		mLotBookType.setOnClickListener(this);
		mLotGoSearch.setOnClickListener(this);
		mCurrentType = URL_VALUE_ALL;

		mView.setOnTouchListener(this);

		mlotSearchContent = (ScrollLayout) findViewById(R.id.lotSearchContent);
		mlotSearchContent.setVisibility(View.GONE);
	}

	@Override
	public void onClick(View view) {
		if (view.equals(mLotGoSearch)) {
			String word = mEdtSearchKeyword.getText().toString();
			if (StringUtil.isBlank(word)) {
				getActivity().showToastMsg(R.string.please_input_key_word);
			}
		}
		else if (view.equals(mLotBookType)) {
			if (mSearchTermsPopWin == null) {
				View lotSearchTerms = getActivity().getLayoutInflater().inflate(R.layout.search_terms_popup, null);
				mBtnTypeAll = lotSearchTerms.findViewById(R.id.btnTypeAll);
				mBtnTypeAll.setOnClickListener(this);
				mBtnTypeText = lotSearchTerms.findViewById(R.id.btnTypeText);
				mBtnTypeText.setOnClickListener(this);
				mBtnTypeVoice = lotSearchTerms.findViewById(R.id.btnTypeVoice);
				mBtnTypeVoice.setOnClickListener(this);

				mSearchTermsPopWin = new PopupWindow(lotSearchTerms);
			}
			if (!showSearchTerms()) hideSearchTerms(true);

		} else if (view.equals(mBtnTypeAll)) {
			mTxvBookType.setText(R.string.search_type_all);
			mCurrentType = URL_VALUE_ALL;
			hideSearchTerms(true);
		} else if (view.equals(mBtnTypeText)) {
			mTxvBookType.setText(R.string.search_type_text);
			mCurrentType = URL_VALUE_TEXT;
			hideSearchTerms(true);
		} else if (view.equals(mBtnTypeVoice)) {
			mTxvBookType.setText(R.string.search_type_voice);
			mCurrentType = URL_VALUE_VOICE;
			hideSearchTerms(true);
		}
	}

	private boolean showSearchTerms() {
		if (mSearchTermsPopWin != null && !mSearchTermsPopWin.isShowing()) {
			Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clock_wise_half_circle);
			mImvSearchArrow.startAnimation(anim);

			int[] loc_int = new int[2];
			mLotBookType.getLocationOnScreen(loc_int);

			Rect location = new Rect();
			location.left = loc_int[0];
			location.top = loc_int[1] + mLotBookType.getHeight();
			location.right = location.left + mLotBookType.getWidth();
			location.bottom = location.top + mLotBookType.getHeight();

			mSearchTermsPopWin.showAsDropDown(mLotBookType, 0, 0);
			mSearchTermsPopWin.update(location.left, location.top, location.width(), LinearLayout.LayoutParams.WRAP_CONTENT);

			mLotBookType.setSelected(true);
			switch (mCurrentType) {
				case URL_VALUE_ALL:
					mBtnTypeAll.setSelected(true);
					mBtnTypeText.setSelected(false);
					mBtnTypeVoice.setSelected(false);
					break;
				case URL_VALUE_TEXT:
					mBtnTypeAll.setSelected(false);
					mBtnTypeText.setSelected(true);
					mBtnTypeVoice.setSelected(false);
					break;
				case URL_VALUE_VOICE:
					mBtnTypeAll.setSelected(false);
					mBtnTypeText.setSelected(false);
					mBtnTypeVoice.setSelected(true);
					break;
			}

			return true;
		}
		return false;
	}

	private boolean hideSearchTerms(boolean playAnim) {
		if (mSearchTermsPopWin != null && mSearchTermsPopWin.isShowing()) {
			if (playAnim) {
				Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_clock_anti_half_circle);
				mImvSearchArrow.startAnimation(anim);
			} else {
				mImvSearchArrow.setRotation(180);
			}

			mLotBookType.setSelected(false);
			mSearchTermsPopWin.dismiss();

			return true;
		}
		return false;
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		hideSearchTerms(true);
		return false;
	}

	@Override
	public void pushBack() {
		hideSearchTerms(false);
		super.pushBack();
	}

	//	@Override
//	public MainActivity getActivity() {
//		return (MainActivity) super.getActivity();
//	}

}
