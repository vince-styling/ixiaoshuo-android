package com.duowan.mobile.ixiaoshuo.view.search;

import com.duowan.mobile.ixiaoshuo.R;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout.LayoutParams;
import android.widget.PopupWindow;
import android.widget.TextView;

/**
 * @ClassName SpinnerButton
 * @Description TODO 防android4.0 Spinner下拉效果
 * @author kenny
 * @date 2012-8-14
 */
public class SpinnerButton extends TextView {
	
	private Context mContext;
	/** 下拉PopupWindow */
	private UMSpinnerDropDownItems mPopupWindow;
	/** 下拉布局文件ResourceId */
	private int mResId;
	/** 下拉布局文件创建监听器 */
	private ViewCreatedListener mViewCreatedListener;

	public SpinnerButton(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		initButton(context);
	}

	public SpinnerButton(Context context, AttributeSet attrs) {
		super(context, attrs);
		initButton(context);
	}
	public SpinnerButton(Context context, final int resourceId,
			ViewCreatedListener mViewCreatedListener) {
		super(context);
		setResIdAndViewCreatedListener(resourceId, mViewCreatedListener);
		initButton(context);
	}

	private void initButton(Context context) {
		this.mContext = context;
		// UMSpinnerButton监听事件
		setOnClickListener(new UMSpinnerButtonOnClickListener());
	}

	public PopupWindow getPopupWindow() {
		return mPopupWindow;
	}

	public void setPopupWindow(UMSpinnerDropDownItems mPopupWindow) {
		this.mPopupWindow = mPopupWindow;
	}

	public int getResId() {
		return mResId;
	}
	/**
	 * @Description: TODO   隐藏下拉布局
	 */
	public void dismiss(){
		mPopupWindow.dismiss();
	}
	/**
	 * @Description: TODO  设置下拉布局文件,及布局文件创建监听器
	 * @param @param mResId 下拉布局文件ID
	 * @param @param mViewCreatedListener  布局文件创建监听器
	 */
	public void setResIdAndViewCreatedListener(int mResId, ViewCreatedListener mViewCreatedListener) {
		this.mViewCreatedListener = mViewCreatedListener;
		// 下拉布局文件id
		this.mResId = mResId;
		// 初始化PopupWindow
		mPopupWindow = new UMSpinnerDropDownItems(mContext);
		mPopupWindow.setBackgroundDrawable(mContext.getResources().getDrawable(R.drawable.type_normal));
	}

	/**
	 * UMSpinnerButton的点击事件
	 */
	class UMSpinnerButtonOnClickListener implements OnClickListener {

		@Override
		public void onClick(View v) {
			if (mPopupWindow != null) {
				if (!mPopupWindow.isShowing()) {
					// 设置PopupWindow弹出,退出样式
					mPopupWindow.setAnimationStyle(R.style.Animation_dropdown);
					// 计算popupWindow下拉x轴的位置
					int lx = (SpinnerButton.this.getWidth()
							- mPopupWindow.getmViewWidth() - 7) / 2;
					// showPopupWindow
					mPopupWindow.showAsDropDown(SpinnerButton.this, lx, +20);
					
				}
			}
		}
	}

	/**
	 * @ClassName UMSpinnerDropDownItems
	 * @Description TODO 下拉界面
	 * @author kenny
	 * @date 2012-8-14
	 */
	public class UMSpinnerDropDownItems extends PopupWindow {

		private Context mContext;
		/** 下拉视图的宽度 */
		private int mViewWidth;
		/** 下拉视图的高度 */
		private int mViewHeight;

		public UMSpinnerDropDownItems(Context context) {
			super(context);
			this.mContext = context;
			loadViews();
		}

		/**
		 * @Description: TODO 加载布局文件
		 * @param
		 * @return void
		 * @throws
		 */
		private void loadViews() {
			// 布局加载器加载布局文件
			LayoutInflater inflater = LayoutInflater.from(mContext);
			final View v = inflater.inflate(mResId, null);
			// 计算view宽高
			onMeasured(v);

			// 必须设置
			setWidth(LayoutParams.WRAP_CONTENT);
			setHeight(LayoutParams.WRAP_CONTENT);
			setContentView(v);
			setFocusable(true);
			
			// 设置布局创建监听器，以便在实例化布局控件对象
			if (mViewCreatedListener != null) {
				mViewCreatedListener.onViewCreated(v);
			}
		}

		/**
		 * @Description: TODO 计算View长宽
		 * @param @param v
		 */
		private void onMeasured(View v) {
			int w = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			int h = MeasureSpec.makeMeasureSpec(0,
					MeasureSpec.UNSPECIFIED);
			v.measure(w, h);
			mViewWidth = v.getMeasuredWidth();
			mViewHeight = v.getMeasuredHeight();
		}

		public int getmViewWidth() {
			return mViewWidth;
		}

		public void setmViewWidth(int mViewWidth) {
			this.mViewWidth = mViewWidth;
		}

		public int getmViewHeight() {
			return mViewHeight;
		}

		public void setmViewHeight(int mViewHeight) {
			this.mViewHeight = mViewHeight;
		}

	}
	/**
	 * @ClassName ViewCreatedListener  
	 * @Description TODO  布局创建监听器，实例化布局控件对象
	 * @author kenny  
	 * @date 2012-8-15
	 */
	public interface ViewCreatedListener {
		void onViewCreated(View v);
	}
}
