package com.vincestyling.ixiaoshuo.db;

public class Tables {
    private static final String CREATE_STATMENT = "create table if not exists ";
    private static final String DROP_STATMENT = "drop table if exists ";

    public static final class Book {
        public static final String TABLE_NAME = "book";
        public static final String ID = "id";
        public static final String NAME = "name";
        public static final String AUTHOR = "author";
        public static final String COVER_URL = "cover_url";
        public static final String SUMMARY = "summary";
        public static final String UPDATE_STATUS = "update_status";
        public static final String WAS_BOTH_TYPE = "was_both_type";
        public static final String CAT_ID = "cat_id";
        public static final String CAT_NAME = "cat_name";
        public static final String CAPACITY = "capacity";
        public static final String TEMPORARY_FLAG = "temporary_flag";
        public static final String LAST_READ_TIME = "last_read_time";
        public static final String CREATE_TIME = "create_time";

        public static String getDropStatment() {
            return DROP_STATMENT + TABLE_NAME;
        }

        public static String getCreateStatment() {
            return CREATE_STATMENT + TABLE_NAME + "(" +
                    ID + " integer primary key, " +
                    NAME + " varchar(200), " +
                    AUTHOR + " varchar(200), " +
                    COVER_URL + " varchar(200), " +
                    SUMMARY + " text, " +
                    UPDATE_STATUS + " int, " +
                    WAS_BOTH_TYPE + " tinyint(1), " +
                    CAT_ID + " int, " +
                    CAT_NAME + " varchar(20), " +
                    CAPACITY + " integer, " +
                    TEMPORARY_FLAG + " tinyint(1), " +
                    LAST_READ_TIME + " datetime, " +
                    CREATE_TIME + " datetime default (datetime('now','localtime'))" +
                    ")";
        }
    }

    public static final class Chapter {
        public static final String TABLE_NAME = "book_chapter";
        public static final String BOOK_ID = "book_id";
        public static final String CHAPTER_ID = "chapter_id";
        public static final String TITLE = "title";
        public static final String READ_STATUS = "read_status";
        public static final String CAPACITY = "capacity";
        public static final String READ_POSITION = "read_position";
        public static final String CREATE_TIME = "create_time";

        public static String getDropStatment() {
            return DROP_STATMENT + TABLE_NAME;
        }

        public static String getCreateStatment() {
            return CREATE_STATMENT + TABLE_NAME + "(" +
                    BOOK_ID + " integer, " +
                    CHAPTER_ID + " integer, " +
                    TITLE + " varchar(200), " +
                    READ_STATUS + " int, " +
                    CAPACITY + " int, " +
                    READ_POSITION + " int, " +
                    CREATE_TIME + " datetime default (datetime('now','localtime')), " +
                    "primary key(" + BOOK_ID + ", " + CHAPTER_ID + ")" +
                    ")";
        }
    }
}
