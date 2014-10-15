package com.vincestyling.ixiaoshuo.utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class ViewUtil {

    public static void setFullScreen(Activity activity) {
        activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
        activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
    }

    // notice : each factor must use once
    public static final int VIEWID_INCREASE_FACTOR_BIG = 100000000;
    public static final int VIEWID_INCREASE_FACTOR_MID = 10000000;
    public static final int VIEWID_INCREASE_FACTOR_SML = 1000000;

    // use increaseFactor to ensure each view ids not conflict, this way isn't best
    public static int generateViewId(int baseViewId, int increaseFactor, int id) {
        return baseViewId + increaseFactor + id;
    }

}
