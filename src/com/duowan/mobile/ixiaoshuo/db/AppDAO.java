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

	public int addBook(Book book) {
		String sql = "insert into " + Tables.Book.NAME + "(book_id, name, author, cover_url, summary, update_status, website_id, website_name) value(" +
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
		sql = "select last_insert_rowid() from " + Tables.Book.NAME;
		return getIntValue(sql);
	}

	public boolean saveBookChapterList(final int bid, List<Chapter> list) {
		return executeTranUpdate(list, new DBOperator<Chapter>() {
			@Override
			public String gen(Chapter chapter) {
				return "insert or ignore into " + Tables.Chapter.NAME + "(bid, chapter_id, title) value(" +
						"'" + bid + "'," +
						"'" + chapter.getId() + "'," +
						"'" + escape(chapter.getTitle()) + "'" +
						")";
			}
		});
	}

}
