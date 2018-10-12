package com.example.android.bookstore.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.bookstore.data.StockContract.BookEntry;

public class BookDbHelper extends SQLiteOpenHelper {
    Context context;

    private static final String DATABASE_NAME = "bookstore.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDB){

     String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE " + StockContract.BookEntry.TABLE_NAME + " ("
                + BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, "
                + BookEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER NOT NULL DEFAULT 0, "
                + BookEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, "
                + BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER + " INTEGER NOT NULL DEFAULT 0 ); ";

        sqLiteDB.execSQL (SQL_CREATE_BOOKS_TABLE);
    }


    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDB, int oldVersion, int newVersion){
        if (newVersion > oldVersion){
            dropBooksTable(sqLiteDB);
        }
        onCreate (sqLiteDB);
    }

    private void dropBooksTable(SQLiteDatabase sqLiteDB){
        String sql = "DROP TABLE IF EXISTS " + BookEntry.TABLE_NAME;
        sqLiteDB.execSQL (sql);
    }
}
