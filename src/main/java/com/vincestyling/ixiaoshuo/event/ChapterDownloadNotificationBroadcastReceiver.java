package com.vincestyling.ixiaoshuo.event;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;
import com.vincestyling.ixiaoshuo.reader.ReaderApplication;

public class ChapterDownloadNotificationBroadcastReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        int bookId = intent.getIntExtra(Const.BOOK_ID, 0);
        if (bookId <= 0) return;

        Activity currentActivity = ((ReaderApplication) context.getApplicationContext()).getCurrentActivity();
        if (currentActivity != null && currentActivity instanceof BookInfoActivity) {
            if (((BookInfoActivity) currentActivity).getBookId() == bookId) return;
        }

        Intent intentActivity = new Intent(context, BookInfoActivity.class);
        intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intentActivity.putExtra(Const.BOOK_ID, bookId);
        context.startActivity(intentActivity);
    }
}
