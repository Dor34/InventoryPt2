package com.example.android.bookstore.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

public final class StockContract {

    private StockContract(){
        throw new AssertionError ("No instances for you!");
    }

    static final String CONTENT_AUTHORITY = "com.example.android.bookstore";

    private static final Uri BASE_CONTENT_URI = Uri.parse ("content://" + CONTENT_AUTHORITY);

    static final String PATH_BOOKS = "books";

    public static final class BookEntry implements BaseColumns{

        public static final Uri CONTENT_URI = Uri.withAppendedPath (BASE_CONTENT_URI, PATH_BOOKS);

        public static final String CONTENT_LIST_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE + "/"
                + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        public final static String TABLE_NAME = "books";

        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "product_name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";

        // SUPPLIER_NAME LIST VALUES
        public static final int SUPPLIER_UNKNOWN = 0;
        public static final int SUPPLIER_AMAZON = 1;
        public static final int SUPPLIER_OVERSTOCK = 2;
        public static final int SUPPLIER_EBAY = 3;

        public static boolean isValidSupplier(int supplier){
            return supplier == SUPPLIER_UNKNOWN || supplier == SUPPLIER_AMAZON ||
                    supplier == SUPPLIER_OVERSTOCK || supplier == SUPPLIER_EBAY;
        }
    }
}
