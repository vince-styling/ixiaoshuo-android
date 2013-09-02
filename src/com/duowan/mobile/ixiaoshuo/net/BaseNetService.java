package com.duowan.mobile.ixiaoshuo.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import android.os.AsyncTask;
import android.util.Log;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;
import com.duowan.mobile.ixiaoshuo.utils.SysUtil;
import org.apache.http.*;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpRequestRetryHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.conn.params.ConnRouteParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.SocketException;
import java.util.LinkedList;

public abstract class BaseNetService {
	protected static final String TAG = "NetService";

	private DefaultHttpClient httpClient;
	private DefaultHttpClient httpClientProxy;
	private HttpRequestRetryHandler retryHandler;
	private int tmpRetryCount = 0;

	private String API;
	protected int versionCode;
	protected String versionName;

	public static final String CTWAP_USERNAME = "ctwap@mycdma.cn";
	public static final String CTWAP_PASSWORD = "vnet.mobi";

	private boolean mShouldUseProxy;
	protected boolean mNetworkAvailable;
	private boolean mIsUseCmwap, mIsUseCtwap, mIsUseUniwap;

	protected final synchronized void doInit (Context context) {
		API = context.getResources().getString(R.string.api);

		retryHandler = new HttpRequestRetryHandler() {
			public boolean retryRequest(IOException e, int executionCount, HttpContext context) {
				// retry at most 5 times
				if (tmpRetryCount > 0) {
					if (executionCount > tmpRetryCount) return false;
				} else if (executionCount > 5) {
					return false;
				}

				return e instanceof java.net.SocketTimeoutException
						|| e instanceof ConnectTimeoutException
						|| e instanceof NoHttpResponseException
						|| e instanceof SocketException;
			}
		};

		versionCode = SysUtil.getVersionCode(context);
		versionName = SysUtil.getVersionName(context);

		updateNetworkInfo(context);

		context.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				updateNetworkInfo(context);
			}
		}, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	// TODO : 想办法在其它线程内初始化httpclient
	private HttpClient getHttpClientGeneral() {
		if(httpClient == null) {
			HttpParams params = new BasicHttpParams();

			// pre-set connection params
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);
			HttpConnectionParams.setSocketBufferSize(params, 8192);
			HttpConnectionParams.setTcpNoDelay(params, true);

			// connection manager params
			HttpParams mangrParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(mangrParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(mangrParams, "GBK");
			HttpProtocolParams.setUseExpectContinue(mangrParams, true);

			SchemeRegistry registry = new SchemeRegistry();
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

			ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(mangrParams, registry);
			httpClient = new DefaultHttpClient(connManager, params);
			httpClient.setHttpRequestRetryHandler(retryHandler);
		}
		return httpClient;
	}

	private HttpClient getHttpClientProxy() {
		if(httpClientProxy == null) {
			String proxyHost = Proxy.getDefaultHost();
			if(StringUtil.isEmpty(proxyHost)) proxyHost = mIsUseCtwap ? "10.0.0.200" : "10.0.0.172";
			int proxyPort = Proxy.getDefaultPort();
			if(proxyPort <= 0) proxyPort = 80;

			HttpParams params = new BasicHttpParams();
			params.setParameter(ConnRouteParams.DEFAULT_PROXY, new HttpHost(proxyHost, proxyPort));
			// pre-set connection params
			HttpConnectionParams.setConnectionTimeout(params, 30000);
			HttpConnectionParams.setSoTimeout(params, 30000);
			HttpConnectionParams.setSocketBufferSize(params, 8192);
			HttpConnectionParams.setTcpNoDelay(params, true);

			// connection manager params
			HttpParams mangrParams = new BasicHttpParams();
			HttpProtocolParams.setVersion(mangrParams, HttpVersion.HTTP_1_1);
			HttpProtocolParams.setContentCharset(mangrParams, "GBK");
			HttpProtocolParams.setUseExpectContinue(mangrParams, true);

			SchemeRegistry registry = new SchemeRegistry();
			// should use 80 as port if we are using "cmwap"`
			registry.register(new Scheme("http", PlainSocketFactory.getSocketFactory(), 80));

			ThreadSafeClientConnManager connManager = new ThreadSafeClientConnManager(mangrParams, registry);
			httpClientProxy = new DefaultHttpClient(connManager, params);
			httpClientProxy.setHttpRequestRetryHandler(retryHandler);

			if(mIsUseCtwap) {
				UsernamePasswordCredentials cred = new UsernamePasswordCredentials(CTWAP_USERNAME, CTWAP_PASSWORD);
				httpClientProxy.getCredentialsProvider().setCredentials(AuthScope.ANY, cred);
			}
		}
		return httpClientProxy;
	}

	public static final String CMWAP = "cmwap";
	public static final String CTWAP = "ctwap";
	public static final String UNIWAP = "uniwap";

	protected final void updateNetworkInfo (Context context) {
		ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo info = manager.getActiveNetworkInfo();

		if (info != null && (info.isConnected() || info.isRoaming())) {
			if (info.getType() == ConnectivityManager.TYPE_MOBILE) {
				mIsUseCmwap = CMWAP.equals(info.getExtraInfo());
				mIsUseCtwap = CTWAP.equals(info.getExtraInfo());
				mIsUseUniwap = UNIWAP.equals(info.getExtraInfo());
				mShouldUseProxy = mIsUseCmwap || mIsUseCtwap || mIsUseUniwap;
			} else {
				mIsUseCmwap = false;
				mIsUseCtwap = false;
				mIsUseUniwap = false;
				mShouldUseProxy = false;
			}
			mNetworkAvailable = true;
		} else mNetworkAvailable = false;
		Log.d(TAG, "updateNetworkInfo : NetworkAvailable : " + mNetworkAvailable + " ShouldUseProxy : " + mShouldUseProxy);
	}

	public final synchronized boolean testNetworkIsAvailable (Context ctx) {
		updateNetworkInfo(ctx);
		return mNetworkAvailable;
	}

	public final synchronized boolean isNetworkAvailable () { return mNetworkAvailable; }
	public final synchronized boolean shouldUseProxy () { return mShouldUseProxy; }
	public final synchronized boolean isUseUniwap() { return mIsUseUniwap; }
	public final synchronized boolean isUseCmwap() { return mIsUseCmwap; }
	public final synchronized boolean isUseCtwap() { return mIsUseCtwap; }

	private String makeAPI(String pageName) {
		return API + pageName + "?versionCode=" + versionCode + "&versionName=" + versionName;
	}

	private String makeUrl(String pageName, String params) {
		if (StringUtil.isEmpty(params)) return makeAPI(pageName);
		if(params.charAt(0) == '&') return makeAPI(pageName) + params;
		return makeAPI(pageName) + '&' + params;
	}

	protected final Respond handleHttpGet(String pageName, String params) throws IOException {
		return handleHttpExecute(makeHttpGet(pageName, params));
	}

	protected HttpGet makeHttpGet(String pageName, String params) {
		String url = makeUrl(pageName, params);
		Log.d(TAG, "makeHttpGet : " + url);
		return new HttpGet(url);
	}

	protected HttpResponse executeHttpGet(String pageName, String params) throws IOException {
		String url = makeUrl(pageName, params);
		Log.d(TAG, "executeHttpGet : " + url);
		return executeHttp(new HttpGet(url));
	}

	protected HttpPost makeHttpPost(String pageName) {
		String url = makeUrl(pageName, null);
		Log.d(TAG, "makeHttpPost : " + url);
		return new HttpPost(url);
	}

//	protected HttpResponse executeHttpPost(String pageName, String params) throws IOException {
//		String url = makeUrl(pageName, params);
//		Log.d(TAG, "executeHttpPost : " + url);
//		return executeHttp(new HttpPost(url));
//	}

	protected final Respond handleHttpExecute(HttpRequestBase request) throws IOException {
		HttpEntity entity = null;
		try {
			HttpResponse response = executeHttp(request);
			int statusCode = response.getStatusLine().getStatusCode();
			if (statusCode == HttpStatus.SC_OK) {
				entity = response.getEntity();
				String json = new String(EntityUtils.toByteArray(entity));
				return GObjectMapper.get().readValue(json, Respond.class);
			}
		} finally {
			closeEntity(entity);
		}
		return null;
	}

	private LinkedList<HttpRequestBase> mHttpRequestList = new LinkedList<HttpRequestBase>();

	public synchronized void abortAll() {
		while (mHttpRequestList.size() > 0) abortLast();
	}

	public synchronized void abortLast() {
		HttpRequestBase request = mHttpRequestList.pollLast();
		if (request == null || request.isAborted()) return;
		request.abort();
	}

	protected final HttpResponse executeHttp(HttpRequestBase request) throws IOException {
		mHttpRequestList.addLast(request);
		return mShouldUseProxy ? getHttpClientProxy().execute(request) : getHttpClientGeneral().execute(request);
	}

	protected void closeEntity(HttpEntity entity) {
		if (entity != null) {
			try {
				entity.consumeContent();
			} catch (IOException e) {
				Log.e(TAG, e.getMessage(), e);
			}
		}
	}

	public static <T> void execute(final NetExecutor<T> exetor) {
		(new AsyncTask<Void, Void, T>() {
			@Override
			protected void onPreExecute() {
				exetor.preExecute();
			}

			@Override
			protected T doInBackground(Void... params) {
				if(NetService.get().isNetworkAvailable()) return exetor.execute();
				return null;
			}

			@Override
			protected void onPostExecute(T result) {
				exetor.callback(result);
			}
		}).execute();
	}

	public static void execute(final SimpleNetExecutor exetor) {
		(new AsyncTask<Void, Void, Void>() {
			@Override
			protected Void doInBackground(Void... params) {
				if(NetService.get().isNetworkAvailable()) exetor.execute();
				return null;
			}
		}).execute();
	}

	// has return result
	public static interface NetExecutor<T> {
		void preExecute();
		T execute();
		void callback(T result);
	}

	// without result
	public static interface SimpleNetExecutor {
		void execute();
	}

}
