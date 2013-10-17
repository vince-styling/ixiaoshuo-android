package com.duowan.mobile.ixiaoshuo.db;

import android.content.Context;
import com.duowan.mobile.ixiaoshuo.event.YYReader;
import com.duowan.mobile.ixiaoshuo.pojo.Book;
import com.duowan.mobile.ixiaoshuo.pojo.Chapter;
import com.duowan.mobile.ixiaoshuo.utils.IOUtil;
import com.duowan.mobile.ixiaoshuo.utils.PaginationList;

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

	/**
	 * @param temporaryFlag 是否为暂存书籍，详细描述于BookOnReading.java
	 */
	public int addBook(Book book, boolean temporaryFlag) {
		int bid = checkIfBookExists(book.getBookId());
		if (bid > 0) return bid;

		String sql = "insert or ignore into " + Tables.Book.NAME + "(book_id, name, author, cover_url, summary, update_status, website_id, website_name, type, was_both_type, cat_id, cat_name, temporary_flag) values(" +
				"'" + book.getBookId() + "', " +
				"'" + escape(book.getName()) + "', " +
				"'" + escape(book.getAuthor()) + "', " +
				"'" + escape(book.getCoverUrl()) + "', " +
				"'" + escape(book.getSummary()) + "', " +
				"'" + book.getUpdateStatus() + "', " +
				"'" + book.getWebsiteId() + "', " +
				"'" + escape(book.getWebsiteName()) + "'," +
				"'" + book.getType() + "', " +
				"'" + book.getIntBothType() + "', " +
				"'" + book.getCatId() + "', " +
				"'" + escape(book.getCatName()) + "', " +
				"'" + (temporaryFlag ? 1 : 0) + "'" +
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
				return "insert or ignore into " + Tables.Chapter.NAME + "(bid, chapter_id, title, read_status, capacity, begin_position) values(" +
						"'" + bid + "'," +
						"'" + chapter.getId() + "'," +
						"'" + escape(chapter.getTitle()) + "'," +
						"'" + chapter.getReadStatus() + "'," +
						"'" + chapter.getCapacity() + "'," +
						"'" + chapter.getBeginPosition() + "'" +
						")";
			}
		});
	}

	public boolean addToBookShelf(int bid) {
		String sql = "update " + Tables.Book.NAME + " set temporary_flag = 0 where bid = " + bid;
		return executeUpdate(sql);
	}

	public List<Book> getBookListOnBookShelf(String type) {
		String sql = "select bid, book_id, name, cover_url, type, was_both_type, update_status from " + Tables.Book.NAME + " where type = '" + type + "' and temporary_flag = 0 order by last_read_time desc";
		return getFetcherList(sql, Book.class);
	}

	public Book getBookOnReading(int bid) {
		if (bid <= 0) return null;
		String sql = "select bid, book_id, name, type, update_status from " + Tables.Book.NAME + " where bid = " + bid;
		Book book = getEntity(sql, Book.class);
		if (book != null) {
			sql = "update " + Tables.Book.NAME + " set last_read_time = datetime('now', 'localtime') where bid = " + bid;
			executeUpdate(sql);
		}
		return book;
	}

	public boolean isBookOnShelf(int bid) {
		String sql = "select temporary_flag from " + Tables.Book.NAME + " where bid = " + bid;
		return getIntValue(sql) == 0;
	}

	public Chapter getReadingChapter(int bid) {
		String sql = "select chapter_id, title, begin_position from " + Tables.Chapter.NAME + " where bid = " + bid + " and read_status = " + Chapter.READSTATUS_READING;
		Chapter chapter = getEntity(sql, Chapter.class);
		if (chapter == null) {
			sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where bid = " + bid + " order by chapter_id limit 1";
			chapter = getEntity(sql, Chapter.class);
			updateBookChapterReadStatus(bid, chapter.getId(), Chapter.READSTATUS_READING);
		}
		return chapter;
	}

	public void makeReadingChapter(int bid, YYReader.ChapterInfo chapter) {
		String sql = "update " + Tables.Chapter.NAME + " set read_status = " + Chapter.READSTATUS_READED + ", begin_position = 0 where bid = " + bid + " and read_status = " + Chapter.READSTATUS_READING;
		executeUpdate(sql);
		sql = "update " + Tables.Chapter.NAME + " set read_status = " + Chapter.READSTATUS_READING + ", begin_position = " + chapter.mPosition + " where bid = " + bid + " and chapter_id = " + chapter.mId;
		executeUpdate(sql);
	}

	public int getBookChapterCount(int bid) {
		String sql = "select count(*) from " + Tables.Chapter.NAME + " where bid = " + bid;
		return getIntValue(sql);
	}

	public int getBookChapterIndex(int bid, int chapterId) {
		String sql = "select count(*) from " + Tables.Chapter.NAME + " where bid = " + bid + " and chapter_id < " + chapterId;
		return getIntValue(sql);
	}

	public Chapter getPreviousChapter(int bid, int chapterId) {
		String sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where bid = " + bid + " and chapter_id < " + chapterId + " order by chapter_id desc limit 1";
		return getEntity(sql, Chapter.class);
	}

	public Chapter getNextChapter(int bid, int chapterId) {
		String sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where bid = " + bid + " and chapter_id > " + chapterId + " order by chapter_id limit 1";
		return getEntity(sql, Chapter.class);
	}

	public void updateBookChapterReadStatus(int bid, int chapterId, int readStatus) {
		String sql = "update " + Tables.Chapter.NAME + " set read_status = " + readStatus + " where bid = " + bid + " and chapter_id = " + chapterId;
		executeUpdate(sql);
	}

	public List<Chapter> getBookChapters(int bid) {
		if (bid <= 0) return null;
		String sql = "select chapter_id, title, read_status from " + Tables.Chapter.NAME + " where bid = " + bid + " order by chapter_id";
		return getFetcherList(sql, Chapter.class);
	}

	public PaginationList<Chapter> getSimpleBookChapterList(int bid, int pageNo, int pageItemCount) {
		if (bid <= 0) return null;
		String sql = "select chapter_id from " + Tables.Chapter.NAME + " where bid = " + bid + " order by chapter_id";
		return getPaginationList(sql, pageNo, pageItemCount, Chapter.class);
	}

	public int getBookUnreadChapterCount(int bid) {
		if (bid <= 0) return 0;
		String sql = "select count(*) from " + Tables.Chapter.NAME + " where bid = " + bid + " and read_status = " + Chapter.READSTATUS_UNREAD;
		return getIntValue(sql);
	}

	public Chapter getBookLastChapter(int bid) {
		if (bid <= 0) return null;
		String sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where bid = " + bid + " order by chapter_id desc limit 1";
		return getEntity(sql, Chapter.class);
	}

	public boolean deleteBook(Book book) {
		if (executeUpdate("delete from " + Tables.Book.NAME + " where bid = " + book.getBid())) {
			saveBookChapters(book.getBid(), null);
			IOUtil.deleteBookCache(book);
			return true;
		}
		return false;
	}

}
