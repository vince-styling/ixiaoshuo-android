package com.vincestyling.ixiaoshuo.reader;

import android.app.Activity;
import android.app.Application;
import android.widget.Toast;
import com.vincestyling.ixiaoshuo.db.AppDBOverseer;
import com.vincestyling.ixiaoshuo.net.Netroid;

public class ReaderApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        AppDBOverseer.init(this);
        Netroid.init(this);
    }

    private Toast mToast;

    public void showToastMsg(CharSequence msg) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, msg, Toast.LENGTH_SHORT);
        mToast.show();
    }

    public void showToastMsg(int resId) {
        if (mToast != null) mToast.cancel();
        mToast = Toast.makeText(this, resId, Toast.LENGTH_SHORT);
        mToast.show();
    }

    private Activity mCurrentActivity;

    public Activity getCurrentActivity() {
        return mCurrentActivity;
    }

    public void setCurrentActivity(Activity currentActivity) {
        this.mCurrentActivity = currentActivity;
    }
}
