package com.duowan.mobile.ixiaoshuo.view.reader;

import android.view.View;
import android.widget.SeekBar;
import android.widget.TextView;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.reader.ReaderActivity;

public class ProgressSetting extends ReaderViewBuilder implements SeekBar.OnSeekBarChangeListener, View.OnClickListener {
	private SeekBar mSkbProgress;
	private TextView mTxvProgress;
	private float originProgress;

	public ProgressSetting(ReaderActivity activity, OnShowListener showListener) {
		super(activity, R.id.lotProgressSetting, showListener);
	}

	@Override
	protected void build() {
		mView = getActivity().getLayoutInflater().inflate(R.layout.reading_board_progress_setting, null);
	}

	@Override
	public void init() {
		findViewById(R.id.btnProgressRevert).setOnClickListener(this);
		mTxvProgress = (TextView) findViewById(R.id.txvProgress);

		mSkbProgress = (SeekBar) findViewById(R.id.skbProgress);
		mSkbProgress.setOnSeekBarChangeListener(this);
	}

	@Override
	public void resume() {
		originProgress = getActivity().getReadingBoard().calculateReadingProgress();
		mSkbProgress.setProgress((int) originProgress);
		super.resume();
	}

	@Override
	public void onClick(View v) {
		mSkbProgress.setProgress((int) originProgress);
	}

	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
		String progressTip = getActivity().getResources().getString(R.string.reading_txt_current_progress);
		mTxvProgress.setText(String.format(progressTip, progress));
	}

	@Override
	public void onStartTrackingTouch(SeekBar seekBar) {
	}

	@Override
	public void onStopTrackingTouch(SeekBar seekBar) {
	}
}
