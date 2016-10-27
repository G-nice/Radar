package com.gnice.radar.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String TABLE_NAME = "PersonList";
    public static final String NAME = "name";
    public static final String PHONENUM = "phonenum";
    public static final String TYPE = "type";
    public static final String LATITUDE = "latitude";
    public static final String LONGITUDE = "longitude";
    public static final String DISTANCE = "distance";
    public static final String LASTUPDATE = "lastupdate";
    //    数据库名称
    private static final String DATABASE_NAME = "radar.db";
    //    数据库版本
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        StringBuilder strBuilder = new StringBuilder();

        strBuilder.append("CREATE TABLE IF NOT EXISTS " + TABLE_NAME + "( ");
        strBuilder.append(NAME + " TEXT NOT NULL, ");
        //        strBuilder.append("[_id] INTEGER NOT NULL PRIMARY KEY AUTOINCREMENT, ");
        strBuilder.append(PHONENUM + " TEXT NOT NULL PRIMARY KEY, ");
        strBuilder.append(TYPE + " INTEGER NOT NULL, ");
        strBuilder.append(LATITUDE + " REAL, ");
        strBuilder.append(LONGITUDE + " REAL, ");
        strBuilder.append(DISTANCE + " REAL, ");
        strBuilder.append(LASTUPDATE + " TEXT )");
        //        strBuilder.append("[age] INTEGER,");
        //        strBuilder.append("[info] TEXT)");
        db.execSQL(strBuilder.toString());
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // TODO: 2016/9/26 add
        //        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        //        onCreate(db);
    }
}
