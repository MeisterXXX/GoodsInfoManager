package com.zsj.ilovemyfamily.homework;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Administrator on 2015/9/2.
 */
public class MyDBHelper extends SQLiteOpenHelper {

    public MyDBHelper(Context context) {
        super(context, "test",null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {//varchar(100),text
        String str = "create table things (_id integer primary key,name varchar(100),content text)";
        db.execSQL(str);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}

