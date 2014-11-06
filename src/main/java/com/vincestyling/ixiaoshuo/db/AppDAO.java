package com.vincestyling.ixiaoshuo.db;

import android.content.Context;
import com.vincestyling.ixiaoshuo.db.statment.CreateStatment;
import com.vincestyling.ixiaoshuo.db.statment.DeleteStatment;
import com.vincestyling.ixiaoshuo.db.statment.QueryStatment;
import com.vincestyling.ixiaoshuo.db.statment.UpdateStatment;
import com.vincestyling.ixiaoshuo.pojo.Book;
import com.vincestyling.ixiaoshuo.pojo.Chapter;
import com.vincestyling.ixiaoshuo.utils.IOUtil;
import com.vincestyling.ixiaoshuo.utils.PaginationList;

import java.util.List;

public class AppDAO extends BaseDAO {
    private AppDAO(DBHelper dbHelper) {
        mDBHelper = dbHelper;
    }

    private static AppDAO mInstance;

    public static AppDAO get() {
        return mInstance;
    }

    public static void init(Context ctx) {
        if (mInstance == null) mInstance = new AppDAO(new DBHelper(ctx));
    }

    public int checkIfBookExists(int bookId) {
        return getIntValue(QueryStatment.build(Tables.Book.ID).from(Tables.Book.TABLE_NAME)
                        .where(Tables.Book.ID).eq(bookId));
    }

    public int addBook(Book book, boolean temporaryFlag) {
        int bookId = checkIfBookExists(book.getId());
        if (bookId > 0) return bookId;
        book.mockWasBothType();
        return executeUpdate(
                CreateStatment.build(Tables.Book.TABLE_NAME)
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
        ) ? book.getId() : 0;
    }

    public boolean saveBookChapters(final int bookId, List<Chapter> list) {
        executeUpdate(DeleteStatment.build(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId));
        return executeTranUpdate(list, new DBOperator<Chapter>() {
            @Override
            public Object build(Chapter chapter) {
                return CreateStatment.build(Tables.Chapter.TABLE_NAME)
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
        return executeUpdate(UpdateStatment.build(Tables.Book.TABLE_NAME)
                .set(Tables.Book.TEMPORARY_FLAG, 0).where(Tables.Book.ID).eq(bookId));
    }

    public List<Book> getBookListOnBookShelf() {
        return getFetcherList(QueryStatment.build(
                Tables.Book.ID, Tables.Book.NAME, Tables.Book.WAS_BOTH_TYPE,
                Tables.Book.COVER_URL, Tables.Book.UPDATE_STATUS).from(Tables.Book.TABLE_NAME)
                .where(Tables.Book.TEMPORARY_FLAG).eq(0).orderby(Tables.Book.LAST_READ_TIME).desc(), Book.class);
    }

    public Book getBookOnReading(int bookId) {
        Book book = getEntity(QueryStatment.build(Tables.Book.ID, Tables.Book.NAME, Tables.Book.UPDATE_STATUS)
                .from(Tables.Book.TABLE_NAME).where(Tables.Book.ID).eq(bookId), Book.class);
        if (book != null) {
            executeUpdate(UpdateStatment.build(Tables.Book.TABLE_NAME)
                    .set(Tables.Book.LAST_READ_TIME, "datetime('now', 'localtime')").where(Tables.Book.ID).eq(bookId));
        }
        return book;
    }

    public boolean isBookOnShelf(int bookId) {
        return getIntValue(QueryStatment.build(Tables.Book.TEMPORARY_FLAG)
                .from(Tables.Book.TABLE_NAME).where(Tables.Book.ID).eq(bookId)) == 0;
    }

    public Chapter getReadingChapter(int bookId) {
        Chapter chapter = getEntity(QueryStatment.build(
                Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE, Tables.Chapter.READ_POSITION)
                .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId)
                .and(Tables.Chapter.READ_STATUS).eq(Chapter.READSTATUS_READING), Chapter.class);
        if (chapter == null) {
            chapter = getEntity(QueryStatment.build(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE)
                    .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId)
                    .orderby(Tables.Chapter.CHAPTER_ID).limit(1), Chapter.class);
            updateBookChapterReadStatus(bookId, chapter.getChapterId(), Chapter.READSTATUS_READING);
        }
        return chapter;
    }

    public void makeReadingChapter(int bookId, Chapter chapter) {
        executeUpdate(UpdateStatment.build(Tables.Chapter.TABLE_NAME)
                .set(Tables.Chapter.READ_STATUS, Chapter.READSTATUS_READED)
                .set(Tables.Chapter.READ_POSITION, 0).where(Tables.Chapter.BOOK_ID)
                .eq(bookId).and(Tables.Chapter.READ_STATUS).eq(Chapter.READSTATUS_READING));
        executeUpdate(UpdateStatment.build(Tables.Chapter.TABLE_NAME)
                .set(Tables.Chapter.READ_STATUS, Chapter.READSTATUS_READING)
                .set(Tables.Chapter.READ_POSITION, chapter.getReadPosition())
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).eq(chapter.getChapterId()));
    }

    public int getBookChapterCount(int bookId) {
        return getIntValue(QueryStatment.rowcount()
                .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID).eq(bookId));
    }

    public int getBookChapterIndex(int bookId, int chapterId) {
        return getIntValue(QueryStatment.rowcount().from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).lt(chapterId));
    }

    public Chapter getPreviousChapter(int bookId, int chapterId) {
        return getEntity(QueryStatment.build(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).lt(chapterId)
                .orderby(Tables.Chapter.CHAPTER_ID).desc().limit(1), Chapter.class);
    }

    public Chapter getNextChapter(int bookId, int chapterId) {
        return getEntity(QueryStatment.build(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).gt(chapterId)
                .orderby(Tables.Chapter.CHAPTER_ID).limit(1), Chapter.class);
    }

    public void updateBookChapterReadStatus(int bookId, int chapterId, int readStatus) {
        executeUpdate(UpdateStatment.build(Tables.Chapter.TABLE_NAME).set(Tables.Chapter.READ_STATUS, readStatus)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).and(Tables.Chapter.CHAPTER_ID).eq(chapterId));
    }

    public List<Chapter> getBookChapters(int bookId) {
        return getFetcherList(QueryStatment.build(Tables.Chapter.CHAPTER_ID, Tables.Chapter.TITLE, Tables.Chapter.READ_STATUS)
                .from(Tables.Chapter.TABLE_NAME).where(Tables.Chapter.BOOK_ID)
                .eq(bookId).orderby(Tables.Chapter.CHAPTER_ID), Chapter.class);
    }

    public PaginationList<Chapter> getSimpleBookChapters(int bookId, int pageNo, int pageItemCount) {
        return getPaginationList(QueryStatment.build(Tables.Chapter.CHAPTER_ID).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).orderby(Tables.Chapter.CHAPTER_ID), pageNo, pageItemCount, Chapter.class);
    }

    public int getBookUnreadChapterCount(int bookId) {
        int chapterCount = getBookChapterCount(bookId);
        int chapterId = getReadingChapter(bookId).getChapterId();
        int index = getBookChapterIndex(bookId, chapterId);
        return chapterCount - index;
    }

    public String getBookLastChapterTitle(int bookId) {
        return getStringValue(QueryStatment.build(Tables.Chapter.TITLE).from(Tables.Chapter.TABLE_NAME)
                .where(Tables.Chapter.BOOK_ID).eq(bookId).orderby(Tables.Chapter.CHAPTER_ID).desc().limit(1), "");
    }

    // this method should be executing async with main thread.
    public boolean deleteBook(Book book) {
        if (executeUpdate(DeleteStatment.build(Tables.Book.TABLE_NAME).where(Tables.Book.ID).eq(book.getId()))) {
            saveBookChapters(book.getId(), null);
            IOUtil.deleteBookCache(book);
            return true;
        }
        return false;
    }
}
