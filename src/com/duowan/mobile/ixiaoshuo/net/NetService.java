package com.duowan.mobile.ixiaoshuo.net;

import android.content.Context;
import android.util.Log;
import com.duowan.mobile.ixiaoshuo.pojo.*;
import com.duowan.mobile.ixiaoshuo.utils.IOUtil;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;
import org.codehaus.jackson.type.TypeReference;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;

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

	public List<BookUpdateInfo> syncChapterUpdateOnBookshelf(List<BookOnUpdate> bookList) {
		if (!mNetworkAvailable) return null;
		try {
			HttpPost httpPost = makeHttpPost("/bookshelf/get_chapter_update.do");
			NetUtil.putListToParams(httpPost, bookList, "bookList");
			Respond respond = handleHttpExecute(httpPost);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<BookUpdateInfo>>(){});
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public String getChapterContent(int bookId, int chapterId) {
		if (!mNetworkAvailable) return null;
		HttpEntity entity = null;
		try {
			String params = "bookId=" + bookId + "&chapterId=" + chapterId;
			HttpResponse response = executeHttpGet("/book/get_chapter_content.do", params);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				entity = response.getEntity();
				InputStream ins = new GZIPInputStream(entity.getContent());
				return new String(IOUtil.toByteArray(ins));
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			closeEntity(entity);
		}
		return null;
	}

	public PaginationList<Chapter> getBookChapterList(int bookId, int lastChapterId, int pageNo, int pageItemCount) {
		if (!mNetworkAvailable) return null;
		return Chapter.getChapterList(pageNo, pageItemCount);
//		try {
			// TODO : lastChapterId 参数名写错了！
//			String params = "bookId=" + bookId + "&lastChapaterId=" + lastChapterId + "&pageNo=" + pageNo + "&pageItemCount=" + pageItemCount;
//			Respond respond = handleHttpGet("/book/newly_chapter.do", params);
//			if (Respond.isCorrect(respond)) {
//				return respond.convertPaginationList(Chapter.class);
//			}
//		} catch (IOException e) {
//			Log.e(TAG, e.getMessage(), e);
//		}
//		return null;
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

	public List<Category> getCategories(String type) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "type=" + type;
			Respond respond = handleHttpGet("/book/get_categories.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Category>>(){});
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getBookListByUpdateStatus(String type, int updateStatus, int pageNo, int pageItemCount) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "type=" + type + "&updateStatus=" + updateStatus + "&pageNo=" + pageNo + "&pageItemCount=" + pageItemCount;
			Respond respond = handleHttpGet("/book/list_bystatus.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getBookListByCategory(String type, int catId, int pageNo, int pageItemCount) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "type=" + type + "&catId=" + catId + "&pageNo=" + pageNo + "&pageItemCount=" + pageItemCount;
			Respond respond = handleHttpGet("/book/list_bycategory.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getNewlyBookList(String type, int pageNo, int pageItemCount) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "type=" + type + "&pageNo=" + pageNo + "&pageItemCount=" + pageItemCount;
			Respond respond = handleHttpGet("/book/list_bynewly.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public PaginationList<Book> getHottestBookList(String type, int pageNo, int pageItemCount) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "type=" + type + "&pageNo=" + pageNo + "&pageItemCount=" + pageItemCount;
			Respond respond = handleHttpGet("/book/list_byhottest.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convertPaginationList(Book.class);
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public List<Book> bookSearch(String keyword, int updateStatus, int pageNo, int pageItemCount) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "keyword=" + keyword + "&updateStatus=" + updateStatus + "&pageNo=" + pageNo + "&pageItemCount=" + pageItemCount;
			Respond respond = handleHttpGet("/book/book_search.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Book>>(){});
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public List<Book> getReadingBookList(int pageNo, int pageItemCount) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "pageNo=" + pageNo + "&pageItemCount=" + pageItemCount;
			Respond respond = handleHttpGet("/book/reading_ranking.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Book>>() {
				});
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

	public List<Chapter> syncNewlyChapterOfBook(int bookId, int lastChapaterId) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "bookId=" + bookId + "&lastChapaterId=" + lastChapaterId;
			Respond respond = handleHttpGet("/book/newly_chapter.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Chapter>>(){});
			}
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return null;
	}

	public boolean userFeedBack(String content, String imei) {
		if (!mNetworkAvailable) return false;
		try {
			HttpPost httpPost = makeHttpPost("/user_feedback.do");

			List<NameValuePair> paramList = new ArrayList<NameValuePair>(2);
			paramList.add(new BasicNameValuePair("content", content));
			paramList.add(new BasicNameValuePair("imei", imei));
			httpPost.setEntity(new UrlEncodedFormEntity(paramList));

			Respond respond = handleHttpExecute(httpPost);
			return Respond.isCorrect(respond);
		} catch (IOException e) {
			Log.e(TAG, e.getMessage(), e);
		}
		return false;
	}

	public VersionUpdate getVersionUpdateInfo() {
		return getVersionUpdateInfo(versionCode, versionName);
	}

	public VersionUpdate getVersionUpdateInfo(int versionCode, String versionName) {
		if (!mNetworkAvailable) return null;
		try {
			String params = "versionCode=" + versionCode + "&versionName=" + versionName;
			Respond respond = handleHttpGet("/get_update_version.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(VersionUpdate.class);
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

}
