package com.vincestyling.ixiaoshuo.ui;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.Display;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;

public abstract class AbsDialog extends Dialog {

    public AbsDialog(Context context) {
        super(context);
    }

    protected ViewGroup mContentView;

//	protected final void initContentView(float widthPercent, float heightPercent) {
//		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		int width = (int) (display.getWidth() * widthPercent);
//		int height = (int) (display.getHeight() * heightPercent);
//		initContentView(width, height);
//	}

//	protected final void initContentView(float widthPercent, int height) {
//		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		initContentView((int) (display.getWidth() * widthPercent), height);
//	}
//
//	protected final void initContentView(int width, float heightPercent) {
//		Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
//		initContentView(width, (int) (display.getHeight() * heightPercent));
//	}

    protected final void initContentView(float widthPercent) {
        Display display = ((WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        initContentView(new ViewGroup.LayoutParams((int) (display.getWidth() * widthPercent), ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    protected final void initContentView(int width, int height) {
        initContentView(new ViewGroup.LayoutParams(width, height));
    }

    protected final void initContentView(ViewGroup.LayoutParams layoutParams) {
        // 注意：使用标题栏可能会导致窗口不垂直居中
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(mContentView, layoutParams);
    }

}
