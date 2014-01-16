package com.vincestyling.ixiaoshuo.view.search;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.duowan.mobile.netroid.Listener;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.net.Netroid;
import com.vincestyling.ixiaoshuo.reader.MainActivity;
import com.vincestyling.ixiaoshuo.ui.ScrollLayout;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class SearchView extends ViewBuilder implements OnClickListener {
	private String[] mKeywords;
	private KeywordsFlow keywordsFlow;
	private SpinnerButton mSpinner;
	private EditText mSearWord;
	private ScrollLayout mSearchResultLayout;
	SearchListView mSearchListView;
	private static final int REFRESH_INTERVE = 5000;
	private Handler mHandler;

	public SearchView(MainActivity activity, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		mViewId = R.id.search_hot_key_search_result;
		setActivity(activity);
		mHandler = new Handler() {
			public void handleMessage(Message msg) {
				switch (msg.what) {
					case 1:
						showHotWord();
						break;
				}
			}
		};
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.search_layout, null);
	}

	@Override
	public void init() {
		ImageView mSearchBt = (ImageView) findViewById(R.id.search_bt);
		mSearWord = (EditText) findViewById(R.id.search_wrod_edit);
		mSearchBt.setOnClickListener(this);
		mSpinner = (SpinnerButton) findViewById(R.id.search_type);
		mSearchResultLayout = (ScrollLayout) findViewById(R.id.search_result_list);
		mSearchResultLayout.setVisibility(View.GONE);
		keywordsFlow = (KeywordsFlow) findViewById(R.id.keywordsflow);
		mSpinner.setResIdAndViewCreatedListener(R.layout.spinner_dropdown_items, new SpinnerButton.ViewCreatedListener() {
			@Override
			public void onViewCreated(View v) {
				v.findViewById(R.id.textView1).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								mSpinner.setText(R.string.type_all);
								mSpinner.dismiss();
							}
						});
				v.findViewById(R.id.textView2).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								mSpinner.setText(R.string.type_txt);
								mSpinner.dismiss();
							}
						});
				v.findViewById(R.id.textView3).setOnClickListener(
						new OnClickListener() {
							@Override
							public void onClick(View v) {
								mSpinner.setText(R.string.type_voice);
								mSpinner.dismiss();
							}
						});
			}
		});

		mSearchListView = new SearchListView(getActivity(), new OnShowListener() {
			@Override
			public void onShow() {
			}
		});

		Netroid.getHotKeywords(new Listener<String[]>() {
			@Override
			public void onSuccess(String[] keywords) {
				mKeywords = keywords;
				showHotWord();
			}
		});

		Timer timer = new Timer();
		timer.scheduleAtFixedRate(new TimerTask() {
			@Override
			public void run() {
				if (keywordsFlow.getVisibility() == View.VISIBLE) {
					if (keywordsFlow.getVisibility() == View.VISIBLE) {
						Message msg = new Message();
						msg.what = 1;
						mHandler.sendMessage(msg);
					}
				}
			}
		}, 0, REFRESH_INTERVE);
	}


	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (mSearchResultLayout.getVisibility() == View.VISIBLE) {
			mSearchResultLayout.setVisibility(View.GONE);
			keywordsFlow.setVisibility(View.VISIBLE);
			showHotWord();
			return true;
		} else {
			return mSearchResultLayout.onKeyDown(keyCode, event);
		}

	}

	private void showHotWord() {
		keywordsFlow.setDuration(800l);
		keywordsFlow.setFocusable(true);
		keywordsFlow.setClickable(true);

		feedKeywordsFlow(keywordsFlow, mKeywords);
		keywordsFlow.setOnItemClickListener(this);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);

	}

	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		if (arr == null || arr.length == 0) return;
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.length);
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	public void onClick(View v) {
		if (v.getId() == R.id.search_bt) {
			closeSoftKeyboard();
			doSearch(mSearWord.getText().toString());
		} else if (v instanceof TextView) {
			doSearch(((TextView) v).getText().toString());
		}
	}

	private void closeSoftKeyboard() {
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
				.hideSoftInputFromWindow(getActivity().getCurrentFocus()
						.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}

	private void doSearch(String key) {
		if (key == null || TextUtils.isEmpty(key)) return;

		keywordsFlow.setVisibility(View.GONE);
		mSearchResultLayout.setVisibility(View.VISIBLE);
		mSearchListView.isInFront();
		mSearchResultLayout.showView(mSearchListView);
		mSearchListView.setSearKey(mSearWord.getText().toString());
		mSearchListView.doSearch(key);
	}

	@Override
	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

}
