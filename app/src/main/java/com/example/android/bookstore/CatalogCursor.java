package com.example.android.bookstore;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bookstore.activity.CatalogActivity;
import com.example.android.bookstore.activity.R;
import com.example.android.bookstore.data.StockContract.BookEntry;


public class CatalogCursor extends CursorAdapter {

    public CatalogCursor(Context context, Cursor c)
    {
        super(context, c, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {

        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {

        TextView nameTextView = (TextView) view.findViewById (R.id.name);
        TextView priceTextView = (TextView) view.findViewById (R.id.price_text_view);
        TextView quantityTextView = (TextView)view.findViewById (R.id.edit_quantity);
        final Button saleButton = view.findViewById (R.id.sale_button);

        final int columnIdIndex = cursor.getColumnIndex (BookEntry._ID);
        int productNameColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_PRODUCT_NAME);
        int productPriceColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_PRODUCT_PRICE);
        int productQuantityColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierPhoneNumberColumnIndex = cursor.getColumnIndex (BookEntry.COLUMN_SUPPLIER_PHONE_NUMBER);

        int currentId = cursor.getInt (cursor.getColumnIndex (BookEntry._ID));

        final String productID = cursor.getString (columnIdIndex);
        String productName = cursor.getString (productNameColumnIndex);
        String productPrice = cursor.getString (productPriceColumnIndex);
        final int productQuantity = cursor.getInt (productQuantityColumnIndex);
        String supplierPhoneNumber = cursor.getString (supplierPhoneNumberColumnIndex);

        if (TextUtils.isEmpty (productName)) {
            productName = context.getString (R.string.unknown_book);
        }

        nameTextView.setText (productName);
        priceTextView.setText (productPrice);
        quantityTextView.setText (Integer.toString(productQuantity));


        final Uri contentUri = Uri.withAppendedPath (BookEntry.CONTENT_URI, Integer.toString (currentId));
        final Cursor cursorData = null;

        saleButton.setOnClickListener (new View.OnClickListener () {
            @Override
            public void onClick(View view) {
                CatalogActivity catalogActivity = (CatalogActivity) context;
                catalogActivity.saleButton (Integer.valueOf (productID), productQuantity);

                if (productQuantity > 0) {
                    Uri currentUri = ContentUris.withAppendedId (BookEntry.CONTENT_URI,
                            Long.parseLong (productID));
                    ContentValues values = new ContentValues ();
                    values.put (BookEntry.COLUMN_PRODUCT_QUANTITY, productQuantity - 1);
                    context.getContentResolver ().update (currentUri, values, null, null);
                    swapCursor (cursorData);

                    if (productQuantity == 1) {
                        Toast.makeText (context, R.string.book_quantity, Toast.LENGTH_SHORT).show ();
                    }
                }
            }
        });

    }
}



