package com.duowan.mobile.ixiaoshuo.view.search;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Adapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.net.NetService;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.reader.MainActivity;
import com.duowan.mobile.ixiaoshuo.ui.ScrollLayout;
import com.duowan.mobile.ixiaoshuo.view.ViewBuilder;

import java.util.Random;

/**
 * 搜索界面
 * 
 * @author gaocong
 * 
 */
public class SearchView extends ViewBuilder implements OnClickListener {

	private static final String TAG = "YYReader_SearchView";
	private static String[] Value = { "全部", "文字", "有声" };// 定义选中后得到的值
	private ImageView mSearchBt;
	private String[] keywords;
	private KeywordsFlow keywordsFlow;
	private SpinnerButton mSpinner;
	private EditText mSearWord;
	private ScrollLayout mSearchResultLayout;
	SearchListView mSearchListView ;
	private static final int REFRESH_INTERVE = 5000;
	private Handler mHandler;
	private static final int TYPE_ALL = 0;
	private static final int TYPE_TXT = 1;
	private static final int TYPE_VOICE = 2;

	public SearchView(MainActivity activity, OnShowListener onShowListener) {
		mShowListener = onShowListener;
		mViewId = R.id.search_hot_key_search_result;
		setActivity(activity);
		Log.i(TAG, "SearchView create");
		mHandler = new Handler(){
			public void handleMessage(Message msg) {
				switch (msg.what) {
				case 1:
					showHotWord();
					break;

				default:
					break;
				}
			}
		};
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.search_layout,null);
		Log.i(TAG, "SearchView create");
	}

	@Override
	public void init() {
		Log.i(TAG, "SearchView init");
		mSearchBt = (ImageView) findViewById(R.id.search_bt);
		mSearWord = (EditText) findViewById(R.id.search_wrod_edit);
		mSearchBt.setOnClickListener(this);
		mSpinner = (SpinnerButton) findViewById(R.id.search_type);
		mSearchResultLayout = (ScrollLayout) findViewById(R.id.search_result_list);
		mSearchResultLayout.setVisibility(View.GONE);
		keywordsFlow = (KeywordsFlow) findViewById(R.id.keywordsflow);
		mSpinner.setResIdAndViewCreatedListener(
				R.layout.spinner_dropdown_items,
				new SpinnerButton.ViewCreatedListener() {
					@Override
					public void onViewCreated(View v) {
						// TODO Auto-generated method stub
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
		
		mSearchListView = new SearchListView(getActivity(),
				new OnShowListener() {
			@Override
			public void onShow() {
			}
		});
		
		NetService.execute(new NetService.NetExecutor<String[]>() {

			@Override
			public void preExecute() {
			}

			@Override
			public String[] execute() {
				return NetService.get().getHotKeyWords();
			}

			@Override
			public void callback(String[] array) {
				if (array != null) {
					keywords = array;
					Log.i(TAG, "keywords length is:" + keywords.length);
				} else {
					Log.i(TAG, "callback null");
				}
				showHotWord();
			}
		});
		
		
		Timer timer = new Timer();
		    timer.scheduleAtFixedRate(new TimerTask() {
		        @Override
		        public void run() {
		        	if(keywordsFlow.getVisibility() == View.VISIBLE){
		        		if(keywordsFlow.getVisibility() == View.VISIBLE){
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
		if(mSearchResultLayout.getVisibility() == View.VISIBLE){
			mSearchResultLayout.setVisibility(View.GONE);
			keywordsFlow.setVisibility(View.VISIBLE);
			showHotWord();
			return true;
		}else{
			return mSearchResultLayout.onKeyDown(keyCode, event);
		}
			
	}

	private void showHotWord() {
		
		keywordsFlow.setDuration(800l);
		keywordsFlow.setFocusable(true);
		keywordsFlow.setClickable(true);
		// 添加
		feedKeywordsFlow(keywordsFlow, keywords);
		keywordsFlow.setOnItemClickListener(this);
		keywordsFlow.go2Show(KeywordsFlow.ANIMATION_IN);
		
	}

	private static void feedKeywordsFlow(KeywordsFlow keywordsFlow, String[] arr) {
		if(arr == null ||arr.length == 0){
			return;
		}
		Random random = new Random();
		for (int i = 0; i < KeywordsFlow.MAX; i++) {
			int ran = random.nextInt(arr.length);
			String tmp = arr[ran];
			keywordsFlow.feedKeyword(tmp);
		}
	}

	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.search_bt){
			closeSoftKeyboard();
			doSearch(mSearWord.getText().toString());
		} else if (v instanceof TextView) {
			Log.i(TAG, "onClick :" + ((TextView) v).getText().toString());
			doSearch(((TextView) v).getText().toString());
		}
	}

	private void closeSoftKeyboard(){
		((InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE))
		.hideSoftInputFromWindow(getActivity().getCurrentFocus()
				.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
	}
	
	private void doSearch(String key) {
		if (key == null
				|| TextUtils.isEmpty(key)) {
			Log.i(TAG,"doSearch txt null");
			return;
		}
		Log.i(TAG,"doSearch continue");
		
		keywordsFlow.setVisibility(View.GONE);
		mSearchResultLayout.setVisibility(View.VISIBLE);
		mSearchListView.isInFront();
		mSearchResultLayout.showView(mSearchListView);
		mSearchListView.setSearKey(mSearWord.getText().toString(), String.valueOf(getSearchType()));
		mSearchListView.doSearch(key);
		
//		if(mSearchResultLayout.getVisibility() == View.GONE){
//			Log.i(TAG,"mSearchResultLayout.getVisibility() == View.GONE ");
//			keywordsFlow.setVisibility(View.GONE);
//			mSearchResultLayout.setVisibility(View.VISIBLE);
//			mSearchListView.setSearKey(key,getSearchType());
//			mSearchResultLayout.showView(mSearchListView);
//		}else{
//			Log.i(TAG,"doSearch");
//			mSearchListView.isInFront();
//			mSearchListView.setSearKey(key,getSearchType());
//			mSearchListView.doSearch(key);
//		}
		
	}
	
	private int getSearchType(){
		if(mSpinner.getText().equals(Value[0])){
			return TYPE_ALL;
		}else if(mSpinner.getText().equals(Value[1])){
			return TYPE_TXT;
		}else if(mSpinner.getText().equals(Value[2])){
			return TYPE_VOICE;
		}
		return TYPE_ALL;
	}

	@Override
	public MainActivity getActivity() {
		return (MainActivity) super.getActivity();
	}

}
