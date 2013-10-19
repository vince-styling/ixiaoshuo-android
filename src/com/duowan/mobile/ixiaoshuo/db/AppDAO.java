package com.duowan.mobile.ixiaoshuo.db;

import android.content.Context;
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
		String sql = "select book_id from " + Tables.Book.NAME + " where book_id = " + bookId;
		return getIntValue(sql);
	}

	/**
	 * @param temporaryFlag 是否为暂存书籍，详细描述于BookOnReading.java
	 */
	public int addBook(Book book, boolean temporaryFlag) {
		int bookId = checkIfBookExists(book.getBookId());
		if (bookId > 0) return bookId;

		String sql = "insert or ignore into " + Tables.Book.NAME + "(book_id, source_book_id, name, author, cover_url, summary, update_status, book_type, was_both_type, cat_id, cat_name, capacity, temporary_flag) values(" +
				"'" + book.getBookId() + "', " +
				"'" + book.getSourceBookId() + "', " +
				"'" + escape(book.getName()) + "', " +
				"'" + escape(book.getAuthor()) + "', " +
				"'" + escape(book.getCoverUrl()) + "', " +
				"'" + escape(book.getSummary()) + "', " +
				"'" + book.getUpdateStatus() + "', " +
				"'" + book.getBookType() + "', " +
				"'" + book.getIntBothType() + "', " +
				"'" + book.getCatId() + "', " +
				"'" + escape(book.getCatName()) + "', " +
				"'" + book.getCapacity() + "', " +
				"'" + (temporaryFlag ? 1 : 0) + "'" +
				")";
		return executeUpdate(sql) ? book.getBookId() : 0;
	}

	public boolean saveBookChapters(final int bookId, List<Chapter> list) {
		String sql = "delete from " + Tables.Chapter.NAME + " where book_id = " + bookId;
		executeUpdate(sql);
		boolean result = executeTranUpdate(list, new DBOperator<Chapter>() {
			@Override
			public String build(Chapter chapter) {
				return "insert or ignore into " + Tables.Chapter.NAME + "(book_id, chapter_id, title, read_status, capacity, read_position) values(" +
						"'" + bookId + "'," +
						"'" + chapter.getChapterId() + "'," +
						"'" + escape(chapter.getTitle()) + "'," +
						"'" + chapter.getReadStatus() + "'," +
						"'" + chapter.getCapacity() + "'," +
						"'" + chapter.getReadPosition() + "'" +
						")";
			}
		});

		if (result) {
			int remoteLastChapterId = list.get(list.size() - 1).getChapterId();
			sql = "update " + Tables.Book.NAME + " set remote_last_chapter_id = " + remoteLastChapterId + " where book_id = " + bookId;
			return executeUpdate(sql);
		}

		return result;
	}

	public boolean addToBookShelf(int bookId) {
		String sql = "update " + Tables.Book.NAME + " set temporary_flag = 0 where book_id = " + bookId;
		return executeUpdate(sql);
	}

	public List<Book> getBookListOnBookShelf(int bookType) {
		String sql = "select book_id, source_book_id, name, cover_url, book_type, was_both_type, update_status from " + Tables.Book.NAME + " where book_type = '" + bookType + "' and temporary_flag = 0 order by last_read_time desc";
		return getFetcherList(sql, Book.class);
	}

	public Book getBookOnReading(int bookId) {
		if (bookId <= 0) return null;
		String sql = "select book_id, source_book_id, name, book_type, update_status from " + Tables.Book.NAME + " where book_id = " + bookId;
		Book book = getEntity(sql, Book.class);
		if (book != null) {
			sql = "update " + Tables.Book.NAME + " set last_read_time = datetime('now', 'localtime') where book_id = " + bookId;
			executeUpdate(sql);
		}
		return book;
	}

	public boolean isBookOnShelf(int bookId) {
		String sql = "select temporary_flag from " + Tables.Book.NAME + " where book_id = " + bookId;
		return getIntValue(sql) == 0;
	}

	public Chapter getReadingChapter(int bookId) {
		String sql = "select chapter_id, title, read_position from " + Tables.Chapter.NAME + " where book_id = " + bookId + " and read_status = " + Chapter.READSTATUS_READING;
		Chapter chapter = getEntity(sql, Chapter.class);
		if (chapter == null) {
			sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where book_id = " + bookId + " order by chapter_id limit 1";
			chapter = getEntity(sql, Chapter.class);
			updateBookChapterReadStatus(bookId, chapter.getChapterId(), Chapter.READSTATUS_READING);
		}
		return chapter;
	}

	public void makeReadingChapter(int bookId, Chapter chapter) {
		String sql = "update " + Tables.Chapter.NAME + " set read_status = " + Chapter.READSTATUS_READED + ", read_position = 0 where book_id = " + bookId + " and read_status = " + Chapter.READSTATUS_READING;
		executeUpdate(sql);
		sql = "update " + Tables.Chapter.NAME + " set read_status = " + Chapter.READSTATUS_READING + ", read_position = " + chapter.getReadPosition() + " where book_id = " + bookId + " and chapter_id = " + chapter.getChapterId();
		executeUpdate(sql);
	}

	public int getBookChapterCount(int bookId) {
		String sql = "select count(*) from " + Tables.Chapter.NAME + " where book_id = " + bookId;
		return getIntValue(sql);
	}

	public int getBookChapterIndex(int bookId, int chapterId) {
		String sql = "select count(*) from " + Tables.Chapter.NAME + " where book_id = " + bookId + " and chapter_id < " + chapterId;
		return getIntValue(sql);
	}

	public Chapter getPreviousChapter(int bookId, int chapterId) {
		String sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where book_id = " + bookId + " and chapter_id < " + chapterId + " order by chapter_id desc limit 1";
		return getEntity(sql, Chapter.class);
	}

	public Chapter getNextChapter(int bookId, int chapterId) {
		String sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where book_id = " + bookId + " and chapter_id > " + chapterId + " order by chapter_id limit 1";
		return getEntity(sql, Chapter.class);
	}

	public void updateBookChapterReadStatus(int bookId, int chapterId, int readStatus) {
		String sql = "update " + Tables.Chapter.NAME + " set read_status = " + readStatus + " where book_id = " + bookId + " and chapter_id = " + chapterId;
		executeUpdate(sql);
	}

	public List<Chapter> getBookChapters(int bookId) {
		if (bookId <= 0) return null;
		String sql = "select chapter_id, title, read_status from " + Tables.Chapter.NAME + " where book_id = " + bookId + " order by chapter_id";
		return getFetcherList(sql, Chapter.class);
	}

	public PaginationList<Chapter> getSimpleBookChapterList(int bookId, int pageNo, int pageItemCount) {
		if (bookId <= 0) return null;
		String sql = "select chapter_id from " + Tables.Chapter.NAME + " where book_id = " + bookId + " order by chapter_id";
		return getPaginationList(sql, pageNo, pageItemCount, Chapter.class);
	}

	public int getBookUnreadChapterCount(int bookId) {
		if (bookId <= 0) return 0;
		// TODO : 修正问题：先查当前阅读到的章节，然后调用getBookChapterCount、getBookChapterIndex来计算出结果
		String sql = "select count(*) from " + Tables.Chapter.NAME + " where book_id = " + bookId + " and read_status = " + Chapter.READSTATUS_UNREAD;
		return getIntValue(sql);
	}

	public Chapter getBookLastChapter(int bookId) {
		if (bookId <= 0) return null;
		String sql = "select chapter_id, title from " + Tables.Chapter.NAME + " where book_id = " + bookId + " order by chapter_id desc limit 1";
		return getEntity(sql, Chapter.class);
	}

	public boolean deleteBook(Book book) {
		if (executeUpdate("delete from " + Tables.Book.NAME + " where book_id = " + book.getBookId())) {
			saveBookChapters(book.getBookId(), null);
			IOUtil.deleteBookCache(book);
			return true;
		}
		return false;
	}

}
