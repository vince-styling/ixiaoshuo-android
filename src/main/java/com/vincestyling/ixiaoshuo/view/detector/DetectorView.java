package com.vincestyling.ixiaoshuo.view.detector;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.Vibrator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.event.AccelerometerListener;
import com.vincestyling.ixiaoshuo.reader.DetectorResultActivity;
import com.vincestyling.ixiaoshuo.ui.DetectorScanView;
import com.vincestyling.ixiaoshuo.ui.DetectorUsageStatisticsView;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.BaseFragment;

public class DetectorView extends BaseFragment
        implements DetectorScanView.OnScanListener, AccelerometerListener.OnShakeListener {
    public static final int PAGER_INDEX = 2;

    private final static int MAX_RANDOM_COUNT = 17783;
    private final static int MIN_RANDOM_COUNT = 8312;
    private final static int PER_RANDOM_COUNT = 400;

    private int mInitUserCount;

    private DetectorUsageStatisticsView mUsageStatisticsView;
    private UpdateUsageStatHandler mHandler;
    private DetectorScanView mScanView;

    private AccelerometerListener mAccelerometerListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.detector, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        mUsageStatisticsView = (DetectorUsageStatisticsView) view.findViewById(R.id.lotUsageStatistics);
        mScanView = (DetectorScanView) view.findViewById(R.id.detectorScanView);
        mScanView.setOnScanListener(this);

        mAccelerometerListener = new AccelerometerListener(getActivity());
        mAccelerometerListener.setOnShakeListener(this);

        mInitUserCount = generateInitUserCount();
        mHandler = new UpdateUsageStatHandler();
    }

    @Override
    public void onPlayStart() {
        startDelayToShowResult();
    }

    @Override
    public void onPlayStop() {
        mHandler.removeMessages(MSG_DELAY_TO_SHOW_RESULT);
    }

    @Override
    public void onShake() {
        if (!mScanView.isPlaying()) {
            ((Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE)).vibrate(300);
            mScanView.start();
        }
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
        // http://stackoverflow.com/a/11075663/1294681
        super.setUserVisibleHint(isVisibleToUser);
        if (mScanView == null || mHandler == null) return;
        if (isVisibleToUser) {
            mAccelerometerListener.start();
            onVisible();
        } else {
            mAccelerometerListener.stop();
            onHidden();
        }
    }

    public void onVisible() {
        if (mUsageStatisticsView.getChildCount() == 0) {
            mUsageStatisticsView.setStatCount(getNextRandomUserCount());
        }
        startNextRandomUpdate();
        mScanView.onVisible();
    }

    public void onHidden() {
        mScanView.onHidden();
        mHandler.removeMessages(MSG_UPDATE_USAGE_STATE);
        mHandler.removeMessages(MSG_DELAY_TO_SHOW_RESULT);
    }

    private class UpdateUsageStatHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case MSG_UPDATE_USAGE_STATE:
                    mUsageStatisticsView.setStatCount(getNextRandomUserCount());
                    startNextRandomUpdate();
                    break;
                case MSG_DELAY_TO_SHOW_RESULT:
                    Intent intent = new Intent(getActivity(), DetectorResultActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    getActivity().startActivity(intent);
                    break;
            }
        }
    }

    private final static int MSG_UPDATE_USAGE_STATE = 100;
    private final static int MSG_DELAY_TO_SHOW_RESULT = 200;

    private static int generateInitUserCount() {
        int count;
        while (true) {
            count = StringUtil.nextRandInt(MAX_RANDOM_COUNT);
            if (count > MIN_RANDOM_COUNT && count < MAX_RANDOM_COUNT) break;
        }
        return count;
    }

    private void startNextRandomUpdate() {
        mHandler.sendEmptyMessageDelayed(
                MSG_UPDATE_USAGE_STATE, StringUtil.nextRandInt(10000, 50000));
    }

    private void startDelayToShowResult() {
        mHandler.sendEmptyMessageDelayed(
                MSG_DELAY_TO_SHOW_RESULT, StringUtil.nextRandInt(2000, 8000));
    }

    private int getNextRandomUserCount() {
        int number = StringUtil.nextRandInt(PER_RANDOM_COUNT);
        return mInitUserCount + number;
    }
}
