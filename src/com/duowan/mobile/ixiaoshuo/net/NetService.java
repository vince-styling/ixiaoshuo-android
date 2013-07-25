package com.duowan.mobile.ixiaoshuo.net;

import android.content.Context;
import android.net.http.AndroidHttpClient;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.BookOnUpdate;
import com.duowan.mobile.ixiaoshuo.pojo.BookUpdateInfo;
import com.duowan.mobile.ixiaoshuo.pojo.Site;
import com.duowan.mobile.ixiaoshuo.utils.IOUtil;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.codehaus.jackson.type.TypeReference;

import java.io.IOException;
import java.io.InputStream;
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

	public String getChapterContent(int bookId, int websiteId, int chapterId) {
		try {
			String params = "bookId=" + bookId + "&websiteId=" + websiteId + "&chapterId=" + chapterId;
			HttpResponse response = executeHttpGet("/book/get_chapter_content.do", params);
			if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
				InputStream ins = AndroidHttpClient.getUngzippedContent(response.getEntity());
				return new String(IOUtil.toByteArray(ins));
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

	public Book getBookDetail(int bookId, int websiteId) {
		try {
			String params = "bookId=" + bookId + "&websiteId=" + websiteId;
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
			// TODO : 未通过（没返回json，直接返回全局的错误页面）
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
			// TODO : 未通过（返回数据不是想要的）
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

}
