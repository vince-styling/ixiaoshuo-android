package com.duowan.mobile.ixiaoshuo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

import java.util.ArrayList;
import java.util.List;

/**
 * contained all database operation method
 * @author vince
 */
public class BaseDAO {
	protected DBHelper mDBHelper;
	protected static final String TAG = "AppDAO";

	public int getIntValue(String sql) { return getIntValue(sql, 0); }
	public int getIntValue(String sql, int defValue) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) return cursor.getInt(0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return defValue;
	}

	public long getLongValue(String sql) { return getLongValue(sql, 0); }
	public long getLongValue(String sql, long defValue) {
		Cursor cursor = null;
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) return cursor.getLong(0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return defValue;
	}

	public String getStringValue(String sql) { return getStringValue(sql, null); }
	public String getStringValue(String sql, String defValue) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) return cursor.getString(0);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return defValue;
	}

	public int[] getAmountIntValue(String sql, int columnAmount) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) {
				int[] result = new int[columnAmount];
				for (int i = 0; i < columnAmount; i++) {
					result[i] = cursor.getInt(i);
				}
				return result;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return null;
	}

	public String[] getAmountStringValue(String sql, int columnAmount) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) {
				String[] result = new String[columnAmount];
				for (int i = 0; i < columnAmount; i++) {
					result[i] = cursor.getString(i);
				}
				return result;
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return null;
	}

	public boolean checkExists(String sql) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			return cursor.moveToFirst();
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return false;
	}

	public boolean executeUpdate(String sql) {
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			dataBase.execSQL(sql);
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			dataBase.close();
		}
		return false;
	}

	/** @deprecated may has "database is locked" exception, recommended use executeTranUpdate method */
	public <T> boolean executeBatchUpdate(T[] array, DBOperator<T> operator) {
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			for (T entity : array) dataBase.execSQL(operator.gen(entity));
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			dataBase.close();
		}
		return false;
	}
	/** @deprecated may has "database is locked" exception, recommended use executeTranUpdate method */
	public <T> boolean executeBatchUpdate(List<T> list, DBOperator<T> operator) {
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			for (T entity : list) dataBase.execSQL(operator.gen(entity));
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			dataBase.close();
		}
		return false;
	}

	public <T> T getEntity(String sql, DBFetcher<T> fetcher) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) return fetcher.fetch(cursor);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return null;
	}

	public <T> void getFetcherList(String sql, List<T> list, DBFetcher<T> fetcher) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) {
				do {
					list.add(fetcher.fetch(cursor));
				} while (cursor.moveToNext());
			}
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
	}

	public <T> List<T> getFetcherList(String sql, DBFetcher<T> fetcher) {
		List<T> list = new ArrayList<T>();
		getFetcherList(sql, list, fetcher);
		return list;
	}

	public <T> boolean executeTranUpdate(T[] array, DBOperator<T> operator) {
		if (array == null || array.length == 0) return false;
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			dataBase.beginTransaction();
			for (T entity : array) dataBase.execSQL(operator.gen(entity));
			dataBase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			dataBase.endTransaction();
			dataBase.close();
		}
		return false;
	}

	public <T> boolean executeTranUpdate(List<T> list, DBOperator<T> operator) {
		if (list == null || list.size() == 0) return false;
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			dataBase.beginTransaction();
			for (T entity : list) dataBase.execSQL(operator.gen(entity));
			dataBase.setTransactionSuccessful();
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			dataBase.endTransaction();
			dataBase.close();
		}
		return false;
	}

	protected static String escape(String str) {
		if (StringUtil.isEmpty(str)) return "";
		str = str.replaceAll("'","''");
		str = str.replaceAll("\\\\","\\\\\\\\");
		return str;
	}

}
interface DBFetcher<T> { public T fetch(Cursor cursor); }
interface DBOperator<T> { public String gen(T entity); }
