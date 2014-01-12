package com.vincestyling.ixiaoshuo.net;

import android.content.Context;
import android.util.Log;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Category;
import com.vincestyling.ixiaoshuo.utils.PaginationList;
import com.vincestyling.ixiaoshuo.utils.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public final class NetService extends BaseNetService {
	private NetService() {}

	private static NetService mInstance;
	public static NetService get() {
		return mInstance;
	}

	/** must init with application startup */
	public static synchronized void init(Context context) {
		if (mInstance != null) return;
		mInstance = new NetService();
		mInstance.doInit(context);
	}

	public Book getBookDetail(int bookId) {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/book/detail.do", "bookId=" + bookId);
			if (Respond.isCorrect(respond)) return respond.convert(Book.class);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}
	
	public Book getVoiceBookDetail(int bookId) {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/book_voice/detail.do", "bookId=" + bookId);
			if (Respond.isCorrect(respond)) return respond.convert(Book.class);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public List<Category> getCategories() {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/categories", null);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Category>>() {
				});
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getBookListByUpdateStatus(int updateStatus, int pageNo) {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/list_bystatus/" + updateStatus + "/" + pageNo, null);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getBookListByCategory(int catId, int pageNo) {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/list_bycategory/" + catId + "/" + pageNo, null);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getNewlyBookList(int pageNo) {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/list_bynewly/" + pageNo, null);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getHottestBookList(int pageNo) {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/list_byhottest/" + pageNo, null);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public String[] getHotKeyWords() {
		if (!mNetworkAvailable) return null;
		try {
			Respond respond = handleHttpGet("/book/hot_keywords.do", null);
			if (Respond.isCorrect(respond)) {
				String keywords = respond.convert(String.class);
				if(StringUtil.isNotEmpty(keywords)) {
					return keywords.split(",");
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public boolean downloadFile(String url, File file) {
		if (!mNetworkAvailable || !StringUtil.isValidUrl(url)) return false;
		HttpEntity entity = null;
		try {
			HttpResponse response = executeHttp(new HttpGet(url));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				entity = response.getEntity();
				byte[] bytes = EntityUtils.toByteArray(entity);
				if (bytes.length > 0) {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(bytes);
					fos.close();
					return true;
				}
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			closeEntity(entity);
		}
		return false;
	}


	public PaginationList<Book> getBookListBySearch(int type, int pageNo, int pageItemCount, String keyWord) {
		if (!mNetworkAvailable) return null;
//		try {
//			RequestParameters parameters = new RequestParameters();
//			parameters.add(RequestParameters.SEARCH_KEY_WORD, keyWord);
//			parameters.add(RequestParameters.TYPE, type);
//			parameters.add(RequestParameters.PAGENO, pageNo);
//			parameters.add(RequestParameters.PAGE_ITEM_COUNT, pageItemCount);
//			Respond respond = handleHttpGet(RequestParameters.BOOK_SEARCH, StringUtil.encodeUrl(parameters));
//			if (Respond.isCorrect(respond)) {
//				Log.i(TAG, "respons is: " + respond.getData().toString());
//				return respond.convertPaginationList(Book.class);
//			}
//		} catch (IOException e) {
//			Log.e(TAG, e.getMessage(), e);
//		}
		return null;
	}
	
}
