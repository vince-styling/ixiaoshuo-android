package com.vincestyling.ixiaoshuo.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.vincestyling.ixiaoshuo.utils.SysUtil;

public class DBHelper extends SQLiteOpenHelper {

    public DBHelper(Context ctx) {
        super(ctx, "ixiaoshuo_reader.db", null, SysUtil.getVersionCode(ctx));
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(Tables.Book.getCreateStatment());
        db.execSQL(Tables.Chapter.getCreateStatment());
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }

}
