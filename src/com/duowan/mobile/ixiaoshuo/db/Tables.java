package com.duowan.mobile.ixiaoshuo.db;

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
					"bid integer primary key autoincrement, " +
					"book_id integer, " +
					"name varchar(200), " +
					"author varchar(200), " +
					"cover_url varchar(200), " +
					"summary text, " +
					"update_status int, " +
					"type varchar(10), " +
					"was_both_type tinyint(1), " +
					"website_id int, " +
					"website_name varchar(60), " +
					"cat_id int, " +
					"cat_name varchar(20), " +
					"temporary_flag tinyint(1), " +
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
					"bid integer, " +
					"chapter_id integer, " +
					"title varchar(200), " +
					"read_status int, " +
					"capacity int, " +
					"begin_position int, " +
					"create_time datetime default (datetime('now','localtime')), " +
					"primary key(bid, chapter_id)" +
					")";
		}
	}

}
