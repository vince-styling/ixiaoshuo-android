package com.vincestyling.ixiaoshuo.db;

public class Tables {

	private static final String DROP_STATMENT = "drop table if exists ";
	private static final String CREATE_STATMENT = "create table if not exists ";

	public static final class Book {
		public static final String NAME = "book";
		public static String getDropStatment() {
			return DROP_STATMENT + NAME;
		}
		public static String getCreateStatment() {
			return CREATE_STATMENT + NAME + "(" +
					"book_id integer primary key, " +
					"source_book_id integer, " +
					"name varchar(200), " +
					"author varchar(200), " +
					"cover_url varchar(200), " +
					"summary text, " +
					"update_status int, " +
					"book_type tinyint(1), " +
					"was_both_type tinyint(1), " +
					"cat_id int, " +
					"cat_name varchar(20), " +
					"capacity integer, " +
					"temporary_flag tinyint(1), " +
					"has_new_chapter tinyint(1), " +
					"remote_last_chapter_id int, " +
					"last_read_time datetime, " +
					"create_time datetime default (datetime('now','localtime'))" +
					")";
		}
	}

	public static final class Chapter {
		public static final String NAME = "book_chapter";
		public static String getDropStatment() {
			return DROP_STATMENT + NAME;
		}
		public static String getCreateStatment() {
			return CREATE_STATMENT + NAME + "(" +
					"book_id integer, " +
					"chapter_id integer, " +
					"title varchar(200), " +
					"read_status int, " +
					"capacity int, " +
					"read_position int, " +
					"create_time datetime default (datetime('now','localtime')), " +
					"primary key(book_id, chapter_id)" +
					")";
		}
	}

}
