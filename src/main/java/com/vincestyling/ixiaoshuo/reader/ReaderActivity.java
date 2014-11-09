package com.vincestyling.ixiaoshuo.reader;

import android.app.AlertDialog;
import android.content.*;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.TextView;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.doc.OnlineDocument;
import com.vincestyling.ixiaoshuo.event.OnChangeReadingInfoListener;
import com.vincestyling.ixiaoshuo.event.YYReader;
import com.vincestyling.ixiaoshuo.pojo.ColorScheme;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.service.ReaderSupport;
import com.vincestyling.ixiaoshuo.ui.BatteryView;
import com.vincestyling.ixiaoshuo.ui.ReadingBoard;
import com.vincestyling.ixiaoshuo.ui.RenderPaint;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.ReadingPreferences;
import com.vincestyling.ixiaoshuo.utils.SysUtil;
import com.vincestyling.ixiaoshuo.view.reader.ReadingMenuView;

import java.text.SimpleDateFormat;
import java.util.Date;

public class ReaderActivity extends BaseActivity {
    private ReadingBoard mReadingBoard;
    private ReadingMenuView mReadingMenuView;
    private TextView mTxvCurTime, mTopInfo, mBottomInfo;
    private BatteryView mBatteryView;
    private ReadingPreferences mPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SysUtil.setFullScreen(this);

        // TODO : apply reader orientation setting
//		if (!SettingTable.isReadPortMode(this)) {
//			setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//		}

        try {
            int bookId = getIntent().getIntExtra(Const.BOOK_ID, 0);
            new ReaderSupport().init(bookId);

            setContentView(R.layout.reading_board);

            mReadingBoard = (ReadingBoard) findViewById(R.id.readingBoard);
            initStatusBar();

            OnChangeReadingInfoListener onChangeReadingInfoListener = new OnChangeReadingInfoListener() {
                @Override
                public void onChangeTopInfo(String topInfo) {
                    mTopInfo.setText(topInfo);
                }

                @Override
                public void onChangeBottomInfo(String bottomInfo) {
                    mBottomInfo.setText(bottomInfo);
                }
            };

            mReadingBoard.init(new OnlineDocument(mReadingBoard, onChangeReadingInfoListener));

            mReadingMenuView = new ReadingMenuView(this);

            mPreferences = new ReadingPreferences(this);

            RenderPaint.init(this);
            RenderPaint.get().setTextSize(mPreferences.getTextSize());

            onChangeColorScheme();
        } catch (Exception e) {
            showToastMsg("初始化失败！");
            AppLog.e(e);
            finish();
        }
    }

    private void initStatusBar() {
        mTxvCurTime = (TextView) findViewById(R.id.txvCurTime);
        invalidateTime();

        mBatteryView = (BatteryView) findViewById(R.id.batteryView);
        mBottomInfo = (TextView) findViewById(R.id.txvBottomInfo);
        mTopInfo = (TextView) findViewById(R.id.txvTopInfo);
    }

    private void invalidateTime() {
        boolean is24Hour = android.text.format.DateFormat.is24HourFormat(this);
        SimpleDateFormat dfTime = new SimpleDateFormat(is24Hour ? "HH:mm" : "hh:mm");
        mTxvCurTime.setText(dfTime.format(new Date()));
    }

    public void onChangeColorScheme() {
        ColorScheme colorScheme = mPreferences.getColorScheme();
        mReadingBoard.setColorScheme(colorScheme);

        mBatteryView.setColor(colorScheme.getTextColor());
        mTxvCurTime.setTextColor(colorScheme.getTextColor());
        mTopInfo.setTextColor(colorScheme.getTextColor());
        mBottomInfo.setTextColor(colorScheme.getTextColor());
    }

    public void increaseTextSize() {
        if (mPreferences.increaseTextSize()) {
            mReadingBoard.adjustTextSize(mPreferences.getTextSize());
        }
    }

    public void decreaseTextSize() {
        if (mPreferences.decreaseTextSize()) {
            mReadingBoard.adjustTextSize(mPreferences.getTextSize());
        }
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                if (!mReadingMenuView.hideMenu()) onFinish();
                break;
            case KeyEvent.KEYCODE_MENU:
                mReadingMenuView.switchMenu();
                break;
        }
        return false;
    }

    public void onFinish() {
        if (YYReader.isBookOnShelf()) {
            finish();
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("是否添加到书架？");
            builder.setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    YYReader.addToBookShelf();
                    dialog.cancel();
                    finish();
                }
            });
            builder.setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    YYReader.removeInBookShelf();
                    dialog.cancel();
                    finish();
                }
            });
            builder.show();
        }
    }

    @Override
    public void finish() {
        if (isTaskRoot()) {
            Intent in = new Intent(this, MainActivity.class);
            in.setAction(String.valueOf(System.currentTimeMillis()));
            startActivity(in);
        }
        super.finish();
    }

    @Override
    protected void onResume() {
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BATTERY_CHANGED);
        intentFilter.addAction(Intent.ACTION_TIME_TICK);
        registerReceiver(mStatusBarReceiver, intentFilter);

        int savedBrightness = mPreferences.getBrightness();
        if (savedBrightness != -1) {
            SysUtil.setBrightness(getWindow(), savedBrightness);
        }

        super.onResume();
        invalidateTime();
    }

    @Override
    protected void onPause() {
        unregisterReceiver(mStatusBarReceiver);
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        YYReader.onCancelRead();
        RenderPaint.destory();
        super.onDestroy();
    }

    private StatusBarBroadcastReceiver mStatusBarReceiver = new StatusBarBroadcastReceiver();

    class StatusBarBroadcastReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (Intent.ACTION_TIME_TICK.equals(intent.getAction())) {
                invalidateTime();
            } else if (Intent.ACTION_BATTERY_CHANGED.equals(intent.getAction())) {
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
