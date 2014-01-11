package com.vincestyling.ixiaoshuo.event;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.reader.BookInfoActivity;

public class ChapterDownloadNotificationBroadcastReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (TextUtils.isEmpty(action)) {
            return;
        }

		if (action.equals("detail")) {
			int bookId = intent.getIntExtra("BookId", 0);
			if (bookId > 0) {
                Intent intentActivity = new Intent(context, BookInfoActivity.class);
                intentActivity.putExtra(Const.BOOK_ID, bookId);
                intentActivity.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(intentActivity);
			}
		}
	}

}
