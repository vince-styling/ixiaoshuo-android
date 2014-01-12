package com.vincestyling.ixiaoshuo.net;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.text.TextUtils;
import com.duowan.mobile.netroid.Network;
import com.duowan.mobile.netroid.RequestQueue;
import com.duowan.mobile.netroid.Response;
import com.duowan.mobile.netroid.cache.CacheWrapper;
import com.duowan.mobile.netroid.cache.DiskBasedCache;
import com.duowan.mobile.netroid.cache.MemoryBasedCache;
import com.duowan.mobile.netroid.stack.HttpClientStack;
import com.duowan.mobile.netroid.stack.HttpStack;
import com.duowan.mobile.netroid.stack.HurlStack;
import com.duowan.mobile.netroid.toolbox.BasicNetwork;
import com.vincestyling.ixiaoshuo.R;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.pojo.Const;
import com.vincestyling.ixiaoshuo.utils.AppLog;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import org.apache.http.protocol.HTTP;

import java.io.File;

public class Netroid {

	/** the queue :-) */
	private static RequestQueue mRequestQueue;

	/** the server api prefix */
	private static String API;

	/** determine if the network availabled */
//	private static boolean mNetworkAvailable;

	/** Nothing to see here. */
	private Netroid() {}

	public static void init(Context ctx) {
		if (mRequestQueue == null) {
			API = ctx.getResources().getString(R.string.api);

//			ctx.registerReceiver(new BroadcastReceiver() {
//				public void onReceive(Context context, Intent intent) {
//					updateNetworkInfo(context);
//				}
//			}, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
//			updateNetworkInfo(ctx);

			mRequestQueue = newRequestQueue(ctx,
					new CacheWrapper(Const.HTTP_CACHE_KEY_MEMORY, new MemoryBasedCache(Const.HTTP_MEMORY_CACHE_SIZE)),
					new CacheWrapper(Const.HTTP_CACHE_KEY_DISK, new DiskBasedCache(
							new File(ctx.getCacheDir(), Const.HTTP_DISK_CACHE_DIR_NAME), Const.HTTP_DISK_CACHE_SIZE)));
		} else {
			throw new IllegalStateException("initialized");
		}
	}

//	private static void updateNetworkInfo(Context context) {
//		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
//		NetworkInfo info = manager.getActiveNetworkInfo();
//
//		mNetworkAvailable = info != null && (info.isConnected() || info.isRoaming());
//		AppLog.d("NetworkAvailable : " + mNetworkAvailable);
//	}

	/**
	 * Creates a default instance of the worker pool and calls {@link com.duowan.mobile.netroid.RequestQueue#start()} on it.
	 * @param context A {@link android.content.Context} to use for creating the cache dir.
	 * @return A started {@link com.duowan.mobile.netroid.RequestQueue} instance.
	 */
	private static RequestQueue newRequestQueue(Context context, CacheWrapper... caches) {
		int poolSize = 6;

		HttpStack stack;
		String userAgent = "ixiaoshuo";
		try {
			String packageName = context.getPackageName();
			PackageInfo info = context.getPackageManager().getPackageInfo(packageName, 0);
			userAgent = packageName + "/" + info.versionCode;
		} catch (PackageManager.NameNotFoundException e) {
		}

		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			stack = new HurlStack(userAgent);
		} else {
			// Prior to Gingerbread, HttpUrlConnection was unreliable.
			// See: http://android-developers.blogspot.com/2011/09/androids-http-clients.html
			stack = new HttpClientStack(userAgent);
		}

		Network network = new BasicNetwork(stack, HTTP.UTF_8);
		RequestQueue queue = new RequestQueue(network, poolSize, caches);
		queue.start();

		return queue;
	}

	private static RequestQueue get() {
		if (mRequestQueue != null) {
			return mRequestQueue;
		} else {
			throw new IllegalStateException("Not initialized");
		}
	}

	private static String makeUrl(String pageName, String params) {
		String url = API + pageName;
		if (TextUtils.isEmpty(params)) {
			AppLog.d("request : " + url);
			return url;
		}

		if(params.charAt(0) == '&') {
			url = url + '?' + params.substring(1, params.length());
			AppLog.d("request : " + url);
			return url;
		}

		url = url + '?' + params;
		AppLog.d("request : " + url);
		return url;
	}

	private static String makeUrl(String pageName) {
		return makeUrl(pageName, null);
	}

	public static void downloadChapterContent(int bookId, int chapterId, Response.Listener<Void> listener) {
		get().add(new ChapterDownloadRequest(bookId, chapterId, makeUrl("/" + bookId + "/" + chapterId), listener));
	}

	public static void getBookChapterList(int bookId, int pageNo, Response.Listener<PaginationList<Chapter>> listener) {
		get().add(new ChapterListRequest(makeUrl("/chapter/list/" + bookId + "/" + pageNo), listener));
	}

}
