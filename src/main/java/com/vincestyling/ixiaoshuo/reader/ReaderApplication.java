package com.vincestyling.ixiaoshuo.reader;

import android.app.Application;
import android.widget.Toast;
import com.vincestyling.ixiaoshuo.db.AppDAO;
import com.vincestyling.ixiaoshuo.event.MainHandler;
import com.vincestyling.ixiaoshuo.net.Netroid;

public class ReaderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();
        AppDAO.init(this);
        Netroid.init(this);
        mMainHandler = new MainHandler();
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

    private MainHandler mMainHandler;

    public MainHandler getMainHandler() {
        return mMainHandler;
    }

}
