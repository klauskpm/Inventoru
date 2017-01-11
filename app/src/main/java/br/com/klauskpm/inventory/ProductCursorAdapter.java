package br.com.klauskpm.inventory;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import br.com.klauskpm.inventory.data.ProductContract.ProductEntry;

/**
 * Created by Kazlauskas on 03/12/2016.
 */

public class ProductCursorAdapter extends CursorAdapter {
    public ProductCursorAdapter(Context context) {
        super(context, null, 0);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
        ViewHolder holder = new ViewHolder();

        holder.title = (TextView) view.findViewById(R.id.title);
        holder.quantity = (TextView) view.findViewById(R.id.quantity);
        holder.price = (TextView) view.findViewById(R.id.item_price);

        view.setTag(holder);

        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ViewHolder holder = (ViewHolder) view.getTag();

        String title = cursor.getString(cursor
                .getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_TITLE));
        String quantity = cursor.getString(cursor
                .getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY));
        String price = cursor.getString(cursor
                .getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE));

        holder.title.setText(title);
        holder.quantity.setText(quantity);
        holder.price.setText(price);
    }

    private class ViewHolder {
        TextView title;
        TextView quantity;
        TextView price;
    }
}
