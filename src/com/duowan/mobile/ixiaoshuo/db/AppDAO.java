package com.duowan.mobile.ixiaoshuo.db;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;

import java.util.List;

public class AppDAO extends BaseDAO {
	private AppDAO(DBHelper dbHelper) { mDBHelper = dbHelper; }

	private static AppDAO mInstance;
	public synchronized static AppDAO get() { return mInstance; }
	public static void init(Context ctx) {
		if (mInstance == null) mInstance = new AppDAO(new DBHelper(ctx));
	}

	public int checkIfBookExists(int bookId) {
		String sql = "select bid from " + Tables.Book.NAME + " where book_id = " + bookId;
		return getIntValue(sql);
	}

	public int addBook(Book book) {
		int bid = checkIfBookExists(book.getBookId());
		if (bid > 0) return bid;

		String sql = "insert or ignore into " + Tables.Book.NAME + "(book_id, name, author, cover_url, summary, update_status, website_id, website_name) values(" +
				"'" + book.getBookId() + "', " +
				"'" + escape(book.getName()) + "', " +
				"'" + escape(book.getAuthor()) + "', " +
				"'" + escape(book.getCoverUrl()) + "', " +
				"'" + escape(book.getSummary()) + "', " +
				"'" + book.getUpdateStatus() + "', " +
				"'" + book.getWebsiteId() + "', " +
				"'" + escape(book.getWebsiteName()) + "'" +
				")";
		executeUpdate(sql);
		return getLastInsertRowId(Tables.Book.NAME);
	}

	public boolean saveBookChapters(final int bid, List<Chapter> list) {
		String sql = "delete from " + Tables.Chapter.NAME + " where bid = " + bid;
		executeUpdate(sql);
		return executeTranUpdate(list, new DBOperator<Chapter>() {
			@Override
			public String build(Chapter chapter) {
				return "insert or ignore into " + Tables.Chapter.NAME + "(bid, chapter_id, title) values(" +
						"'" + bid + "'," +
						"'" + chapter.getId() + "'," +
						"'" + escape(chapter.getTitle()) + "'" +
						")";
			}
		});
	}

	public List<Book> getBookList() {
		String sql = "select bid, book_id, name, author, cover_url, summary, update_status from " + Tables.Book.NAME + " order by create_time desc";
		return getFetcherList(sql, Book.class);
	}

	public Book getBook(int bid) {
		if (bid <= 0) return null;
		String sql = "select bid, book_id, name, author, cover_url, summary, update_status from " + Tables.Book.NAME + " where bid = " + bid;
		return getEntity(sql, Book.class);
	}

	public Book getBookForReader(int bid) {
		Book book = getBook(bid);
		if(book != null) book.setChapterList(getBookChapters(bid));
		return book;
	}

	public List<Chapter> getBookChapters(int bid) {
		if (bid <= 0) return null;
		String sql = "select chapter_id, title, read_status, begin_position from " + Tables.Chapter.NAME + " where bid = " + bid;
		return getFetcherList(sql, Chapter.class);
	}

}
