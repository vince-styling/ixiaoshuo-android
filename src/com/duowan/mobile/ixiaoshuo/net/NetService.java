package com.duowan.mobile.ixiaoshuo.net;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.pojo.*;
import com.duowan.mobile.ixiaoshuo.utils.IOUtil;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.message.BasicNameValuePair;
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
		try {
			HttpPost httpPost = makeHttpPost("/bookshelf/get_chapter_update.do");
			NetUtil.putListToParams(httpPost, bookList, "bookList");
			Respond respond = handleHttpExecute(httpPost);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<BookUpdateInfo>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String getChapterContent(int bookId, int chapterId) {
		try {
			String params = "bookId=" + bookId + "&chapterId=" + chapterId;
			HttpResponse response = executeHttpGet("/book/get_chapter_content.do", params);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream ins = new GZIPInputStream(response.getEntity().getContent());
				return new String(IOUtil.toByteArray(ins));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Book getBookDetail(int bookId) {
		try {
			// TODO : 要确定来源ID及来源网站名称有传。
			String params = "bookId=" + bookId;
			Respond respond = handleHttpGet("/book/book_detail.do", params);
			if (Respond.isCorrect(respond)) return respond.convert(Book.class);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Book> bookSearch(String keyword, int updateStatus, int pageNo, int pageSize) {
		try {
			String params = "keyword=" + keyword + "&updateStatus=" + updateStatus + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
			Respond respond = handleHttpGet("/book/book_search.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Book>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Book> getReadingBookList(int pageNo, int pageSize) {
		try {
			String params = "pageNo=" + pageNo + "&pageSize=" + pageSize;
			Respond respond = handleHttpGet("/book/reading_ranking.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Book>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Book> getBookRanking(String type, int pageNo, int pageSize) {
		try {
			String params = "type=" + type + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
			Respond respond = handleHttpGet("/book/book_ranking.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Book>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Site> getSiteRankingList() {
		try {
			Respond respond = handleHttpGet("/get_site_list_ranking.do", null);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Site>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Book> getSiteBookRanking(int siteId, int pageNo, int pageSize) {
		try {
			String params = "siteId=" + siteId + "&pageNo=" + pageNo + "&pageSize=" + pageSize;
			Respond respond = handleHttpGet("/book/site_ranking.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Book>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public String[] getHotKeyWords() {
		try {
			Respond respond = handleHttpGet("/book/hot_keywords.do", null);
			if (Respond.isCorrect(respond)) {
				String keywords = respond.convert(String.class);
				if(StringUtil.isNotEmpty(keywords)) {
					return keywords.split(",");
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<Chapter> syncNewlyChapterOfBook(int bookId, int lastChapaterId) {
		try {
			String params = "bookId=" + bookId + "&lastChapaterId=" + lastChapaterId;
			Respond respond = handleHttpGet("/book/newly_chapter.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(new TypeReference<List<Chapter>>(){});
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean userFeedBack(String content, String imei) {
		try {
			HttpPost httpPost = makeHttpPost("/user_feedback.do");

			List<NameValuePair> paramList = new ArrayList<NameValuePair>(2);
			paramList.add(new BasicNameValuePair("content", content));
			paramList.add(new BasicNameValuePair("imei", imei));
			httpPost.setEntity(new UrlEncodedFormEntity(paramList));

			Respond respond = handleHttpExecute(httpPost);
			return Respond.isCorrect(respond);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	public VersionUpdate getVersionUpdateInfo() {
		return getVersionUpdateInfo(versionCode, versionName);
	}

	public VersionUpdate getVersionUpdateInfo(int versionCode, String versionName) {
		try {
			String params = "versionCode=" + versionCode + "&versionName=" + versionName;
			Respond respond = handleHttpGet("/get_update_version.do", params);
			if (Respond.isCorrect(respond)) {
				return respond.convert(VersionUpdate.class);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public boolean downloadFile(String url, File file) {
		try {
			HttpResponse response = executeHttp(new HttpGet(url));
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				byte[] bytes = IOUtil.toByteArray(response.getEntity().getContent());
				if (bytes.length > 0) {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(bytes);
					fos.close();
					return true;
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

}
