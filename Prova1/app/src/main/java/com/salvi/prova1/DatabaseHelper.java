package com.salvi.prova1;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DB_NAME = "product_db";
    private static final int VERSION = 1;

    public DatabaseHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(
                "CREATE TABLE product_items(" +
                        "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                        "product TEXT," +
                        "brand TEXT," +
                        "erp_id TEXT unique," +
                        "expire_at TEXT)"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS product_items");
        onCreate(db);
    }

    public Cursor getAllProducts() {
        SQLiteDatabase db = this.getReadableDatabase();

        return db.rawQuery(
                "SELECT id, product, brand, erp_id, expire_at FROM product_items ORDER BY expire_at DESC",
                null
        );
    }

    public void deleteProductById(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete("product_items", "id=?", new String[]{String.valueOf(id)});
        db.close();
    }
}