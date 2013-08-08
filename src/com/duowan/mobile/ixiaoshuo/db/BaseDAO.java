package com.duowan.mobile.ixiaoshuo.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;
import com.duowan.mobile.ixiaoshuo.utils.StringUtil;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

/**
 * contained all database operation method
 * @author vince
 */
public class BaseDAO {
	protected DBHelper mDBHelper;
	protected static final String TAG = "AppDAO";

	protected final int getIntValue(String sql) { return getIntValue(sql, 0); }
	protected final int getIntValue(String sql, int defValue) {
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

	// 'select last_insert_rowid()' may do that same
	protected final int getLastInsertRowId(String tableName) {
		return getIntValue("select seq from sqlite_sequence where name='" + tableName + "'");
	}

	protected final long getLongValue(String sql) { return getLongValue(sql, 0); }
	protected final long getLongValue(String sql, long defValue) {
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

	protected final String getStringValue(String sql) { return getStringValue(sql, null); }
	protected final String getStringValue(String sql, String defValue) {
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

	protected final int[] getAmountIntValue(String sql, int columnAmount) {
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

	protected final String[] getAmountStringValue(String sql, int columnAmount) {
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

	protected final boolean checkExists(String sql) {
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

	protected final boolean executeUpdate(String sql) {
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
	protected final <T> boolean executeBatchUpdate(T[] array, DBOperator<T> operator) {
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			for (T entity : array) dataBase.execSQL(operator.build(entity));
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			dataBase.close();
		}
		return false;
	}
	/** @deprecated may has "database is locked" exception, recommended use executeTranUpdate method */
	protected final <T> boolean executeBatchUpdate(List<T> list, DBOperator<T> operator) {
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			for (T entity : list) dataBase.execSQL(operator.build(entity));
			return true;
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			dataBase.close();
		}
		return false;
	}

	protected final <T> T getEntity(String sql, DBFetcher<T> fetcher) {
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

	protected final <T> T getEntity(String sql, Class<T> clazz) {
		SQLiteDatabase dataBase = mDBHelper.getReadableDatabase();
		Cursor cursor = null;
		try {
			cursor = dataBase.rawQuery(sql, null);
			if(cursor.moveToFirst()) return getEntity(cursor, clazz);
		} catch (Exception e) {
			Log.e(TAG, e.getMessage(), e);
		} finally {
			if(cursor != null) cursor.close();
		}
		return null;
	}

	protected final <T> void getFetcherList(String sql, List<T> list, DBFetcher<T> fetcher) {
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

	protected final <T> List<T> getFetcherList(String sql, DBFetcher<T> fetcher) {
		List<T> list = new ArrayList<T>();
		getFetcherList(sql, list, fetcher);
		return list;
	}

	protected final <T> List<T> getFetcherList(String sql, final Class<T> clazz) {
		return getFetcherList(sql, new DBFetcher<T>() {
			public T fetch(Cursor cursor) {
				return getEntity(cursor, clazz);
			}
		});
	}

	protected final <T> boolean executeTranUpdate(T[] array, DBOperator<T> operator) {
		if (array == null || array.length == 0) return false;
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			dataBase.beginTransaction();
			for (T entity : array) dataBase.execSQL(operator.build(entity));
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

	protected final <T> boolean executeTranUpdate(List<T> list, DBOperator<T> operator) {
		if (list == null || list.size() == 0) return false;
		SQLiteDatabase dataBase = mDBHelper.getWritableDatabase();
		try {
			dataBase.beginTransaction();
			for (T entity : list) dataBase.execSQL(operator.build(entity));
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

	private static final String methodPrefix = "set";
	protected final <T> T getEntity(Cursor cursor, Class<T> clazz) {
		try {
			T entity = clazz.newInstance();
			String[] columnNames = cursor.getColumnNames();
			for (int index = 0; index < columnNames.length; index++) {
				columnNames[index] = convertColumnName(columnNames[index]);
				for (Method method : clazz.getMethods()) {
					if ((method.getName().equalsIgnoreCase(methodPrefix + columnNames[index]))) {
						Class paramType = method.getParameterTypes()[0];
						if (paramType == String.class)
							method.invoke(entity, cursor.getString(index));
						else if (paramType == int.class)
							method.invoke(entity, cursor.getInt(index));
						else if (paramType == long.class)
							method.invoke(entity, cursor.getLong(index));
						else if (paramType == float.class)
							method.invoke(entity, cursor.getFloat(index));
						else if (paramType == double.class)
							method.invoke(entity, cursor.getDouble(index));
						break;
					}
				}
			}
			return entity;
		} catch (Exception ex) {}
		return null;
	}

	/** naming convention：login_time > loginTime */
	protected static String convertColumnName(String columnName) {
		StringBuilder nameBuffer = new StringBuilder();
		int index;
		int nextIndex = 0;
		do {
			index = columnName.indexOf('_', nextIndex);
			if(index < 0) {
				nameBuffer.append(columnName.substring(nextIndex));
				break;
			}
			nameBuffer.append(columnName.substring(nextIndex, index));
			//是否还有下一位字符，否则退出
			if(++index >= columnName.length()) break;
			//如果前面已经有输出，则转为大写，否则不操作
			if(nameBuffer.length() > 0) {
				nameBuffer.append(Character.toUpperCase(columnName.charAt(index)));
				nextIndex = ++index;
			} else nextIndex = index;
		} while (true);
		return nameBuffer.toString();
	}

	protected static String escape(String str) {
		if (StringUtil.isEmpty(str)) return "";
		str = str.replaceAll("'","''");
		str = str.replaceAll("\\\\","\\\\\\\\");
		return str;
	}

	protected interface DBFetcher<T> { T fetch(Cursor cursor); }
	protected interface DBOperator<T> { String build(T entity); }

}
