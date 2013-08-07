package com.duowan.mobile.ixiaoshuo.utils;

import android.app.Activity;
import android.view.Window;
import android.view.WindowManager;

public class ViewUtil {

	public static void setFullScreen(Activity activity) {
		activity.requestWindowFeature(Window.FEATURE_NO_TITLE);
		activity.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
	}

}
