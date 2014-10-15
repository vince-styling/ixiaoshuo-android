package com.vincestyling.ixiaoshuo.ui;

import android.content.Context;
import android.util.AttributeSet;
import com.vincestyling.ixiaoshuo.R;

public class DetectorUsageStatisticsView extends WithoutBookStatisticsView {
    public DetectorUsageStatisticsView(Context context) {
        this(context, null);
    }

    public DetectorUsageStatisticsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected int[] getNumRes() {
        return new int[]{
                R.drawable.detector_usage_statistic_num0,
                R.drawable.detector_usage_statistic_num1,
                R.drawable.detector_usage_statistic_num2,
                R.drawable.detector_usage_statistic_num3,
                R.drawable.detector_usage_statistic_num4,
                R.drawable.detector_usage_statistic_num5,
                R.drawable.detector_usage_statistic_num6,
                R.drawable.detector_usage_statistic_num7,
                R.drawable.detector_usage_statistic_num8,
                R.drawable.detector_usage_statistic_num9
        };
    }

}
