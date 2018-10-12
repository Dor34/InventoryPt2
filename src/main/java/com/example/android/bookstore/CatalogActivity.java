package com.example.android.bookstore;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.bookstore.data.StockContract.BookEntry;

public class CatalogActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    CatalogCursor mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate (savedInstanceState);
        setContentView (R.layout.activity_catalog);

        FloatingActionButton fab = (FloatingActionButton) findViewById (R.id.fab);
        fab.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent (CatalogActivity.this, EditorActivity.class);
                startActivity (intent);
            }
        });

            ListView inventoryListView = findViewById (R.id.list);
            View emptyView = findViewById (R.id.empty_view);
            inventoryListView.setEmptyView (emptyView);

            mCursorAdapter = new CatalogCursor (this, null);
            inventoryListView.setAdapter (mCursorAdapter);

            inventoryListView.setOnItemClickListener (new AdapterView.OnItemClickListener() {

                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    Intent intent = new Intent (CatalogActivity.this, EditorActivity.class);

                    Uri currentProductUri = ContentUris.withAppendedId (BookEntry.CONTENT_URI, id);

                    intent.setData (currentProductUri);

                    startActivity (intent);
                }
            });

            getLoaderManager ().initLoader (INVENTORY_LOADER, null, this);
        }

    @Override
    protected void onStart(){
        super.onStart ();
    }

    private void insertBook(){
        ContentValues values = new ContentValues ();
        values.put(BookEntry.COLUMN_PRODUCT_NAME, getString(R.string.example_book_name));
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, "$9.99");
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, 4);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, getString(R.string.example_supplier_name));
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, R.string.example_phone_number);

        getContentResolver().insert(BookEntry.CONTENT_URI, values);
    }

    private void deleteAllBooks(){
        int rowsDeleted = getContentResolver().delete (BookEntry.CONTENT_URI, null, null);
        Toast.makeText (this, rowsDeleted + " " + getString (R.string.deleted_all_products_message),
                Toast.LENGTH_SHORT).show ();
    }

    @Override
    public boolean onCreateOptionsMenu (Menu menu){
        getMenuInflater().inflate (R.menu.menu_catalog, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        switch (item.getItemId ()){
            case R.id.action_insert_dummy_data:
                insertBook();
                return true;

            case R.id.action_delete_all_entries:
                deleteAllBooks();
                return true;
        }
        return super.onOptionsItemSelected (item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER,
        };

        return new CursorLoader (this,
                BookEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor (data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mCursorAdapter.swapCursor (null);
    }

    @Override
    public void onBackPressed(){
        super.onBackPressed ();
    }

    public void saleButton(Integer integer, int productQuantity) {
    }
}






