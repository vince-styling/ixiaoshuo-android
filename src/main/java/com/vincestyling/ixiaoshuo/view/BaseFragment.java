package com.vincestyling.ixiaoshuo.view;

import android.support.v4.app.Fragment;
import com.vincestyling.ixiaoshuo.reader.BaseActivity;
import com.vincestyling.ixiaoshuo.utils.AppLog;

public class BaseFragment extends Fragment {

	protected BaseActivity getBaseActivity() {
		return (BaseActivity) getActivity();
	}

	public static class FragmentCreator {
		private int titleResId;
		private Class<? extends BaseFragment> clazz;

		public FragmentCreator(int titleResId, Class<? extends BaseFragment> clazz) {
			this.titleResId = titleResId;
			this.clazz = clazz;
		}

		public FragmentCreator(Class<? extends BaseFragment> clazz) {
			this.clazz = clazz;
		}

		public int getTitleResId() {
			return titleResId;
		}

		public Fragment newInstance() {
			try {
				return clazz.newInstance();
			} catch (Exception e) {
				AppLog.e(e);
			}
			return null;
		}
	}

}
