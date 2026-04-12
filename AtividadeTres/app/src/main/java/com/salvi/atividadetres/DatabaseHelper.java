package com.salvi.atividadetres;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "aplicacaodb";
    private static final int VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE item(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "name TEXT," +
                        "email TEXT," +
                        "account TEXT UNIQUE," +
                        "password TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS item");
        onCreate(db);
    }

    public int getTotalRows(String table){
        SQLiteDatabase db = getReadableDatabase();
        int count = 0;
        Cursor cursor = null;

        try{
            cursor = db.rawQuery("SELECT COUNT(*) FROM " + table, null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
        }catch (Exception e){
            e.printStackTrace();
        } finally {
            if (cursor != null) {
                cursor.close();
            }
            db.close();
        }
        return count;
    }
}