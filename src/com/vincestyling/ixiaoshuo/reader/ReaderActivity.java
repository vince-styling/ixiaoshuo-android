package com.vincestyling.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.Notifier;
import com.vincestyling.ixiaoshuo.event.YYReader;
import com.vincestyling.ixiaoshuo.pojo.ColorScheme;
import com.vincestyling.ixiaoshuo.ui.BatteryView;
import com.vincestyling.ixiaoshuo.ui.ReadingBoard;
import com.vincestyling.ixiaoshuo.utils.ReadingPreferences;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.utils.SysUtil;
import com.vincestyling.ixiaoshuo.utils.ViewUtil;
import com.vincestyling.ixiaoshuo.view.ViewBuilder;
import com.vincestyling.ixiaoshuo.view.reader.ChapterListView;
import com.vincestyling.ixiaoshuo.view.reader.ReadingMenuView;

import java.util.Date;

public class ReaderActivity extends BaseActivity {
	private static final String TAG = "ReaderActivity";
	private ReadingBoard mReadingBoard;
	private ReadingMenuView mReadingMenuView;
	private TextView mTxvCurTime, mTxvReadingInfo, mTxvRemainChapterInfo;
	private BatteryView mBatteryView;
	private ChapterListView mLsvChapterList;
	private ReadingPreferences mPreferences;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		ViewUtil.setFullScreen(this);
		setContentView(R.layout.reading_board);

		try {
			mReadingMenuView = new ReadingMenuView(this);
			initStatusBar();

			mReadingBoard = (ReadingBoard) findViewById(R.id.readingBoard);
			mReadingBoard.init();

			mPreferences = new ReadingPreferences(this);
			setColorScheme(mPreferences.getColorScheme());

			mLsvChapterList = new ChapterListView(this, new ViewBuilder.OnShowListener() {
				@Override
				public void onShow() {
					mReadingMenuView.hideMenu();
				}
			});

		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
			showToastMsg("初始化失败！");
		}
	}

	private void initStatusBar() {
		mTxvCurTime = (TextView) findViewById(R.id.txvCurTime);
		invalidateTime();

		mBatteryView = (BatteryView) findViewById(R.id.batteryView);
		mTxvReadingInfo = (TextView) findViewById(R.id.txvReadingInfo);
		mTxvRemainChapterInfo = (TextView) findViewById(R.id.txvRemainChapterInfo);
	}

	private void invalidateTime() {
		mTxvCurTime.setText(StringUtil.dfTime.format(new Date()));
	}

	public void refreshStatusBar(String readingInfo) {
		mTxvReadingInfo.setText(readingInfo);
		mTxvRemainChapterInfo.setText("剩余" + YYReader.getUnReadChapterCount() + "章");
	}

	public void showChapterListView() {
		mLsvChapterList.resume();
	}

	public void setColorScheme(ColorScheme colorScheme) {
		mReadingBoard.setColorScheme(colorScheme);

		mBatteryView.setColor(colorScheme.getTextColor());
		mTxvCurTime.setTextColor(colorScheme.getTextColor());
		mTxvReadingInfo.setTextColor(colorScheme.getTextColor());
		mTxvRemainChapterInfo.setTextColor(colorScheme.getTextColor());
	}

	@Override
	public boolean onKeyUp(int keyCode, KeyEvent event) {
		switch (keyCode) {
			case KeyEvent.KEYCODE_BACK:
				if (mLsvChapterList.isInFront()) {
					mLsvChapterList.pushBack();
					return true;
				}
				return mReadingMenuView.hideMenu() || onFinish();
			case KeyEvent.KEYCODE_MENU:
				mReadingMenuView.switchMenu();
				break;
		}
		return true;
	}

	public boolean onFinish() {
		if (!YYReader.isBookOnShelf()) {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			builder.setMessage("是否添加到书架？");
			builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					YYReader.addToBookShelf();
					dialog.cancel();
					finish();
				}
			});
			builder.setNegativeButton("取消", new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					YYReader.removeInBookShelf();
					dialog.cancel();
					finish();
				}
			});
			builder.show();
			return true;
		}

		if (isTaskRoot()) {
			Intent in = new Intent();
			in.setClass(this, MainActivity.class);
			in.setAction(String.valueOf(System.currentTimeMillis()));
			startActivity(in);
		}

		finish();
		return true;
	}

	@Override
	protected void onResume() {
		registerReceiver(mStatusBarReceiver, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
		registerReceiver(mStatusBarReceiver, new IntentFilter(Intent.ACTION_TIME_TICK));

		int savedBrightness = mPreferences.getBrightness();
		if (savedBrightness != -1) {
			SysUtil.setBrightness(getWindow(), savedBrightness);
		}

		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceiver(mStatusBarReceiver);
		if (isFinishing()) {
			getReaderApplication().getMainHandler().sendMessage(Notifier.NOTIFIER_BOOKSHELF_REFRESH);
		}
	}

	private StatusBarBroadcastReceiver mStatusBarReceiver = new StatusBarBroadcastReceiver();
	class StatusBarBroadcastReceiver extends BroadcastReceiver {
		@Override
		public void onReceive(Context context, Intent intent) {
			if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
				invalidateTime();
			}
			else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
				int level = intent.getIntExtra("level", 0);
				int scale = intent.getIntExtra("scale", 100);
				mBatteryView.setBatteryPercentage((float) level / scale);
			}
		}
	}

	public ReadingBoard getReadingBoard() {
		return mReadingBoard;
	}

	public ReadingMenuView getReadingMenu() {
		return mReadingMenuView;
	}

	public ReadingPreferences getPreferences() {
		return mPreferences;
	}

}
