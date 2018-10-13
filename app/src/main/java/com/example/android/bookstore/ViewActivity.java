package com.example.android.bookstore;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.StockContract.BookEntry;


    public class ViewActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

        private static final int INVENTORY_LOADER = 0;
        private Uri mCurrentProductUri;

        private TextView mProductNameViewText;
        private TextView mProductPriceViewText;
        private TextView mProductQuantityViewText;
        private TextView mProductSupplierNameSpinner;
        private TextView mProductSupplierPhoneNumberViewText;

        @Override
        protected void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_view);

            FloatingActionButton edit = findViewById (R.id.fab_edit);
            edit.setOnClickListener (new View.OnClickListener(){

                public void onClick(View view){
                    Intent intent = new Intent (ViewActivity.this, EditorActivity.class);
                    mCurrentProductUri = getIntent ().getData ();
                    intent.setData (mCurrentProductUri);
                    startActivity (intent);
                }
            });

            FloatingActionButton delete = findViewById (R.id.fab_delete_forever);
            delete.setOnClickListener (new View.OnClickListener () {
                @Override
                public void onClick(View v) {
                    showDeleteConfirmationDialog ();
                }
            });

            mProductNameViewText = findViewById(R.id.name);
            mProductPriceViewText = findViewById(R.id.price_text_view);
            mProductQuantityViewText = findViewById(R.id.quantity_view_text);
            mProductSupplierNameSpinner = findViewById(R.id.view_supplier_name);
            mProductSupplierPhoneNumberViewText = findViewById(R.id.edit_phone_number);

            Intent intent = getIntent();
            mCurrentProductUri = intent.getData();
            if (mCurrentProductUri == null) {
                invalidateOptionsMenu();
            } else {
                getLoaderManager().initLoader(INVENTORY_LOADER, null, this);
            }

            Log.d("message", "onCreate ViewActivity");

        }

        @Override
        public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
            String[] projection = {
                    BookEntry._ID,
                    BookEntry.COLUMN_PRODUCT_NAME,
                    BookEntry.COLUMN_PRODUCT_PRICE,
                    BookEntry.COLUMN_PRODUCT_QUANTITY,
                    BookEntry.COLUMN_SUPPLIER_NAME,
                    BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

            return new CursorLoader (this,
                    BookEntry.CONTENT_URI,
                    projection,
                    null,
                    null,
                    null);
        }

        @Override
        public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
            if (cursor == null || cursor.getCount() < 1) {
                return;
            }
            if (cursor.moveToFirst()) {

                final int idColumnIndex = cursor.getColumnIndex(BookEntry._ID);
                int nameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_NAME);
                int priceColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_PRICE);
                int quantityColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_PRODUCT_QUANTITY);
                int supplierNameColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_NAME);
                int supplierPhoneColumnIndex = cursor.getColumnIndex(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

                String currentName = cursor.getString(nameColumnIndex);
                final int currentPrice = cursor.getInt(priceColumnIndex);
                final int currentQuantity = cursor.getInt(quantityColumnIndex);
                final int currentSupplierPhone = cursor.getInt(supplierPhoneColumnIndex);

                mProductNameViewText.setText(currentName);
                mProductPriceViewText.setText(currentPrice);
                mProductQuantityViewText.setText(currentQuantity);
                mProductSupplierPhoneNumberViewText.setText(currentSupplierPhone);

            }
        }

        @Override
        public void onLoaderReset(Loader<Cursor> loader) {

        }

        private void updateProduct(int productQuantity) {
            Log.d("message", "updateProduct at ViewActivity");

            if (mCurrentProductUri == null) {
                return;
            }
            ContentValues values = new ContentValues();
            values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, productQuantity);

            if (mCurrentProductUri == null) {
                Uri newUri = getContentResolver().insert(BookEntry.CONTENT_URI, values);
                if (newUri == null) {
                    Toast.makeText(this, getString(R.string.insert_successful),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_failed),
                            Toast.LENGTH_SHORT).show();
                }
            } else {
                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);
                if (rowsAffected == 0) {
                    Toast.makeText(this, getString(R.string.update_successful),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.insert_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }

        private void deleteProduct() {
            if (mCurrentProductUri != null) {
                int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
                if (rowsDeleted == 0) {
                    Toast.makeText(this, getString(R.string.delete_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, getString(R.string.delete_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
            finish();
        }

        private void showDeleteConfirmationDialog() {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(R.string.delete_dialog_msg);
            builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    deleteProduct();
                }
            });
            builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    if (dialog != null) {
                        dialog.dismiss();
                    }
                }
            });
            AlertDialog alertDialog = builder.create();
            alertDialog.show();
        }

    }
