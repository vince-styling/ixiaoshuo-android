package com.vincestyling.ixiaoshuo.view;

import android.support.v4.app.Fragment;
import com.vincestyling.ixiaoshuo.reader.BaseActivity;

public class BaseFragment extends Fragment {

    protected BaseActivity getBaseActivity() {
        return (BaseActivity) getActivity();
    }

}
