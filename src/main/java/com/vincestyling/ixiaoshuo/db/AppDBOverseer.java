package com.vincestyling.ixiaoshuo.db;

import android.content.Context;
import android.database.sqlite.SQLiteOpenHelper;
import com.vincestyling.asqliteplus.DBOperator;
import com.vincestyling.asqliteplus.DBOverseer;
import com.vincestyling.asqliteplus.PaginationList;
import com.vincestyling.asqliteplus.statement.*;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.IOUtil;

import java.util.List;

public class AppDBOverseer extends DBOverseer {
    private AppDBOverseer(SQLiteOpenHelper dbHelper) {
        super(dbHelper);
    }

    private static AppDBOverseer mInstance;

    public static AppDBOverseer get() {
        return mInstance;
    }

    public static void init(Context ctx) {
        if (mInstance == null) mInstance = new AppDBOverseer(new DBHelper(ctx));
    }

    public boolean checkIfBookExists(int bookId) {
        return checkIfExists(QueryStatement.produce(Tables.Book.ID).from(Tables.Book.TABLE_NAME)
                .where(Tables.Book.ID).eq(bookId));
    }

    public int addBook(Book book, boolean temporaryFlag) {
        if (checkIfBookExists(book.getId())) return book.getId();
        book.mockWasBothType();
        return executeSql(
                CreateStatement.produce(Tables.Book.TABLE_NAME)
                        .put(Tables.Book.ID, book.getId())
                        .put(Tables.Book.NAME, book.getName())
                        .put(Tables.Book.AUTHOR, book.getAuthor())
                        .put(Tables.Book.COVER_URL, book.getCoverUrl())
                        .put(Tables.Book.SUMMARY, book.getSummary())
                        .put(Tables.Book.UPDATE_STATUS, book.getUpdateStatus())
                        .put(Tables.Book.WAS_BOTH_TYPE, book.getIntBothType())
                        .put(Tables.Book.CAT_ID, book.getCatId())
                        .put(Tables.Book.CAT_NAME, book.getCatName())
                        .put(Tables.Book.CAPACITY, book.getCapacity())
                        .put(Tables.Book.TEMPORARY_FLAG, temporaryFlag ? 1 : 0)
        ) > 0 ? book.getId() : 0;
    }

    public boolean saveBookChapters(final int bookId, List<Chapter> list) {
        executeSql(DeleteStatement.produce(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId));
        return executeBatch(list, new DBOperator<Chapter>() {
            @Override
            public Object produce(Chapter chapter) {
                return CreateStatement.produce(Tables.Chapter.TABLE_NAME)
                        .put(Tables.Chapter.BOOK_ID, bookId)
                        .put(Tables.Chapter.CHAPTER_ID, chapter.getChapterId())
                        .put(Tables.Chapter.TITLE, chapter.getTitle())
                        .put(Tables.Chapter.READ_STATUS, chapter.getReadStatus())
                        .put(Tables.Chapter.CAPACITY, chapter.getCapacity())
                        .put(Tables.Chapter.READ_POSITION, chapter.getReadPosition());
            }
        });
    }

    public boolean addToBookShelf(int bookId) {
        return executeSql(UpdateStatement.produce(Tables.Book.TABLE_NAME)
                .set(Tables.Book.TEMPORARY_FLAG, 0).where(Tables.Book.ID).eq(bookId)) > 0;
    }

    public List<Book> getBookListOnBookShelf() {
        return getList(QueryStatement.produce(
                Tables.Book.ID, Tables.Book.NAME, Tables.Book.WAS_BOTH_TYPE,
                Tables.Book.COVER_URL, Tables.Book.UPDATE_STATUS).from(Tables.Book.TABLE_NAME)
                .where(Tables.Book.TEMPORARY_FLAG).eq(0).orderBy(Tables.Book.LAST_READ_TIME).desc(), Book.class);
    }

    public Book getBookOnReading(int bookId) {
        Book book = getEntity(QueryStatement.produce(Tables.Book.ID, Tables.Book.NAME, Tables.Book.UPDATE_STATUS)
                .from(Tables.Book.TABLE_NAME).where(Tables.Book.ID).eq(bookId), Book.class);
        if (book != null) {
            executeSql(UpdateStatement.produce(Tables.Book.TABLE_NAME).set(Tables.Book.LAST_READ_TIME,
                    new UnescapeString("datetime('now', 'localtime')")).where(Tables.Book.ID).eq(bookId));
        }
        return book;
    }

    public boolean isBookOnShelf(int bookId) {
        return getInt(QueryStatement.produce(Tables.Book.TEMPORARY_FLAG)
                .from(Tables.Book.TABLE_NAME).where(Tables.Book.ID).eq(bookId)) == 0;
    }

    public Chapter getReadingChapter(int bookId) {
        Chapter chapter = getEntity(QueryStatement.produce(
                Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE, Tables.Chapter.READ_POSITION)
                .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId)
                .and(Tables.Chapter.READ_STATUS).eq(Chapter.READSTATUS_READING), Chapter.class);
        if (chapter == null) {
            chapter = getEntity(QueryStatement.produce(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE)
                    .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId)
                    .orderBy(Tables.Chapter.CHAPTER_ID).limit(1), Chapter.class);
            updateBookChapterReadStatus(bookId, chapter.getChapterId(), Chapter.READSTATUS_READING);
        }
        return chapter;
    }

    public void makeReadingChapter(int bookId, Chapter chapter) {
        executeSql(UpdateStatement.produce(Tables.Chapter.TABLE_NAME)
                .set(Tables.Chapter.READ_STATUS, Chapter.READSTATUS_READED)
                .set(Tables.Chapter.READ_POSITION, 0).where(Tables.Chapter.BOOK_ID)
                .eq(bookId).and(Tables.Chapter.READ_STATUS).eq(Chapter.READSTATUS_READING));
        executeSql(UpdateStatement.produce(Tables.Chapter.TABLE_NAME)
                .set(Tables.Chapter.READ_STATUS, Chapter.READSTATUS_READING)
                .set(Tables.Chapter.READ_POSITION, chapter.getReadPosition())
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).eq(chapter.getChapterId()));
    }

    public int getBookChapterCount(int bookId) {
        return getInt(QueryStatement.rowCount()
                .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId));
    }

    public int getBookChapterIndex(int bookId, int chapterId) {
        return getInt(QueryStatement.rowCount().from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).lt(chapterId));
    }

    public Chapter getPreviousChapter(int bookId, int chapterId) {
        return getEntity(QueryStatement.produce(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).lt(chapterId)
                .orderBy(Tables.Chapter.CHAPTER_ID).desc().limit(1), Chapter.class);
    }

    public Chapter getNextChapter(int bookId, int chapterId) {
        return getEntity(QueryStatement.produce(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).gt(chapterId)
                .orderBy(Tables.Chapter.CHAPTER_ID).limit(1), Chapter.class);
    }

    public void updateBookChapterReadStatus(int bookId, int chapterId, int readStatus) {
        executeSql(UpdateStatement.produce(Tables.Chapter.TABLE_NAME).set(Tables.Chapter.READ_STATUS, readStatus)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).eq(chapterId));
    }

    public List<Chapter> getBookChapters(int bookId) {
        return getList(QueryStatement.produce(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE, Tables.Chapter.READ_STATUS)
                .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID)
                .eq(bookId).orderBy(Tables.Chapter.CHAPTER_ID), Chapter.class);
    }

    public PaginationList<Chapter> getSimpleBookChapters(int bookId, int pageNo, int pageItemCount) {
        return getPaginationList(QueryStatement.produce(Tables.Chapter.CHAPTER_ID).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).orderBy(Tables.Chapter.CHAPTER_ID), pageNo, pageItemCount, Chapter.class);
    }

    public int getBookUnreadChapterCount(int bookId) {
        int chapterCount = getBookChapterCount(bookId);
        int chapterId = getReadingChapter(bookId).getChapterId();
        int index = getBookChapterIndex(bookId, chapterId);
        return chapterCount - index;
    }

    public String getBookLastChapterTitle(int bookId) {
        return getString(QueryStatement.produce(Tables.Chapter.TITLE).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).orderBy(Tables.Chapter.CHAPTER_ID).desc().limit(1), "");
    }

    // this method should be executing async with main thread.
    public boolean deleteBook(Book book) {
        if (executeSql(DeleteStatement.produce(Tables.Book.TABLE_NAME).where(Tables.Book.ID).eq(book.getId())) > 0) {
            saveBookChapters(book.getId(), null);
            IOUtil.deleteBookCache(book);
            return true;
        }
        return false;
    }
}
