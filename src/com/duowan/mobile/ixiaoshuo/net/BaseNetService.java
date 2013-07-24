package com.duowan.mobile.ixiaoshuo.net;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Proxy;
import com.duowan.mobile.ixiaoshuo.R;
import com.duowan.mobile.ixiaoshuo.utils.IOUtil;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;
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
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;
import java.net.SocketException;

public abstract class BaseNetService {

	private DefaultHttpClient httpClient;
	private DefaultHttpClient httpClientProxy;
	private HttpRequestRetryHandler retryHandler;
	private int tmpRetryCount = 0;

	private String API;
	private int versionCode;
	private String versionName;

	protected ObjectMapper mapper;

	public static final String CTWAP_USERNAME = "ctwap@mycdma.cn";
	public static final String CTWAP_PASSWORD = "vnet.mobi";

	private boolean mNetworkAvailable, mShouldUseProxy;
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

		try {
			PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(context.getPackageName(), 0);
			versionCode = pkgInfo.versionCode;
			versionName = pkgInfo.versionName;
		} catch (PackageManager.NameNotFoundException e) {
			e.printStackTrace();
		}

		mapper = GObjectMapper.get();

		updateNetworkInfo(context);

		context.registerReceiver(new BroadcastReceiver() {
			public void onReceive(Context context, Intent intent) {
				updateNetworkInfo(context);
			}
		}, new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
	}

	private HttpClient getHttpClientGeneral() {
		if(httpClient == null) {
			HttpParams params = new BasicHttpParams();
			// pre-set connection params
			HttpConnectionParams.setConnectionTimeout(params,30000);
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
			HttpConnectionParams.setConnectionTimeout(params,30000);
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

		if(info != null && (info.isConnected() || info.isRoaming())) {
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

//	protected final Respond handleHttpPost(String pageName) throws IOException {
//		return handleHttpExecute(makeHttpPost(pageName));
//	}

	protected final Respond handleHttpExecute(HttpRequestBase request) throws IOException {
		HttpResponse response = executeHttp(request);
		if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
			String json = new String(IOUtil.toByteArray(response.getEntity().getContent()));
			Respond respond = mapper.readValue(json, Respond.class);
			if(respond.getStatus() == HttpStatus.SC_OK) return respond;
		}
		return null;
	}

	protected HttpGet makeHttpGet(String pageName, String params) {
		String url = makeUrl(pageName, params);
		return new HttpGet(url);
	}

	protected HttpPost makeHttpPost(String pageName) {
		String url = makeUrl(pageName, null);
		return new HttpPost(url);
	}

	protected final HttpResponse executeHttp(HttpRequestBase request) throws IOException {
		return mShouldUseProxy ? getHttpClientProxy().execute(request) : getHttpClientGeneral().execute(request);
	}

}