package br.com.klauskpm.inventory;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import br.com.klauskpm.inventory.data.ProductContract.ProductEntry;

import static br.com.klauskpm.inventory.R.id.quantity;

/**
 * Created by Kazlauskas on 03/12/2016.
 */

public class ProductCursorAdapter extends CursorAdapter {
    private static final String TAG = ProductCursorAdapter.class.getSimpleName();

    public ProductCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.title = (TextView) view.findViewById(R.id.title);
        holder.quantity = (TextView) view.findViewById(quantity);
        holder.price = (TextView) view.findViewById(R.id.item_price);
        holder.saleButton = (Button) view.findViewById(R.id.sale);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        int id = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));
        Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);

        String title = cursor.getString(cursor
                .getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_TITLE));
        String quantityString = cursor.getString(cursor
                .getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String priceString = cursor.getString(cursor
                .getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));

        int quantity = Integer.parseInt(quantityString);

        holder.saleButton.setOnClickListener(clickView -> {
            Log.d(TAG, "bindView: " + productUri);
            if (quantity > 0) {
                ContentValues values = new ContentValues();
                values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity - 1);

                int updatedRows = context.getContentResolver()
                        .update(productUri, values, null, null);

                if (updatedRows == 0) {
                    Toast.makeText(context, R.string.product_sale_fail, Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(context, R.string.product_sale_success, Toast.LENGTH_SHORT).show();
                }
            } else {
                Toast.makeText(context, R.string.product_unavailable, Toast.LENGTH_SHORT).show();
            }
        });

        holder.title.setText(title);
        holder.quantity.setText(quantityString);
        holder.price.setText(priceString);
    }

    private class ViewHolder {
        TextView title;
        TextView quantity;
        TextView price;

        Button saleButton;
    }
}
