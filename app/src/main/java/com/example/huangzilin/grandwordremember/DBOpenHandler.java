package com.example.huangzilin.grandwordremember;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import static android.content.ContentValues.TAG;

/**
 * Created by isszym on 2018/6/26.
 */

class DBOpenHandler extends SQLiteOpenHelper {
    int version;
    public DBOpenHandler(Context context, String name,
                         SQLiteDatabase.CursorFactory factory, int version) {
        super(context, name, factory, version);
        this.version = version;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {// 当数据库创建时就用SQL命令创建一个表
        db.execSQL("CREATE TABLE words(_id integer primary key autoincrement,word varchar(64) unique,level int default 0, test_count int default 0, correct_count int default 0,last_test_time timestamp)");
        Log.d(TAG, "onCreate");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "onUpgrade");
    }
}
