package com.vincestyling.ixiaoshuo.view.detector;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.ui.DetectorScanView;
import com.vincestyling.ixiaoshuo.ui.DetectorUsageStatisticsView;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import com.vincestyling.ixiaoshuo.view.BaseFragment;

public class DetectorView extends BaseFragment {
	public static final int PAGER_INDEX = 2;

	private static int mInitUserCount = generateInitUserCount();  // 虚拟默认用户数目

	private final static int MAX_RANDOM_COUNT = 17783;  // 最大随机值
	private final static int MIN_RANDOM_COUNT = 8312;   // 最小随机值
	private final static int PER_RANDOM_COUNT = 400;    // 每一次随机的范围
	private final static int MAX_RANDOM_TIME = 50000;
	private final static int MIN_RANDOM_TIME = 10000;
	private final static int UPDATE_USER_COUNT = 3;  // 更新用户数

	private DetectorUsageStatisticsView mUsageStatisticsView;
	private DetectorScanView mScanView;
	private InnerHandler mHandler;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.detector, container, false);

		mUsageStatisticsView = (DetectorUsageStatisticsView) view.findViewById(R.id.lotUsageStatistics);
		mScanView = (DetectorScanView) view.findViewById(R.id.radarSechView);
//		mScanView.setOnPlayingListener(this);

		mHandler = new InnerHandler();

		return view;
	}

	@Override
	public void onResume() {
		mHandler.sendEmptyMessage(UPDATE_USER_COUNT);
		super.onResume();
	}

	private class InnerHandler extends Handler {
		@Override
		public void handleMessage(Message msg) {
			if (msg.what == UPDATE_USER_COUNT) {
				mUsageStatisticsView.setStatCount(getNextRandomUserCount());
				startNextRandomUpdate();
			}
		}
	}

	private static int generateInitUserCount() {
		int count;
		while (true) {
			count = StringUtil.nextRandInt(MAX_RANDOM_COUNT);
			if (count >= MIN_RANDOM_COUNT && count <= MAX_RANDOM_COUNT) {
				break;
			}
		}
		return count;
	}

	private void startNextRandomUpdate() {
		int number = StringUtil.nextRandInt(MAX_RANDOM_TIME);
		while (number < MIN_RANDOM_TIME) {
			number = StringUtil.nextRandInt(MAX_RANDOM_TIME);
		}
		mHandler.sendEmptyMessageDelayed(UPDATE_USER_COUNT, number);
	}

	private int getNextRandomUserCount() {
		int number = StringUtil.nextRandInt(PER_RANDOM_COUNT);
		return mInitUserCount + number;
	}

}
