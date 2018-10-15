package com.example.android.bookstore.data;

import android.annotation.TargetApi;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.RequiresApi;
import android.util.Log;

import com.example.android.bookstore.data.StockContract.BookEntry;

import java.util.Objects;

import static com.example.android.bookstore.data.StockContract.BookEntry.CONTENT_ITEM_TYPE;
import static com.example.android.bookstore.data.StockContract.BookEntry.CONTENT_LIST_TYPE;

public class InventoryProvider extends ContentProvider {

    public static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate(){
        mDbHelper = new BookDbHelper(getContext ());
        return true;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, String selection,
                        String[] selectionArgs, String sortOrder){
        SQLiteDatabase database = mDbHelper.getReadableDatabase ();
        Cursor cursor;

        int match = sUriMatcher.match (uri);
        switch (match){
            case BOOKS:
                cursor = database.query(StockContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            case BOOK_ID:
                selection = StockContract.BookEntry._ID + "=?";
                selectionArgs = new String[] {String.valueOf (ContentUris.parseId (uri))};
                cursor = database.query (StockContract.BookEntry.TABLE_NAME, projection, selection,
                        selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException ("Cannot query unknown URI" + uri);
        }

        cursor.setNotificationUri (Objects.requireNonNull(getContext()).getContentResolver(), uri);
        return cursor;
    }

    @TargetApi(Build.VERSION_CODES.KITKAT)
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues){
        final int match = sUriMatcher.match (uri);
        switch (match){
            case BOOKS:
                assert contentValues != null;
                return insertBook (uri, contentValues);
                default:
                    throw new IllegalArgumentException ("Insertion is not supported for" + uri);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private Uri insertBook(Uri uri, ContentValues values){
        String name = values.getAsString (BookEntry.COLUMN_PRODUCT_NAME);
        if (name == null){
        throw new IllegalArgumentException ("Book requires a name");
    }

    Integer price = values.getAsInteger(BookEntry.COLUMN_PRODUCT_PRICE);
    if(price != null && price < 0){
        throw new IllegalArgumentException ("Book requires a valid price");
    }

    Integer quantity = values.getAsInteger (BookEntry.COLUMN_PRODUCT_QUANTITY);
    if (quantity != null && quantity < 0){
        throw new IllegalArgumentException ("A valid quantity is needed");
    }

    SQLiteDatabase database = mDbHelper.getWritableDatabase ();

    long id = database.insert (BookEntry.TABLE_NAME, null, values);
    if(id == -1){
        Log.e(LOG_TAG, "Row failed to insert for " + uri);
        return null;
    }

    Objects.requireNonNull (getContext ()).getContentResolver ().notifyChange (uri, null);
    return ContentUris.withAppendedId (uri, id);
}

@RequiresApi(api = Build.VERSION_CODES.KITKAT)
@Override
public int update(@NonNull Uri uri, ContentValues contentValues,
                  @Nullable String selection, @Nullable String[] selectionArgs){
        final int match = sUriMatcher.match (uri);
        switch (match){
            case BOOKS:
                return updateBooks(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf (ContentUris.parseId (uri))};
                return updateBooks(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException ("Update is not supported for " + uri);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    private int updateBooks(Uri uri, ContentValues values, String selection, String[] selectionArgs){
        if (values.containsKey (BookEntry.COLUMN_PRODUCT_NAME)){
            String name = values.getAsString (BookEntry.COLUMN_PRODUCT_NAME);
            if (name == null){
                throw new IllegalArgumentException ("Please enter book name");
            }
        }

        if (values.containsKey (BookEntry.COLUMN_PRODUCT_PRICE)){
            Integer price = values.getAsInteger (BookEntry.COLUMN_PRODUCT_PRICE);
            if (price == null){
                throw new IllegalArgumentException ("A valid price is required");
            }
        }

        if (values.containsKey (BookEntry.COLUMN_PRODUCT_QUANTITY)){
            Integer quantity = values.getAsInteger (BookEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null){
                throw new IllegalArgumentException ("Books require a valid quantity");
            }
        }

        if (values.containsKey ((BookEntry.COLUMN_SUPPLIER_NAME))){
            String supplier = values.getAsString (BookEntry.COLUMN_SUPPLIER_NAME);
            if (supplier == null){
                throw new IllegalArgumentException ("Please select a valid supplier");
            }
        }

        if (values.containsKey (BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER)){
            String supplierPhoneNumber = values.getAsString (BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);
            if (supplierPhoneNumber == null){
                throw new IllegalArgumentException ("Please enter a valid phone number");
            }
        }

        if (values.size () == 0){
            return 0;
        }

        SQLiteDatabase database = mDbHelper.getWritableDatabase ();

        int rowsUpdated = database.update(BookEntry.TABLE_NAME, values, selection, selectionArgs);
        if (rowsUpdated != 0){
            Objects.requireNonNull (getContext()).getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    @Override
    public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) {

        SQLiteDatabase database = mDbHelper.getWritableDatabase();

        int rowsDeleted;

        final int match = sUriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                selection = BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }

        if (rowsDeleted != 0) {
            Objects.requireNonNull (getContext ()).getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri){
        final int match = sUriMatcher.match (uri);
        switch (match){
            case BOOKS:
                return CONTENT_LIST_TYPE;
            case BOOK_ID:
                return CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI" + uri + "with match" + match);
        }
    }

    public static final int BOOKS = 100;

    public static final int BOOK_ID = 101;

    private static final UriMatcher sUriMatcher = new UriMatcher (UriMatcher.NO_MATCH);

    static {
        sUriMatcher.addURI (StockContract.CONTENT_AUTHORITY, StockContract.PATH_BOOKS, BOOKS);
        sUriMatcher.addURI (StockContract.CONTENT_AUTHORITY, StockContract.PATH_BOOKS + "/#", BOOK_ID);
    }
}
