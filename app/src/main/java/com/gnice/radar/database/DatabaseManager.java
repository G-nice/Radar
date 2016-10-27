package com.gnice.radar.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.gnice.radar.util.PersonItem;

public class DatabaseManager {
    private DatabaseHelper dbHelper;
    private SQLiteDatabase database;

    public DatabaseManager(Context context) {
        dbHelper = new DatabaseHelper(context);
        database = dbHelper.getWritableDatabase();
    }

    public void open() throws SQLException {
        database = dbHelper.getWritableDatabase();
    }

    public void close() {
        dbHelper.close();
    }

    public void add(PersonItem personItem) {
        database.beginTransaction();
        ContentValues contentValues = new ContentValues();
        contentValues.put(DatabaseHelper.NAME, personItem.getName());
        contentValues.put(DatabaseHelper.PHONENUM, personItem.getPhoneNum());
        contentValues.put(DatabaseHelper.TYPE, personItem.getType());
        contentValues.put(DatabaseHelper.LATITUDE, personItem.getLatitude());
        contentValues.put(DatabaseHelper.LONGITUDE, personItem.getLongitude());
        contentValues.put(DatabaseHelper.DISTANCE, personItem.getDistance());
        contentValues.put(DatabaseHelper.LASTUPDATE, personItem.getLastUpdate());

        try {
            long in = database.insert(DatabaseHelper.TABLE_NAME, null, contentValues);
            Log.i("DB insert", "" + in);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void delete(PersonItem personItem) {
        database.beginTransaction();
        try {
            String[] whereArgs = {personItem.getPhoneNum()};//删除的条件参数
            int in = database.delete(DatabaseHelper.TABLE_NAME, DatabaseHelper.PHONENUM + "=?", whereArgs);
            Log.i("DB delete", "" + in);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void updateMyself(PersonItem personItem) {
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            //            contentValues.put(DatabaseHelper.LATITUDE, personItem.getLatitude());
            //            contentValues.put(DatabaseHelper.LONGITUDE, personItem.getLongitude());
            //            String[] whereArgs = {String.valueOf(PersonItem.MYSELF)};
            //            int in = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.TYPE + "=?", whereArgs);

            contentValues.put(DatabaseHelper.NAME, personItem.getName());
            contentValues.put(DatabaseHelper.PHONENUM, personItem.getPhoneNum());
            contentValues.put(DatabaseHelper.TYPE, personItem.getType());
            contentValues.put(DatabaseHelper.LATITUDE, personItem.getLatitude());
            contentValues.put(DatabaseHelper.LONGITUDE, personItem.getLongitude());
            contentValues.put(DatabaseHelper.DISTANCE, "" + 0);
            contentValues.put(DatabaseHelper.LASTUPDATE, personItem.getLastUpdate());

            // 不存在插入，存在更新语句
            long in = database.replace(DatabaseHelper.TABLE_NAME, "88888888888", contentValues);

            Log.i("DB update", "" + in);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void updateAll(PersonItem personItem) {
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.NAME, personItem.getName());
            contentValues.put(DatabaseHelper.PHONENUM, personItem.getPhoneNum());
            contentValues.put(DatabaseHelper.TYPE, personItem.getType());
            contentValues.put(DatabaseHelper.LATITUDE, personItem.getLatitude());
            contentValues.put(DatabaseHelper.LONGITUDE, personItem.getLongitude());
            contentValues.put(DatabaseHelper.DISTANCE, personItem.getDistance());
            contentValues.put(DatabaseHelper.LASTUPDATE, personItem.getLastUpdate());

            String[] whereArgs = {personItem.getPhoneNum()};
            int in = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.PHONENUM + "=?", whereArgs);
            Log.i("DB update", "" + in);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void updateInfo(PersonItem personItem) {
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.NAME, personItem.getName());
            contentValues.put(DatabaseHelper.PHONENUM, personItem.getPhoneNum());

            String[] whereArgs = {personItem.getPhoneNum()};
            int in = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.PHONENUM + "=?", whereArgs);
            Log.i("DB update", "" + in);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void updatePosition(PersonItem personItem) {
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.LATITUDE, personItem.getLatitude());
            contentValues.put(DatabaseHelper.LONGITUDE, personItem.getLongitude());
            contentValues.put(DatabaseHelper.DISTANCE, personItem.getDistance());
            contentValues.put(DatabaseHelper.LASTUPDATE, personItem.getLastUpdate());

            String[] whereArgs = {personItem.getPhoneNum()};
            int in = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.PHONENUM + "=?", whereArgs);
            Log.i("DB update", "" + in);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    public void updateType(PersonItem personItem) {
        database.beginTransaction();
        try {
            ContentValues contentValues = new ContentValues();
            contentValues.put(DatabaseHelper.TYPE, personItem.getType());

            String[] whereArgs = {personItem.getPhoneNum()};
            int in = database.update(DatabaseHelper.TABLE_NAME, contentValues, DatabaseHelper.PHONENUM + "=?", whereArgs);
            Log.i("DB update", "" + in);
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
    }

    // 获取某一月所有
    public Cursor getAType(int type) {

        StringBuilder SQLStr = new StringBuilder();
        SQLStr.append("SELECT * FROM ");
        SQLStr.append(DatabaseHelper.TABLE_NAME);
        SQLStr.append(" WHERE ");
        SQLStr.append(DatabaseHelper.TYPE);
        SQLStr.append(" = ");
        //        SQLStr.append("\'" + type + "\'");
        SQLStr.append(type);
        SQLStr.append(" ORDER BY " + DatabaseHelper.NAME);

        Log.i("query str", SQLStr.toString());

        database.beginTransaction();
        Cursor cursor;
        try {
            cursor = database.rawQuery(SQLStr.toString(), null);
            Log.i("DB query", "Query");
            database.setTransactionSuccessful();
        } finally {
            database.endTransaction();
        }
        return cursor;
    }

}
