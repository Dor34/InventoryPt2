package com.example.android.bookstore.activity;

import android.annotation.SuppressLint;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.data.StockContract.BookEntry;

import static com.example.android.bookstore.data.InventoryProvider.LOG_TAG;
import static com.example.android.bookstore.data.StockContract.BookEntry.CONTENT_URI;

public class EditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int INVENTORY_LOADER = 0;

    private Uri mCurrentUri;
    private EditText mProductName;
    private EditText mPrice;
    private EditText mQuantity;
    private EditText mSupplierPhoneNumber;
    private Spinner mSupplierNameSpinner;

    public int mSupplier = BookEntry.SUPPLIER_UNKNOWN;

    private boolean mBookHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener (){
      @Override
      public boolean onTouch(View view, MotionEvent motionEvent){
          mBookHasChanged = true;
          return false;
      }
    };

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_editor);

        Button increase = (Button) findViewById (R.id.increase_Button);
        Button decrease = (Button) findViewById (R.id.decrease_Button);

        increase.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                int quantity;
                try{
                quantity = Integer.parseInt (mQuantity.getText ().toString ());
                quantity += 1;
                if (quantity > 100) {
                    quantity = 100;
                }
                } catch (Exception e) {
                    quantity = 1;
                    Toast.makeText (EditorActivity.this, R.string.state_quantity_increase,
                            Toast.LENGTH_SHORT).show ();
                }
                mQuantity.setText (Integer.toString (quantity));
            }
        });

        decrease.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v){
                int quantity;
                try{
                quantity = Integer.parseInt (mQuantity.getText ().toString ());
                quantity -= 1;
                if (quantity < 0){
                    quantity = 0;
                }
                }catch (Exception e){
                    quantity = 0;
                    Toast.makeText (EditorActivity.this, R.string.state_quantity_decrease
                            ,Toast.LENGTH_SHORT).show ();
                }
                mQuantity.setText (Integer.toString (quantity));
            }
        });

        ImageButton phoneButton = (ImageButton) findViewById (R.id.phone_button);

        phoneButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View v) {
                Log.e (LOG_TAG, "Phone button is used");
                String mSupplierPhoneNumberString = mSupplierPhoneNumber.getText ().toString ().trim ();
                mSupplierPhoneNumber = (EditText) findViewById (R.id.edit_phone_number);

                Intent intent = new Intent (Intent.ACTION_DIAL);

                //Verify intent will resolve
                if(intent.resolveActivity (getPackageManager ()) != null){
                    startActivity (intent);
                }

                intent.setData (Uri.parse ("tel:" + mSupplierPhoneNumberString));
            }
        });

        mCurrentUri = getIntent().getData ();

        if (mCurrentUri == null){
            setTitle (getString (R.string.edit_activity_title_new_item));
            invalidateOptionsMenu ();
        }else{
            setTitle (getString (R.string.edit_product));
            getLoaderManager ().initLoader (INVENTORY_LOADER, null, this);
        }

        mProductName = findViewById (R.id.edit_product_name);
        mPrice = findViewById (R.id.edit_price);
        mQuantity = findViewById (R.id.quantity_view_text);
        mSupplierPhoneNumber = findViewById (R.id.edit_phone_number);
        mSupplierNameSpinner = findViewById (R.id.supplier_spinner);

        mProductName.setOnTouchListener (mTouchListener);
        mPrice.setOnTouchListener (mTouchListener);
        mQuantity.setOnTouchListener (mTouchListener);
        mSupplierPhoneNumber.setOnTouchListener (mTouchListener);
        mSupplierNameSpinner.setOnTouchListener (mTouchListener);

        setupSpinner ();
    }

    private void setupSpinner(){

        ArrayAdapter mSupplierNameSpinnerAdapter = ArrayAdapter.createFromResource(this,
                R.array.array_supplier_names, android.R.layout.simple_dropdown_item_1line);

        mSupplierNameSpinner.setAdapter (mSupplierNameSpinnerAdapter);

            mSupplierNameSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    String selection = (String) parent.getItemAtPosition(position);
                    if (!TextUtils.isEmpty(selection)) {
                        if (selection.equals(getString(R.string.supplier_name1))) {
                            mSupplier = BookEntry.SUPPLIER_AMAZON;
                        } else if (selection.equals(getString(R.string.supplier_name2))) {
                            mSupplier = BookEntry.SUPPLIER_OVERSTOCK;
                        } else if (selection.equals(getString(R.string.supplier_name3))) {
                            mSupplier = BookEntry.SUPPLIER_EBAY;
                        } else {
                            mSupplier = BookEntry.SUPPLIER_UNKNOWN;
                        }
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent){
                    mSupplier = BookEntry.SUPPLIER_UNKNOWN;
                }
            });
        }

    private void saveBook(){
        String productNameString = mProductName.getText ().toString ().trim ();

        String bookPriceString = mPrice.getText ().toString ().trim ();

        String quantityString = mQuantity.getText ().toString ().trim ();

        String supplierPhoneNumberString = mSupplierPhoneNumber.getText ().toString ().trim ();

        if (mCurrentUri == null){
            if (TextUtils.isEmpty (productNameString)){
                Toast.makeText (this, "A book title is required!", Toast.LENGTH_SHORT).show ();
                return;
            }
            if (TextUtils.isEmpty (bookPriceString)){
                Toast.makeText (this, "Book price is required", Toast.LENGTH_SHORT).show ();
                return;
            }
            if (TextUtils.isEmpty (quantityString)){
                Toast.makeText (this, "Book quantity is required", Toast.LENGTH_SHORT).show ();
                return;
            }

            if (TextUtils.isEmpty (supplierPhoneNumberString)){
                Toast.makeText (this, "Phone number required", Toast.LENGTH_SHORT).show ();
                return;
            }
        }

        ContentValues values = new ContentValues ();

        values.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString );
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, bookPriceString);
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantityString);
        values.put(BookEntry.COLUMN_SUPPLIER_NAME, String.valueOf (mSupplierNameSpinner));
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        Uri newUri = getContentResolver ().insert (CONTENT_URI, values);

        if (newUri == null){
            Toast.makeText (this, getString (R.string.insert_failed), Toast.LENGTH_SHORT).show ();
            finish ();
            return;
        }else{
            Toast.makeText (this, getString (R.string.insert_successful), Toast.LENGTH_SHORT).show ();

            if (TextUtils.isEmpty (productNameString)){
                Toast.makeText (this, getString(R.string.book_title_required), Toast.LENGTH_SHORT).show ();
                finish ();
                return;
            }else{
                Toast.makeText (this, getString (R.string.insert_failed), Toast.LENGTH_SHORT).show ();
            }
            if (TextUtils.isEmpty (bookPriceString)){
                Toast.makeText (this, getString(R.string.book_price_required), Toast.LENGTH_SHORT).show ();
                finish ();
                return;
            }
            if (TextUtils.isEmpty (quantityString)){
                Toast.makeText (this, getString(R.string.quantity_required), Toast.LENGTH_SHORT).show ();
                finish ();
                return;
            }
            if (TextUtils.isEmpty (supplierPhoneNumberString)){
                Toast.makeText (this, getString(R.string.phone_number_required), Toast.LENGTH_SHORT).show ();
                finish ();
                return;
            }
        }

        values = new ContentValues ();

        values.put(BookEntry.COLUMN_PRODUCT_NAME, productNameString );
        values.put(BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER, supplierPhoneNumberString);

        if (!TextUtils.isEmpty (bookPriceString)){
            int price = 0;
            try {
                price = Integer.parseInt (bookPriceString);
            }catch (NumberFormatException e){
                return;
            }
        }
        values.put(BookEntry.COLUMN_PRODUCT_PRICE, bookPriceString);

        if (!TextUtils.isEmpty (quantityString)){
            int quantity = Integer.parseInt (quantityString);
        }
        values.put(BookEntry.COLUMN_PRODUCT_QUANTITY, quantityString);

        int rowsAffected = getContentResolver ().update (mCurrentUri, values, null, null);
            if (rowsAffected == 0){
                Toast.makeText (this, getString (R.string.update_successful), Toast.LENGTH_SHORT).show ();
                finish ();
            }else{
               Toast.makeText (this, getString (R.string.update_failed), Toast.LENGTH_SHORT).show ();
               finish ();
            }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater ().inflate (R.menu.menu_editor, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save:
                saveBook();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:
                if (!mBookHasChanged){
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener () {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        NavUtils.navigateUpFromSameTask (EditorActivity.this);
                    }
                };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed(){
        if (!mBookHasChanged){
            super.onBackPressed ();
            return;
        }

        DialogInterface.OnClickListener discardButtonClicker =
                new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                finish ();
            }
        };
        showUnsavedChangesDialog(discardButtonClicker);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Since the editor shows all item attributes, define a projection that contains
        // all columns from the item table
        String[] projection = {
                BookEntry._ID,
                BookEntry.COLUMN_PRODUCT_NAME,
                BookEntry.COLUMN_PRODUCT_PRICE,
                BookEntry.COLUMN_PRODUCT_QUANTITY,
                BookEntry.COLUMN_SUPPLIER_NAME,
                BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER};

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader (this,   // Parent activity context
                mCurrentUri,            // Query the content URI for the current item
                projection,             // Columns to include in the resulting Cursor
                null,                   // No selection clause
                null,                   // No selection arguments
                null);                  // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor){
        if (cursor == null || cursor.getCount() < 1){
            int quantity = 0;
            displayQuantity(quantity);
            return;
        }

        if (cursor.moveToFirst ()){
            int nameColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_PRODUCT_NAME);
            int priceColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_PRODUCT_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneNumberColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

            String name = cursor.getString (nameColumnIndex);
            int price = cursor.getInt (priceColumnIndex);
            int quantity = cursor.getInt (quantityColumnIndex);
            int supplierName = cursor.getInt (supplierNameColumnIndex);
            String supplierNumber = cursor.getString (supplierPhoneNumberColumnIndex);

            mProductName.setText(name);
            mPrice.setText (String.valueOf (price));
            mQuantity.setText (String.valueOf (quantity));
            mSupplierPhoneNumber.setText (supplierNumber);

            switch (supplierName){
                case BookEntry.SUPPLIER_AMAZON:
                    mSupplierNameSpinner.setSelection(1);
                    break;
                case BookEntry.SUPPLIER_OVERSTOCK:
                    mSupplierNameSpinner.setSelection(2);
                    break;
                case BookEntry.SUPPLIER_EBAY:
                    mSupplierNameSpinner.setSelection(3);
                    break;
                default:
                    mSupplierNameSpinner.setSelection(0);
                    break;
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader){
        mProductName.setText ("");
        mPrice.setText ("");
        mQuantity.setText ("");
        mSupplierPhoneNumber.setText ("");
        mSupplierNameSpinner.setSelection (0);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener){
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton (R.string.discard, discardButtonClickListener);
        builder.setNegativeButton (R.string.keep_editing, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null){
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder (this);
        builder.setMessage (R.string.delete_dialog_msg);
        builder.setPositiveButton (R.string.delete, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                deleteBook();
            }
        });
        builder.setNegativeButton (R.string.cancel, new DialogInterface.OnClickListener () {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                if (dialog !=null){
                    dialog.dismiss ();
                }
            }
        });
        AlertDialog alertDialog = builder.create ();
        alertDialog.show ();
    }

    private void deleteBook() {
        // Only perform the delete if this is an existing pet.
        if (mCurrentUri != null) {
            int rowsDeleted = getContentResolver ().delete (mCurrentUri, null, null);
            // Show a toast message depending on whether or not the delete was successful.
            if (rowsDeleted == 0) {
                // If no rows were deleted, then there was an error with the delete.
                Toast.makeText (this, getString (R.string.delete_product_failed),
                        Toast.LENGTH_SHORT).show ();
            } else {
                // Otherwise, the delete was successful and we can display a toast.
                Toast.makeText (this, getString (R.string.delete_product_successful),
                        Toast.LENGTH_SHORT).show ();
            }
        }
        finish ();
    }

        private void displayQuantity(int numberOfBooks) {
            mQuantity.setText (Integer.toString (numberOfBooks));
            TextView quantityViewText = (TextView) findViewById (R.id.quantity_view_text);
            quantityViewText.setText (" " + numberOfBooks);
        }

    }

