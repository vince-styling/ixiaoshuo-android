package com.vincestyling.ixiaoshuo.view.search;

import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.BaseFragment;

public class SearchView extends BaseFragment implements View.OnClickListener, View.OnTouchListener {
    public static final int PAGER_INDEX = 3;

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
    private final static int URL_VALUE_ALL = 0;
    private final static int URL_VALUE_TEXT = 1;
    private final static int URL_VALUE_VOICE = 2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.search, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mLotGoSearch = view.findViewById(R.id.btnGoSearch);
        mEdtSearchKeyword = (EditText) view.findViewById(R.id.edtSearchKeyword);

        mTxvBookType = (TextView) view.findViewById(R.id.txvBookType);
        mImvSearchArrow = view.findViewById(R.id.imvSearchArrow);
        mLotBookType = view.findViewById(R.id.lotBookType);
        mLotBookType.setOnClickListener(this);
        mLotGoSearch.setOnClickListener(this);

        view.setOnTouchListener(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        onVisible();
    }

    @Override
    public void onPause() {
        super.onPause();
        onHidden();
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            onVisible();
        } else {
            onHidden();
        }
    }

    private void onVisible() {
    }

    private void onHidden() {
        hideSearchTerms(false);
    }

    @Override
    public void onClick(View view) {
        if (view.equals(mLotGoSearch)) {
            String word = mEdtSearchKeyword.getText().toString();
            if (StringUtil.isBlank(word)) {
                getBaseActivity().showToastMsg(R.string.please_input_key_word);
            }
        } else if (view.equals(mLotBookType)) {
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

}
